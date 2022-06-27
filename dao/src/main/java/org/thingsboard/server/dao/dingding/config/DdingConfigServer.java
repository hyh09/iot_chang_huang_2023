package org.thingsboard.server.dao.dingding.config;

import com.fasterxml.jackson.databind.JsonNode;
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

import java.util.List;

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


   public List<DingdingVo> queryDingdingConfig()
    {
        try {
            AdminSettings adminSettings = adminSettingsService.findAdminSettingsByKey(null, AdminSettingsKeyEmuns.dingding_webhook.name());
            if (adminSettings == null) {
                return null;
            }
            JsonNode jsonNode =  adminSettings.getJsonValue();
            if(jsonNode == null)
            {
                return null;
            }
            List<DingdingVo> dingdingVo = JsonUtils.jsonToList(JsonUtils.objectToJson(jsonNode), DingdingVo.class);
//            tokenMangerServiceRedis.set(AdminSettingsKeyEmuns.dingding_webhook.name(), JsonUtils.objectToJson(dingdingVo), 4L);
            return dingdingVo;
        }catch (Exception e)
        {
            log.info("异常:{}",e);
            return  null;
        }
    }

}
