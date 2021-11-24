package org.thingsboard.server.dao.sql.role.service;

import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.user.CodeVo;
import org.thingsboard.server.common.data.vo.user.UserVo;


/**
 * 校验数据重复等逻辑
 */
public interface CheckSvc {

    //用户的一些字段
    Boolean  checkValueByKey(UserVo vo);

    /**
     * 用户编码  原逻辑
     * @param vo
     * @return
     */
    Object queryCode(CodeVo vo);


    public Object queryCodeNew(CodeVo vo, TenantId tenantId);

}
