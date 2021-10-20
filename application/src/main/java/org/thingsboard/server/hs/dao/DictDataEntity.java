package org.thingsboard.server.hs.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.dao.model.BasePgEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;
import org.thingsboard.server.hs.entity.po.DictData;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.DICT_DATA_TABLE_NAME)
public class DictDataEntity extends BasePgEntity<DictData> {
    /**
     * 租户Id
     */
    @Column(name = ModelConstants.DEVICE_TENANT_ID_PROPERTY, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID tenantId;

    /**
     * 编码
     */
    @Column(name = ModelConstants.DICT_DATA_CODE)
    private String code;

    /**
     * 名称
     */
    @Column(name = ModelConstants.DICT_DATA_NAME)
    private String name;

    /**
     * 类型
     */
    @Column(name = ModelConstants.DICT_DATA_TYPE)
    private String type;

    /**
     * 单位
     */
    @Column(name = ModelConstants.DICT_DATA_UNIT)
    private String unit;

    /**
     * 备注
     */
    @Column(name = ModelConstants.DICT_DATA_COMMENT)
    private String comment;

    /**
     * 图标
     */
    @Column(name = ModelConstants.DICT_DATA_ICON)
    private String icon;

    /**
     * 图片
     */
    @Column(name = ModelConstants.DICT_DATA_PICTURE)
    private String picture;

    public DictDataEntity() {
    }

    public DictDataEntity(DictData dictData) {
        if (dictData.getId() != null)
            this.id = UUID.fromString(dictData.getId());
        if (dictData.getTenantId() != null)
            this.tenantId = UUID.fromString(dictData.getTenantId());
        this.code = dictData.getCode();
        this.name = dictData.getName();
        this.type = dictData.getType();
        this.unit = dictData.getUnit();
        this.comment = dictData.getComment();
        this.icon = dictData.getIcon();
        this.picture = dictData.getPicture();

        this.createdUser = dictData.getCreatedUser();
        if (dictData.getCreatedTime() != null) {
            this.setCreatedTime(dictData.getCreatedTime());
        }
        this.createdUser = dictData.getCreatedUser();
        if (dictData.getUpdatedTime() != null) {
            this.setUpdatedTime(dictData.getUpdatedTime());
        }
    }

    /**
     * to data
     */
    @Override
    public DictData toData() {
        DictData dictData = new DictData();
        dictData.setId(id.toString());
        if (tenantId != null) {
            dictData.setTenantId(tenantId.toString());
        }
        dictData.setCreatedTime(createdTime);
        dictData.setCode(code);
        dictData.setName(name);
        dictData.setType(type);
        dictData.setUnit(unit);
        dictData.setComment(comment);
        dictData.setCreatedUser(createdUser);
        dictData.setUpdatedTime(updatedTime);
        dictData.setUpdatedUser(updatedUser);
        dictData.setIcon(icon);
        dictData.setPicture(picture);
        return dictData;
    }
}
