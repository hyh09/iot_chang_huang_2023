package org.thingsboard.server.dao.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 接口描述： 开机时长记录表
 * 作者： 范王勇
 * 创建日期: 2023-01-02
 */
@Repository
public interface TrepDayStaDetailRepository extends PagingAndSortingRepository<TrepDayStaDetailEntity, UUID>, JpaSpecificationExecutor<TrepDayStaDetailEntity> {

    List<TrepDayStaDetailEntity> findAllByBdateEqualsOrStartTimeIsNotNull(Date bdate);

    List<TrepDayStaDetailEntity> findAllByBdateEqualsAndEntityIdIn(Date bdate, List<UUID> entityIdList);

    List<TrepDayStaDetailEntity> findAllByBdateEqualsAndEntityId(Date bdate, UUID entityIdList);
}
