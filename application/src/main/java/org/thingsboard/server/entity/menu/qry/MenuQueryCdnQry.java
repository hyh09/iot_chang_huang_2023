package org.thingsboard.server.entity.menu.qry;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.entity.menu.AbstractMenu;

@ApiModel("MenuQueryCdnQry")
@Data
public class MenuQueryCdnQry extends AbstractMenu {

    public MenuQueryCdnQry(){}

    public Menu toMenu(){
        return super.toMenu();
    }
}
