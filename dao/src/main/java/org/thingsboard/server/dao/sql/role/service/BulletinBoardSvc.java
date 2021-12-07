package org.thingsboard.server.dao.sql.role.service;

import org.thingsboard.server.common.data.vo.tskv.MaxTsVo;

/**
 * @program: thingsboard
 * @description: 看板相关接口
 * @author: HU.YUNHUI
 * @create: 2021-12-07 10:36
 **/
public interface BulletinBoardSvc {





    /**
     * 历史产能接口
     *   就是查询当前最大
     */
   String  historicalCapacity (MaxTsVo MaxTsVo);
}
