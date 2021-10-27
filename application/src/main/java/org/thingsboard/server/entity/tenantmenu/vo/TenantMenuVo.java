package org.thingsboard.server.entity.tenantmenu.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.entity.tenantmenu.AbstractTenantMenu;

@ApiModel("TenantMenuVo")
@Data
public class TenantMenuVo extends AbstractTenantMenu {

    public TenantMenuVo(){
        super();
    }
    public TenantMenuVo(TenantMenu tenantMenu){
        super(tenantMenu);
    }
}
