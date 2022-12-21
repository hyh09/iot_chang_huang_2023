package org.thingsboard.server.dao.hsms.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.dao.hs.dao.DictDataEntity;
import org.thingsboard.server.dao.hs.dao.DictDeviceGraphItemEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    Optional<DictDeviceSwitchEntity> findByPropertyIdAndPropertyType(UUID propertyId, String propertyType);

    List<DictDeviceSwitchEntity> findAllByDeviceId(UUID deviceId);

    List<DictDeviceSwitchEntity> findAllByDictDeviceId(UUID dictDeviceId);

}
