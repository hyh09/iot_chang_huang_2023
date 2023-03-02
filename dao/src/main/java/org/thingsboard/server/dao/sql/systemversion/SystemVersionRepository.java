package org.thingsboard.server.dao.sql.systemversion;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.thingsboard.server.dao.model.sql.SystemVersionEntity;

import java.util.UUID;

public interface SystemVersionRepository extends PagingAndSortingRepository<SystemVersionEntity, UUID>, JpaSpecificationExecutor<SystemVersionEntity> {
}
