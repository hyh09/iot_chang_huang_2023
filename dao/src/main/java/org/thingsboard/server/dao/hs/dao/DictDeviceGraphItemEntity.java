package org.thingsboard.server.dao.hs.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.dao.hs.entity.enums.DictDevicePropertyTypeEnum;
import org.thingsboard.server.dao.hs.entity.po.DictDeviceGraphItem;
import org.thingsboard.server.dao.model.ToData;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.util.UUID;

/**
 * 设备字典图表实体类
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = HsModelConstants.DICT_DEVICE_GRAPH_ITEM_TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
public class DictDeviceGraphItemEntity extends BasePgEntity<DictDeviceGraphItemEntity> implements ToData<DictDeviceGraphItem> {
    /**
     * 设备字典Id
     */
    @Column(name = HsModelConstants.DICT_DEVICE_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID dictDeviceId;

    /**
     * 属性Id
     */
    @Column(name = HsModelConstants.DICT_DEVICE_GRAPH_ITEM_PROPERTY_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID propertyId;

    /**
     * 属性类型
     */
    @Column(name = HsModelConstants.DICT_DEVICE_GRAPH_ITEM_PROPERTY_TYPE)
    private String propertyType;

    /**
     * 图表Id
     */
    @Column(name = HsModelConstants.DICT_DEVICE_GRAPH_ITEM_GRAPH_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID graphId;

    /**
     * 排序
     */
    @Column(name = HsModelConstants.DICT_DEVICE_GRAPH_ITEM_SORT)
    private Integer sort;

    public DictDeviceGraphItemEntity() {
    }

    public DictDeviceGraphItemEntity(DictDeviceGraphItem common) {
        this.id = common.getId();
        this.dictDeviceId = common.getDictDeviceId();
        this.propertyId = common.getPropertyId();
        this.propertyType = common.getPropertyType().getCode();
        this.graphId = common.getGraphId();
        this.sort = common.getSort();
        this.setCreatedTimeAndCreatedUser(common);
    }

    /**
     * to data
     */
    public DictDeviceGraphItem toData() {
        DictDeviceGraphItem common = new DictDeviceGraphItem();
        common.setId(id);
        common.setDictDeviceId(dictDeviceId);
        common.setPropertyId(propertyId);
        common.setPropertyType(DictDevicePropertyTypeEnum.valueOf(propertyType));
        common.setGraphId(graphId);
        common.setSort(sort);

        common.setCreatedTime(createdTime);
        common.setCreatedUser(createdUser);
        common.setUpdatedTime(updatedTime);
        common.setUpdatedUser(updatedUser);
        return common;
    }
}
