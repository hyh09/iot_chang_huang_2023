package org.thingsboard.server.service.userrole.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.dao.sql.role.service.TenantSysRoleService;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.service.userrole.CheckSvc;

/**
 * 校验
 */
@Slf4j
@Service
public class CheckImpl  implements CheckSvc {


    @Autowired private TenantSysRoleService tenantSysRoleService;

    @Override
    public Boolean checkValueByKey() {
         return  false;
    }


}
