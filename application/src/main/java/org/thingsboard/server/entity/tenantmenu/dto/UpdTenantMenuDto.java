package org.thingsboard.server.entity.tenantmenu.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;

import java.util.UUID;

@ApiModel("UpdTenantMenuDto")
@Data
public class UpdTenantMenuDto {

    @ApiModelProperty(value = "租户菜单标识",required = true)
    private UUID id;
    @ApiModelProperty("租户标识")
    private UUID tenantId;
    @ApiModelProperty("租户菜单名称")
    private String tenantMenuName;
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

    public UpdTenantMenuDto(){}

    public UpdTenantMenuDto(TenantMenu tenantMenu){
        this.tenantId = tenantMenu.getTenantId();
        this.tenantMenuName = tenantMenu.getTenantMenuName();
        this.url = tenantMenu.getUrl();
        this.tenantMenuIcon = tenantMenu.getTenantMenuIcon();
        this.tenantMenuImages = tenantMenu.getTenantMenuImages();
        this.parentId = tenantMenu.getParentId();
        this.menuType = tenantMenu.getMenuType();
    }

    public TenantMenu toTenantMenu(){
        TenantMenu tenantMenu = new TenantMenu();
        tenantMenu.setTenantId(tenantId);
        tenantMenu.setTenantMenuName(tenantMenuName);
        tenantMenu.setUrl(url);
        tenantMenu.setTenantMenuIcon(tenantMenuIcon);
        tenantMenu.setTenantMenuImages(tenantMenuImages);
        tenantMenu.setParentId(parentId);
        tenantMenu.setMenuType(menuType);
        return tenantMenu;
    }

}
