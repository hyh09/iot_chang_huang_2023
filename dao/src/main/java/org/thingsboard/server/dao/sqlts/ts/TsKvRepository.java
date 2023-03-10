/**
 * Copyright © 2016-2021 The Thingsboard Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.sqlts.ts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.dao.model.sqlts.ts.TsKvCompositeKey;
import org.thingsboard.server.dao.model.sqlts.ts.TsKvEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface TsKvRepository extends CrudRepository<TsKvEntity, TsKvCompositeKey> {

    @Query("SELECT tskv FROM TsKvEntity tskv WHERE tskv.entityId = :entityId " +
            "AND tskv.key in (:entityKeys) AND tskv.ts >= :startTs AND tskv.ts < :endTs  order by  tskv.ts asc ")
    List<TsKvEntity> findAllByKeysAndEntityIdAndStartTimeAndEndTime(@Param("entityId") UUID entityId,
                                                                    @Param("entityKeys") List<Integer> key,
                                                                    @Param("startTs") long startTs,
                                                                    @Param("endTs") long endTs
    );

    @Query("SELECT count(1) FROM TsKvEntity tskv WHERE tskv.entityId = :entityId " +
            "AND tskv.key in (:entityKeys) AND tskv.ts >= :startTs AND tskv.ts < :endTs   ")
    long countByKeysAndEntityIdAndStartTimeAndEndTime(@Param("entityId") UUID entityId,
                                                      @Param("entityKeys") List<Integer> key,
                                                      @Param("startTs") long startTs,
                                                      @Param("endTs") long endTs
    );


//    @Query("SELECT tskv FROM TsKvEntity tskv WHERE tskv.entityId = :entityId " +
//            "AND tskv.key in (:entityKeys) AND tskv.ts >= :startTs AND tskv.ts < :endTs  order by  tskv.ts asc ")
//    List<TsKvEntity> findAllByKeysAndEntityIdAndStartTimeAndEndTimePage(@Param("entityId") UUID entityId,
//                                                                    @Param("entityKeys") List<Integer> key,
//                                                                    @Param("startTs") long startTs,
//                                                                    @Param("endTs") long endTs,
//                                                                    Pageable pageable
//    );


    @Query("SELECT tskv FROM TsKvEntity tskv WHERE tskv.entityId = :entityId " +
            "AND tskv.key in (:entityKeys)  ")
    List<TsKvEntity> findAllByKeysAndEntityIdAndTime(@Param("entityId") UUID entityId,
                                                     @Param("entityKeys") List<Integer> key
//                                      @Param("startTs") long startTs,
//                                      @Param("endTs") long endTs
    );


    @Query("SELECT tskv FROM TsKvEntity tskv WHERE tskv.entityId = :entityId " +
            "AND tskv.key = :entityKey AND tskv.ts >= :startTs AND tskv.ts < :endTs")
    List<TsKvEntity> findAllWithLimit(@Param("entityId") UUID entityId,
                                      @Param("entityKey") int key,
                                      @Param("startTs") long startTs,
                                      @Param("endTs") long endTs,
                                      Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM TsKvEntity tskv WHERE tskv.entityId = :entityId " +
            "AND tskv.key = :entityKey AND tskv.ts >= :startTs AND tskv.ts < :endTs")
    void delete(@Param("entityId") UUID entityId,
                @Param("entityKey") int key,
                @Param("startTs") long startTs,
                @Param("endTs") long endTs);

    @Async
    @Query("SELECT new TsKvEntity(MAX(tskv.strValue)) FROM TsKvEntity tskv " +
            "WHERE tskv.strValue IS NOT NULL " +
            "AND tskv.entityId = :entityId AND tskv.key = :entityKey AND tskv.ts >= :startTs AND tskv.ts < :endTs")
    CompletableFuture<TsKvEntity> findStringMax(@Param("entityId") UUID entityId,
                                                @Param("entityKey") int entityKey,
                                                @Param("startTs") long startTs,
                                                @Param("endTs") long endTs);

    @Async
    @Query("SELECT new TsKvEntity(MAX(COALESCE(tskv.longValue, -9223372036854775807)), " +
            "MAX(COALESCE(tskv.doubleValue, -1.79769E+308)), " +
            "SUM(CASE WHEN tskv.longValue IS NULL THEN 0 ELSE 1 END), " +
            "SUM(CASE WHEN tskv.doubleValue IS NULL THEN 0 ELSE 1 END), " +
            "'MAX') FROM TsKvEntity tskv " +
            "WHERE tskv.entityId = :entityId AND tskv.key = :entityKey AND tskv.ts >= :startTs AND tskv.ts < :endTs")
    CompletableFuture<TsKvEntity> findNumericMax(@Param("entityId") UUID entityId,
                                                 @Param("entityKey") int entityKey,
                                                 @Param("startTs") long startTs,
                                                 @Param("endTs") long endTs);


    @Async
    @Query("SELECT new TsKvEntity(MIN(tskv.strValue)) FROM TsKvEntity tskv " +
            "WHERE tskv.strValue IS NOT NULL " +
            "AND tskv.entityId = :entityId AND tskv.key = :entityKey AND tskv.ts >= :startTs AND tskv.ts < :endTs")
    CompletableFuture<TsKvEntity> findStringMin(@Param("entityId") UUID entityId,
                                                @Param("entityKey") int entityKey,
                                                @Param("startTs") long startTs,
                                                @Param("endTs") long endTs);

    @Async
    @Query("SELECT new TsKvEntity(MIN(COALESCE(tskv.longValue, 9223372036854775807)), " +
            "MIN(COALESCE(tskv.doubleValue, 1.79769E+308)), " +
            "SUM(CASE WHEN tskv.longValue IS NULL THEN 0 ELSE 1 END), " +
            "SUM(CASE WHEN tskv.doubleValue IS NULL THEN 0 ELSE 1 END), " +
            "'MIN') FROM TsKvEntity tskv " +
            "WHERE tskv.entityId = :entityId AND tskv.key = :entityKey AND tskv.ts >= :startTs AND tskv.ts < :endTs")
    CompletableFuture<TsKvEntity> findNumericMin(
            @Param("entityId") UUID entityId,
            @Param("entityKey") int entityKey,
            @Param("startTs") long startTs,
            @Param("endTs") long endTs);

    @Async
    @Query("SELECT new TsKvEntity(SUM(CASE WHEN tskv.booleanValue IS NULL THEN 0 ELSE 1 END), " +
            "SUM(CASE WHEN tskv.strValue IS NULL THEN 0 ELSE 1 END), " +
            "SUM(CASE WHEN tskv.longValue IS NULL THEN 0 ELSE 1 END), " +
            "SUM(CASE WHEN tskv.doubleValue IS NULL THEN 0 ELSE 1 END), " +
            "SUM(CASE WHEN tskv.jsonValue IS NULL THEN 0 ELSE 1 END)) FROM TsKvEntity tskv " +
            "WHERE tskv.entityId = :entityId AND tskv.key = :entityKey AND tskv.ts >= :startTs AND tskv.ts < :endTs")
    CompletableFuture<TsKvEntity> findCount(@Param("entityId") UUID entityId,
                                            @Param("entityKey") int entityKey,
                                            @Param("startTs") long startTs,
                                            @Param("endTs") long endTs);

    @Async
    @Query("SELECT new TsKvEntity(SUM(COALESCE(tskv.longValue, 0)), " +
            "SUM(COALESCE(tskv.doubleValue, 0.0)), " +
            "SUM(CASE WHEN tskv.longValue IS NULL THEN 0 ELSE 1 END), " +
            "SUM(CASE WHEN tskv.doubleValue IS NULL THEN 0 ELSE 1 END), " +
            "'AVG') FROM TsKvEntity tskv " +
            "WHERE tskv.entityId = :entityId AND tskv.key = :entityKey AND tskv.ts >= :startTs AND tskv.ts < :endTs")
    CompletableFuture<TsKvEntity> findAvg(@Param("entityId") UUID entityId,
                                          @Param("entityKey") int entityKey,
                                          @Param("startTs") long startTs,
                                          @Param("endTs") long endTs);

    @Async
    @Query("SELECT new TsKvEntity(SUM(COALESCE(tskv.longValue, 0)), " +
            "SUM(COALESCE(tskv.doubleValue, 0.0)), " +
            "SUM(CASE WHEN tskv.longValue IS NULL THEN 0 ELSE 1 END), " +
            "SUM(CASE WHEN tskv.doubleValue IS NULL THEN 0 ELSE 1 END), " +
            "'SUM') FROM TsKvEntity tskv " +
            "WHERE tskv.entityId = :entityId AND tskv.key = :entityKey AND tskv.ts >= :startTs AND tskv.ts < :endTs")
    CompletableFuture<TsKvEntity> findSum(@Param("entityId") UUID entityId,
                                          @Param("entityKey") int entityKey,
                                          @Param("startTs") long startTs,
                                          @Param("endTs") long endTs);

    @Query("SELECT distinct tskv.ts FROM TsKvEntity tskv WHERE tskv.entityId = :entityId " +
            "AND tskv.key in (:entityKeys) AND tskv.ts >= :startTs AND tskv.ts < :endTs order by tskv.ts asc")
    List<Long> findTss(@Param("entityId") UUID entityId,
                       @Param("entityKeys") Set<Integer> keys,
                       @Param("startTs") long startTs,
                       @Param("endTs") long endTs);

    @Query("SELECT distinct tskv.ts FROM TsKvEntity tskv WHERE tskv.entityId = :entityId " +
            "AND tskv.key in (:entityKeys) AND tskv.ts >= :startTs AND tskv.ts < :endTs ")
    Page<Long> findTss(@Param("entityId") UUID entityId,
                       @Param("entityKeys") Set<Integer> keys,
                       @Param("startTs") long startTs,
                       @Param("endTs") long endTs, Pageable pageable);

    @Query("SELECT distinct tskv.ts FROM TsKvEntity tskv WHERE tskv.entityId = :entityId " +
            "AND tskv.key = :key AND tskv.ts >= :startTs AND tskv.ts < :endTs ")
    Page<Long> findTss(@Param("entityId") UUID entityId,
                       @Param("key") Integer key,
                       @Param("startTs") long startTs,
                       @Param("endTs") long endTs, Pageable pageable);

    @Query(value = "SELECT distinct tskv.ts FROM ts_kv tskv WHERE tskv.entity_id = :entityId " +
            "AND tskv.key = :key AND tskv.ts >= :startTs AND tskv.ts < :endTs order by tskv.ts desc limit :limitSize offset :offsetSize", nativeQuery = true)
    List<Long> findTss(@Param("entityId") UUID entityId,
                       @Param("key") Integer key,
                       @Param("startTs") long startTs,
                       @Param("endTs") long endTs, @Param("limitSize") long limitSize, @Param("offsetSize") long offsetSize);

    @Query("SELECT tskv FROM TsKvEntity tskv WHERE tskv.entityId = :entityId " +
            "AND tskv.key in (:entityKeys) AND tskv.ts >= :startTs AND tskv.ts <= :endTs")
    List<TsKvEntity> findAllByStartTsAndEndTs(@Param("entityId") UUID entityId,
                                              @Param("entityKeys") Set<Integer> keys,
                                              @Param("startTs") long startTs,
                                              @Param("endTs") long endTs);

    @Query("SELECT tskv FROM TsKvEntity tskv WHERE tskv.entityId = :entityId " +
            "AND tskv.key in (:entityKeys) AND tskv.ts >= :startTs AND tskv.ts <= :endTs order by tskv.ts asc")
    List<TsKvEntity> findAllByStartTsAndEndTsOrderByTsAsc(@Param("entityId") UUID entityId,
                                                          @Param("entityKeys") Set<Integer> keys,
                                                          @Param("startTs") long startTs,
                                                          @Param("endTs") long endTs);

    @Query("SELECT tskv FROM TsKvEntity tskv WHERE tskv.entityId = :entityId " +
            "AND tskv.key in (:entityKeys) AND tskv.ts >= :startTs AND tskv.ts <= :endTs order by tskv.ts desc")
    List<TsKvEntity> findAllByStartTsAndEndTsOrderByTsDesc(@Param("entityId") UUID entityId,
                                                           @Param("entityKeys") Set<Integer> keys,
                                                           @Param("startTs") long startTs,
                                                           @Param("endTs") long endTs);

    @Query("SELECT tskv FROM TsKvEntity tskv WHERE tskv.entityId = :entityId " +
            "AND tskv.key = :entityKey AND tskv.ts >= :startTs AND tskv.ts < :endTs")
    Page<TsKvEntity> findAll(@Param("entityId") UUID entityId,
                             @Param("entityKey") int key,
                             @Param("startTs") long startTs,
                             @Param("endTs") long endTs,
                             Pageable pageable);


    @Query("SELECT tskv FROM TsKvEntity tskv WHERE tskv.entityId = :entityId " +
            "AND tskv.key = :entityKey  AND tskv.ts = :time ")
    TsKvEntity findAllByTsAndEntityIdAndKey(@Param("entityId") UUID entityId,
                                            @Param("entityKey") int entityKey,
                                            @Param("time") long time
    );


    @Query(value = "select  max(ts) from  ts_kv  where  ts< :time and  entity_id= :entityId and key= :entityKey " +
            " and  concat(long_v,dbl_v,str_v) <>'0'", nativeQuery = true)
    Long findAllMaxTime(@Param("entityId") UUID entityId,
                        @Param("entityKey") int entityKey,
                        @Param("time") long time
    );

    @Query(value = "SELECT  * FROM ts_kv tskv WHERE tskv.entity_id = :entityId " +
            "  AND tskv.key = :entityKey  and  tskv.ts in (select  min(tskv1.ts)  from ts_kv tskv1 WHERE tskv1.entity_id = :entityId  AND tskv1.key = :entityKey " +
            "  AND tskv1.ts >= :startTime AND tskv1.ts < :endTime )  ", nativeQuery = true)
    TsKvEntity findAllTodayFirstData(@Param("entityId") UUID entityId, @Param("entityKey") int entityKey, @Param("startTime") long startTime, @Param("endTime") long endTime);


    /**
     * 查询个时间范围内的最大的 那条数据
     * @param entityId
     * @param entityKey
     * @param startTime
     * @param endTime
     * @return
     */
    @Query(value = "SELECT  * FROM ts_kv tskv WHERE tskv.entity_id = :entityId " +
            "  AND tskv.key = :entityKey  and  tskv.ts in (select  max(tskv1.ts)  from ts_kv tskv1 WHERE tskv1.entity_id = :entityId  AND tskv1.key = :entityKey " +
            "  AND tskv1.ts >= :startTime AND tskv1.ts < :endTime )  ", nativeQuery = true)
    TsKvEntity findAllTodayLastData(@Param("entityId") UUID entityId, @Param("entityKey") int entityKey, @Param("startTime") long startTime, @Param("endTime") long endTime);

}
