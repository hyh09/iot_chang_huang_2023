package org.thingsboard.server.common.data.memu;

import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.HasCustomerId;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.SearchTextBasedWithAdditionalInfo;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
public class Menu extends SearchTextBasedWithAdditionalInfo<MenuId> implements HasName, HasTenantId, HasCustomerId {
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
    @NoXss
    private UUID createdUser;
    @NoXss
    private long updatedTime;
    @NoXss
    private UUID updatedUser;
    @NoXss
    private String region;

    public Menu() {
        super();
    }

    public Menu(MenuId id) {
        super(id);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    public String getMenuImages() {
        return menuImages;
    }

    public void setMenuImages(String menuImages) {
        this.menuImages = menuImages;
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

    @Override
    public String getSearchText() {
        return null;
    }

    @Override
    public CustomerId getCustomerId() {
        return null;
    }

    @Override
    public TenantId getTenantId() {
        return null;
    }

}
