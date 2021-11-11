package org.thingsboard.server.dao.factory;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.factory.FactoryListVo;

import java.util.List;
import java.util.UUID;

public interface FactoryService {

    /**
     * 保存后刷新值
     * @param factory
     * @return
     */
    Factory saveFactory(Factory factory)throws ThingsboardException;

    /**
     * 修改后刷新值
     * @param factory
     * @return
     */
    Factory updFactory(Factory factory)throws ThingsboardException;

    /**
     * 删除后刷新值
     * @param id
     * @param id
     * @return
     */
    void delFactory(UUID id);


    /**
     * 查询工厂列表
     * @param tenantId
     * @return
     */
    List<Factory> findFactoryList(UUID tenantId);

    /**
     * 查询详情
     * @param id
     * @return
     */
    Factory findById(UUID id);

    /**
     * 条件查询工厂列表
     * @param factory
     * @return
     */
    FactoryListVo findFactoryListBuyCdn(Factory factory);

    /**
     * 查询工厂最新版本
     * @param factory
     * @return
     */
    List<Factory> findFactoryVersion(Factory factory) throws Exception;

    /**
     * 根据登录人角色查询工厂列表
     * @param userId
     * @param tenantId
     * @return
     */
    List<Factory> findFactoryListByLoginRole(UUID userId,UUID tenantId);

}
