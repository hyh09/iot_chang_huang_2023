package org.thingsboard.server.dao.factory;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.factory.FactoryListVo;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.factory.FactoryId;

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
    void delFactory(UUID id)throws ThingsboardException ;


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
    FactoryListVo findFactoryListByCdn(Factory factory);

    /**
     * 查询工厂最新版本
     * @param factory
     * @return
     */
    List<Factory> findFactoryVersion(Factory factory) throws Exception;

    /**
     *查询工厂本列表
     * @param factory
     * @return
     * @throws Exception
     */
    List<Factory> findFactoryVersionList(Factory factoryk) throws Exception;

    /**
     * 根据登录人角色查询工厂列表
     * @param userId
     * @param tenantId
     * @return
     */
    List<Factory> findFactoryListByLoginRole(UUID userId,UUID tenantId);

    /**
     * 根据名称查询
     * @return
     */
    List<Factory> findByName(String name,UUID tenantId);

    /**
     * 获取实体属性
     * @param o
     */
    String[] getEntityAttributeList(Object o);

    ListenableFuture<Factory> findFactoryByIdAsync(TenantId callerId, FactoryId factoryId);

    /**
     * 校验工厂下是否有网关（true-有，false-无）
     * @param factoryId
     * @return
     * @throws ThingsboardException
     */
    Boolean checkFactoryHaveGateway(String factoryId) throws ThingsboardException;

    /**
     * 根据登录人角色查询工厂状态
     * @param userId
     * @param tenantId
     * @return
     */
    List<Factory> findFactoryStatusByLoginRole(UUID userId,UUID tenantId)throws ThingsboardException;

}
