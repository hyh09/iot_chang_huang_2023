package org.thingsboard.server.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * 设备字典部件Repository
 *
 * @author wwj
 * @since 2021.10.21
 */
@Repository
public interface DictDeviceComponentRepository extends PagingAndSortingRepository<DictDeviceComponentEntity, UUID>, JpaSpecificationExecutor<DictDeviceComponentEntity> {

}
