package org.thingsboard.server.entity.menu.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.entity.menu.AbstractMenu;

@ApiModel("AddMenuDto")
@Data
public class AddMenuDto extends AbstractMenu{

    public AddMenuDto(){
    }
    public Menu toMenu(){
        return super.toMenu();
    }
}
