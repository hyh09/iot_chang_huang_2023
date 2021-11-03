package org.thingsboard.server.hs.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.dao.model.ToData;
import org.thingsboard.server.dao.util.mapping.JsonStringType;
import org.thingsboard.server.hs.entity.po.DictDeviceGroupProperty;
import org.thingsboard.server.hs.entity.po.DictDeviceProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.util.UUID;

/**
 * 设备字典分组属性实体类
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = HsModelConstants.DICT_DEVICE_GROUP_PROPERTY_TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
public class DictDeviceGroupPropertyEntity extends BasePgEntity<DictDeviceGroupPropertyEntity> implements ToData<DictDeviceGroupProperty> {
    /**
     * 设备字典分组Id
     */
    @Column(name = HsModelConstants.DICT_DEVICE_GROUP_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID dictDeviceGroupId;

    /**
     * 名称
     */
    @Column(name = HsModelConstants.DICT_DEVICE_GROUP_PROPERTY_NAME)
    private String name;

    /**
     * 内容
     */
    @Column(name = HsModelConstants.DICT_DEVICE_GROUP_PROPERTY_CONTENT)
    private String content;

    public DictDeviceGroupPropertyEntity() {
    }

    public DictDeviceGroupPropertyEntity(DictDeviceGroupProperty common) {
        if (common.getId() != null)
            this.id = UUID.fromString(common.getId());
        if (common.getDictDeviceGroupId() != null)
            this.dictDeviceGroupId = UUID.fromString(common.getDictDeviceGroupId());

        this.name = common.getName();
        this.content = common.getContent();

//        this.createdUser = common.getCreatedUser();
//        this.setCreatedTime(common.getCreatedTime());
//        this.createdUser = common.getCreatedUser();
//        this.setUpdatedTime(common.getUpdatedTime());
    }

    /**
     * to data
     */
    public DictDeviceGroupProperty toData() {
        DictDeviceGroupProperty common = new DictDeviceGroupProperty();
        common.setId(id.toString());
        if (dictDeviceGroupId != null) {
            common.setDictDeviceGroupId(dictDeviceGroupId.toString());
        }
        common.setName(name);
        common.setContent(content);

        common.setCreatedTime(createdTime);
        common.setCreatedUser(createdUser);
        common.setUpdatedTime(updatedTime);
        common.setUpdatedUser(updatedUser);
        return common;
    }
}
