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
package org.thingsboard.server.dao.sql.alarm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.alarm.AlarmSeverity;
import org.thingsboard.server.common.data.alarm.AlarmStatus;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.model.sql.AlarmEntity;
import org.thingsboard.server.dao.model.sql.AlarmInfoEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 5/21/2017.
 */
public interface AlarmRepository extends CrudRepository<AlarmEntity, UUID> {

    Optional<AlarmEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    @Query("SELECT a FROM AlarmEntity a WHERE a.originatorId = :originatorId AND a.type = :alarmType ORDER BY a.startTs DESC")
    List<AlarmEntity> findLatestByOriginatorAndType(@Param("originatorId") UUID originatorId,
                                                    @Param("alarmType") String alarmType,
                                                    Pageable pageable);

    @Query(value = "SELECT new org.thingsboard.server.dao.model.sql.AlarmInfoEntity(a) FROM AlarmEntity a " +
            "LEFT JOIN RelationEntity re ON a.id = re.toId " +
            "AND re.relationTypeGroup = 'ALARM' " +
            "AND re.toType = 'ALARM' " +
            "AND re.fromId = :affectedEntityId " +
            "AND re.fromType = :affectedEntityType " +
            "WHERE a.tenantId = :tenantId " +
            "AND (a.originatorId = :affectedEntityId or re.fromId IS NOT NULL) " +
            "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
            "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
            "AND ((:alarmStatuses) IS NULL OR a.status in (:alarmStatuses)) " +
            "AND (LOWER(a.type) LIKE LOWER(CONCAT(:searchText, '%')) " +
            "  OR LOWER(a.severity) LIKE LOWER(CONCAT(:searchText, '%')) " +
            "  OR LOWER(a.status) LIKE LOWER(CONCAT(:searchText, '%'))) "
            ,
            countQuery = "" +
                    "SELECT count(a) + " + //alarms with relations only
                    " (SELECT count(a) FROM AlarmEntity a " + //alarms WITHOUT any relations
                    "    LEFT JOIN RelationEntity re ON a.id = re.toId " +
                    "    AND re.relationTypeGroup = 'ALARM' " +
                    "    AND re.toType = 'ALARM' " +
                    "    AND re.fromId = :affectedEntityId " +
                    "    AND re.fromType = :affectedEntityType " +
                    "    WHERE a.tenantId = :tenantId " +
                    "    AND (a.originatorId = :affectedEntityId) " +
                    "    AND (re.fromId IS NULL) " + //anti join
                    "    AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
                    "    AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
                    "    AND ((:alarmStatuses) IS NULL OR a.status in (:alarmStatuses)) " +
                    "    AND (LOWER(a.type) LIKE LOWER(CONCAT(:searchText, '%')) " +
                    "    OR LOWER(a.severity) LIKE LOWER(CONCAT(:searchText, '%')) " +
                    "    OR LOWER(a.status) LIKE LOWER(CONCAT(:searchText, '%'))) " +
                    " )" +
                    "FROM AlarmEntity a " +
                    "INNER JOIN RelationEntity re ON a.id = re.toId " +
                    "AND re.relationTypeGroup = 'ALARM' " +
                    "AND re.toType = 'ALARM' " +
                    "AND re.fromId = :affectedEntityId " +
                    "AND re.fromType = :affectedEntityType " +
                    "WHERE a.tenantId = :tenantId " +
                    "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
                    "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
                    "AND ((:alarmStatuses) IS NULL OR a.status in (:alarmStatuses)) " +
                    "AND (LOWER(a.type) LIKE LOWER(CONCAT(:searchText, '%')) " +
                    "  OR LOWER(a.severity) LIKE LOWER(CONCAT(:searchText, '%')) " +
                    "  OR LOWER(a.status) LIKE LOWER(CONCAT(:searchText, '%'))) ")
    Page<AlarmInfoEntity> findAlarms(@Param("tenantId") UUID tenantId,
                                     @Param("affectedEntityId") UUID affectedEntityId,
                                     @Param("affectedEntityType") String affectedEntityType,
                                     @Param("startTime") Long startTime,
                                     @Param("endTime") Long endTime,
                                     @Param("alarmStatuses") Set<AlarmStatus> alarmStatuses,
                                     @Param("searchText") String searchText,
                                     Pageable pageable);

