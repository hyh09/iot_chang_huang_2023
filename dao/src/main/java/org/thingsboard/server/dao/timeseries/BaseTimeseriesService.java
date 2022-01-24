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
package org.thingsboard.server.dao.timeseries;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.EntityView;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.*;
import org.thingsboard.server.common.data.vo.enums.EfficiencyEnums;
import org.thingsboard.server.dao.entityview.EntityViewService;
import org.thingsboard.server.dao.exception.IncorrectParameterException;
import org.thingsboard.server.dao.hs.service.DeviceDictPropertiesSvc;
import org.thingsboard.server.dao.service.Validator;
import org.thingsboard.server.dao.sql.census.service.StatisticalDataService;
import org.thingsboard.server.dao.sql.energyTime.service.EneryTimeGapService;
import org.thingsboard.server.dao.sql.trendChart.service.EnergyChartService;
import org.thingsboard.server.dao.sql.tskv.svc.EnergyHistoryMinuteSvc;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author Andrew Shvayka
 */
@Service
@Slf4j
public class BaseTimeseriesService implements TimeseriesService  {

    private  Map<String, String> dataInitMap = new ConcurrentHashMap<>();

    private  List<String> globalEneryList = new ArrayList<>();

    private static final int INSERTS_PER_ENTRY = 3;
    private static final int DELETES_PER_ENTRY = INSERTS_PER_ENTRY;
    public static final Function<List<Integer>, Integer> SUM_ALL_INTEGERS = new Function<List<Integer>, Integer>() {
        @Override
        public @Nullable Integer apply(@Nullable List<Integer> input) {
            int result = 0;
            if (input != null) {
                for (Integer tmp : input) {
                    if (tmp != null) {
                        result += tmp;
                    }
                }
            }
            return result;
        }
    };

    @Value("${database.ts_max_intervals}")
    private long maxTsIntervals;

    @Autowired
    private TimeseriesDao timeseriesDao;

    @Autowired
    private TimeseriesLatestDao timeseriesLatestDao;

    @Autowired
    private EntityViewService entityViewService;
    @Autowired private DeviceDictPropertiesSvc deviceDictPropertiesSvc;
    @Autowired private EneryTimeGapService eneryTimeGapService;
    @Autowired private StatisticalDataService statisticalDataService;
    @Autowired private EnergyChartService energyChartService;
    @Autowired private EnergyHistoryMinuteSvc energyHistoryMinuteSvc;

    @Override
    public ListenableFuture<List<TsKvEntry>> findAll(TenantId tenantId, EntityId entityId, List<ReadTsKvQuery> queries) {
        validate(entityId);
        queries.forEach(this::validate);
        if (entityId.getEntityType().equals(EntityType.ENTITY_VIEW)) {
            EntityView entityView = entityViewService.findEntityViewById(tenantId, (EntityViewId) entityId);
            List<String> keys = entityView.getKeys() != null && entityView.getKeys().getTimeseries() != null ?
                    entityView.getKeys().getTimeseries() : Collections.emptyList();
            List<ReadTsKvQuery> filteredQueries =
                    queries.stream()
                            .filter(query -> keys.isEmpty() || keys.contains(query.getKey()))
                            .collect(Collectors.toList());
            return timeseriesDao.findAllAsync(tenantId, entityView.getEntityId(), updateQueriesForEntityView(entityView, filteredQueries));
        }
        return timeseriesDao.findAllAsync(tenantId, entityId, queries);
    }

    @Override
    public ListenableFuture<List<TsKvEntry>> findLatest(TenantId tenantId, EntityId entityId, Collection<String> keys) {
        validate(entityId);
        List<ListenableFuture<TsKvEntry>> futures = Lists.newArrayListWithExpectedSize(keys.size());
        keys.forEach(key -> Validator.validateString(key, "Incorrect key " + key));
        keys.forEach(key -> futures.add(timeseriesLatestDao.findLatest(tenantId, entityId, key)));
        return Futures.allAsList(futures);
    }

    @Override
    public ListenableFuture<List<TsKvEntry>> findAllLatest(TenantId tenantId, EntityId entityId) {
        validate(entityId);
        return timeseriesLatestDao.findAllLatest(tenantId, entityId);
    }

    @Override
    public List<String> findAllKeysByDeviceProfileId(TenantId tenantId, DeviceProfileId deviceProfileId) {
        return timeseriesLatestDao.findAllKeysByDeviceProfileId(tenantId, deviceProfileId);
    }

    @Override
    public List<String> findAllKeysByEntityIds(TenantId tenantId, List<EntityId> entityIds) {
        return timeseriesLatestDao.findAllKeysByEntityIds(tenantId, entityIds);
    }

    @Override
    public void cleanup(long systemTtl) {
        timeseriesDao.cleanup(systemTtl);
    }

