package org.thingsboard.server.common.data.vo.enums;

/**
 * @program: thingsboard
 * @description: 角色的枚举 --系统生成的默认一条
 * @author: HU.YUNHUI
 * @create: 2021-11-02 11:16
 **/

public enum  RoleEnums {
    //工厂管理员角色
    FACTORY_ADMINISTRATOR("Factory","Factory administrator"),//租户创建的

    TENANT_ADMIN("001","Factory administrator"),//租户是系统管理创建的
    ;


    private  String roleCode;

    private  String roleName;


    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    RoleEnums(String roleCode, String roleName) {
        this.roleCode = roleCode;
        this.roleName = roleName;
    }
}
