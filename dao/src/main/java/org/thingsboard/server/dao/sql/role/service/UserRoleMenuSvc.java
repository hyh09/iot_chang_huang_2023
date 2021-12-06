package org.thingsboard.server.dao.sql.role.service;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.vo.JudgeUserVo;
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 用户角色菜单相关接口
 * @author: HU.YUNHUI
 * @create: 2021-11-02 10:28
 **/
public interface UserRoleMenuSvc {

    //判断当前用户是不是租户
    Boolean  isTENANT(UUID userId);

    //判断当人角色
    JudgeUserVo  decideUser(UserId userId);

    //创建工厂管理员接口

    /**
     * 创建工厂管理员接口
     * @param user  用户对象 (工厂id 必传)
     * @param user1  登录人信息(用户id:id，组合id:TenantId) 必传
     * @return
     * @throws Exception
     */
    User save(User user,User user1) ;


    /**
     * 由工厂创建工厂管理员角色
     * @param tenantId 租户id
     * @param userId  当前登录人;
     * @param factoryId  工厂id
     */
    TenantSysRoleEntity saveRole(UUID tenantId, UUID  userId, UUID  factoryId) throws ThingsboardException;


}
