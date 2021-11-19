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
 * 设备字典部件Repository
 *
 * @author wwj
 * @since 2021.10.21
 */
@Repository
public interface DictDeviceComponentRepository extends PagingAndSortingRepository<DictDeviceComponentEntity, UUID>, JpaSpecificationExecutor<DictDeviceComponentEntity> {

    List<DictDeviceComponentEntity> findAllByDictDataId(UUID dictDataId);

    @Modifying
    @Query("delete from DictDeviceComponentEntity d where d.dictDeviceId = :dictDeviceId")
    void deleteByDictDeviceId(@Param("dictDeviceId") UUID dictDeviceId);

    @Query("select t from DictDeviceComponentEntity t where t.dictDeviceId = :dictDeviceId order by t.sort asc")
    List<DictDeviceComponentEntity> findAllByDictDeviceId(@Param("dictDeviceId") UUID dictDeviceId);
}
