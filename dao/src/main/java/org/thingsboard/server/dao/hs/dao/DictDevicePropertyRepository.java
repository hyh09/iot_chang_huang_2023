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
 * 设备字典属性Repository
 *
 * @author wwj
 * @since 2021.10.21
 */
@Repository
public interface DictDevicePropertyRepository extends PagingAndSortingRepository<DictDevicePropertyEntity, UUID>, JpaSpecificationExecutor<DictDevicePropertyEntity> {


    @Modifying
    @Query("delete from DictDevicePropertyEntity d where d.dictDeviceId = :dictDeviceId")
    void deleteByDictDeviceId(@Param("dictDeviceId") UUID dictDeviceId);

    @Query("select t from DictDevicePropertyEntity t where t.dictDeviceId = :dictDeviceId")
    List<DictDevicePropertyEntity> findAllByDictDeviceId(@Param("dictDeviceId") UUID dictDeviceId);
}
