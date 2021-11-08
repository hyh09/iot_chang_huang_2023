package org.thingsboard.server.common.data.memu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.UUID;

@Data
public class Menu{
    private UUID id;
    @NoXss
    private String code;
    @NoXss
    private String name;
    @NoXss
    private Integer level;
    @NoXss
    private Integer sort;
    @NoXss
    private String url;
    @NoXss
    private UUID parentId;
    @NoXss
    private String menuIcon;
    @NoXss
    private String menuImages;
    @NoXss
    private String menuType;

    //"是按钮（true/false）")
    public Boolean isButton;
    //多语言Key
    private String langKey;
    private String path;
    private long createdTime;
    @NoXss
    private UUID createdUser;
    @NoXss
    private long updatedTime;
    @NoXss
    private UUID updatedUser;
    @NoXss
    private String region;

    private UUID tenantId;

    /*****非数据库字段*****/
    @ApiModelProperty("上级菜单名称")
    private String parentName;

    public Menu() {
        super();
    }

    public Menu(MenuId id) {
        this.id = id.getId();
    }
    public Menu(UUID id) {
        this.id = id;
    }

    public void updMenu(Menu menu){
        this.id = menu.getId();
        this.tenantId = menu.getTenantId();
        this.name = menu.getName();
        this.path = menu.getPath();
        this.menuIcon= menu.getMenuIcon();
        this.menuImages = menu.getMenuImages();
        this.parentId = menu.getParentId();
    }

}

