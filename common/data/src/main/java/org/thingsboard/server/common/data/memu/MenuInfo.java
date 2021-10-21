package org.thingsboard.server.common.data.memu;

import lombok.Data;

import java.util.UUID;

@Data
public class MenuInfo {
    private UUID id;

    private UUID sysMenuId;

    private String code;

    private String name;

    private Integer level;

    private Integer sort;

    private String url;

    private UUID parentId;

    private String menuIcon;

    private String menuImages;

    private UUID createdUser;

    private long updatedTime;

    private UUID updatedUser;

    private String region;

    private String menuType;

    //该系统菜单是否被租户关联
    private Boolean associatedTenant;

    private Long createdTime;

    public MenuInfo(){}

    public MenuInfo(Menu menu ){
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
