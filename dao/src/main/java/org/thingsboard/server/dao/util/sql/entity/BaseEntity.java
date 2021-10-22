package org.thingsboard.server.dao.util.sql.entity;

import lombok.Data;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.sql.AbstractAlarmEntity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.UUID;

/**
 * 基础实体类
 *   等用到再说
 *
 */
@Data
@MappedSuperclass
public  class BaseEntity   implements Serializable {

    @Id
    @Column(name = ModelConstants.ID_PROPERTY, columnDefinition = "uuid")
    protected UUID id;

    @Column(name = ModelConstants.CREATED_TIME_PROPERTY)
    protected long createdTime;

}
