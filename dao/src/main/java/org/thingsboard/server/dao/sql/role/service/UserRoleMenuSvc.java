package org.thingsboard.server.dao.sql.role.service;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.vo.JudgeUserVo;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 用户角色菜单相关接口
 * @author: HU.YUNHUI
 * @create: 2021-11-02 10:28
 **/
public interface UserRoleMenuSvc {

    //判断当人角色
    JudgeUserVo  decideUser(UserId userId);

    //创建工厂管理员接口
    User save(User user,User user1);

}
