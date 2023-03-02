package org.thingsboard.server.dao.sql.tskv.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.sql.tskv.entity.EnergyHistoryHourEntity;
import org.thingsboard.server.dao.util.sql.BaseSqlDao;

import java.util.List;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 统计能耗历史的数据表（分钟维度)
 * @author: HU.YUNHUI
 * @create: 2022-01-18 16:10
 **/
public interface EnergyHistoryHourDao extends BaseSqlDao<EnergyHistoryHourEntity, UUID> {


    @Query(value = "select t  from  EnergyHistoryHourEntity t  where t.entityId= :entityId AND t.ts = :ts")
    EnergyHistoryHourEntity queryAllByEntityIdAndDate(@Param("entityId") UUID entityId, @Param("ts") Long ts);


    @Query("select t  from  EnergyHistoryMinuteEntity t  where t.entityId= :entityId and t.ts>= :startTs and t.ts<= :endTs ")
    Page<EnergyHistoryHourEntity> queryByDeviceIdAndTs(@Param("entityId") UUID entityId, @Param("startTs") Long startTs, @Param("endTs") Long endTs, Pageable pageable);

    @Query(value = "select t  from  EnergyHistoryHourEntity t  where t.entityId= :entityId AND t.ts between :statrtTime AND :endTime ORDER BY ts ASC")
    List<EnergyHistoryHourEntity> queryAllByEntityIdAndBetweenDate(@Param("entityId") UUID entityId, @Param("statrtTime") Long statrtTime, @Param("endTime") Long endTime);

}
