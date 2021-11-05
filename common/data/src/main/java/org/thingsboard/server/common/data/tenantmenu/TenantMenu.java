package org.thingsboard.server.common.data.tenantmenu;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.SearchTextBasedWithAdditionalInfo;
import org.thingsboard.server.common.data.id.tenantmenu.TenantMenuId;
import org.thingsboard.server.common.data.vo.menu.TenantMenuVo;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class TenantMenu extends SearchTextBasedWithAdditionalInfo<TenantMenuId>{

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

    //额外加的逻辑字段配合前端
    private  String name;

    private  Boolean checked=false;

    public TenantMenu() {
        super();
    }

    public TenantMenu(TenantMenuId id) {
        super(id);
    }

    @Override
    public String getSearchText() {
        return null;
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



    public TenantMenuVo toTenantMenuVo(TenantMenu menu){
        TenantMenuVo tenantMenu = new TenantMenuVo();
        tenantMenu.setTenantId(menu.getTenantId());
        tenantMenu.setSysMenuId(menu.getSysMenuId());
        tenantMenu.setRegion(menu.getRegion());
        tenantMenu.setSysMenuCode(menu.getSysMenuCode());
        tenantMenu.setSysMenuName(menu.getSysMenuName());
        tenantMenu.setTenantMenuCode(menu.getTenantMenuCode());
        tenantMenu.setTenantMenuName(menu.getTenantMenuName());
        tenantMenu.setLevel(menu.getLevel());
        tenantMenu.setSort(menu.getSort());
        tenantMenu.setUrl(menu.getUrl());
        tenantMenu.setTenantMenuIcon(menu.getTenantMenuIcon());
        tenantMenu.setTenantMenuImages(menu.getTenantMenuImages());
        tenantMenu.setParentId(menu.getParentId());
        tenantMenu.setMenuType(menu.getMenuType());
        tenantMenu.setIsButton(menu.getIsButton());
        tenantMenu.setLangKey(menu.getLangKey());
        tenantMenu.setCreatedTime(menu.getCreatedTime());
        tenantMenu.setCreatedUser(menu.getCreatedUser());
        tenantMenu.setUpdatedTime(menu.getUpdatedTime());
        tenantMenu.setUpdatedUser(menu.getUpdatedUser());
        tenantMenu.setName(menu.getName());
        tenantMenu.setId(menu.getUuidId());
        return tenantMenu;
    }

}
