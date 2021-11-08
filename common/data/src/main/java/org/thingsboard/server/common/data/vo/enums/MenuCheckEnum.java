package org.thingsboard.server.common.data.vo.enums;

/**
 * @program: thingsboard
 * @description: 勾选状态
 * @author: HU.YUNHUI
 * @create: 2021-11-05 11:25
 **/

public enum  MenuCheckEnum {

    SELECT_ALL("SELECT_ALL"),//全选

    SEMI_SELECTION("SEMI_SELECTION")//半选
    ;



     MenuCheckEnum(String roleCode) {
        this.roleCode = roleCode;
    }


    private  String roleCode;

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
}
