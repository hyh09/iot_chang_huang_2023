package org.thingsboard.server.dao.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 设备字典分组Repository
 *
 * @author wwj
 * @since 2021.10.21
 */
@Repository
public interface DictDeviceGroupRepository extends PagingAndSortingRepository<DictDeviceGroupEntity, UUID>, JpaSpecificationExecutor<DictDeviceGroupEntity> {

    @Modifying
    @Query("delete from DictDeviceGroupEntity d where d.dictDeviceId = :dictDeviceId")
    void deleteByDictDeviceId(@Param("dictDeviceId") UUID dictDeviceId);

    @Query("select t from DictDeviceGroupEntity t where t.dictDeviceId = :dictDeviceId")
    List<DictDeviceGroupEntity> findAllByDictDeviceId(@Param("dictDeviceId") UUID dictDeviceId);
}
