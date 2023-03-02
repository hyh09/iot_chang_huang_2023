package org.thingsboard.server.entity.menu.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.entity.menu.AbstractMenu;

@Data
@ApiModel("MenuVo")
public class MenuVo extends AbstractMenu {

    //该系统菜单是否被租户关联
    @ApiModelProperty("该系统菜单是否被租户关联(true/fasle)")
    private Boolean associatedTenant;

    @ApiModelProperty("上级菜单名称")
    private String parentName;

    public MenuVo() {
        super();
    }
    public MenuVo(Menu menu) {
        super(menu);
        this.parentName = menu.getParentName();
    }
}
