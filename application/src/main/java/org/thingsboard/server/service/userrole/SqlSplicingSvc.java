package org.thingsboard.server.service.userrole;

import org.thingsboard.server.common.data.vo.QueryUserVo;
import org.thingsboard.server.entity.rolemenu.InMenuByUserVo;
import org.thingsboard.server.common.data.vo.user.CodeVo;
import org.thingsboard.server.common.data.vo.user.UserVo;
import org.thingsboard.server.service.userrole.sqldata.SqlVo;

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


    /**
     * 查询用户编码
     */
    SqlVo  getUserCode(CodeVo vo);


    /**
     * 查询角色下的用户
     */
    SqlVo  getUserByInRole(QueryUserVo vo);


    SqlVo  getUserByNotInRole(QueryUserVo vo);

}
