package org.thingsboard.server.common.data.tenantmenu;

import lombok.Data;
import org.thingsboard.server.common.data.id.tenantmenu.TenantMenuId;

import java.util.UUID;

@Data
public class TenantMenu{

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
    //"是按钮（true/false）")
    public Boolean isButton;
    private UUID createdUser;
    private long createdTime;
    private long updatedTime;
    private UUID updatedUser;
    private String region;
    //多语言Key
    private String langKey;

    public TenantMenu() {
    }

    public TenantMenu(TenantMenuId id) {
        this.id = id.getId();
    }

    public TenantMenu(UUID tenantId,Boolean isButton) {
        this.tenantId = tenantId;
        this.isButton = isButton;
    }

    public void updTenantMenu(TenantMenu tenantMenu){
        this.tenantId = tenantMenu.getTenantId();
        this.tenantMenuName = tenantMenu.getTenantMenuName();
        this.url = tenantMenu.getUrl();
        this.tenantMenuIcon= tenantMenu.getTenantMenuIcon();
        this.tenantMenuImages = tenantMenu.getTenantMenuImages();
        this.parentId = tenantMenu.getParentId();
        this.menuType = tenantMenu.getMenuType();
    }

}
