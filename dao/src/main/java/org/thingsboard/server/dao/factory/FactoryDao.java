package org.thingsboard.server.dao.factory;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.factory.FactoryListVo;
import org.thingsboard.server.common.data.vo.JudgeUserVo;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.model.sql.FactoryEntity;

import java.util.List;
import java.util.UUID;

public interface FactoryDao extends Dao<Factory>{

    Factory saveFactory(Factory factory)throws ThingsboardException;
    /**
     * 条件查询工厂列表
     * @param factory
     * @return
     */
    FactoryListVo findFactoryListBuyCdn(Factory factory, JudgeUserVo judgeUserVo);

    /**
     * 删除后刷新值(逻辑删除)
     * @param id
     */
    void delFactory(UUID id);

    /**
     * 根据工厂管理员查询
     * @param factoryAdminId
     * @return
     */
    Factory findFactoryByAdmin(UUID factoryAdminId);

    /**
     * 根据租户查询
     * @param tenantId
     * @return
     */
    List<Factory> findFactoryByTenantId(UUID tenantId);

    /**
     * 查询租户的第一条工厂
     * @param tenantId 租户id
     * @return
     */
    FactoryEntity findFactoryByTenantIdFirst(UUID tenantId);

    /**
     * 查询详情
     * @param id
     * @return
     */
    Factory findById(UUID id);
}
