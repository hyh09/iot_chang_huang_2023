package org.thingsboard.server.common.data.tenantmenu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.UUID;

@ApiModel("TenantMenuInfo")
@Data
public class TenantMenuInfo{

    @ApiModelProperty("租户菜单标识")
    private UUID id;
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
    @ApiModelProperty("路径")
    private String path;
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

    public TenantMenuInfo(){}

    public TenantMenuInfo(TenantMenu tenantMenu){
        if (tenantMenu.getId() != null) {
            this.setId(tenantMenu.getId().getId());
        }
        this.tenantId = tenantMenu.getTenantId();
        this.sysMenuId = tenantMenu.getSysMenuId();
        this.region = tenantMenu.getRegion();
        this.sysMenuCode = tenantMenu.getSysMenuCode();
        this.sysMenuName = tenantMenu.getSysMenuName();
        this.tenantMenuName = tenantMenu.getTenantMenuName();
        this.tenantMenuCode = tenantMenu.getTenantMenuCode();
        this.level = tenantMenu.getLevel();
        this.sort = tenantMenu.getSort();
        this.url = tenantMenu.getUrl();
        this.tenantMenuIcon = tenantMenu.getTenantMenuIcon();
        this.tenantMenuImages = tenantMenu.getTenantMenuImages();
        this.parentId = tenantMenu.getParentId();
        this.menuType = tenantMenu.getMenuType();
        this.createdTime = tenantMenu.getUpdatedTime();
        this.createdUser = tenantMenu.getCreatedUser();
        this.updatedTime = tenantMenu.getUpdatedTime();
        this.updatedUser = tenantMenu.getUpdatedUser();

    }

}
