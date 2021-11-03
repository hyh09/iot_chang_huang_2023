package org.thingsboard.server.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * 设备字典属性Repository
 *
 * @author wwj
 * @since 2021.10.21
 */
@Repository
public interface DictDevicePropertyRepository extends PagingAndSortingRepository<DictDevicePropertyEntity, UUID>, JpaSpecificationExecutor<DictDevicePropertyEntity> {

}