    @Query(value = "SELECT new org.thingsboard.server.dao.model.sql.AlarmInfoEntity(a) FROM AlarmEntity a " +
            "WHERE a.tenantId = :tenantId " +
            "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
            "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
            "AND ((:alarmStatuses) IS NULL OR a.status in (:alarmStatuses)) " +
            "AND (LOWER(a.type) LIKE LOWER(CONCAT(:searchText, '%')) " +
            "  OR LOWER(a.severity) LIKE LOWER(CONCAT(:searchText, '%')) " +
            "  OR LOWER(a.status) LIKE LOWER(CONCAT(:searchText, '%'))) ",
            countQuery = "" +
                    "SELECT count(a) " +
                    "FROM AlarmEntity a " +
                    "WHERE a.tenantId = :tenantId " +
                    "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
                    "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
                    "AND ((:alarmStatuses) IS NULL OR a.status in (:alarmStatuses)) " +
                    "AND (LOWER(a.type) LIKE LOWER(CONCAT(:searchText, '%')) " +
                    "  OR LOWER(a.severity) LIKE LOWER(CONCAT(:searchText, '%')) " +
                    "  OR LOWER(a.status) LIKE LOWER(CONCAT(:searchText, '%'))) ")
    Page<AlarmInfoEntity> findAllAlarms(@Param("tenantId") UUID tenantId,
                                        @Param("startTime") Long startTime,
                                        @Param("endTime") Long endTime,
                                        @Param("alarmStatuses") Set<AlarmStatus> alarmStatuses,
                                        @Param("searchText") String searchText,
                                        Pageable pageable);

    @Query(value = "SELECT new org.thingsboard.server.dao.model.sql.AlarmInfoEntity(a) FROM AlarmEntity a " +
            "WHERE a.tenantId = :tenantId AND a.customerId = :customerId " +
            "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
            "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
            "AND ((:alarmStatuses) IS NULL OR a.status in (:alarmStatuses)) " +
            "AND (LOWER(a.type) LIKE LOWER(CONCAT(:searchText, '%')) " +
            "  OR LOWER(a.severity) LIKE LOWER(CONCAT(:searchText, '%')) " +
            "  OR LOWER(a.status) LIKE LOWER(CONCAT(:searchText, '%'))) "
            ,
            countQuery = "" +
                    "SELECT count(a) " +
                    "FROM AlarmEntity a " +
                    "WHERE a.tenantId = :tenantId AND a.customerId = :customerId " +
                    "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
                    "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
                    "AND ((:alarmStatuses) IS NULL OR a.status in (:alarmStatuses)) " +
                    "AND (LOWER(a.type) LIKE LOWER(CONCAT(:searchText, '%')) " +
                    "  OR LOWER(a.severity) LIKE LOWER(CONCAT(:searchText, '%')) " +
                    "  OR LOWER(a.status) LIKE LOWER(CONCAT(:searchText, '%'))) ")
    Page<AlarmInfoEntity> findCustomerAlarms(@Param("tenantId") UUID tenantId,
                                             @Param("customerId") UUID customerId,
                                             @Param("startTime") Long startTime,
                                             @Param("endTime") Long endTime,
                                             @Param("alarmStatuses") Set<AlarmStatus> alarmStatuses,
                                             @Param("searchText") String searchText,
                                             Pageable pageable);

    @Query(value = "SELECT a.severity FROM AlarmEntity a " +
            "LEFT JOIN RelationEntity re ON a.id = re.toId " +
            "AND re.relationTypeGroup = 'ALARM' " +
            "AND re.toType = 'ALARM' " +
            "AND re.fromId = :affectedEntityId " +
            "AND re.fromType = :affectedEntityType " +
            "WHERE a.tenantId = :tenantId " +
            "AND (a.originatorId = :affectedEntityId or re.fromId IS NOT NULL) " +
            "AND ((:alarmStatuses) IS NULL OR a.status in (:alarmStatuses))")
    Set<AlarmSeverity> findAlarmSeverities(@Param("tenantId") UUID tenantId,
                                           @Param("affectedEntityId") UUID affectedEntityId,
                                           @Param("affectedEntityType") String affectedEntityType,
                                           @Param("alarmStatuses") Set<AlarmStatus> alarmStatuses);

