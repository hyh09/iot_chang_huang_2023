package org.thingsboard.server.dao.hs.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.dao.model.ToData;
import org.thingsboard.server.dao.util.mapping.JsonStringType;
import org.thingsboard.server.dao.hs.entity.po.DictDeviceProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.util.UUID;

/**
 * 设备字典属性实体类
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = HsModelConstants.DICT_DEVICE_PROPERTY_TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
public class DictDevicePropertyEntity extends BasePgEntity<DictDevicePropertyEntity> implements ToData<DictDeviceProperty> {
    /**
     * 设备字典Id
     */
    @Column(name = HsModelConstants.DICT_DEVICE_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID dictDeviceId;

    /**
     * 名称
     */
    @Column(name = HsModelConstants.DICT_DEVICE_PROPERTY_NAME)
    private String name;

    /**
     * 内容
     */
    @Column(name = HsModelConstants.DICT_DEVICE_PROPERTY_CONTENT)
    private String content;

    /**
     * 排序
     */
    @Column(name = HsModelConstants.GENERAL_SORT)
    private Integer sort;

    public DictDevicePropertyEntity() {
    }

    public DictDevicePropertyEntity(DictDeviceProperty common) {
        if (common.getId() != null)
            this.id = UUID.fromString(common.getId());
        if (common.getDictDeviceId() != null)
            this.dictDeviceId = UUID.fromString(common.getDictDeviceId());

        this.name = common.getName();
        this.content = common.getContent();
        this.sort = common.getSort();

        this.setCreatedTimeAndCreatedUser(common);
    }

    /**
     * to data
     */
    public DictDeviceProperty toData() {
        DictDeviceProperty common = new DictDeviceProperty();
        common.setId(id.toString());
        if (dictDeviceId != null) {
            common.setDictDeviceId(dictDeviceId.toString());
        }
        common.setName(name);
        common.setContent(content);
        common.setSort(sort);

        common.setCreatedTime(createdTime);
        common.setCreatedUser(createdUser);
        common.setUpdatedTime(updatedTime);
        common.setUpdatedUser(updatedUser);
        return common;
    }
}
