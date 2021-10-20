package org.thingsboard.server.hs.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 数据字典Repository
 *
 * @author wwj
 * @since 2021.10.19
 */
@Repository
public interface DictDataRepository extends PagingAndSortingRepository<DictDataEntity, UUID>, JpaSpecificationExecutor<DictDataEntity> {

    @Query("select code from DictDataEntity")
    List<String> findCodes();
}