    @Query("SELECT a.id FROM AlarmEntity a WHERE a.tenantId = :tenantId AND a.createdTime < :time AND a.endTs < :time")
    Page<UUID> findAlarmsIdsByEndTsBeforeAndTenantId(@Param("time") Long time, @Param("tenantId") UUID tenantId, Pageable pageable);

    @Query(value = "SELECT new org.thingsboard.server.dao.model.sql.AlarmInfoEntity(a) FROM AlarmEntity a " +
            "LEFT JOIN RelationEntity re ON a.id = re.toId " +
            "AND re.relationTypeGroup = 'ALARM' " +
            "AND re.toType = 'ALARM' " +
            "AND re.fromId in (:affectedEntityIds) " +
            "AND re.fromType = :affectedEntityType " +
            "WHERE a.tenantId = :tenantId " +
            "AND (a.originatorId in (:affectedEntityIds) or re.fromId IS NOT NULL) " +
            "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
            "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
            "AND ((:alarmStatuses) IS NULL OR a.status in (:alarmStatuses)) " +
            "AND ((:alarmSeverities) IS NULL OR a.severity in (:alarmSeverities)) "
            ,
            countQuery = "" +
                    "SELECT count(a) + " + //alarms with relations only
                    " (SELECT count(a) FROM AlarmEntity a " + //alarms WITHOUT any relations
                    "    LEFT JOIN RelationEntity re ON a.id = re.toId " +
                    "    AND re.relationTypeGroup = 'ALARM' " +
                    "    AND re.toType = 'ALARM' " +
                    "    AND re.fromId in (:affectedEntityIds) " +
                    "    AND re.fromType = :affectedEntityType " +
                    "    WHERE a.tenantId = :tenantId " +
                    "    AND (a.originatorId in (:affectedEntityIds) ) " +
                    "    AND (re.fromId IS NULL) " + //anti join
                    "    AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
                    "    AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
                    "    AND ((:alarmStatuses) IS NULL OR a.status in (:alarmStatuses)) " +
                    "    AND ((:alarmSeverities) IS NULL OR a.severity in (:alarmSeverities)) " +
                    " )" +
                    "FROM AlarmEntity a " +
                    "INNER JOIN RelationEntity re ON a.id = re.toId " +
                    "AND re.relationTypeGroup = 'ALARM' " +
                    "AND re.toType = 'ALARM' " +
                    "AND re.fromId in (:affectedEntityIds) " +
                    "AND re.fromType = :affectedEntityType " +
                    "WHERE a.tenantId = :tenantId " +
                    "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
                    "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
                    "AND ((:alarmStatuses) IS NULL OR a.status in (:alarmStatuses)) " +
                    "AND ((:alarmSeverities) IS NULL OR a.severity in (:alarmSeverities)) "
    )
    Page<AlarmInfoEntity> findAlarmsByDeviceIdList(@Param("tenantId") UUID tenantId,
                                                   @Param("affectedEntityIds") List<UUID> affectedEntityIds,
                                                   @Param("affectedEntityType") String affectedEntityType,
                                                   @Param("startTime") Long startTime,
                                                   @Param("endTime") Long endTime,
                                                   @Param("alarmStatuses") Set<AlarmStatus> alarmStatuses,
                                                   @Param("alarmSeverities") Set<AlarmSeverity> alarmSeverities,
                                                   Pageable pageable);

    @Query(value = "SELECT a FROM AlarmEntity a " +
            "LEFT JOIN RelationEntity re ON a.id = re.toId " +
            "AND re.relationTypeGroup = 'ALARM' " +
            "AND re.toType = 'ALARM' " +
            "AND re.fromId in (:affectedEntityIds) " +
            "AND re.fromType = :affectedEntityType " +
            "WHERE a.tenantId = :tenantId " +
            "AND (a.originatorId in (:affectedEntityIds) or re.fromId IS NOT NULL) " +
            "AND (:startTime IS NULL OR a.createdTime >= :startTime) "
    )
    List<AlarmEntity> findAllAlarmsByStartTime(@Param("tenantId") UUID tenantId,
                                               @Param("affectedEntityIds") List<UUID> affectedEntityIds,
                                               @Param("affectedEntityType") String affectedEntityType,
                                               @Param("startTime") Long startTime);

