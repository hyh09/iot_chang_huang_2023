package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.thingsboard.server.common.data.devicecomponent.DeviceComponent;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * 设备字典部件实体类
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
public class AbstractDeviceComponentEntity<T> extends BaseSqlEntity<T>{
    /**
     * 设备Id
     */
    @Column(name = "device_id", columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID deviceId;

    /**
     * 父部件Id
     */
    @Column(name = "parent_id", columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID parentId;

    /**
     * 编码
     */
    @Column(name = "code")
    private String code;

    /**
     * 名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 类型
     */
    @Column(name = "type")
    private String type;

    /**
     * 供应商
     */
    @Column(name = "supplier")
    private String supplier;

    /**
     * 型号
     */
    @Column(name = "model")
    private String model;

    /**
     * 版本号
     */
    @Column(name = "version")
    private String version;

    /**
     * 保修期
     */
    @Column(name = "warranty_period")
    private String warrantyPeriod;

    /**
     * 备注
     */
    @Column(name = "comment")
    private String comment;

    /**
     * 图标
     */
    @Column(name = "icon")
    private String icon;

    /**
     * 图片
     */
    @Column(name = "picture")
    private String picture;

    @CreatedDate
    @Column(name = "created_time")
    private long createdTime;

    @Column(name = "created_user")
    private UUID createdUser;

    @LastModifiedDate
    @Column(name = "updated_time")
    private long updatedTime;

    @Column(name = "updated_user")
    private UUID updatedUser;


    public AbstractDeviceComponentEntity() {
    }

    public AbstractDeviceComponentEntity(DeviceComponent deviceComponent) {
        if (deviceComponent.getId() != null)
            this.id = deviceComponent.getId();
        if (deviceComponent.getParentId() != null)
            this.parentId = deviceComponent.getParentId();
        this.deviceId = deviceComponent.getDeviceId();
        this.code = deviceComponent.getCode();
        this.name = deviceComponent.getName();
        this.type = deviceComponent.getType();
        this.supplier = deviceComponent.getSupplier();
        this.model = deviceComponent.getModel();
        this.version = deviceComponent.getVersion();
        this.warrantyPeriod = deviceComponent.getWarrantyPeriod();
        this.comment = deviceComponent.getComment();
        this.icon = deviceComponent.getIcon();
        this.picture = deviceComponent.getPicture();
        this.createdTime = deviceComponent.getUpdatedTime();
        this.createdUser = deviceComponent.getCreatedUser();
        this.updatedTime = deviceComponent.getUpdatedTime();
        this.updatedUser = deviceComponent.getUpdatedUser();
    }

    /**
     * to data
     */
    public DeviceComponent toDeviceComponent() {
        DeviceComponent common = new DeviceComponent();
        if (id != null) {
            common.setId(id);
        }
        if (parentId != null) {
            common.setParentId(parentId);
        }
        if (deviceId != null) {
            common.setDeviceId(deviceId);
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

        common.setCreatedTime(createdTime);
        common.setCreatedUser(createdUser);
        common.setUpdatedTime(updatedTime);
        common.setUpdatedUser(updatedUser);
        return common;
    }

    @Override
    public T toData() {
        return null;
    }
}
