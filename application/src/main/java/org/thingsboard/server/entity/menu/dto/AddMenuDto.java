package org.thingsboard.server.entity.menu.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.memu.Menu;

import java.util.UUID;

@ApiModel("AddMenuDto")
@Data
public class AddMenuDto {

    @ApiModelProperty("系统菜单标识")
    public UUID id;

    @ApiModelProperty("系统菜单名称")
    public String name;

    @ApiModelProperty("层级")
    public Integer level;

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

    @ApiModelProperty("路径")
    private String path;

    public Menu toMenu(){
        Menu menu = new Menu(new MenuId(null));
        if(this.getId() != null){
            menu.setId(new MenuId(this.getId()));
        }
        menu.setName(name);
        menu.setLevel(level);
        menu.setUrl(url);
        menu.setMenuIcon(menuIcon);
        menu.setMenuImages(menuImages);
        menu.setParentId(parentId);
        menu.setMenuType(menuType);
        menu.setPath(path);
        return menu;
    }
}
