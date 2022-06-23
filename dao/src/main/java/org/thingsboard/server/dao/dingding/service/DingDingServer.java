package org.thingsboard.server.dao.dingding.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.energyboard.EnergyBoard;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.common.data.vo.bodrd.energy.Input.EnergyHourVo;
import org.thingsboard.server.dao.attributes.AttributesDao;
import org.thingsboard.server.dao.board.repository.EnergyLargeScreenReposutory;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.dingding.ben.DingdingVo;
import org.thingsboard.server.dao.dingding.ben.ResultDingDingVo;
import org.thingsboard.server.dao.dingding.ben.input.ParamTextVo;
import org.thingsboard.server.dao.dingding.ben.input.ParamVo;
import org.thingsboard.server.dao.dingding.config.DdingConfigServer;
import org.thingsboard.server.dao.dingding.service.svc.DdingDingSendMssSvc;
import org.thingsboard.server.dao.factory.FactoryService;
import org.thingsboard.server.dao.model.sql.AttributeKvEntity;
import org.thingsboard.server.dao.util.JsonUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project Name: thingsboard
 * File Name: DingDingServer
 * Package Name: org.thingsboard.server.dao.dingding.service
 * Date: 2022/6/21 14:31
 * author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Slf4j
@Service
public class DingDingServer implements DdingDingSendMssSvc {

    @Autowired
    private DdingConfigServer ddingConfigServer;
    @Autowired
    private DeviceDao deviceDao;
    @Autowired
    private EnergyLargeScreenReposutory energyLargeScreenReposutory;
    @Autowired
    private AttributesDao attributesDao;
    @Autowired
    private FactoryService factoryService;

    private final RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
    private Map<UUID,String> mapFactory = new ConcurrentHashMap();

    private String ACTIVE = "active";

    @Override
    @Async("threadPoolTaskExecutor_1")
    public void send(UUID entityId, AttributeKvEntry entry) {
        try {

            if (ACTIVE.equals(entry.getKey()) && entry.getBooleanValue() != null && !entry.getBooleanValue().get()) {
                Device device = deviceDao.findById(entityId);
                if (device == null) {
                    log.error("钉钉发送查询不到设备,入参{}", entityId);
                    return;
                }
                UUID factoryId = device.getFactoryId();
                if (factoryId == null) {
                    log.error("钉钉发送查询不到设备该工厂id,入参{}", entityId);
                    return;
                }
                Factory factory = factoryService.findById(factoryId);
                List<UUID> deviceIdList = energyLargeScreenReposutory.getDeviceIdByVo(new EnergyHourVo(factoryId, null, null, null));
                if (CollectionUtils.isEmpty(deviceIdList)) {
                    log.error("设备该工厂id查询不到设备了,入参{}", factoryId);
                    return;
                }
                Boolean flg = factoryIsOnline(deviceIdList,factoryId);
                if (!flg) {
                    toSendMess(new ParamVo(new ParamTextVo(factory.getName())));
                }
            }
        } catch (Exception e) {
            log.error("【钉钉的异常信息]", e.getMessage());
        }

    }


    /**
     * 判断该工厂是否在线
     *
     * @param deviceIds
     * @return
     */
    private Boolean factoryIsOnline(List<UUID> deviceIds,UUID factoryId ) {
        List<AttributeKvEntity> activeByDeviceIds = attributesDao.findActiveByDeviceIds(deviceIds);
        if (CollectionUtils.isNotEmpty(activeByDeviceIds)) {
            int online = (int) activeByDeviceIds.stream().filter(AttributeKvEntity::getBooleanValue).count();
            if (online > 0) {
                mapFactory.put(factoryId,"不发");
                return true;
            }
        }
        if(StringUtils.isNotEmpty(mapFactory.get(factoryId))  && mapFactory.get(factoryId).equals("发"))
        {
            return true;
        }
        mapFactory.put(factoryId,"发");

        return false;
    }


    public ResultDingDingVo toSendMess(ParamVo vo) throws ThingsboardException {
        DingdingVo dingdingVo = ddingConfigServer.queryDingdingConfig();
        if (dingdingVo == null) {
            log.error("配置信息钉钉信息为空");
            return null;
        }
        log.debug("[钉钉]方法执行打印入参:{}", JsonUtils.objectToJson(vo));
        ResultDingDingVo dingDingVo = restTemplateBuilder.build().postForObject(dingdingVo.getUrl(), new HttpEntity<>(vo, getHeaders()), ResultDingDingVo.class);
        log.debug("[钉钉]方法执行打印出参:{}", JsonUtils.objectToJson(dingDingVo));
        return dingDingVo;
    }


    public String getUrl(DingdingVo dingdingVo) throws Exception {
        String secret = dingdingVo.getSignName();
        if (StringUtils.isNotEmpty(secret)) {
            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
            return dingdingVo.getUrl() + "&timestamp=" + timestamp + "&sign=" + sign;
        }
        return dingdingVo.getUrl();
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
