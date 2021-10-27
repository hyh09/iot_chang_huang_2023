package org.thingsboard.server.common.data.tenantmenu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.SearchTextBasedWithAdditionalInfo;
import org.thingsboard.server.common.data.id.tenantmenu.TenantMenuId;

import java.util.UUID;

@Data
@ApiModel("TenantMenu")
@EqualsAndHashCode(callSuper = true)
public class TenantMenu extends SearchTextBasedWithAdditionalInfo<TenantMenuId>{

    @ApiModelProperty("租户单标识")
    private UUID tenantId;
    @ApiModelProperty("系统单标识")
    private UUID sysMenuId;
    @ApiModelProperty("系统菜单编码")
    private String sysMenuCode;
    @ApiModelProperty("系统菜单名称")
    private String sysMenuName;
    @ApiModelProperty("租户菜单名称")
    private String tenantMenuName;
    @ApiModelProperty("租户菜单编码")
    private String tenantMenuCode;
    @ApiModelProperty("层级")
    private Integer level;
    @ApiModelProperty("排序")
    private Integer sort;
    @ApiModelProperty("页面链接")
    private String url;
    @ApiModelProperty("父级租户菜单")
    private UUID parentId;
    @ApiModelProperty("租户菜单图标")
    private String tenantMenuIcon;
    @ApiModelProperty("租户菜单自定义图片")
    private String tenantMenuImages;
    @ApiModelProperty("菜单类型（PC/APP）")
    private String menuType;
    @ApiModelProperty("创建人标识")
    private UUID createdUser;
    @ApiModelProperty("创建时间")
    private long createdTime;
    @ApiModelProperty("修改时间")
    private long updatedTime;
    @ApiModelProperty("修改人")
    private UUID updatedUser;
    @ApiModelProperty("区域")
    private String region;

    public TenantMenu() {
        super();
    }

    public TenantMenu(TenantMenuId id) {
        super(id);
    }

    @Override
    public String getSearchText() {
        return null;
    }

}
