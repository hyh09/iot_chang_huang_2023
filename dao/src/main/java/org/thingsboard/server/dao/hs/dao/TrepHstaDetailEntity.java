package org.thingsboard.server.dao.hs.dao;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.*;
import java.util.UUID;

/**
 * 设备字典图表实体类
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = HsModelConstants.TREP_HSTA_DETAILL_TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
public class TrepHstaDetailEntity {

    /**
     * 主键Id
     */
    @Id
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @GeneratedValue(generator = "uuid-gen")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @Column(name = HsModelConstants.GENERAL_ID, columnDefinition = "uuid")
    protected UUID id;

    /**
     * 设备Id
     */
    @Column(name = HsModelConstants.TREP_STA_ENTITY_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID entityId;

    /**
     * 租户Id
     */
    @Column(name = HsModelConstants.GENERAL_TENANT_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID tenantId;

    @Column(name = HsModelConstants.TREP_STA_TOTAL_TIME)
    private Long totalTime;
    @Column(name = HsModelConstants.TREP_STA_START_TIME)
    private Long startTime;
    @Column(name = HsModelConstants.TREP_STA_END_TIME)
    private Long endTime;
}
