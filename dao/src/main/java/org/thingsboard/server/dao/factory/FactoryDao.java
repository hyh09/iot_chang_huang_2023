package org.thingsboard.server.dao.factory;

import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.factory.FactoryListVo;
import org.thingsboard.server.common.data.vo.JudgeUserVo;
import org.thingsboard.server.dao.Dao;

import java.util.UUID;

public interface FactoryDao extends Dao<Factory>{

    Factory saveFactory(Factory factory);
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
}
