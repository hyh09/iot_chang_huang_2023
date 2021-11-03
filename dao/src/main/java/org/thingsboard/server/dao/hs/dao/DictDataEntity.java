package org.thingsboard.server.dao.hs.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.dao.model.ToData;
import org.thingsboard.server.dao.util.mapping.JsonStringType;
import org.thingsboard.server.dao.hs.entity.po.DictData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = HsModelConstants.DICT_DATA_TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
public class DictDataEntity extends BasePgEntity<DictDataEntity> implements ToData<DictData> {
    /**
     * 租户Id
     */
    @Column(name = HsModelConstants.GENERAL_TENANT_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID tenantId;

    /**
     * 编码
     */
    @Column(name = HsModelConstants.DICT_DATA_CODE)
    private String code;

    /**
     * 名称
     */
    @Column(name = HsModelConstants.DICT_DATA_NAME)
    private String name;

    /**
     * 类型
     */
    @Column(name = HsModelConstants.DICT_DATA_TYPE)
    private String type;

    /**
     * 单位
     */
    @Column(name = HsModelConstants.DICT_DATA_UNIT)
    private String unit;

    /**
     * 备注
     */
    @Column(name = HsModelConstants.DICT_DATA_COMMENT)
    private String comment;

    /**
     * 图标
     */
    @Column(name = HsModelConstants.DICT_DATA_ICON)
    private String icon;

    /**
     * 图片
     */
    @Column(name = HsModelConstants.DICT_DATA_PICTURE)
    private String picture;

    public DictDataEntity() {
    }

    public DictDataEntity(DictData common) {
        if (common.getId() != null)
            this.id = UUID.fromString(common.getId());
        if (common.getTenantId() != null)
            this.tenantId = UUID.fromString(common.getTenantId());
        this.code = common.getCode();
        this.name = common.getName();
        this.type = common.getType();
        this.unit = common.getUnit();
        this.comment = common.getComment();
        this.icon = common.getIcon();
        this.picture = common.getPicture();

        this.setCreatedTimeAndCreatedUser(common);
    }

    /**
     * to data
     */
    public DictData toData() {
        DictData dictData = new DictData();
        dictData.setId(id.toString());
        if (tenantId != null) {
            dictData.setTenantId(tenantId.toString());
        }
        dictData.setCode(code);
        dictData.setName(name);
        dictData.setType(type);
        dictData.setUnit(unit);
        dictData.setComment(comment);
        dictData.setIcon(icon);
        dictData.setPicture(picture);

        dictData.setCreatedTime(createdTime);
        dictData.setCreatedUser(createdUser);
        dictData.setUpdatedTime(updatedTime);
        dictData.setUpdatedUser(updatedUser);
        return dictData;
    }
}
