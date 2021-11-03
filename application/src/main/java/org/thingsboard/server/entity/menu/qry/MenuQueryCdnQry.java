package org.thingsboard.server.entity.menu.qry;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.memu.Menu;

@ApiModel("MenuQueryCdnQry")
@Data
public class MenuQueryCdnQry {

    @ApiModelProperty("菜单名称")
    private String name;

    @ApiModelProperty("菜单类型")
    private String menuType;

    public Menu toMenu(){
        Menu menu = new Menu();
        menu.setName(name);
        menu.setMenuType(menuType);
        return menu;
    }
}
