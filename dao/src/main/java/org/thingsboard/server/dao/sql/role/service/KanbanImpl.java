package org.thingsboard.server.dao.sql.role.service;

import lombok.Data;
import org.springframework.stereotype.Service;

/**
 * @program: thingsboard
 * @description: 看板的相关接口
 * @author: HU.YUNHUI
 * @create: 2021-12-07 10:52
 **/
@Data
@Service
public class KanbanImpl implements  KanbanSvc {

    /**
     * 查询历史产能的接口
     *
     * @return
     */
    @Override
    public String historicalCapacity() {
        return null;
    }
}
