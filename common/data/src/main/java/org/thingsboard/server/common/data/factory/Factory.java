package org.thingsboard.server.common.data.factory;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.HasCustomerId;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.HasOtaPackage;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.OtaPackageId;

import java.util.UUID;

@Data
public class Factory implements HasName,HasCustomerId, HasOtaPackage {

    //("工厂标识")
    private UUID id;
    private String code;

    private String name;

    private String logoIcon;

    private String logoImages;

    private String address;

    @ApiModelProperty("国家")
    private String country;

    @ApiModelProperty("省")
    private String province;

    @ApiModelProperty("市")
    private String city;

    @ApiModelProperty("区")
    private String area;

    private String longitude;

    private String latitude;

    private String postalCode;

    private String mobile;

    private String email;

    private String remark;

    private UUID tenantId;
    private long createdTime;
    private UUID createdUser;
    private long updatedTime;
    private UUID updatedUser;
    private String delFlag;

    private Integer sort;

    /*********************************************查询条件***********************************/
    //车间名称
    public String workshopName;

    //产线名称
    public String productionLineName;

    //产线名称
    public String deviceName;

    @ApiModelProperty("发布时间")
    private long publishTime;
    @ApiModelProperty("登录用户")
    private UUID loginUserId;
    @ApiModelProperty("工厂版本")
    private String factoryVersion;
    @ApiModelProperty("在线状态true-在线，false-离线")
    private Boolean active;
    @ApiModelProperty("网关设备名称")
    private String gatewayName;
    @ApiModelProperty("工厂下网关的在线、离线状态。有一个在线视为正常，全部离线视为异常")
    private Boolean factoryStatus;
    /*********************************************查询条件***********************************/

    public Factory() {

    }
    public Factory(UUID id) {
        this.id = id;
    }

    public Factory toFactoryFromBoardRequst() {
        Factory factory = new Factory(id);
        factory.setName(name);
        factory.setLongitude(longitude);
        factory.setLatitude(latitude);
        factory.setLogoIcon(logoIcon);
        factory.setLogoImages(logoImages);
        return factory;
    }

    @Override
    public String toString() {
        return "Factory{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", logoIcon='" + logoIcon + '\'' +
                ", logoImages='" + logoImages + '\'' +
                ", address='" + address + '\'' +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", remark='" + remark + '\'' +
                ", tenantId=" + tenantId +
                ", createdTime=" + createdTime +
                ", createdUser=" + createdUser +
                ", updatedTime=" + updatedTime +
                ", updatedUser=" + updatedUser +
                ", delFlag='" + delFlag + '\'' +
                ", workshopName='" + workshopName + '\'' +
                ", productionLineName='" + productionLineName + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", publishTime=" + publishTime +
                ", loginUserId=" + loginUserId +
                ", factoryVersion='" + factoryVersion + '\'' +
                ", active=" + active +
                ", gatewayName='" + gatewayName + '\'' +
                '}';
    }

    @Override
    public CustomerId getCustomerId() {
        return null;
    }

    @Override
    public OtaPackageId getFirmwareId() {
        return null;
    }

    @Override
    public OtaPackageId getSoftwareId() {
        return null;
    }
}
