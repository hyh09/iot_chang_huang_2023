package org.thingsboard.server.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * 设备字典分组属性Repository
 *
 * @author wwj
 * @since 2021.10.21
 */
@Repository
public interface DictDeviceGroupPropertyRepository extends PagingAndSortingRepository<DictDeviceGroupPropertyEntity, UUID>, JpaSpecificationExecutor<DictDeviceGroupPropertyEntity> {

}
