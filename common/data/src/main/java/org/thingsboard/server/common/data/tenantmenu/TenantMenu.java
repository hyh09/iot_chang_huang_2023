package org.thingsboard.server.common.data.tenantmenu;

import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.SearchTextBasedWithAdditionalInfo;
import org.thingsboard.server.common.data.id.tenantmenu.TenantMenuId;
import org.thingsboard.server.common.data.validation.NoXss;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
public class TenantMenu extends SearchTextBasedWithAdditionalInfo<TenantMenuId>{

    private UUID tenantId;
    private UUID sysMenuId;
    @NoXss
    private String sysMenuCode;
    @NoXss
    private String sysMenuName;
    @NoXss
    private String tenantMenuName;
    @NoXss
    private String tenantMenuCode;
    @NoXss
    private Integer level;
    @NoXss
    private Integer sort;
    @NoXss
    private String url;
    @NoXss
    private UUID parentId;
    @NoXss
    private String tenatMenuIcon;
    @NoXss
    private String tenentMenuImages;
    @NoXss
    private String menuType;
    @NoXss
    private UUID createdUser;
    @NoXss
    private long updatedTime;
    @NoXss
    private UUID updatedUser;
    @NoXss
    private String region;

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

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public UUID getSysMenuId() {
        return sysMenuId;
    }

    public void setSysMenuId(UUID sysMenuId) {
        this.sysMenuId = sysMenuId;
    }

    public String getSysMenuCode() {
        return sysMenuCode;
    }

    public void setSysMenuCode(String sysMenuCode) {
        this.sysMenuCode = sysMenuCode;
    }

    public String getSysMenuName() {
        return sysMenuName;
    }

    public void setSysMenuName(String sysMenuName) {
        this.sysMenuName = sysMenuName;
    }

    public String getTenantMenuName() {
        return tenantMenuName;
    }

    public void setTenantMenuName(String tenantMenuName) {
        this.tenantMenuName = tenantMenuName;
    }

    public String getTenantMenuCode() {
        return tenantMenuCode;
    }

    public void setTenantMenuCode(String tenantMenuCode) {
        this.tenantMenuCode = tenantMenuCode;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public String getTenatMenuIcon() {
        return tenatMenuIcon;
    }

    public void setTenatMenuIcon(String tenatMenuIcon) {
        this.tenatMenuIcon = tenatMenuIcon;
    }

    public String getTenentMenuImages() {
        return tenentMenuImages;
    }

    public void setTenentMenuImages(String tenentMenuImages) {
        this.tenentMenuImages = tenentMenuImages;
    }

    public UUID getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(UUID createdUser) {
        this.createdUser = createdUser;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public UUID getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(UUID updatedUser) {
        this.updatedUser = updatedUser;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }
}
