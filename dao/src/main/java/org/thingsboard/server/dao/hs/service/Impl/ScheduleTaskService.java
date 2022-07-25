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
package org.thingsboard.server.dao.hs.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.ThingsBoardThreadFactory;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.hs.entity.bo.DDMsgBO;
import org.thingsboard.server.dao.hs.entity.vo.FactoryDeviceQuery;
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.model.sql.TenantEntity;
import org.thingsboard.server.dao.sql.tenant.TenantRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by ashvayka on 01.05.18.
 */
@Component
@Slf4j
public class ScheduleTaskService {

    private final RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
    private ListeningScheduledExecutorService scheduledExecutor;
    private final ConcurrentMap<UUID, Boolean> factoryStatusMap = new ConcurrentHashMap<>();

    @Autowired
    private ClientService clientService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Value("${hs.dd.urls}")
    private String urls;

    @Value("${hs.dd.enabled}")
    private Boolean DDEnabled;

    @PostConstruct
    public void init() {
        scheduledExecutor = MoreExecutors.listeningDecorator(Executors.newSingleThreadScheduledExecutor(ThingsBoardThreadFactory.forName("device-state-notification-scheduled")));
        scheduledExecutor.scheduleAtFixedRate(this::scheduleCheckFactoryStatus, 30, 180, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void stop() {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdownNow();
        }
    }

    public void rtCheckFactoryStatus(DeviceId deviceId) {
        CompletableFuture.runAsync(() -> Optional.ofNullable(this.clientService.getSimpleDevice(deviceId.getId())).filter(v -> !(v.getAdditionalInfo().has("gateway") && v.getAdditionalInfo().get("gateway").asBoolean())).map(Device::getFactoryId)
                .ifPresent(v -> this.factoryStatusMap.put(v, true)), threadPoolTaskExecutor).join();
    }

    void scheduleCheckFactoryStatus() {
        log.info("starting check factory status ...");
        if (!DDEnabled || StringUtils.isBlank(urls))
            return;
        List<String> urlList = Arrays.stream(urls.split(",")).map(String::trim).collect(Collectors.toList());
        this.clientService.listFactories().stream()
                .map(v -> CompletableFuture.supplyAsync(() -> this.clientService.listSimpleDevicesByQuery(new TenantId(v.getTenantId()), new FactoryDeviceQuery().setFactoryId(v.getId().toString())), threadPoolTaskExecutor)
                        .thenApplyAsync(devices -> {
                            if (!devices.isEmpty()) {
                                var r = devices.stream().map(f -> CompletableFuture.supplyAsync(() -> this.clientService.isDeviceOnline(f.getId().getId()), threadPoolTaskExecutor)).map(CompletableFuture::join)
                                        .allMatch(Boolean.FALSE::equals);
                                if (r) {
                                    if (Boolean.TRUE.equals(factoryStatusMap.getOrDefault(v.getId(), true))) {
                                        factoryStatusMap.put(v.getId(), false);
                                        return DDMsgBO.builder()
                                                .tenantName(this.tenantRepository.findById(v.getTenantId()).map(TenantEntity::getTitle).orElse(""))
                                                .factoryName(v.getName())
                                                .build();
                                    }
                                } else {
                                    factoryStatusMap.put(v.getId(), true);
                                }
                            }
                            return null;
                        }))
                .map(CompletableFuture::join).filter(Objects::nonNull)
                .forEach(v -> CompletableFuture.runAsync(() -> urlList.forEach(k -> {
                    v.setUrl(k);
                    this.sendDDMsg(v);
                }), threadPoolTaskExecutor).join());
        log.info("end check factory status ...");
    }

    /**
     * 发送DD消息
     */
    void sendDDMsg(DDMsgBO ddMsgBO) {
        try {
            var r = Optional.of(restTemplateBuilder.build().exchange(ddMsgBO.getUrl(), HttpMethod.POST, new HttpEntity<>(ddMsgBO.getMsgResult(), getHeaders()), JsonNode.class))
                    .map(ResponseEntity::getBody).map(JsonNode::asText).orElse("null");
            log.info(String.format("发送DD消息：租户[%s] 工厂[%s] 结果：%s", ddMsgBO.getTenantName(), ddMsgBO.getFactoryName(), r));
        } catch (Exception ex) {
            log.error(String.format("发送DD消息：租户[%s] 工厂[%s] 失败：%s", ddMsgBO.getTenantName(), ddMsgBO.getFactoryName(), ex.getMessage()));
        }
    }

    /**
     * 获得header
     */
    HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        return httpHeaders;
    }
}
