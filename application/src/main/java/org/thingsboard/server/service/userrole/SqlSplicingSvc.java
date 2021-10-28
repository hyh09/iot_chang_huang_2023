package org.thingsboard.server.service.userrole;

import org.thingsboard.server.entity.rolemenu.InMenuByUserVo;
import org.thingsboard.server.service.userrole.sqldata.SqlVo;

/**
 * JPA调用原生sql的统一sql管理
 */
public interface SqlSplicingSvc {

    /**
     * 查询用户下的
     * @param vo
     * @return
     */
    SqlVo getSqlByVo(InMenuByUserVo vo);
}
