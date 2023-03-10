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
 * ??????????????????:
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
    @Autowired private  AttributesDao attributesDao;
    @Autowired private FactoryService factoryService;

    private final RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

    private Map<UUID,String> mapFactory = new ConcurrentHashMap();

    private  String ACTIVE="active";
    private  String ADDITIONAL_INFO_FILED_NAME="gateway";

    @Override
    @Async("threadPoolTaskExecutor_1")
    public void send(UUID entityId, AttributeKvEntry entry) {
        try {


            if(ACTIVE.equals(entry.getKey()) && entry.getBooleanValue() != null){
//                if(entry.getBooleanValue().get()) {
//                    return;
//                }
                Device device = deviceDao.findById(entityId);
                if (device == null) {
                    log.error("??????????????????????????????,??????{}", entityId);
                    return;
                }

                UUID factoryId = device.getFactoryId();
                if (factoryId == null) {
                    log.error("???????????????????????????????????????id,??????{}", entityId);
                    return;
                }
                Factory factory = factoryService.findById(factoryId);


                List<UUID> deviceIdList = energyLargeScreenReposutory.getDeviceIdByVo(new EnergyHourVo(factoryId, null, null, null));
                if (CollectionUtils.isEmpty(deviceIdList)) {
                    log.error("???????????????id?????????????????????,??????{}", factoryId);
                    return;
                }
                Boolean flg = factoryIsOnline(deviceIdList,factoryId);

                if (!flg) {
                    toSendMess(new ParamVo(new ParamTextVo(factory.getName())));
                }
            }



        }catch (Exception  e)
        {
            log.error("????????????????????????]",e.getMessage());
        }

    }


    /**
     * ???????????????????????????
     * @param deviceIds
     * @return
     */
    private Boolean factoryIsOnline( List<UUID> deviceIds, UUID factoryId ) {
        List<AttributeKvEntity> activeByDeviceIds = attributesDao.findActiveByDeviceIds(deviceIds);
        if (CollectionUtils.isNotEmpty(activeByDeviceIds)) {
            int online = (int) activeByDeviceIds.stream().filter(AttributeKvEntity::getBooleanValue).count();
            if(online>0)
            {
                mapFactory.put(factoryId,"??????");
                return true;
            }
        }
        if(StringUtils.isNotEmpty(mapFactory.get(factoryId))  && mapFactory.get(factoryId).equals("???"))
        {
            return true;
        }
        mapFactory.put(factoryId,"???");
        return false;
    }


    public void toSendMess(ParamVo vo) throws ThingsboardException {
        List<DingdingVo> dingdingVoList = ddingConfigServer.queryDingdingConfig();
        if (dingdingVoList == null) {
            log.error("??????????????????????????????");
            return;
        }
        dingdingVoList.stream().forEach(m1->{
            log.debug("[??????]????????????????????????:{}", JsonUtils.objectToJson(vo));
            ResultDingDingVo dingDingVo = restTemplateBuilder.build().postForObject(m1.getUrl(), new HttpEntity<>(vo, getHeaders()), ResultDingDingVo.class);
            log.debug("[??????]????????????????????????:{}", JsonUtils.objectToJson(dingDingVo));
        });
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
     * ??????header
     */
    HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        return httpHeaders;
    }



}

