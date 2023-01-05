package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.MesDeviceRelation;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = "hs_mes_device_relation")
public class MesDeviceRelationEntity extends AbstractMesDeviceRelationEntity<MesDeviceRelation> {

    public MesDeviceRelationEntity(){super();}

    public MesDeviceRelationEntity(MesDeviceRelation mesDeviceRelation){super(mesDeviceRelation);}

    public MesDeviceRelation toData() {
        return super.toMesDeviceRelation();
    }
}
