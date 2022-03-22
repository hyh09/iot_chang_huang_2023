package org.thingsboard.server.dao.tool.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.vo.enums.ErrorMessageEnums;
import org.thingsboard.server.dao.tool.UserLanguageSvc;
import org.thingsboard.server.dao.user.UserService;

/**
 * @program: thingsboard
 * @description: 获取当前的提示语言
 * @author: HU.YUNHUI
 * @create: 2021-11-30 15:41
 **/
@Log4j
@Service
public class UserLanguageImpl  implements UserLanguageSvc {
    @Autowired
    protected UserService userService;

    /**
     * "lang":"zh_CN"
     * @param tenantId 当前登录人的租户id
     * @param userId  当前登录人的用户id
     * @return
     * @throws ThingsboardException
     */
    @Override
    public String getLanguageByUserLang(String key,TenantId tenantId, UserId userId) throws ThingsboardException {
        User user = userService.findUserById(tenantId, userId);
        if (user == null) {
            throw new ThingsboardException("Requested item wasn't found!", ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        if(user.getAdditionalInfo().isObject()) {
            JsonNode additionalInfo = user.getAdditionalInfo();
            String  lang = additionalInfo.get("lang").asText();
           return ErrorMessageEnums.getLanguage(key,lang);
        }
        return ErrorMessageEnums.getLanguage(key,"");
    }

    @Override
    public String getLanguageByUserLang(ErrorMessageEnums enums, TenantId tenantId, UserId userId) throws ThingsboardException {
        if(userId == null)
        {
            return enums.getCNLanguage();

        }
        User user = userService.findUserById(tenantId, userId);
        if (user == null) {
            return enums.getCNLanguage();
//            throw new ThingsboardException("Requested item wasn't found!", ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        if(user.getAdditionalInfo().isObject()) {
            JsonNode additionalInfo = user.getAdditionalInfo();
            String  lang = additionalInfo.get("lang").asText();
            if(StringUtils.isEmpty(lang))
            {
              return   enums.getCNLanguage();
            }
            if(lang.equals("zh_CN"))
            {
                return   enums.getCNLanguage();
            }else {

                return  enums.getEnLanguage();
            }
        }
        return enums.getCNLanguage();
    }


}
