package org.thingsboard.server.dao.hs.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.device.profile.DeviceProfileData;
import org.thingsboard.server.dao.hs.entity.po.Init;
import org.thingsboard.server.dao.model.ToData;
import org.thingsboard.server.dao.util.mapping.JsonBinaryType;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.util.UUID;

import static org.thingsboard.server.common.data.SearchTextBasedWithAdditionalInfo.mapper;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(name = HsModelConstants.INIT_TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
public class InitEntity extends BasePgEntity<InitEntity> implements ToData<Init> {

    /**
     * 初始化数据
     */
    @Type(type = "jsonb")
    @Column(name = HsModelConstants.INIT_DATA, columnDefinition = "jsonb")
    private JsonNode initData;

    /**
     * 范围
     */
    @Column(name = HsModelConstants.INIT_SCOPE)
    private String scope;

    public InitEntity() {
    }

    public InitEntity(Init common) {
        if (common.getId() != null)
            this.id = UUID.fromString(common.getId());

        this.scope = common.getScope();
        this.initData = common.getInitData();

        this.setCreatedTimeAndCreatedUser(common);
    }


    /**
     * to data
     */
    public Init toData() {
        Init common = new Init();
        common.setId(id.toString());

        common.setInitData(initData);
        common.setScope(scope);

        common.setCreatedTime(createdTime);
        common.setCreatedUser(createdUser);
        common.setUpdatedTime(updatedTime);
        common.setUpdatedUser(updatedUser);
        return common;
    }
}
