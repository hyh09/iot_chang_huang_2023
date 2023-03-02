package org.thingsboard.server.dao.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 设备字典标准属性Repository
 *
 * @author wwj
 * @since 2021.10.21
 */
@Repository
public interface DictDeviceStandardPropertyRepository extends PagingAndSortingRepository<DictDeviceStandardPropertyEntity, UUID>, JpaSpecificationExecutor<DictDeviceStandardPropertyEntity> {

    List<DictDeviceStandardPropertyEntity> findAllByDictDataId(UUID toUUID);

    List<DictDeviceStandardPropertyEntity> findAllByDictDeviceIdOrderBySortAsc(UUID dictDataId);

    void deleteAllByDictDeviceId(UUID dictDeviceId);

    @Query("select t  from  DictDeviceStandardPropertyEntity  t where t.dictDeviceId= :dictDeviceId and t.name in (:name) ")
    List<DictDeviceStandardPropertyEntity>  findAllByInContentAndDictDataId(@Param("dictDeviceId") UUID dictDeviceId,@Param("name") List<String> name);
}
