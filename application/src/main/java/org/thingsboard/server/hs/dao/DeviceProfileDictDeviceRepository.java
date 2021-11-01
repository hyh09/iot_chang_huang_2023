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
 * 设备配置设备字典关系表Repository
 *
 * @author wwj
 * @since 2021.10.19
 */
@Repository
public interface DeviceProfileDictDeviceRepository extends PagingAndSortingRepository<DeviceProfileDictDeviceEntity, UUID>, JpaSpecificationExecutor<DeviceProfileDictDeviceEntity> {

    @Modifying
    @Query("delete from DeviceProfileDictDeviceEntity d where d.deviceProfileId =:deviceProfileId")
    void deleteByDeviceProfileId(@Param("deviceProfileId") UUID deviceProfileId);

    @Query("select p from DeviceProfileDictDeviceEntity d " +
            "left join DictDeviceEntity p on d.dictDeviceId = p.id " +
            "where d.deviceProfileId =:deviceProfileId")
    List<DictDeviceEntity> findAllBindDeviceProfile(@Param("deviceProfileId") UUID deviceProfileId);
}
