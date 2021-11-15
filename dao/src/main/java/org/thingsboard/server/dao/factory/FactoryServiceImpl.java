package org.thingsboard.server.dao.factory;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Device;
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

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class FactoryServiceImpl extends AbstractEntityService implements FactoryService {

    private static final String PREFIX_ENCODING_GC = "GC";

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
    public Factory saveFactory(Factory factory) throws ThingsboardException{
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
        factory.setCode(PREFIX_ENCODING_GC + String.valueOf(System.currentTimeMillis()));
        return factoryDao.saveFactory(factory);
    }

    /**
     * 修改后刷新值
     * @param factory
     * @return
     */
    @Override
    public Factory updFactory(Factory factory)throws ThingsboardException{
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
        return factoryDao.findFactoryByTenantId(tenantId);
    }

    /**
     * 查询详情
     * @param id
     * @return
     */
    @Override
    public Factory findById(UUID id){
        log.trace("Executing findById [{}]", id);
        return factoryDao.findById(id);
    }

    /**
     * 条件查询工厂列表
     * @param factory
     * @return
     */
    @Override
    public FactoryListVo findFactoryListByCdn(Factory factory){
        log.trace("Executing findFactoryListBuyCdn [{}]", factory);
        return factoryDao.findFactoryListByCdn( factory,userRoleMenuSvc.decideUser(new UserId(factory.getLoginUserId())));
    }

    /**
     * 查询工厂最新版本
     * @param factory
     * @return
     */
    @Override
    public List<Factory> findFactoryVersion(Factory factory) throws ThingsboardException{
        List<Factory> resultFactory = this.findFactoryListByLoginRole(factory.getLoginUserId(),factory.getTenantId());
        //查询工厂关联的最新版本的网关设备版本信息
        if (CollectionUtils.isNotEmpty(resultFactory)) {
            //筛选出当前登录人能查看的所有工厂
            List<UUID> factoryIdList = resultFactory.stream().map(Factory::getId).collect(Collectors.toList());
            //查询工厂最大版本
            List<Device> gatewayNewVersionByFactory = deviceService.findGatewayNewVersionByFactory(factoryIdList);
            if(CollectionUtils.isNotEmpty(gatewayNewVersionByFactory)){
                resultFactory.forEach(i->{
                    gatewayNewVersionByFactory.forEach(j->{
                        if(i.getId().toString().equals(j.getFactoryId().toString())){
                            i.setFactoryVersion(j.getGatewayVersion());
                            i.setPublishTime(j.getGatewayUpdateTs());
                        }
                    });
                });
            }
        }
        return resultFactory;
    }

    /**
     * 根据登录人角色查询工厂列表
     * @param userId
     * @param tenantId
     * @return
     */
    @Override
    public List<Factory> findFactoryListByLoginRole(UUID userId,UUID tenantId){
        List<Factory> resultFactory = new ArrayList<>();
        //查询登录人角色
        //查询登录人角色及所属工厂
        JudgeUserVo judgeUserVo = userRoleMenuSvc.decideUser(new UserId(userId));
        if (judgeUserVo != null && judgeUserVo.getTenantFlag() != null &&judgeUserVo.getTenantFlag()) {
            //租户管理员/租户有菜单权限的用户，拥有全部数据权限
            resultFactory = factoryDao.findFactoryByTenantId(tenantId);
        } else if(judgeUserVo != null && judgeUserVo.getFactoryManagementFlag() != null && judgeUserVo.getFactoryManagementFlag()){
            //工厂管理员/工厂用户，拥有所属工厂数据权限
            //查询工厂信息
            Factory queryFactory = factoryDao.findFactoryByAdmin(judgeUserVo.getUserId());
            if (queryFactory != null) {
                resultFactory.add(queryFactory);
            }
        }
        return resultFactory;
    }

    /**
     * 根据名称查询
     * @return
     */
    @Override
    public List<Factory> findByName(String name,UUID tenantId){
        Factory queryFactory = new Factory();
        queryFactory.setTenantId(tenantId);
        queryFactory.setName(name);
        return factoryDao.findAllByCdn(queryFactory);
    }

}
