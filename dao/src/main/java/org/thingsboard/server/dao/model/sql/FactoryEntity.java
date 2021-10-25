package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.FACTORY_COLUMN_FAMILY_NAME)
public final class FactoryEntity extends AbstractFactoryEntity<Factory>  {

    public FactoryEntity() {
        super();
    }

    @Override
    public Factory toData() {
        return super.toFactory();
    }

    public FactoryEntity(Factory factory){
        super(factory);
    }
}
