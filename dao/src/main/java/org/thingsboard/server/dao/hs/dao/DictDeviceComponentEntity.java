package org.thingsboard.server.dao.hs.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.dao.model.ToData;
import org.thingsboard.server.dao.util.mapping.JsonStringType;
import org.thingsboard.server.dao.hs.entity.po.DictDeviceComponent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.util.UUID;

/**
 * 设备字典部件实体类
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = HsModelConstants.DICT_DEVICE_COMPONENT_TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
public class DictDeviceComponentEntity extends BasePgEntity<DictDeviceComponentEntity> implements ToData<DictDeviceComponent> {
    /**
     * 设备字典Id
     */
    @Column(name = HsModelConstants.DICT_DEVICE_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID dictDeviceId;

    /**
     * 父部件Id
     */
    @Column(name = HsModelConstants.DICT_DEVICE_COMPONENT_PARENT_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID parentId;

    /**
     * 编码
     */
    @Column(name = HsModelConstants.DICT_DEVICE_COMPONENT_CODE)
    private String code;

    /**
     * 名称
     */
    @Column(name = HsModelConstants.DICT_DEVICE_COMPONENT_NAME)
    private String name;

    /**
     * 类型
     */
    @Column(name = HsModelConstants.DICT_DEVICE_COMPONENT_TYPE)
    private String type;

    /**
     * 供应商
     */
    @Column(name = HsModelConstants.DICT_DEVICE_COMPONENT_SUPPLIER)
    private String supplier;

    /**
     * 型号
     */
    @Column(name = HsModelConstants.DICT_DEVICE_COMPONENT_MODEL)
    private String model;

    /**
     * 版本号
     */
    @Column(name = HsModelConstants.DICT_DEVICE_COMPONENT_VERSION)
    private String version;

    /**
     * 保修期
     */
    @Column(name = HsModelConstants.DICT_DEVICE_COMPONENT_WARRANTY_PERIOD)
    private String warrantyPeriod;

    /**
     * 备注
     */
    @Column(name = HsModelConstants.DICT_DEVICE_COMPONENT_COMMENT)
    private String comment;

    /**
     * 图标
     */
    @Column(name = HsModelConstants.DICT_DEVICE_COMPONENT_ICON)
    private String icon;

    /**
     * 图片
     */
    @Column(name = HsModelConstants.DICT_DEVICE_COMPONENT_PICTURE)
    private String picture;

    /**
     * 排序
     */
    @Column(name = HsModelConstants.GENERAL_SORT)
    private Integer sort;

    public DictDeviceComponentEntity() {
    }

    public DictDeviceComponentEntity(DictDeviceComponent common) {
        if (common.getId() != null)
            this.id = UUID.fromString(common.getId());
        if (common.getDictDeviceId() != null)
            this.dictDeviceId = UUID.fromString(common.getDictDeviceId());
        if (common.getParentId() != null)
            this.parentId = UUID.fromString(common.getParentId());
        this.code = common.getCode();
        this.name = common.getName();
        this.type = common.getType();
        this.supplier = common.getSupplier();
        this.model = common.getModel();
        this.version = common.getVersion();
        this.warrantyPeriod = common.getWarrantyPeriod();
        this.comment = common.getComment();
        this.icon = common.getIcon();
        this.picture = common.getPicture();
        this.sort = common.getSort();

        this.setCreatedTimeAndCreatedUser(common);
    }

    /**
     * to data
     */
    public DictDeviceComponent toData() {
        DictDeviceComponent common = new DictDeviceComponent();
        if (id != null) {
            common.setId(id.toString());
        }
        if (parentId != null) {
            common.setParentId(parentId.toString());
        }
        if (dictDeviceId != null) {
            common.setDictDeviceId(dictDeviceId.toString());
        }
        common.setCode(code);
        common.setName(name);
        common.setType(type);
        common.setSupplier(supplier);
        common.setModel(model);
        common.setVersion(version);
        common.setWarrantyPeriod(warrantyPeriod);
        common.setComment(comment);
        common.setIcon(icon);
        common.setPicture(picture);
        common.setSort(sort);

        common.setCreatedTime(createdTime);
        common.setCreatedUser(createdUser);
        common.setUpdatedTime(updatedTime);
        common.setUpdatedUser(updatedUser);
        return common;
    }
}
