package org.thingsboard.server.common.data.tenantmenu;

import lombok.Data;

import java.util.UUID;

@Data
public class TenantMenuInfo{

    private UUID id;
    private UUID tenantId;
    private UUID sysMenuId;
    private String sysMenuCode;
    private String sysMenuName;
    private String tenantMenuName;
    private String tenantMenuCode;
    private Integer level;
    private Integer sort;
    private String url;
    private UUID parentId;
    private String tenantMenuIcon;
    private String tenantMenuImages;
    private String menuType;
    private String path;
    private UUID createdUser;
    private long createdTime;
    private long updatedTime;
    private UUID updatedUser;
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
