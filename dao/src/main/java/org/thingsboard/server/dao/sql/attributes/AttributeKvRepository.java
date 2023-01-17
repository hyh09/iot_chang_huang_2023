/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.sql.attributes;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.dao.model.sql.AttributeKvCompositeKey;
import org.thingsboard.server.dao.model.sql.AttributeKvEntity;

import java.util.List;
import java.util.UUID;

public interface AttributeKvRepository extends CrudRepository<AttributeKvEntity, AttributeKvCompositeKey> {

    @Query("SELECT a FROM AttributeKvEntity a WHERE a.id.entityType = :entityType " +
            "AND a.id.entityId = :entityId " +
            "AND a.id.attributeType = :attributeType")
    List<AttributeKvEntity> findAllByEntityTypeAndEntityIdAndAttributeType(@Param("entityType") EntityType entityType,
                                                                           @Param("entityId") UUID entityId,
                                                                           @Param("attributeType") String attributeType);

    @Transactional
    @Modifying
    @Query("DELETE FROM AttributeKvEntity a WHERE a.id.entityType = :entityType " +
            "AND a.id.entityId = :entityId " +
            "AND a.id.attributeType = :attributeType " +
            "AND a.id.attributeKey = :attributeKey")
    void delete(@Param("entityType") EntityType entityType,
                @Param("entityId") UUID entityId,
                @Param("attributeType") String attributeType,
                @Param("attributeKey") String attributeKey);

    @Query(value = "SELECT DISTINCT attribute_key FROM attribute_kv WHERE entity_type = 'DEVICE' " +
            "AND entity_id in (SELECT id FROM device WHERE tenant_id = :tenantId and device_profile_id = :deviceProfileId limit 100) ORDER BY attribute_key", nativeQuery = true)
    List<String> findAllKeysByDeviceProfileId(@Param("tenantId") UUID tenantId,
                                              @Param("deviceProfileId") UUID deviceProfileId);

    @Query(value = "SELECT DISTINCT attribute_key FROM attribute_kv WHERE entity_type = 'DEVICE' " +
            "AND entity_id in (SELECT id FROM device WHERE tenant_id = :tenantId limit 100) ORDER BY attribute_key", nativeQuery = true)
    List<String> findAllKeysByTenantId(@Param("tenantId") UUID tenantId);

    @Query(value = "SELECT DISTINCT attribute_key FROM attribute_kv WHERE entity_type = :entityType " +
            "AND entity_id in :entityIds ORDER BY attribute_key", nativeQuery = true)
    List<String> findAllKeysByEntityIds(@Param("entityType") String entityType, @Param("entityIds") List<UUID> entityIds);

    @Query ("SELECT a FROM AttributeKvEntity a WHERE a.id.entityType = :entityType " +
            "AND a.id.entityId in (:entityIds) " +
            "AND a.id.attributeKey = :attributeKey ")
    List<AttributeKvEntity> findAllOneKeyByEntityIdList(@Param("entityType") EntityType entityType,
                                                        @Param("entityIds") List<UUID> entityIds,
                                                        @Param("attributeKey") String key);

    @Query ("SELECT a FROM AttributeKvEntity a WHERE a.id.entityType = :entityType " +
            "AND a.id.entityId = :entityId " +
            "AND a.id.attributeKey = :attributeKey ")
    AttributeKvEntity findOneKeyByEntityId(@Param("entityType") EntityType entityType,
                                                        @Param("entityId") UUID entityId,
                                                        @Param("attributeKey") String key);

    @Transactional
    @Modifying
    @Query(value = "update attribute_kv set bool_v = :value where entity_id = :entityId and attribute_key = 'active' ", nativeQuery = true)
    void updateActiveByEntityId(@Param("entityId") UUID entityId, @Param("value") boolean value);

    /**
     * 根据设备标识以及属性类型查询属性
     * @param entityIds
     * @param attributeType
     * @return
     */
    @Query(value = "SELECT * FROM attribute_kv WHERE entity_id in :entityIds " +
            "AND attribute_key = :attributeKey AND attribute_type = :attributeType ", nativeQuery = true)
    List<AttributeKvEntity> findAllByEntityIds(@Param("entityIds") List<UUID> entityIds,@Param("attributeType") String attributeType,@Param("attributeKey") String attributeKey);



    @Query ("SELECT a FROM AttributeKvEntity a WHERE a.id.attributeType = :attributeType " +
            "AND a.id.entityType = :entityType " +
            "AND a.id.entityId in :entityId " +
            "AND a.id.attributeKey = :attributeKey " )
    List<AttributeKvEntity> findActiveByAttributeTypeAndEntityTypeAndEntityIdsAndAttributeKey(@Param("attributeType") String attributeType,
                                                                                              @Param("entityType") EntityType entityType,
                                                                                              @Param("entityId") List<UUID> entityId,
                                                                                              @Param("attributeKey") String key);
    List<AttributeKvEntity> findAllByIdAttributeKey(String attributeKey);

    List<AttributeKvEntity> findAllByIdEntityId(UUID entityId);
}



