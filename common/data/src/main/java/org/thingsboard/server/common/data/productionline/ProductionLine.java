package org.thingsboard.server.common.data.productionline;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.SearchTextBasedWithAdditionalInfo;
import org.thingsboard.server.common.data.id.productionline.ProductionLineId;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductionLine extends SearchTextBasedWithAdditionalInfo<ProductionLineId> {

    @ApiModelProperty("车间编码")
    private UUID workshopId;

    @ApiModelProperty("生产线编码")
    private String code;

    @ApiModelProperty("生产线名称")
    private String name;

    @ApiModelProperty("logo图标")
    private String logoIcon;

    @ApiModelProperty("logo图片")
    private String logoImages;

    @ApiModelProperty("生产线地址")
    private String adress;

    @ApiModelProperty("经度")
    private String longitude;

    @ApiModelProperty("纬度")
    private String latitude;

    @ApiModelProperty("邮政编码")
    private String postalCode;

    @ApiModelProperty("手机号码")
    private String mobile;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("生产线管理员用户标识")
    private UUID adminUserId;

    @ApiModelProperty("生产线管理员用户标识")
    private String adminUserName;

    @ApiModelProperty(name = "备注")
    private String remark;

    @ApiModelProperty(name = "租户")
    private UUID tenantId;
    @ApiModelProperty("创建时间")
    private long createdTime;
    @ApiModelProperty("创建人标识")
    private UUID createdUser;
    @ApiModelProperty("修改时间")
    private long updatedTime;
    @ApiModelProperty("修改人")
    private UUID updatedUser;

    @ApiModelProperty("删除标记（A-未删除；D-已删除）")
    private String delFlag;

    public ProductionLine() {
        super();
    }

    public ProductionLine(ProductionLineId id) {
        super(id);
    }

    @Override
    public String getSearchText() {
        return null;
    }

}
