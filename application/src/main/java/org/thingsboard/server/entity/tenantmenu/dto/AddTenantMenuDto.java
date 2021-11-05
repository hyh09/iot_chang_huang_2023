package org.thingsboard.server.entity.tenantmenu.dto;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.id.tenantmenu.TenantMenuId;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.entity.tenantmenu.AbstractTenantMenu;

import java.util.List;
import java.util.UUID;

@ApiModel("AddTenantMenuDto")
@Data
public class AddTenantMenuDto extends AbstractTenantMenu {

    @ApiModelProperty("子集菜单")
    List<AddTenantMenuDto> children;

    public AddTenantMenuDto(){}

    public AddTenantMenuDto(TenantMenu tenantMenu){
        this.tenantId = tenantMenu.getTenantId();
        this.sysMenuId = tenantMenu.getSysMenuId();
        this.sysMenuCode = tenantMenu.getSysMenuCode();
        this.sysMenuName = tenantMenu.getSysMenuName();
        this.tenantMenuName = tenantMenu.getTenantMenuName();
        this.tenantMenuCode = tenantMenu.getTenantMenuCode();
        this.level = tenantMenu.getLevel();
        this.url = tenantMenu.getUrl();
        this.tenantMenuIcon = tenantMenu.getTenantMenuIcon();
        this.tenantMenuImages = tenantMenu.getTenantMenuImages();
        this.parentId = tenantMenu.getParentId();
        this.menuType = tenantMenu.getMenuType();
        this.isButton = tenantMenu.getIsButton();
        this.langKey = tenantMenu.getLangKey();
    }

    public TenantMenu toTenantMenu(){
        TenantMenu tenantMenu = new TenantMenu();
        tenantMenu.setTenantId(tenantId);
        tenantMenu.setSysMenuId(sysMenuId);
        tenantMenu.setSysMenuCode(sysMenuCode);
        tenantMenu.setSysMenuName(sysMenuName);
        tenantMenu.setTenantMenuCode(tenantMenuCode);
        tenantMenu.setTenantMenuName(tenantMenuName);
        tenantMenu.setLevel(level);
        tenantMenu.setUrl(url);
        tenantMenu.setTenantMenuIcon(tenantMenuIcon);
        tenantMenu.setTenantMenuImages(tenantMenuImages);
        tenantMenu.setParentId(parentId);
        tenantMenu.setMenuType(menuType);
        tenantMenu.setIsButton(isButton);
        tenantMenu.setLangKey(langKey);
        if(tenantMenu.getId() == null || tenantMenu.getId().getId() == null ){
            tenantMenu.setCreatedUser(id);
            tenantMenu.setCreatedTime(Uuids.unixTimestamp(Uuids.timeBased()));
        }
        return tenantMenu;
    }
    public TenantMenu toTenantMenu(UUID loginUserId){
        TenantMenu tenantMenu = new TenantMenu();
        tenantMenu.setTenantId(tenantId);
        tenantMenu.setSysMenuId(sysMenuId);
        tenantMenu.setSysMenuCode(sysMenuCode);
        tenantMenu.setSysMenuName(sysMenuName);
        tenantMenu.setTenantMenuCode(tenantMenuCode);
        tenantMenu.setTenantMenuName(tenantMenuName);
        tenantMenu.setLevel(level);
        tenantMenu.setUrl(url);
        tenantMenu.setTenantMenuIcon(tenantMenuIcon);
        tenantMenu.setTenantMenuImages(tenantMenuImages);
        tenantMenu.setParentId(parentId);
        tenantMenu.setMenuType(menuType);
        tenantMenu.setIsButton(isButton);
        tenantMenu.setLangKey(langKey);
        if(tenantMenu.getId() != null && tenantMenu.getId().getId() != null ){
            tenantMenu.setCreatedUser(loginUserId);
            tenantMenu.setCreatedTime(Uuids.unixTimestamp(Uuids.timeBased()));
        }else {
            tenantMenu.setId(new TenantMenuId(Uuids.timeBased()));
            tenantMenu.setUpdatedUser(loginUserId);
            tenantMenu.setUpdatedTime(Uuids.unixTimestamp(Uuids.timeBased()));
        }
        return tenantMenu;
    }

}
