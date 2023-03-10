package org.thingsboard.server.dao.hs.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.dao.hs.entity.po.DictDevice;
import org.thingsboard.server.dao.model.ToData;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * 设备字典实体类
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = HsModelConstants.DICT_DEVICE_TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
public class DictDeviceEntity extends BasePgEntity<DictDeviceEntity> implements ToData<DictDevice> {
    /**
     * 租户Id
     */
    @Column(name = HsModelConstants.GENERAL_TENANT_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID tenantId;

    /**
     * 编码
     */
    @Column(name = HsModelConstants.DICT_DEVICE_CODE)
    private String code;

    /**
     * 名称
     */
    @Column(name = HsModelConstants.DICT_DEVICE_NAME)
    private String name;

    /**
     * 类型
     */
    @Column(name = HsModelConstants.DICT_DEVICE_TYPE)
    private String type;

    /**
     * 供应商
     */
    @Column(name = HsModelConstants.DICT_DEVICE_SUPPLIER)
    private String supplier;

    /**
     * 型号
     */
    @Column(name = HsModelConstants.DICT_DEVICE_MODEL)
    private String model;

    /**
     * 版本号
     */
    @Column(name = HsModelConstants.DICT_DEVICE_VERSION)
    private String version;

    /**
     * 保修期
     */
    @Column(name = HsModelConstants.DICT_DEVICE_WARRANTY_PERIOD)
    private String warrantyPeriod;

    /**
     * 备注
     */
    @Column(name = HsModelConstants.DICT_DEVICE_COMMENT)
    private String comment;

    /**
     * 图标
     */
    @Column(name = HsModelConstants.DICT_DEVICE_ICON)
    private String icon;

    /**
     * 图片
     */
    @Column(name = HsModelConstants.DICT_DEVICE_PICTURE)
    private String picture;

    /**
     * 是否默认
     */
    @Column(name = HsModelConstants.DICT_DEVICE_IS_DEFAULT)
    private Boolean isDefault;

    /**
     * 是否核心
     */
    @Column(name = HsModelConstants.DICT_DEVICE_IS_CORE)
    private Boolean isCore;

    /**
     * 额定产能
     */
    @Column(name = HsModelConstants.DICT_DEVICE_RATED_CAPACITY)
    private String ratedCapacity;

    public DictDeviceEntity() {
    }

    public DictDeviceEntity(UUID id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public DictDeviceEntity(UUID id, String name, boolean isDefault) {
        super();
        this.id = id;
        this.name = name;
        this.isDefault = isDefault;
    }

    public DictDeviceEntity(UUID id, String name, boolean isDefault, String model) {
        super();
        this.id = id;
        this.name = name;
        this.isDefault = isDefault;
        this.model = model;
    }

    public DictDeviceEntity(DictDevice common) {
        if (common.getId() != null)
            this.id = UUID.fromString(common.getId());
        if (common.getTenantId() != null)
            this.tenantId = UUID.fromString(common.getTenantId());
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
        if (common.getIsDefault() != null)
            this.isDefault = common.getIsDefault();
        else
            this.isDefault = Boolean.FALSE;
        if (common.getIsCore() != null)
            this.isCore = common.getIsCore();
        else
            this.isCore = Boolean.FALSE;
        this.ratedCapacity = common.getRatedCapacity().stripTrailingZeros().toPlainString();

        this.setCreatedTimeAndCreatedUser(common);
    }

    /**
     * to data
     */
    public DictDevice toData() {
        DictDevice common = new DictDevice();
        common.setId(id.toString());
        if (tenantId != null) {
            common.setTenantId(tenantId.toString());
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
        common.setIsDefault(Objects.requireNonNullElse(isDefault, false));
        common.setIsCore(Objects.requireNonNullElse(isCore, false));
        common.setRatedCapacity(new BigDecimal(Objects.requireNonNullElse(ratedCapacity, "0")));

        common.setCreatedTime(createdTime);
        common.setCreatedUser(createdUser);
        common.setUpdatedTime(updatedTime);
        common.setUpdatedUser(updatedUser);
        return common;
    }
}
