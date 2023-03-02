package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import org.hibernate.annotations.TypeDef;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.common.data.MesDeviceRelation;
import org.thingsboard.server.dao.util.anno.JpaOperatorsType;
import org.thingsboard.server.dao.util.mapping.JsonStringType;
import org.thingsboard.server.dao.util.sql.JpaQueryHelper;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Data
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractMesDeviceRelationEntity <T extends MesDeviceRelation> {
    @Id
    @Column(name = "device_id", columnDefinition = "uuid")
    @JpaOperatorsType(JpaQueryHelper.Operators.eq)
    protected UUID deviceId;

    @Column(name = "mes_device_id", columnDefinition = "uuid")
    protected UUID mesDeviceId;

    @Column(name = "factory_id", columnDefinition = "uuid")
    private UUID factoryId;

    @Column(name = "workshop_id", columnDefinition = "uuid")
    private UUID workshopId;

    @Column(name = "production_line_id", columnDefinition = "uuid")
    private UUID productionLineId;

    @Column(name = "tenant_id", columnDefinition = "uuid")
    private UUID tenantId;

    public AbstractMesDeviceRelationEntity(){}

    public AbstractMesDeviceRelationEntity(MesDeviceRelation mesDeviceRelation){
        if(mesDeviceRelation != null){
            BeanUtils.copyProperties(mesDeviceRelation,this);
        }

    }
    public MesDeviceRelation toMesDeviceRelation(){
        MesDeviceRelation mesDeviceRelation = new MesDeviceRelation();
        BeanUtils.copyProperties(this,mesDeviceRelation);
        return mesDeviceRelation;
    }

}
