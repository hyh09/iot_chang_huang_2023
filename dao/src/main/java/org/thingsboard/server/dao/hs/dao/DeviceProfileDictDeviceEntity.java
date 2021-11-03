package org.thingsboard.server.dao.hs.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.util.UUID;

/**
 * 设备配置-设备字典关联实体类
 *
 * @author wwj
 * @since 2021.10.25
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = HsModelConstants.DEVICE_PROFILE_DICT_DEVICE_TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
public class DeviceProfileDictDeviceEntity extends BasePgEntity<DeviceProfileDictDeviceEntity>{
    /**
     * 设备字典Id
     */
    @Column(name = HsModelConstants.DICT_DEVICE_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID dictDeviceId;
    /**
     * 设备配置Id
     */
    @Column(name = HsModelConstants.DEVICE_PROFILE_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID deviceProfileId;
}
