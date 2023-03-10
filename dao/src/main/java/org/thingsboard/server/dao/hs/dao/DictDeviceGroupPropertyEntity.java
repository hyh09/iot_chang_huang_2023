package org.thingsboard.server.dao.hs.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.dao.model.ToData;
import org.thingsboard.server.dao.util.mapping.JsonStringType;
import org.thingsboard.server.dao.hs.entity.po.DictDeviceGroupProperty;

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
     * 设备字典Id
     */
    @Column(name = HsModelConstants.DICT_DEVICE_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID dictDeviceId;

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

    /**
     * 标题
     */
    @Column(name = HsModelConstants.DICT_DEVICE_GROUP_PROPERTY_TITLE)
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

    public DictDeviceGroupPropertyEntity() {
    }

    public DictDeviceGroupPropertyEntity(DictDeviceGroupProperty common) {
        if (common.getId() != null)
            this.id = UUID.fromString(common.getId());
        if (common.getDictDeviceGroupId() != null)
            this.dictDeviceGroupId = UUID.fromString(common.getDictDeviceGroupId());
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
    public DictDeviceGroupProperty toData() {
        DictDeviceGroupProperty common = new DictDeviceGroupProperty();
        common.setId(id.toString());
        if (dictDeviceGroupId != null) {
            common.setDictDeviceGroupId(dictDeviceGroupId.toString());
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
