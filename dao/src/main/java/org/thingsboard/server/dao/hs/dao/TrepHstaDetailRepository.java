package org.thingsboard.server.dao.hs.dao;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 设备字典图表实体类
 *
 * @author wwj
 * @since 2021.10.21
 */
@Repository
public interface TrepHstaDetailRepository extends PagingAndSortingRepository<TrepHstaDetailEntity, UUID>, JpaSpecificationExecutor<TrepHstaDetailEntity> {

    List<TrepHstaDetailEntity> findAllByUpdateTimeIsNull();
}
