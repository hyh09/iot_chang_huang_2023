package org.thingsboard.server.entity.tenantmenu.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;

import java.util.UUID;

@ApiModel("AddTenantMenuDto")
@Data
public class AddTenantMenuDto {

    @ApiModelProperty("租户标识")
    private UUID tenantId;
    @ApiModelProperty("系统菜单标识")
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
    @ApiModelProperty("是按钮（true/false）")
    public Boolean isButton;
    @ApiModelProperty("多语言Key")
    private String langKey;

    public AddTenantMenuDto(){}

    public AddTenantMenuDto(TenantMenu tenantMenu){
        this.tenantId = tenantMenu.getTenantId();
        this.sysMenuId = tenantMenu.getSysMenuId();
        this.sysMenuCode = tenantMenu.getSysMenuCode();
        this.sysMenuName = tenantMenu.getSysMenuName();
        this.tenantMenuName = tenantMenu.getTenantMenuName();
        this.tenantMenuCode = tenantMenu.getTenantMenuCode();
        this.level = tenantMenu.getLevel();
        this.url = tenantMenu.getUrl();
        this.tenantMenuIcon = tenantMenu.getTenantMenuIcon();
        this.tenantMenuImages = tenantMenu.getTenantMenuImages();
        this.parentId = tenantMenu.getParentId();
        this.menuType = tenantMenu.getMenuType();
    }

    public TenantMenu toTenantMenu(){
        TenantMenu tenantMenu = new TenantMenu();
        tenantMenu.setTenantId(tenantId);
        tenantMenu.setSysMenuId(sysMenuId);
        tenantMenu.setSysMenuCode(sysMenuCode);
        tenantMenu.setSysMenuName(sysMenuName);
        tenantMenu.setTenantMenuCode(tenantMenuCode);
        tenantMenu.setTenantMenuName(tenantMenuName);
        tenantMenu.setLevel(level);
        tenantMenu.setUrl(url);
        tenantMenu.setTenantMenuIcon(tenantMenuIcon);
        tenantMenu.setTenantMenuImages(tenantMenuImages);
        tenantMenu.setParentId(parentId);
        tenantMenu.setMenuType(menuType);
        return tenantMenu;
    }

}
