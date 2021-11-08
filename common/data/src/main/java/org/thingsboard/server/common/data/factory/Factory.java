package org.thingsboard.server.common.data.factory;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class Factory {

    //("工厂标识")
    private UUID id;
    private String code;

    private String name;

    private String logoIcon;

    private String logoImages;

    private String address;

    private String longitude;

    private String latitude;

    private String postalCode;

    private String mobile;

    private String email;

    private UUID adminUserId;

    private String adminUserName;

    private String remark;

    private UUID tenantId;
    private long createdTime;
    private UUID createdUser;
    private long updatedTime;
    private UUID updatedUser;
    private String delFlag;

    /**查询条件*/
    //车间名称
    public String workshopName;

    //产线名称
    public String productionlineName;

    //产线名称
    public String deviceName;

    @ApiModelProperty("发布时间")
    private long publishTime;
    @ApiModelProperty("登录用户")
    private UUID loginUserId;
    @ApiModelProperty("工厂版本")
    private String factoryVersion;


    public Factory() {

    }
    public Factory(UUID id) {
        this.id = id;
    }

}
