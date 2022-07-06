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
package org.thingsboard.server.dao.sql.device;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.thingsboard.server.common.data.DeviceTransportType;
import org.thingsboard.server.common.data.vo.device.DeviceDataSvc;
import org.thingsboard.server.common.data.vo.device.DeviceDataVo;
import org.thingsboard.server.common.data.vo.device.DeviceRatingValueVo;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sql.DeviceInfoEntity;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Valerii Sosliuk on 5/6/2017.
 */
public interface DeviceRepository extends PagingAndSortingRepository<DeviceEntity, UUID>, JpaSpecificationExecutor<DeviceEntity> {

    List<DeviceEntity> findAllByTenantIdAndDeviceProfileId(UUID tenantId, UUID deviceProfileId);

    List<DeviceEntity> findAllByTenantIdAndDictDeviceId(UUID tenantId, UUID dictDeviceId);

    List<DeviceEntity> findAllByTenantIdAndDictDeviceIdIn(UUID tenantId, Set<UUID> dictDeviceIds);

    @Async
    @Query("select new DeviceEntity(t.id, t.name, t.factoryId, t.workshopId, t.productionLineId, t.additionalInfo) from DeviceEntity t where " +
            "t.deviceProfileId = :deviceProfileId ")
    CompletableFuture<List<DeviceEntity>> findAllByDeviceProfileId(@Param("deviceProfileId") UUID deviceProfileId);

    @Async
    @Query("select new DeviceEntity(t.id, t.name, t.factoryId, t.workshopId, t.productionLineId, t.additionalInfo) from DeviceEntity t where " +
            "t.tenantId = :tenantId " +
            "order by t.createdTime desc")
    CompletableFuture<List<DeviceEntity>> findAllByTenantId(@Param("tenantId") UUID tenantId);

    @Async
    @Query("select new DeviceEntity(t.id, t.name, t.factoryId, t.workshopId, t.productionLineId, t.additionalInfo) from DeviceEntity t where " +
            "t.tenantId = :tenantId and " +
            "t.factoryId = :factoryId " +
            "order by t.createdTime desc")
    CompletableFuture<List<DeviceEntity>> findAllByTenantIdAndFactoryId(@Param("tenantId") UUID tenantId, @Param("factoryId") UUID factoryId);

    @Async
    @Query("select new DeviceEntity(t.id, t.name, t.factoryId, t.workshopId, t.productionLineId, t.additionalInfo, t.sort) from DeviceEntity t where " +
            "t.tenantId = :tenantId " +
            "order by t.createdTime desc")
    CompletableFuture<List<DeviceEntity>> findAllIdAndNameByTenantIdOrderByCreatedTimeDesc(@Param("tenantId") UUID tenantId);

