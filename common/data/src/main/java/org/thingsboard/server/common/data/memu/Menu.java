package org.thingsboard.server.common.data.memu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
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

@Data
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

    //"是按钮（true/false）")
    public Boolean isButton;
    //多语言Key
    private String langKey;
    private String path;
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
        super(id);
    }

    public void updMenu(Menu menu){
        this.id = menu.getId();
        this.tenantId = menu.getTenantId().getId();
        this.name = menu.getName();
        this.path = menu.getPath();
        this.menuIcon= menu.getMenuIcon();
        this.menuImages = menu.getMenuImages();
        this.parentId = menu.getParentId();
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
        return this.getTenantId();
    }
}

