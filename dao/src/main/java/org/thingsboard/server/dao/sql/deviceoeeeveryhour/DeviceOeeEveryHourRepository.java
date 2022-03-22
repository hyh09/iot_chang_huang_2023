package org.thingsboard.server.dao.sql.deviceoeeeveryhour;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.thingsboard.server.dao.model.sql.DeviceOeeEveryHourEntity;

import java.util.UUID;

public interface DeviceOeeEveryHourRepository extends PagingAndSortingRepository<DeviceOeeEveryHourEntity, UUID>, JpaSpecificationExecutor<DeviceOeeEveryHourEntity> {

}
