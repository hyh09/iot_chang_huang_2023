package org.thingsboard.server.dao.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 设备字典图表实体类
 *
 * @author wwj
 * @since 2021.10.21
 */
@Repository
public interface TrepDayStaDetailRepository extends PagingAndSortingRepository<TrepDayStaDetailEntity, UUID>, JpaSpecificationExecutor<TrepDayStaDetailEntity> {

    List<TrepDayStaDetailEntity> findAllByBdateEqualsOrStartTimeIsNotNull(Date bdate);

    List<TrepDayStaDetailEntity> findAllByBdateEqualsAndEntityIdIn(Date bdate,List<UUID> entityIdList);
}
