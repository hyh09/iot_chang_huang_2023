package org.thingsboard.server.dao.sql.mesdevicerelation;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.thingsboard.server.dao.model.sql.MesDeviceRelationEntity;

import java.util.List;
import java.util.UUID;

public interface MesDeviceRelationRepository extends PagingAndSortingRepository<MesDeviceRelationEntity, UUID>, JpaSpecificationExecutor<MesDeviceRelationEntity> {
    List<MesDeviceRelationEntity> findAllByDeviceIdIn(List<UUID> deviceIds);
}
