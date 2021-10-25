package org.thingsboard.server.common.data.workshop;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.SearchTextBasedWithAdditionalInfo;
import org.thingsboard.server.common.data.id.workshop.WorkshopId;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class Workshop extends SearchTextBasedWithAdditionalInfo<WorkshopId> {

    @ApiModelProperty("工厂标识")
    private UUID factoryId;
    
    @ApiModelProperty("车间编码")
    private String code;

    @ApiModelProperty("车间名称")
    private String name;

    @ApiModelProperty("logo图标")
    private String logoIcon;

    @ApiModelProperty("logo图片")
    private String logoImages;

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

    public Workshop() {
        super();
    }

    public Workshop(WorkshopId id) {
        super(id);
    }

    @Override
    public String getSearchText() {
        return null;
    }

}
