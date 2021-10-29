package org.thingsboard.server.service.userrole.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.dao.sql.role.service.TenantSysRoleService;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.entity.ResultVo;
import org.thingsboard.server.entity.rolemenu.OutMenuByUserVo;
import org.thingsboard.server.entity.user.UserVo;
import org.thingsboard.server.service.userrole.CheckSvc;
import org.thingsboard.server.service.userrole.SqlSplicingSvc;
import org.thingsboard.server.service.userrole.sqldata.SqlVo;

import java.util.List;

/**
 * 校验
 */
@Slf4j
@Service
public class CheckImpl  implements CheckSvc {


    @Autowired private SqlSplicingSvc splicingSvc;
    @Autowired private TenantSysRoleService tenantSysRoleService;

    @Override
    public Boolean checkValueByKey(UserVo vo) {
        log.info("调用查询菜单列表的入参{}",vo);
        SqlVo sqlVo= splicingSvc.getCountUserSqlByVo(vo);
        log.debug("调用查询菜单列表的入参{},通过sql{},查询到得结果{}",sqlVo.getParam(),sqlVo.getSql());
        Long count= tenantSysRoleService.queryContListSqlLocal(sqlVo.getSql(),sqlVo.getParam());
        log.info("调用查询菜单列表的入参{},通过sql{},查询到得结果{}",sqlVo.getParam(),sqlVo.getSql(),count);
        return  (count>0?true:false);
    }


}
