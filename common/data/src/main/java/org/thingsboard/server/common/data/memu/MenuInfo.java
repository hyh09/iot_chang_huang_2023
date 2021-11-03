package org.thingsboard.server.common.data.memu;

import lombok.Data;

import java.util.UUID;

@Data
public class MenuInfo {
    public UUID id;

    public String code;

    public String name;

    public Integer level;

    public Integer sort;

    public String url;

    public String menuIcon;

    public String menuImages;

    public UUID parentId;

    public String menuType;

    public UUID createdUser;
    public long createdTime;
    public long updatedTime;
    public UUID updatedUser;
    public String region;
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