    @Query("select new DeviceEntity(t.id, t.name, t.factoryId, t.workshopId, t.productionLineId, t.additionalInfo) from DeviceEntity t where " +
            "t.id = :id ")
    DeviceEntity findSimpleById(@Param("id") UUID id);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DeviceInfoEntity(d, c.title, c.additionalInfo, p.name) " +
            "FROM DeviceEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "LEFT JOIN DeviceProfileEntity p on p.id = d.deviceProfileId " +
            "WHERE d.id = :deviceId")
    DeviceInfoEntity findDeviceInfoById(@Param("deviceId") UUID deviceId);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.customerId = :customerId " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT(:searchText, '%'))")
    Page<DeviceEntity> findByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                   @Param("customerId") UUID customerId,
                                                   @Param("searchText") String searchText,
                                                   Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :profileId " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT(:searchText, '%'))")
    Page<DeviceEntity> findByTenantIdAndProfileId(@Param("tenantId") UUID tenantId,
                                                  @Param("profileId") UUID profileId,
                                                  @Param("searchText") String searchText,
                                                  Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DeviceInfoEntity(d, c.title, c.additionalInfo, p.name) " +
            "FROM DeviceEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "LEFT JOIN DeviceProfileEntity p on p.id = d.deviceProfileId " +
            "WHERE d.tenantId = :tenantId " +
            "AND d.customerId = :customerId " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT(:searchText, '%'))")
    Page<DeviceInfoEntity> findDeviceInfosByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                                  @Param("customerId") UUID customerId,
                                                                  @Param("searchText") String searchText,
                                                                  Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId")
    Page<DeviceEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                      Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<DeviceEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                      @Param("textSearch") String textSearch,
                                      Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DeviceInfoEntity(d, c.title, c.additionalInfo, p.name) " +
            "FROM DeviceEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "LEFT JOIN DeviceProfileEntity p on p.id = d.deviceProfileId " +
            "WHERE d.tenantId = :tenantId " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<DeviceInfoEntity> findDeviceInfosByTenantId(@Param("tenantId") UUID tenantId,
                                                     @Param("textSearch") String textSearch,
                                                     Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.type = :type " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<DeviceEntity> findByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                             @Param("type") String type,
                                             @Param("textSearch") String textSearch,
                                             Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :deviceProfileId " +
            "AND d.firmwareId = null " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<DeviceEntity> findByTenantIdAndTypeAndFirmwareIdIsNull(@Param("tenantId") UUID tenantId,
                                                                @Param("deviceProfileId") UUID deviceProfileId,
                                                                @Param("textSearch") String textSearch,
                                                                Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :deviceProfileId " +
            "AND d.softwareId = null " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<DeviceEntity> findByTenantIdAndTypeAndSoftwareIdIsNull(@Param("tenantId") UUID tenantId,
                                                                @Param("deviceProfileId") UUID deviceProfileId,
                                                                @Param("textSearch") String textSearch,
                                                                Pageable pageable);

    @Query("SELECT count(*) FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :deviceProfileId " +
            "AND d.firmwareId = null")
    Long countByTenantIdAndDeviceProfileIdAndFirmwareIdIsNull(@Param("tenantId") UUID tenantId,
                                                              @Param("deviceProfileId") UUID deviceProfileId);

    @Query("SELECT count(*) FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :deviceProfileId " +
            "AND d.softwareId = null")
    Long countByTenantIdAndDeviceProfileIdAndSoftwareIdIsNull(@Param("tenantId") UUID tenantId,
                                                              @Param("deviceProfileId") UUID deviceProfileId);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DeviceInfoEntity(d, c.title, c.additionalInfo, p.name) " +
            "FROM DeviceEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "LEFT JOIN DeviceProfileEntity p on p.id = d.deviceProfileId " +
            "WHERE d.tenantId = :tenantId " +
            "AND d.type = :type " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<DeviceInfoEntity> findDeviceInfosByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                                            @Param("type") String type,
                                                            @Param("textSearch") String textSearch,
                                                            Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DeviceInfoEntity(d, c.title, c.additionalInfo, p.name) " +
            "FROM DeviceEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "LEFT JOIN DeviceProfileEntity p on p.id = d.deviceProfileId " +
            "WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :deviceProfileId " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<DeviceInfoEntity> findDeviceInfosByTenantIdAndDeviceProfileId(@Param("tenantId") UUID tenantId,
                                                                       @Param("deviceProfileId") UUID deviceProfileId,
                                                                       @Param("textSearch") String textSearch,
                                                                       Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.customerId = :customerId " +
            "AND d.type = :type " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<DeviceEntity> findByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                          @Param("customerId") UUID customerId,
                                                          @Param("type") String type,
                                                          @Param("textSearch") String textSearch,
                                                          Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DeviceInfoEntity(d, c.title, c.additionalInfo, p.name) " +
            "FROM DeviceEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "LEFT JOIN DeviceProfileEntity p on p.id = d.deviceProfileId " +
            "WHERE d.tenantId = :tenantId " +
            "AND d.customerId = :customerId " +
            "AND d.type = :type " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<DeviceInfoEntity> findDeviceInfosByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                                         @Param("customerId") UUID customerId,
                                                                         @Param("type") String type,
                                                                         @Param("textSearch") String textSearch,
                                                                         Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DeviceInfoEntity(d, c.title, c.additionalInfo, p.name) " +
            "FROM DeviceEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "LEFT JOIN DeviceProfileEntity p on p.id = d.deviceProfileId " +
            "WHERE d.tenantId = :tenantId " +
            "AND d.customerId = :customerId " +
            "AND d.deviceProfileId = :deviceProfileId " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<DeviceInfoEntity> findDeviceInfosByTenantIdAndCustomerIdAndDeviceProfileId(@Param("tenantId") UUID tenantId,
                                                                                    @Param("customerId") UUID customerId,
                                                                                    @Param("deviceProfileId") UUID deviceProfileId,
                                                                                    @Param("textSearch") String textSearch,
                                                                                    Pageable pageable);

    @Query("SELECT DISTINCT d.type FROM DeviceEntity d WHERE d.tenantId = :tenantId")
    List<String> findTenantDeviceTypes(@Param("tenantId") UUID tenantId);

    DeviceEntity findByTenantIdAndName(UUID tenantId, String name);

    List<DeviceEntity> findDevicesByTenantIdAndCustomerIdAndIdIn(UUID tenantId, UUID customerId, List<UUID> deviceIds);

    List<DeviceEntity> findDevicesByTenantIdAndIdIn(UUID tenantId, List<UUID> deviceIds);

    DeviceEntity findByTenantIdAndId(UUID tenantId, UUID id);

    Long countByDeviceProfileId(UUID deviceProfileId);

    @Query("SELECT d FROM DeviceEntity d, RelationEntity re WHERE d.tenantId = :tenantId " +
            "AND d.id = re.toId AND re.toType = 'DEVICE' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT(:searchText, '%'))")
    Page<DeviceEntity> findByTenantIdAndEdgeId(@Param("tenantId") UUID tenantId,
                                               @Param("edgeId") UUID edgeId,
                                               @Param("searchText") String searchText,
                                               Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d, RelationEntity re WHERE d.tenantId = :tenantId " +
            "AND d.id = re.toId AND re.toType = 'DEVICE' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND d.type = :type " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT(:searchText, '%'))")
    Page<DeviceEntity> findByTenantIdAndEdgeIdAndType(@Param("tenantId") UUID tenantId,
                                                      @Param("edgeId") UUID edgeId,
                                                      @Param("type") String type,
                                                      @Param("searchText") String searchText,
                                                      Pageable pageable);

    /**
     * Count devices by tenantId.
     * Custom query applied because default QueryDSL produces slow count(id).
     * <p>
     * There is two way to count devices.
     * OPTIMAL: count(*)
     *   - returns _row_count_ and use index-only scan (super fast).
     * SLOW: count(id)
     *   - returns _NON_NULL_id_count and performs table scan to verify isNull for each id in filtered rows.
     * */
    @Query("SELECT count(*) FROM DeviceEntity d WHERE d.tenantId = :tenantId")
    Long countByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT d.id FROM DeviceEntity d " +
            "INNER JOIN DeviceProfileEntity p ON d.deviceProfileId = p.id " +
            "WHERE p.transportType = :transportType")
    Page<UUID> findIdsByDeviceProfileTransportType(@Param("transportType") DeviceTransportType transportType, Pageable pageable);

    /**
     * 批量查询设备属性
     */
    @Query("SELECT d FROM DeviceEntity d WHERE d.id  in (:ids)")
    List<DeviceEntity> queryAllByIds(@Param("ids") List<UUID> ids);


    @Query(value = "select new org.thingsboard.server.common.data.vo.device.DeviceDataVo(t.id,t.name,t.code,f1.id,f1.name,t.workshopId,w1.name,t.productionLineId,p1.name,t.picture) " +
            "from DeviceEntity  t LEFT  JOIN  FactoryEntity f1  on  t.factoryId = f1.id" +
            " LEFT JOIN  WorkshopEntity  w1  ON  w1.id = t.workshopId     LEFT JOIN ProductionLineEntity  p1  ON  p1.id = t.productionLineId  " +
            "  where  t.factoryId=?1 and t.name like  %?2%    ")
    Page<DeviceDataVo> queryAllByNameLike(UUID factoryId, String Name, Pageable pageable);


    @Query(nativeQuery = true, value = "select cast(t.id as VARCHAR ), t.name,t.code, cast(f1.id as VARCHAR ) as factoryId, f1.name  as factoryName," +
            " cast(t.workshop_id as VARCHAR )  as workshopId , w1.name as workshopName," +
            " cast(t.production_line_id as VARCHAR )   as productionLineId ,p1.name as productionLineName, t.picture " +
            "from device  t LEFT  JOIN  hs_factory f1  on  t.factory_id = f1.id" +
            " LEFT JOIN  hs_workshop  w1  ON  w1.id = t.workshop_id     LEFT JOIN hs_production_line  p1  ON  p1.id = t.production_line_id  " +
            "  where  t.factory_id=?1 and t.name like  %?2%  and  position('\"gateway\":true' in t.additional_info)=0")
    Page<DeviceDataSvc> queryAllByNameLikeNativeQuery(UUID factoryId, String Name, Pageable pageable);


    @Query(value = "select new org.thingsboard.server.common.data.vo.device.DeviceRatingValueVo(d.id,d2.content)  from DeviceEntity d Left JOIN  DictDeviceEntity d1 ON  d.dictDeviceId = d1.id " +
            "left  join  DictDeviceStandardPropertyEntity d2 ON d1.id = d2.dictDeviceId" +
            " where  d.id in (:ids) and d2.name = :name ")
    List<DeviceRatingValueVo> queryDeviceIdAndValue(@Param("ids") List<UUID> ids, @Param("name") String name);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update DeviceEntity d set  d.deviceFlg= :deviceFlg   where    d.id =:id")
    void updateFlgById(@Param("deviceFlg") Boolean deviceFlg, @Param("id") UUID id);


    @Query(value = "select d  from DeviceEntity d  where d.tenantId = :tenantId and d.name =:name ")
    List<DeviceEntity> queryAllByTenantIdAndName(@Param("tenantId") UUID tenantId, @Param("name") String name);

    @Query(nativeQuery = true, value = "select * from device d  where d.tenant_id = ?1 and position('\"gateway\":true' in d.additional_info)=0")
    List<DeviceEntity> findDeviceFilterGatewayByTenantId(UUID tenantId);


    long countAllByDictDeviceIdAndTenantId(UUID dictDeviceId, UUID tenantId);

    /**
     * 查询工厂下网关
     * @param factoryId
     * @return
     */
    @Query(nativeQuery = true, value = "select * from device d  where d.factory_id = ?1 and position('\"gateway\":true' in d.additional_info)!=0")
    List<DeviceEntity> findGatewayByFactoryId(UUID factoryId);


    /**
     * 查询所有的设备
     * @return
     */
    @Query(nativeQuery = true, value = "select * from device d  where   ( position('\"gateway\":true' in d1.additional_info)=0  or  d1.additional_info is null )  ")
    List<DeviceEntity> findAllBy();


}
