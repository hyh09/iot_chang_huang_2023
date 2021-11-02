package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.PRODUCTION_LINE_COLUMN_FAMILY_NAME)
public class ProductionLineEntity extends AbstractProductionLineEntity<ProductionLine>  {

    public ProductionLineEntity() {
        super();
    }
    public ProductionLineEntity (Factory factory){
        this.setName(factory.getWorkshopName());
        this.setTenantId(factory.getTenantId());
    }
    public ProductionLineEntity (UUID tenantId){
        this.setTenantId(tenantId);
    }

    @Override
    public ProductionLine toData() {
        return super.toProductionLine();
    }

    public ProductionLineEntity(ProductionLine productionLine){
        super(productionLine);
    }
}
