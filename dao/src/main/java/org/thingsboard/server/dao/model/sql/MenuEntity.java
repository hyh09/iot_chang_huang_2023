package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.MENU_COLUMN_FAMILY_NAME)
public class MenuEntity extends AbstractMenuEntity<Menu>  {

    public MenuEntity(){super();}
    public MenuEntity(Menu menu){super(menu);}

    @Override
    public Menu toData() {
        return super.toMenu();
    }

    @Override
    public String getSearchTextSource() {
        return null;
    }

    @Override
    public void setSearchText(String searchText) {

    }
}
