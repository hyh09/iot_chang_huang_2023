package org.thingsboard.server.common.data.vo.user.enums;

/**
 * @program: thingsboard
 * @description: 操作类型
 * @author: HU.YUNHUI
 * @create: 2022-02-10 15:57
 **/
public enum OperationTypeEums {
    USER_EDITABLE(0),//用户可编辑
    USER_NON_EDITABLE(1),//用户不能修改
    USER_DEFAULT(null),//默认值


    ROLE_EDITABLE(0),//角色可编辑
    ROLE_NON_EDITABLE(1),//角色不能修改
    ROLE_DEFAULT(null),//角色默认值

    ;


    private  Integer value;

    OperationTypeEums(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
