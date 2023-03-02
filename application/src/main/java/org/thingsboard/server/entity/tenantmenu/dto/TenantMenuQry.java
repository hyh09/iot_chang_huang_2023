package org.thingsboard.server.entity.tenantmenu.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.entity.tenantmenu.AbstractTenantMenu;

@ApiModel(value = "TenantMenuQry",description = "查询条件")
@Data
public class TenantMenuQry extends AbstractTenantMenu {

    public TenantMenuQry(){
        super();
    }
    public TenantMenu toTenantMenu(){
        return super.toTenantMenu();
    }

}
