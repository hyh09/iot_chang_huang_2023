package org.thingsboard.server.dao.sql.role.service;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.vo.tskv.MaxTsVo;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 看板相关接口
 * @author: HU.YUNHUI
 * @create: 2021-12-07 10:36
 **/
public interface BulletinBoardSvc {

    /**
     * 历史产能的接口
     * @param factoryId
     * @param tenantId
     * @return
     */
   String getHistoryCapValue(String factoryId, UUID tenantId);



    /**
     * 历史产能接口
     *   就是查询当前最大
     */
   String  historySumByKey (MaxTsVo MaxTsVo);
}
