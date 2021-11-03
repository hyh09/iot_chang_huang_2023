package org.thingsboard.server.entity.menu.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.memu.Menu;

import java.util.UUID;

@ApiModel("UpdMenuDto")
@Data
public class UpdMenuDto {

    @ApiModelProperty(value = "租户菜单标识",required = true)
    private UUID id;
    @ApiModelProperty("租户标识")
    private UUID tenantId;
    @ApiModelProperty("系统菜单名称")
    public String name;
    @ApiModelProperty("路径")
    private String path;
    @ApiModelProperty("父级租户菜单")
    private UUID parentId;
    @ApiModelProperty("系统菜单图标")
    public String menuIcon;
    @ApiModelProperty("系统菜单自定义图片")
    public String menuImages;
    @ApiModelProperty(value = "菜单类型（PC/APP）",required = true)
    public String menuType;

    public Menu toMenu(){
        Menu menu = new Menu(new MenuId(this.getId()));
        menu.setTenantId(tenantId);
        menu.setName(name);
        menu.setPath(path);
        menu.setParentId(parentId);
        menu.setMenuIcon(menuIcon);
        menu.setMenuImages(menuImages);
        menu.setMenuType(menuType);
        return menu;
    }
}
