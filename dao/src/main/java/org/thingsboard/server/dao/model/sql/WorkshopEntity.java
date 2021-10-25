package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.WORKSHOP_COLUMN_FAMILY_NAME)
public final class WorkshopEntity extends AbstractWorkshopEntity<Workshop>  {

    public WorkshopEntity() {
        super();
    }

    @Override
    public Workshop toData() {
        return super.toWorkshop();
    }

    public WorkshopEntity(Workshop workshop){
        super(workshop);
    }
}
