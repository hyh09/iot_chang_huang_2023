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
package org.thingsboard.server.dao.sqlts.latest;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.hs.entity.dto.LatestKvDTO;
import org.thingsboard.server.dao.model.sqlts.latest.TsKvLatestCompositeKey;
import org.thingsboard.server.dao.model.sqlts.latest.TsKvLatestEntity;

import java.util.List;
import java.util.UUID;

public interface TsKvLatestRepository extends CrudRepository<TsKvLatestEntity, TsKvLatestCompositeKey> {

    @Query(value = "SELECT DISTINCT ts_kv_dictionary.key AS strKey FROM ts_kv_latest " +
            "INNER JOIN ts_kv_dictionary ON ts_kv_latest.key = ts_kv_dictionary.key_id " +
            "WHERE ts_kv_latest.entity_id IN (SELECT id FROM device WHERE device_profile_id = :device_profile_id AND tenant_id = :tenant_id limit 100) ORDER BY ts_kv_dictionary.key", nativeQuery = true)
    List<String> getKeysByDeviceProfileId(@Param("tenant_id") UUID tenantId, @Param("device_profile_id") UUID deviceProfileId);

    @Query(value = "SELECT DISTINCT ts_kv_dictionary.key AS strKey FROM ts_kv_latest " +
            "INNER JOIN ts_kv_dictionary ON ts_kv_latest.key = ts_kv_dictionary.key_id " +
            "WHERE ts_kv_latest.entity_id IN (SELECT id FROM device WHERE tenant_id = :tenant_id limit 100) ORDER BY ts_kv_dictionary.key", nativeQuery = true)
    List<String> getKeysByTenantId(@Param("tenant_id") UUID tenantId);

    @Query(value = "SELECT DISTINCT ts_kv_dictionary.key AS strKey FROM ts_kv_latest " +
            "INNER JOIN ts_kv_dictionary ON ts_kv_latest.key = ts_kv_dictionary.key_id " +
            "WHERE ts_kv_latest.entity_id IN :entityIds ORDER BY ts_kv_dictionary.key", nativeQuery = true)
    List<String> findAllKeysByEntityIds(@Param("entityIds") List<UUID> entityIds);

    @Query(value = "select distinct ts_kv_latest.key from ts_kv_latest where ts_kv_latest.entity_id = :entityId", nativeQuery = true)
    List<Integer> findAllKeyIdsByEntityId(@Param("entityId") UUID entityId);

    @Query(value = "select t.key from ts_kv_latest t where t.entity_id = :entityId order by t.ts desc limit 1 ", nativeQuery = true)
    Integer findLatestKey(@Param("entityId") UUID entityId);

    @Query("select new TsKvLatestEntity(t.entityId, max(t.ts)) from TsKvLatestEntity t where t.entityId in :entityIds group by t.entityId")
    List<TsKvLatestEntity> findAllLatestByEntityIds(@Param("entityIds") List<UUID> entityIds);
}
