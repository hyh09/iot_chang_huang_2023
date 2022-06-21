package org.thingsboard.server.dao.dingding.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.AdminSettings;
import org.thingsboard.server.common.data.enums.AdminSettingsKeyEmuns;
import org.thingsboard.server.dao.dingding.ben.DingdingVo;
import org.thingsboard.server.dao.settings.AdminSettingsService;
import org.thingsboard.server.dao.token.TokenMangerServiceRedis;
import org.thingsboard.server.dao.util.JsonUtils;

/**
 * Project Name: thingsboard
 * File Name: DdingConfigServer
 * Package Name: org.thingsboard.server.dao.dingding.config
 * Date: 2022/6/21 14:19
 * author: wb04
 * 业务中文描述: 钉钉配置信息
 * Copyright (c) 2022,All Rights Reserved.
 */
@Service
@Slf4j
public class DdingConfigServer {
    @Autowired
    private AdminSettingsService adminSettingsService;
//    @Autowired
//    private TokenMangerServiceRedis tokenMangerServiceRedis;


   public  DingdingVo queryDingdingConfig()
    {
        try {
//            String json = (String) tokenMangerServiceRedis.get(AdminSettingsKeyEmuns.dingding_webhook.name());
//            if (StringUtils.isNotEmpty(json)) {
//                return JsonUtils.jsonToPojo(json, DingdingVo.class);
//            }
            AdminSettings adminSettings = adminSettingsService.findAdminSettingsByKey(null, AdminSettingsKeyEmuns.dingding_webhook.name());
            if (adminSettings == null) {
                return null;
            }
            DingdingVo dingdingVo = JsonUtils.beanToBean(adminSettings.getJsonValue(), DingdingVo.class);
//            tokenMangerServiceRedis.set(AdminSettingsKeyEmuns.dingding_webhook.name(), JsonUtils.objectToJson(dingdingVo), 4L);
            return dingdingVo;
        }catch (Exception e)
        {
            log.info("异常:{}",e);
            return  null;
        }
    }

}
