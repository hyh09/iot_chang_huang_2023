package org.thingsboard.server.service.userrole;

import org.thingsboard.server.entity.rolemenu.InMenuByUserVo;
import org.thingsboard.server.entity.user.UserVo;
import org.thingsboard.server.service.userrole.sqldata.SqlVo;

import java.util.Map;

/**
 * JPA调用原生sql的统一sql管理
 */
public interface SqlSplicingSvc {

    /**
     * 查询用户下的菜单
     * @param vo
     * @return
     */
    SqlVo getSqlByVo(InMenuByUserVo vo);


    SqlVo  getCountUserSqlByVo(UserVo vo);
}