    @Query(value = "SELECT a FROM AlarmEntity a " +
            "LEFT JOIN RelationEntity re ON a.id = re.toId " +
            "AND re.relationTypeGroup = 'ALARM' " +
            "AND re.toType = 'ALARM' " +
            "AND re.fromId in (:affectedEntityIds) " +
            "AND re.fromType = :affectedEntityType " +
            "WHERE a.tenantId = :tenantId " +
            "AND (a.originatorId in (:affectedEntityIds) or re.fromId IS NOT NULL) " +
            "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
            "AND (:endTime IS NULL OR a.createdTime < :endTime) "
    )
    List<AlarmEntity> findAllAlarmsByStartTimeAndEndTime(@Param("tenantId") UUID tenantId,
                                               @Param("affectedEntityIds") List<UUID> affectedEntityIds,
                                               @Param("affectedEntityType") String affectedEntityType,
                                               @Param("startTime") Long startTime,
                                               @Param("endTime") Long endTime);

    @Query(value = "SELECT count(a) FROM AlarmEntity a " +
            "LEFT JOIN RelationEntity re ON a.id = re.toId " +
            "AND re.relationTypeGroup = 'ALARM' " +
            "AND re.toType = 'ALARM' " +
            "AND re.fromId in (:affectedEntityIds) " +
            "AND re.fromType = :affectedEntityType " +
            "WHERE a.tenantId = :tenantId " +
            "AND (a.originatorId in (:affectedEntityIds) or re.fromId IS NOT NULL) " +
            "AND (:startTime IS NULL OR a.createdTime >= :startTime) "
    )
    Integer countAllAlarmsByStartTime(@Param("tenantId") UUID tenantId,
                                      @Param("affectedEntityIds") List<UUID> affectedEntityIds,
                                      @Param("affectedEntityType") String affectedEntityType,
                                      @Param("startTime") Long startTime);

    @Query(value = "SELECT count(a) FROM AlarmEntity a " +
            "LEFT JOIN RelationEntity re ON a.id = re.toId " +
            "AND re.relationTypeGroup = 'ALARM' " +
            "AND re.toType = 'ALARM' " +
            "AND re.fromId in (:affectedEntityIds) " +
            "AND re.fromType = :affectedEntityType " +
            "WHERE a.tenantId = :tenantId " +
            "AND (a.originatorId in (:affectedEntityIds) or re.fromId IS NOT NULL) " +
            "AND a.createdTime >= :startTime " +
            "AND a.createdTime < :endTime "
    )
    Integer countAllAlarmsByStartTimeAndEndTime(@Param("tenantId") UUID tenantId,
                                                @Param("affectedEntityIds") List<UUID> affectedEntityIds,
                                                @Param("affectedEntityType") String affectedEntityType,
                                                @Param("startTime") Long startTime,
                                                @Param("endTime") Long endTime);

    @Query(value = "SELECT count(a) FROM AlarmEntity a " +
            "LEFT JOIN RelationEntity re ON a.id = re.toId " +
            "AND re.relationTypeGroup = 'ALARM' " +
            "AND re.toType = 'ALARM' " +
            "AND re.fromId in (:affectedEntityIds) " +
            "AND re.fromType = :affectedEntityType " +
            "WHERE a.tenantId = :tenantId " +
            "AND (a.originatorId in (:affectedEntityIds) or re.fromId IS NOT NULL) "
    )
    Integer countAllAlarmsHistory(@Param("tenantId") UUID tenantId,
                                  @Param("affectedEntityIds") List<UUID> affectedEntityIds,
                                  @Param("affectedEntityType") String affectedEntityType);

    Integer countAllByTenantId(UUID tenantId);

    Integer countAllByTenantIdAndCreatedTimeGreaterThan(UUID tenantId, Long startTime);

    Integer countAllByTenantIdAndCreatedTimeBetween(UUID tenantId, Long startTime, Long endTime);
}
