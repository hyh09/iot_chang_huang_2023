package org.thingsboard.server.dao.hs.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.dao.hs.entity.po.OrderPlan;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.dao.model.ToData;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = HsModelConstants.ORDER_PLAN_TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
public class OrderPlanEntity extends BasePgEntity<OrderPlanEntity> implements ToData<OrderPlan> {
    /**
     * 租户Id
     */
    @Column(name = HsModelConstants.GENERAL_TENANT_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID tenantId;

    @Column(name = HsModelConstants.ORDER_PLAN_DEVICE_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID deviceId;

    @Column(name = HsModelConstants.ORDER_PLAN_ORDER_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID orderId;

    @Column(name = HsModelConstants.ORDER_PLAN_INTENDED_START_TIME)
    private Long intendedStartTime;

    @Column(name = HsModelConstants.ORDER_PLAN_INTENDED_END_TIME)
    private Long intendedEndTime;

    @Column(name = HsModelConstants.ORDER_PLAN_ACTUAL_START_TIME)
    private Long actualStartTime;

    @Column(name = HsModelConstants.ORDER_PLAN_ACTUAL_END_TIME)
    private Long actualEndTime;

    @Column(name = HsModelConstants.ORDER_PLAN_ENABLED)
    private Boolean enabled;

    @Column(name = HsModelConstants.ORDER_PLAN_SORT)
    private Integer sort;

    @Column(name = HsModelConstants.ORDER_PLAN_ACTUAL_CAPACITY)
    private String actualCapacity;

    @Column(name = HsModelConstants.ORDER_PLAN_INTENDED_CAPACITY)
    private String intendedCapacity;

    @Column(name = HsModelConstants.ORDER_PLAN_MAINTAIN_START_TIME)
    private Long maintainStartTime;

    @Column(name = HsModelConstants.ORDER_PLAN_MAINTAIN_END_TIME)
    private Long maintainEndTime;

    public OrderPlanEntity() {
    }

    public OrderPlanEntity(OrderPlan common) {
        this.id = CommonUtil.toUUIDNullable(common.getId());
        this.tenantId = CommonUtil.toUUIDNullable(common.getTenantId());
        this.deviceId = CommonUtil.toUUIDNullable(common.getDeviceId());
        this.orderId = CommonUtil.toUUIDNullable(common.getOrderId());
        this.intendedEndTime = common.getIntendedEndTime();
        this.intendedStartTime = common.getIntendedStartTime();
        this.actualEndTime = common.getActualEndTime();
        this.actualStartTime = common.getActualStartTime();
        this.enabled = common.getEnabled();
        this.sort = common.getSort();
        this.actualCapacity = common.getActualCapacity().stripTrailingZeros().toPlainString();
        this.intendedCapacity = common.getIntendedCapacity().stripTrailingZeros().toPlainString();
        this.maintainStartTime = common.getMaintainStartTime();
        this.maintainEndTime = common.getMaintainEndTime();

        this.setCreatedTimeAndCreatedUser(common);
    }

    /**
     * to data
     */
    public OrderPlan toData() {
        OrderPlan common = new OrderPlan();
        common.setId(id.toString());
        common.setTenantId(CommonUtil.toStrUUIDNullable(tenantId));
        common.setDeviceId(CommonUtil.toStrUUIDNullable(deviceId));
        common.setOrderId(CommonUtil.toStrUUIDNullable(orderId));
        common.setActualEndTime(actualEndTime);
        common.setActualStartTime(actualStartTime);
        common.setIntendedEndTime(intendedEndTime);
        common.setIntendedStartTime(intendedStartTime);
        common.setEnabled(enabled);
        common.setSort(sort);
        common.setActualCapacity(Optional.ofNullable(actualCapacity).map(BigDecimal::new).orElse(null));
        common.setIntendedCapacity(Optional.ofNullable(intendedCapacity).map(BigDecimal::new).orElse(null));
        common.setMaintainEndTime(maintainEndTime);
        common.setMaintainStartTime(maintainStartTime);

        common.setCreatedTime(createdTime);
        common.setCreatedUser(createdUser);
        common.setUpdatedTime(updatedTime);
        common.setUpdatedUser(updatedUser);
        return common;
    }
}