    @Override
    public ListenableFuture<Integer> save(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry) {
        validate(entityId);
        if (tsKvEntry == null) {
            throw new IncorrectParameterException("Key value entry can't be null");
        }
        List<ListenableFuture<Integer>> futures = Lists.newArrayListWithExpectedSize(INSERTS_PER_ENTRY);
        saveAndRegisterFutures(tenantId, futures, entityId, tsKvEntry, 0L);
        return Futures.transform(Futures.allAsList(futures), SUM_ALL_INTEGERS, MoreExecutors.directExecutor());
    }

    @Override
    public ListenableFuture<Integer> save(TenantId tenantId, EntityId entityId, List<TsKvEntry> tsKvEntries, long ttl) {
        List<ListenableFuture<Integer>> futures = Lists.newArrayListWithExpectedSize(tsKvEntries.size() * INSERTS_PER_ENTRY);
        for (TsKvEntry tsKvEntry : tsKvEntries) {
            if (tsKvEntry == null) {
                throw new IncorrectParameterException("Key value entry can't be null");
            }
            saveAndRegisterFutures(tenantId, futures, entityId, tsKvEntry, ttl);
        }
        return Futures.transform(Futures.allAsList(futures), SUM_ALL_INTEGERS, MoreExecutors.directExecutor());
    }

    @Override
    public ListenableFuture<List<Void>> saveLatest(TenantId tenantId, EntityId entityId, List<TsKvEntry> tsKvEntries) {
        List<ListenableFuture<Void>> futures = Lists.newArrayListWithExpectedSize(tsKvEntries.size());
        for (TsKvEntry tsKvEntry : tsKvEntries) {
            if (tsKvEntry == null) {
                throw new IncorrectParameterException("Key value entry can't be null");
            }
            futures.add(timeseriesLatestDao.saveLatest(tenantId, entityId, tsKvEntry));
        }
        return Futures.allAsList(futures);
    }

    private void saveAndRegisterFutures(TenantId tenantId, List<ListenableFuture<Integer>> futures, EntityId entityId, TsKvEntry tsKvEntry, long ttl) {
        if (entityId.getEntityType().equals(EntityType.ENTITY_VIEW)) {
            throw new IncorrectParameterException("Telemetry data can't be stored for entity view. Read only");
        }
//        log.info("tsKvEntry打印当前的数据:tsKvEntry{}",tsKvEntry);
//        log.info("tsKvEntry打印当前的数据:EntityId{}",entityId);
        if(CollectionUtils.isEmpty(globalEneryList)) {
            List<String> keys1 = deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.ENERGY_002.getgName());
            globalEneryList.addAll(keys1);
        }
        if(CollectionUtils.isEmpty(dataInitMap))
        {
            Map<String,String>   map=  deviceDictPropertiesSvc.getUnit();
            dataInitMap=map;
        }

////        log.info("打印能耗的saveAndRegisterFutures.keys1{}",globalEneryList);
//       Long  count =  globalEneryList.stream().filter(str->str.equals(tsKvEntry.getKey())).count();
//        if(count>0) {
//            ListenableFuture<TsKvEntry> tsKvEntryListenableFuture = timeseriesLatestDao.findLatest(tenantId, entityId, tsKvEntry.getKey());
////            log.info("tsKvEntry打印当前的数据:tsKvEntryListenableFuture{}", tsKvEntryListenableFuture);
//            try {
//                TsKvEntry tsKvEntry1 =   tsKvEntryListenableFuture.get();
////                log.info("tsKvEntry打印当前的数据:tsKvEntryListenableFuture.tsKvEntry1{}", tsKvEntryListenableFuture);
//                long  t1=  tsKvEntry.getTs();
//                long  t2=  tsKvEntry1.getTs();//要避免夸天的相减
//              if(CommonUtils.isItToday(t2)) {
//                  long t3 = t1 - t2;
////                  log.info("---tsKvEntry打印当前的数据:tsKvEntryListenableFuture.tsKvEntry1打印的数据-->{}", (t1 - t2));
//                  if (t3 > ENERGY_TIME_GAP) {
//                      EneryTimeGapEntity eneryTimeGapEntity = new EneryTimeGapEntity();
//                      eneryTimeGapEntity.setEntityId(entityId.getId());
//                      eneryTimeGapEntity.setTenantId(tenantId.getId());
//                      eneryTimeGapEntity.setKeyName(tsKvEntry.getKey());
//                      eneryTimeGapEntity.setValue(tsKvEntry.getValue().toString());
//                      eneryTimeGapEntity.setTs(tsKvEntry.getTs());
//                      eneryTimeGapEntity.setTimeGap(t3);
//                      eneryTimeGapService.save(eneryTimeGapEntity);
//                  }
//              }
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//
//
//        }

         String  title =    dataInitMap.get(tsKvEntry.getKey());
        if(StringUtils.isNotBlank(title))
        {
//            log.info("打印当前的数据打印标题{}",title);
            statisticalDataService.todayDataProcessing( entityId,tsKvEntry,title);
            energyHistoryMinuteSvc.saveByMinute( entityId,tsKvEntry,title);

        }
        Long  count =  globalEneryList.stream().filter(str->str.equals(tsKvEntry.getKey())).count();
        if(count>0) {
            energyChartService.todayDataProcessing( entityId,tsKvEntry,title);
        }

