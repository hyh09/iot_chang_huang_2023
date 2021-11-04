package org.thingsboard.server.dao.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.factory.FactoryListVo;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.vo.JudgeUserVo;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.sql.role.service.UserRoleMenuSvc;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FactoryServiceImpl extends AbstractEntityService implements FactoryService {

    private final FactoryDao factoryDao;
    private final UserRoleMenuSvc userRoleMenuSvc;

    public FactoryServiceImpl(FactoryDao factoryDao,UserRoleMenuSvc userRoleMenuSvc){
        this.factoryDao = factoryDao;
        this.userRoleMenuSvc = userRoleMenuSvc;
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
    public FactoryListVo findFactoryListBuyCdn(Factory factory, JudgeUserVo judgeUserVo){
        log.trace("Executing findFactoryListBuyCdn [{}]", factory,judgeUserVo);
        return factoryDao.findFactoryListBuyCdn( factory,judgeUserVo);
    }
}
