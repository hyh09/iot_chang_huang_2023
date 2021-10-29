package org.thingsboard.server.service.userrole;

import org.thingsboard.server.entity.user.UserVo;

/**
 * 校验数据重复等逻辑
 */
public interface CheckSvc {

    //用户的一些字段
    Boolean  checkValueByKey(UserVo vo);
}
