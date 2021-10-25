package org.thingsboard.server.dao.factory;

import org.thingsboard.server.common.data.factory.Factory;

import java.util.List;
import java.util.UUID;

public interface FactoryService {

    /**
     * 保存后刷新值
     * @param factory
     * @return
     */
    Factory saveFactory(Factory factory);

    /**
     * 修改后刷新值
     * @param factory
     * @return
     */
    Factory updFactory(Factory factory);

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

}
