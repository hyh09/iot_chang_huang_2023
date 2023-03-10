package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.TENANT_MENU_COLUMN_FAMILY_NAME)
public final class TenantMenuEntity extends AbstractTenantMenuEntity<TenantMenu>  {

    public TenantMenuEntity() {
        super();
    }

    @Override
    public TenantMenu toData() {
        return super.toTenantMenu();
    }

    public TenantMenuEntity(TenantMenu tenantMenu){
        super(tenantMenu);
    }

    public TenantMenuEntity(Menu menu, int level, UUID createdUser,UUID parentId){
        super(menu,level,createdUser,parentId);
    }
}
