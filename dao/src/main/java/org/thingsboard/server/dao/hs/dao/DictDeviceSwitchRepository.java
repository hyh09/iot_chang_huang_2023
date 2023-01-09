package org.thingsboard.server.dao.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 设备字典属性开关数据Repository
 *
 * @author wwj
 * @since 2021.11.10
 */
@Repository
public interface DictDeviceSwitchRepository extends PagingAndSortingRepository<DictDeviceSwitchEntity, UUID>, JpaSpecificationExecutor<DictDeviceSwitchEntity> {

    void deleteAllByDeviceId(UUID deviceId);

    void deleteAllByDictDeviceId(UUID dictDeviceId);

    void deleteByPropertyIdAndPropertyType(UUID propertyId, String propertyType);

    Optional<DictDeviceSwitchEntity> findByDeviceIdAndPropertyIdAndPropertyType(UUID deviceId, UUID propertyId, String propertyType);

    List<DictDeviceSwitchEntity> findAllByDeviceId(UUID deviceId);

    List<DictDeviceSwitchEntity> findAllByDictDeviceId(UUID dictDeviceId);

}
