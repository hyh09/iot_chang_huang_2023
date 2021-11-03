package org.thingsboard.server.service.userrole;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.entity.user.CodeVo;
import org.thingsboard.server.entity.user.UserVo;

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
