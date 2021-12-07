package org.thingsboard.server.dao.sql.role.service;

/**
 * @program: thingsboard
 * @description: 看板相关接口
 * @author: HU.YUNHUI
 * @create: 2021-12-07 10:36
 **/
public interface KanbanSvc {


    /**
     * 历史产能接口
     *   就是查询当前最大
     */
   String  historicalCapacity ();
}
