package org.thingsboard.server.dao.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 设备字典部件属性Repository
 *
 * @author wwj
 * @since 2021.10.21
 */
@Repository
public interface DictDeviceComponentPropertyRepository extends PagingAndSortingRepository<DictDeviceComponentPropertyEntity, UUID>, JpaSpecificationExecutor<DictDeviceComponentPropertyEntity> {

    List<DictDeviceComponentPropertyEntity> findAllByComponentIdOrderBySortAsc(UUID componentId);

    @Modifying
    @Query("delete from DictDeviceComponentPropertyEntity d where d.dictDeviceId = :dictDeviceId and d.id not in :ids")
    void deleteByDictDeviceAndIdsNotIn(@Param("dictDeviceId") UUID dictDeviceId, @Param("ids") List<UUID> ids);

    @Modifying
    @Query("delete from DictDeviceComponentPropertyEntity d where d.dictDeviceId = :dictDeviceId")
    void deleteByDictDeviceId(@Param("dictDeviceId") UUID dictDeviceId);

    @Query("select t from DictDeviceComponentPropertyEntity t where t.dictDeviceId = :dictDeviceId order by t.sort asc")
    List<DictDeviceComponentPropertyEntity> findAllByDictDeviceId(@Param("dictDeviceId") UUID dictDeviceId);

    List<DictDeviceComponentPropertyEntity> findAllByDictDataId(UUID toUUID);

    Optional<DictDeviceComponentPropertyEntity> findByDictDeviceIdAndName(UUID dictDeviceId, String tsPropertyName);
}
