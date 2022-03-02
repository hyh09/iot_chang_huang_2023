package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.PRODUCTION_CALENDAR_COLUMN_FAMILY_NAME)
public class ProductionCalenderEntity extends AbstractProductionCalenderEntity<ProductionCalender> {

    public ProductionCalenderEntity() {
        super();
    }

    public ProductionCalenderEntity(ProductionCalender productionCalender) {
        super(productionCalender);
    }

    @Override
    public ProductionCalender toData() {
        return super.toProductionCalender();
    }

}
