package org.thingsboard.server.common.data.vo;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 判断用户是否是工厂管理员 /租户用户实体对象
 * @author: HU.YUNHUI
 * @create: 2021-11-02 10:52
 **/
@Data
@ToString
public class JudgeUserVo {

    /**
     *是否是租户管理员
     *
     */
    private  Boolean tenantFlag;

    /**
     * 是否是工厂管理员
     */
    private  Boolean factoryManagementFlag;

    /**
     * 当前创建人id
     * tenantFlag 租户管理true: userId就是租户管理id
     *  factoryManagementFlag 为true: userId就是工厂管理员id
     */
    private UUID  userId;


    public JudgeUserVo(Boolean tenantFlag, Boolean factoryManagementFlag, UUID userId) {
        this.tenantFlag = tenantFlag;
        this.factoryManagementFlag = factoryManagementFlag;
        this.userId = userId;
    }

    public JudgeUserVo() {
    }
}
