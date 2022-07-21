package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.FACTORY_COLUMN_FAMILY_NAME)
public class FactoryEntity extends AbstractFactoryEntity<Factory>  {

    public FactoryEntity() {
        super();
    }

    public FactoryEntity(UUID id, String name) {
        super();
        this.id = id;
        this.setName(name);
    }

    public FactoryEntity(UUID id, String name, UUID tenantId) {
        super();
        this.id = id;
        this.setTenantId(tenantId);
        this.setName(name);
    }

    @Override
    public Factory toData() {
        return super.toFactory();
    }

    public FactoryEntity(Factory factory){
        super(factory);
    }

}
