package org.thingsboard.server.dao.factory;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.factory.FactoryListVo;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.vo.JudgeUserVo;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.sql.role.service.UserRoleMenuSvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FactoryServiceImpl extends AbstractEntityService implements FactoryService {

    private final FactoryDao factoryDao;
    private final UserRoleMenuSvc userRoleMenuSvc;
    private final DeviceService deviceService;
    public FactoryServiceImpl(FactoryDao factoryDao,UserRoleMenuSvc userRoleMenuSvc,DeviceService deviceService){
        this.factoryDao = factoryDao;
        this.userRoleMenuSvc = userRoleMenuSvc;
        this.deviceService = deviceService;
    }


    /**
     * 保存后刷新值
     * @param factory
     * @return
     */
    @Override
    public Factory saveFactory(Factory factory){
        log.trace("Executing saveFactory [{}]", factory);
        if (factory != null && factory.getId() == null) {
            //创建管理员账号
            User adduser = new User();
            adduser.setPhoneNumber(factory.getMobile());
            adduser.setEmail(factory.getEmail());
            adduser.setUserName(factory.getName());
            User loginUser = new User();
            loginUser.setId(new UserId(factory.getCreatedUser()));
            loginUser.setTenantId(new TenantId(factory.getTenantId()));
            User saveUser = userRoleMenuSvc.save(adduser,loginUser);

            factory.setAdminUserId(saveUser.getId().getId());
            factory.setAdminUserName(saveUser.getUserName());
        }
        factory.setCode(String.valueOf(System.currentTimeMillis()));
        return factoryDao.saveFactory(factory);
    }

    /**
     * 修改后刷新值
     * @param factory
     * @return
     */
    @Override
    public Factory updFactory(Factory factory){
        log.trace("Executing updFactory [{}]", factory);
        return factoryDao.saveFactory(factory);
    }

    /**
     * 删除后刷新值(逻辑删除)
     * @param id
     * @param id
     * @return
     */
    @Override
    public void delFactory(UUID id){
        log.trace("Executing delFactory [{}]", id);
        factoryDao.delFactory(id);
    }


    /**
     * 查询工厂列表
     * @param tenantId
     * @return
     */
    @Override
    public List<Factory> findFactoryList(UUID tenantId){
        log.trace("Executing findFactoryList [{}]", tenantId);
        return factoryDao.find(new TenantId(tenantId));
    }

    /**
     * 查询详情
     * @param id
     * @return
     */
    @Override
    public Factory findById(UUID id){
        log.trace("Executing findById [{}]", id);
        return factoryDao.findById(null, id);
    }

    /**
     * 条件查询工厂列表
     * @param factory
     * @return
     */
    @Override
    public FactoryListVo findFactoryListBuyCdn(Factory factory){
        log.trace("Executing findFactoryListBuyCdn [{}]", factory);
        return factoryDao.findFactoryListBuyCdn( factory,userRoleMenuSvc.decideUser(new UserId(factory.getLoginUserId())));
    }

    /**
     * 查询工厂最新版本
     * @param factory
     * @return
     */
    @Override
    public List<Factory> findFactoryVersion(Factory factory) throws ThingsboardException{
        List<Factory> resultFactory = new ArrayList<>();
        //查询登录人角色及所属工厂
        JudgeUserVo judgeUserVo = userRoleMenuSvc.decideUser(new UserId(factory.getLoginUserId()));
        List<Factory> queryFactoryList = new ArrayList<>();
        if(judgeUserVo != null && judgeUserVo.getFactoryManagementFlag()){
            //是工厂管理员/工厂用户
            //查询工厂信息
            Factory queryFactory = factoryDao.findFactoryByAdmin(judgeUserVo.getUserId());
            if(queryFactory != null){
                queryFactoryList.add(queryFactory);
            }
        }else {
            //查询工厂信息
            queryFactoryList = factoryDao.findFactoryByTenantId(factory.getTenantId());
        }
        //查询工厂关联的最新版本的网关设备版本信息
        if(CollectionUtils.isNotEmpty(queryFactoryList)){
            //筛选出当前登录人能查看的所有工厂
            List<UUID> factoryIdList = queryFactoryList.stream().map(Factory::getId).collect(Collectors.toList());
            queryFactoryList.forEach(i->{
                // TODO: 2021/11/4 查询工厂的网关设备级共享属性
                //deviceService.findGatewayNewVersionByFactory(factoryIdList);
            });

        }
        return resultFactory;
    }
}
