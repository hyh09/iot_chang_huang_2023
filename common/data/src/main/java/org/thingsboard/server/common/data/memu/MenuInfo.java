package org.thingsboard.server.common.data.memu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.UUID;

@ApiModel("MenuInfo")
@Data
public class MenuInfo {
    @ApiModelProperty(name = "菜单标识")
    public UUID id;

    @ApiModelProperty(name = "菜单编码")
    public String code;

    @ApiModelProperty("系统菜单名称")
    public String name;

    @ApiModelProperty("层级")

    public Integer level;
    @ApiModelProperty("排序")
    public Integer sort;

    @ApiModelProperty("页面链接")
    public String url;

    @ApiModelProperty("系统菜单图标")
    public String menuIcon;

    @ApiModelProperty("系统菜单自定义图片")
    public String menuImages;

    @ApiModelProperty("父级租户菜单")
    public UUID parentId;

    @ApiModelProperty("菜单类型（PC/APP）")
    public String menuType;

    @ApiModelProperty("创建人标识")
    public UUID createdUser;
    @ApiModelProperty("创建时间")
    public long createdTime;
    @ApiModelProperty("修改时间")
    public long updatedTime;
    @ApiModelProperty("修改人")
    public UUID updatedUser;
    @ApiModelProperty("区域")
    public String region;
    @ApiModelProperty("该系统菜单是否被租户关联(true/fasle)")
    private Boolean associatedTenant;

    public MenuInfo(){}

    public MenuInfo(Menu menu){
        if (menu.getId() != null) {
            this.id= menu.getId().getId();
        }
        this.code = menu.getCode();
        this.region = menu.getRegion();
        this.name = menu.getName();
        this.level = menu.getLevel();
        this.sort = menu.getSort();
        this.url = menu.getUrl();
        this.menuIcon = menu.getMenuIcon();
        this.menuImages = menu.getMenuImages();
        this.parentId = menu.getParentId();
        this.createdTime = menu.getUpdatedTime();
        this.createdUser = menu.getCreatedUser();
        this.updatedTime = menu.getUpdatedTime();
        this.updatedUser = menu.getUpdatedUser();
        this.associatedTenant = false;
    }

}