        futures.add(timeseriesDao.savePartition(tenantId, entityId, tsKvEntry.getTs(), tsKvEntry.getKey()));
        futures.add(Futures.transform(timeseriesLatestDao.saveLatest(tenantId, entityId, tsKvEntry), v -> 0, MoreExecutors.directExecutor()));
        futures.add(timeseriesDao.save(tenantId, entityId, tsKvEntry, ttl));
    }

    private List<ReadTsKvQuery> updateQueriesForEntityView(EntityView entityView, List<ReadTsKvQuery> queries) {
        return queries.stream().map(query -> {
            long startTs;
            if (entityView.getStartTimeMs() != 0 && entityView.getStartTimeMs() > query.getStartTs()) {
                startTs = entityView.getStartTimeMs();
            } else {
                startTs = query.getStartTs();
            }

            long endTs;
            if (entityView.getEndTimeMs() != 0 && entityView.getEndTimeMs() < query.getEndTs()) {
                endTs = entityView.getEndTimeMs();
            } else {
                endTs = query.getEndTs();
            }
            return new BaseReadTsKvQuery(query.getKey(), startTs, endTs, query.getInterval(), query.getLimit(), query.getAggregation(), query.getOrder());
        }).collect(Collectors.toList());
    }

    @Override
    public ListenableFuture<List<Void>> remove(TenantId tenantId, EntityId entityId, List<DeleteTsKvQuery> deleteTsKvQueries) {
        validate(entityId);
        deleteTsKvQueries.forEach(BaseTimeseriesService::validate);
        List<ListenableFuture<Void>> futures = Lists.newArrayListWithExpectedSize(deleteTsKvQueries.size() * DELETES_PER_ENTRY);
        for (DeleteTsKvQuery tsKvQuery : deleteTsKvQueries) {
            deleteAndRegisterFutures(tenantId, futures, entityId, tsKvQuery);
        }
        return Futures.allAsList(futures);
    }

    @Override
    public ListenableFuture<List<Void>> removeLatest(TenantId tenantId, EntityId entityId, Collection<String> keys) {
        validate(entityId);
        List<ListenableFuture<Void>> futures = Lists.newArrayListWithExpectedSize(keys.size());
        for (String key : keys) {
            DeleteTsKvQuery query = new BaseDeleteTsKvQuery(key, 0, System.currentTimeMillis(), false);
            futures.add(timeseriesLatestDao.removeLatest(tenantId, entityId, query));
        }
        return Futures.allAsList(futures);
    }

    @Override
    public ListenableFuture<Collection<String>> removeAllLatest(TenantId tenantId, EntityId entityId) {
        validate(entityId);
        return Futures.transformAsync(this.findAllLatest(tenantId, entityId), latest -> {
            if (!latest.isEmpty()) {
                Collection<String> keys = latest.stream().map(TsKvEntry::getKey).collect(Collectors.toList());
                return Futures.transform(this.removeLatest(tenantId, entityId, keys), res -> keys, MoreExecutors.directExecutor());
            } else {
                return Futures.immediateFuture(Collections.emptyList());
            }
        }, MoreExecutors.directExecutor());
    }

    private void deleteAndRegisterFutures(TenantId tenantId, List<ListenableFuture<Void>> futures, EntityId entityId, DeleteTsKvQuery query) {
        futures.add(timeseriesDao.remove(tenantId, entityId, query));
        futures.add(timeseriesLatestDao.removeLatest(tenantId, entityId, query));
        futures.add(timeseriesDao.removePartition(tenantId, entityId, query));
    }

    private static void validate(EntityId entityId) {
        Validator.validateEntityId(entityId, "Incorrect entityId " + entityId);
    }

    private void validate(ReadTsKvQuery query) {
        if (query == null) {
            throw new IncorrectParameterException("ReadTsKvQuery can't be null");
        } else if (isBlank(query.getKey())) {
            throw new IncorrectParameterException("Incorrect ReadTsKvQuery. Key can't be empty");
        } else if (query.getAggregation() == null) {
            throw new IncorrectParameterException("Incorrect ReadTsKvQuery. Aggregation can't be empty");
        }
        if (!Aggregation.NONE.equals(query.getAggregation())) {
            long step = Math.max(query.getInterval(), 1000);
            long intervalCounts = (query.getEndTs() - query.getStartTs()) / step;
            if (intervalCounts > maxTsIntervals || intervalCounts < 0) {
                throw new IncorrectParameterException("Incorrect TsKvQuery. Number of intervals is to high - " + intervalCounts + ". " +
                        "Please increase 'interval' parameter for your query or reduce the time range of the query.");
            }
        }
    }

    private static void validate(DeleteTsKvQuery query) {
        if (query == null) {
            throw new IncorrectParameterException("DeleteTsKvQuery can't be null");
        } else if (isBlank(query.getKey())) {
            throw new IncorrectParameterException("Incorrect DeleteTsKvQuery. Key can't be empty");
        }
    }
}
