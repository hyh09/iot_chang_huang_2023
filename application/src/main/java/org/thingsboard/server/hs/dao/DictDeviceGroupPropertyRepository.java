package org.thingsboard.server.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 设备字典分组属性Repository
 *
 * @author wwj
 * @since 2021.10.21
 */
@Repository
public interface DictDeviceGroupPropertyRepository extends PagingAndSortingRepository<DictDeviceGroupPropertyEntity, UUID>, JpaSpecificationExecutor<DictDeviceGroupPropertyEntity> {

    @Modifying
    @Query("delete from DictDeviceGroupPropertyEntity d where d.dictDeviceId = :dictDeviceId")
    void deleteByDictDeviceId(@Param("dictDeviceId") UUID dictDeviceId);

    @Query("select t from DictDeviceGroupPropertyEntity t where t.dictDeviceGroupId in (?1)")
    List<DictDeviceGroupPropertyEntity> findAllInDictDeviceGroupId(List<UUID> groupIdList);
}
