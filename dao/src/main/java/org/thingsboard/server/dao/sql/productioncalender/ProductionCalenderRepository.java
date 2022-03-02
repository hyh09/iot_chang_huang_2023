package org.thingsboard.server.dao.sql.productioncalender;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.thingsboard.server.dao.model.sql.ProductionCalenderEntity;

import java.util.UUID;

public interface ProductionCalenderRepository extends PagingAndSortingRepository<ProductionCalenderEntity, UUID>, JpaSpecificationExecutor<ProductionCalenderEntity> {
}
