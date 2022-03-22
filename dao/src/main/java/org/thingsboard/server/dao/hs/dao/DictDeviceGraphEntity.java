package org.thingsboard.server.dao.hs.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.dao.hs.entity.po.DictDeviceGraph;
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
@Table(name = HsModelConstants.DICT_DEVICE_GRAPH_TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
public class DictDeviceGraphEntity extends BasePgEntity<DictDeviceGraphEntity> implements ToData<DictDeviceGraph> {
    /**
     * 设备字典Id
     */
    @Column(name = HsModelConstants.DICT_DEVICE_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID dictDeviceId;

    /**
     * 名称
     */
    @Column(name = HsModelConstants.DICT_DEVICE_GRAPH_NAME)
    private String name;

    /**
     * 是否显示图表
     */
    @Column(name = HsModelConstants.DICT_DEVICE_GRAPH_ENABLE)
    private Boolean enable;


    public DictDeviceGraphEntity() {
    }

    public DictDeviceGraphEntity(DictDeviceGraph common) {
        this.id = common.getId();
        this.dictDeviceId = common.getDictDeviceId();
        this.name = common.getName();
        this.enable = common.getEnable();
        this.setCreatedTimeAndCreatedUser(common);
    }

    /**
     * to data
     */
    public DictDeviceGraph toData() {
        DictDeviceGraph common = new DictDeviceGraph();
        common.setId(id);
        common.setDictDeviceId(dictDeviceId);
        common.setName(name);
        common.setEnable(enable);

        common.setCreatedTime(createdTime);
        common.setCreatedUser(createdUser);
        common.setUpdatedTime(updatedTime);
        common.setUpdatedUser(updatedUser);
        return common;
    }
}
