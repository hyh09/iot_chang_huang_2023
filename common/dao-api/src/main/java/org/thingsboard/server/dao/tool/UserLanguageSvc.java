package org.thingsboard.server.dao.tool;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.vo.enums.ErrorMessageEnums;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 用户提示语言
 * @author: HU.YUNHUI
 * @create: 2021-11-30 15:37
 **/
public interface UserLanguageSvc {

    /**
     *
     * @param   key
     * @param tenantId 当前登录人的租户id
     * @param userId  当前登录人的用户id
     * @return
     */
    String  getLanguageByUserLang(String key,TenantId tenantId, UserId userId) throws ThingsboardException;


    /**
     *  1.查询不到用户,或者lang 没空 都默认返回中文
     *  2.userId 传 为null  默认返回中文
     *  返回  ErrorMessageEnums 中的数据
     *
     *
     * 中英文的区分
     * @param enums
     * @param tenantId
     * @param userId
     * @return
     * @throws ThingsboardException
     */
     String getLanguageByUserLang(ErrorMessageEnums enums, TenantId tenantId, UserId userId) throws ThingsboardException;

}
