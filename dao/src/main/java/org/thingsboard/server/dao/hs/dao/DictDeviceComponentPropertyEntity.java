package org.thingsboard.server.dao.hs.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.dao.hs.entity.po.DictDeviceComponentProperty;
import org.thingsboard.server.dao.model.ToData;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.util.UUID;

/**
 * 设备字典部件属性实体类
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = HsModelConstants.DICT_DEVICE_COMPONENT_PROPERTY_TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
public class DictDeviceComponentPropertyEntity extends BasePgEntity<DictDeviceComponentPropertyEntity> implements ToData<DictDeviceComponentProperty> {
    /**
     * 部件Id
     */
    @Column(name = HsModelConstants.DICT_DEVICE_COMPONENT_PROPERTY_COMPONENT_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID componentId;

    /**
     * 设备字典Id
     */
    @Column(name = HsModelConstants.DICT_DEVICE_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID dictDeviceId;

    /**
     * 名称
     */
    @Column(name = HsModelConstants.DICT_DEVICE_COMPONENT_PROPERTY_NAME)
    private String name;

    /**
     * 内容
     */
    @Column(name = HsModelConstants.DICT_DEVICE_COMPONENT_PROPERTY_CONTENT)
    private String content;

    /**
     * 标题
     */
    @Column(name = HsModelConstants.DICT_DEVICE_COMPONENT_PROPERTY_TITLE)
    private String title;

    /**
     * 排序
     */
    @Column(name = HsModelConstants.GENERAL_SORT)
    private Integer sort;

    /**
     * 数据字典Id
     */
    @Column(name = HsModelConstants.DICT_DATA_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID dictDataId;

    public DictDeviceComponentPropertyEntity() {
    }

    public DictDeviceComponentPropertyEntity(DictDeviceComponentProperty common) {
        if (common.getId() != null)
            this.id = UUID.fromString(common.getId());
        if (common.getComponentId() != null)
            this.componentId = UUID.fromString(common.getComponentId());
        if (common.getDictDeviceId() != null)
            this.dictDeviceId = UUID.fromString(common.getDictDeviceId());

        this.name = common.getName();
        this.content = common.getContent();
        this.title = common.getTitle();
        this.sort = common.getSort();

        if (common.getDictDataId() != null && !StringUtils.isBlank(common.getDictDataId()))
            this.dictDataId = UUID.fromString(common.getDictDataId());

        this.setCreatedTimeAndCreatedUser(common);
    }

    /**
     * to data
     */
    public DictDeviceComponentProperty toData() {
        DictDeviceComponentProperty common = new DictDeviceComponentProperty();
        common.setId(id.toString());
        if (componentId != null) {
            common.setComponentId(componentId.toString());
        }
        if (dictDeviceId != null) {
            common.setDictDeviceId(dictDeviceId.toString());
        }
        common.setName(name);
        common.setContent(content);
        common.setTitle(title);
        common.setSort(sort);
        if (dictDataId != null) {
            common.setDictDataId(dictDataId.toString());
        }

        common.setCreatedTime(createdTime);
        common.setCreatedUser(createdUser);
        common.setUpdatedTime(updatedTime);
        common.setUpdatedUser(updatedUser);
        return common;
    }
}
