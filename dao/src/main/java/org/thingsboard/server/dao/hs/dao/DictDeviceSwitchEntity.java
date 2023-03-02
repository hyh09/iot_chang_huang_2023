package org.thingsboard.server.dao.hs.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.dao.hs.entity.enums.DictDevicePropertyTypeEnum;
import org.thingsboard.server.dao.hsms.entity.enums.DictDevicePropertySwitchEnum;
import org.thingsboard.server.dao.hsms.entity.po.DictDeviceSwitch;
import org.thingsboard.server.dao.model.ToData;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.util.UUID;

/**
 * 设备字典图表实体类
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = HsModelConstants.DICT_DEVICE_SWITCH_TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
public class DictDeviceSwitchEntity extends BasePgEntity<DictDeviceSwitchEntity> implements ToData<DictDeviceSwitch> {
    /**
     * 设备字典Id
     */
    @Column(name = HsModelConstants.DICT_DEVICE_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID dictDeviceId;

    /**
     * 属性Id
     */
    @Column(name = HsModelConstants.DICT_DEVICE_SWITCH_PROPERTY_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID propertyId;

    /**
     * 属性类型
     */
    @Column(name = HsModelConstants.DICT_DEVICE_SWITCH_PROPERTY_TYPE)
    private String propertyType;

    /**
     * 设备Id
     */
    @Column(name = HsModelConstants.ORDER_PLAN_DEVICE_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID deviceId;

    /**
     * 开关
     */
    @Column(name = HsModelConstants.DICT_DEVICE_SWITCH_SWITCH)
    private Integer switchValue;

    public DictDeviceSwitchEntity() {
    }

    public DictDeviceSwitchEntity(DictDeviceSwitch common) {
        this.id = common.getId();
        this.dictDeviceId = common.getDictDeviceId();
        this.propertyId = common.getPropertyId();
        this.propertyType = common.getPropertyType().getCode();
        this.deviceId = common.getDeviceId();
        this.switchValue = common.getPropertySwitch().getCode();
        this.setCreatedTimeAndCreatedUser(common);
    }

    /**
     * to data
     */
    public DictDeviceSwitch toData() {
        DictDeviceSwitch common = new DictDeviceSwitch();
        common.setId(id);
        common.setDictDeviceId(dictDeviceId);
        common.setPropertyId(propertyId);
        common.setPropertyType(DictDevicePropertyTypeEnum.valueOf(propertyType));
        common.setDeviceId(deviceId);
        common.setPropertySwitch(DictDevicePropertySwitchEnum.valueOfCode(switchValue));

        common.setCreatedTime(createdTime);
        common.setCreatedUser(createdUser);
        common.setUpdatedTime(updatedTime);
        common.setUpdatedUser(updatedUser);
        return common;
    }
}
