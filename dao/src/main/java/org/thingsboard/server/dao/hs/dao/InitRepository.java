package org.thingsboard.server.dao.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 初始化数据Repository
 *
 * @author wwj
 * @since 2021.11.10
 */
@Repository
public interface InitRepository extends PagingAndSortingRepository<InitEntity, UUID>, JpaSpecificationExecutor<InitEntity> {

    Optional<InitEntity> findByScope(String scope);
}
