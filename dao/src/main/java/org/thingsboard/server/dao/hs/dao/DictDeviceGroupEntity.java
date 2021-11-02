package org.thingsboard.server.dao.hs.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.dao.model.ToData;
import org.thingsboard.server.dao.util.mapping.JsonStringType;
import org.thingsboard.server.dao.hs.entity.po.DictDeviceGroup;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.util.UUID;

/**
 * 设备字典分组实体类
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = HsModelConstants.DICT_DEVICE_GROUP_TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
public class DictDeviceGroupEntity extends BasePgEntity<DictDeviceGroupEntity> implements ToData<DictDeviceGroup> {
    /**
     * 设备字典Id
     */
    @Column(name = HsModelConstants.DICT_DEVICE_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID dictDeviceId;

    /**
     * 名称
     */
    @Column(name = HsModelConstants.DICT_DEVICE_GROUP_NAME)
    private String name;

    public DictDeviceGroupEntity() {
    }

    public DictDeviceGroupEntity(DictDeviceGroup common) {
        if (common.getId() != null)
            this.id = UUID.fromString(common.getId());
        if (common.getDictDeviceId() != null)
            this.dictDeviceId = UUID.fromString(common.getDictDeviceId());

        this.name = common.getName();

        this.setCreatedTimeAndCreatedUser(common);
    }

    /**
     * to data
     */
    public DictDeviceGroup toData() {
        DictDeviceGroup common = new DictDeviceGroup();
        common.setId(id.toString());
        if (dictDeviceId != null) {
            common.setDictDeviceId(dictDeviceId.toString());
        }
        common.setName(name);

        common.setCreatedTime(createdTime);
        common.setCreatedUser(createdUser);
        common.setUpdatedTime(updatedTime);
        common.setUpdatedUser(updatedUser);
        return common;
    }
}
