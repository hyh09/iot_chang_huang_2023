package org.thingsboard.server.dao.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.common.data.vo.device.DictDeviceDataVo;

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

    List<DictDeviceGroupPropertyEntity> findAllByDictDataId(UUID dictDataId);

    @Modifying
    @Query("delete from DictDeviceGroupPropertyEntity d where d.dictDeviceId = :dictDeviceId")
    void deleteByDictDeviceId(@Param("dictDeviceId") UUID dictDeviceId);

    @Query("select t from DictDeviceGroupPropertyEntity t where t.dictDeviceGroupId in (?1) order by t.sort asc")
    List<DictDeviceGroupPropertyEntity> findAllInDictDeviceGroupId(List<UUID> groupIdList);

    @Query("select t from DictDeviceGroupPropertyEntity t where t.dictDeviceId = :dictDeviceId order by t.sort asc")
    List<DictDeviceGroupPropertyEntity> findAllByDictDeviceId(@Param("dictDeviceId") UUID dictDeviceId);

    @Query("select t from DictDeviceGroupPropertyEntity t, DictDeviceGroupEntity t1 where  t.dictDeviceGroupId= t1.id   and t1.name =:name   order by t.sort asc")
    List<DictDeviceGroupPropertyEntity> findAllByName(@Param("name") String name);

    @Query("select new org.thingsboard.server.common.data.vo.device.DictDeviceDataVo(t1.name,t.name) from DictDeviceGroupPropertyEntity t, DictDeviceGroupEntity t1 where  t.dictDeviceGroupId= t1.id   and t1.dictDeviceId =:dictDeviceId   order by t.sort,t1.sort asc")
    List<DictDeviceDataVo> findGroupNameAndName(@Param("dictDeviceId") UUID dictDeviceId);


}
