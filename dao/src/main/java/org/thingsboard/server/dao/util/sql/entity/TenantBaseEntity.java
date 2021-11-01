package org.thingsboard.server.dao.util.sql.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.thingsboard.server.dao.model.ModelConstants;

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
public  class TenantBaseEntity implements Serializable {

    @Id
    @Column(name = ModelConstants.ID_PROPERTY, columnDefinition = "uuid")
    protected UUID id;

    @Column(name = ModelConstants.CREATED_TIME_PROPERTY)
    protected long createdTime;

    /**
     * 修改时间
     */
    @Column(name="updated_time")
    protected long updatedTime;

    /**
     * 创建人标识
     */
    @Column(name="created_user")
    protected UUID createdUser;

    /**
     * 修改人标识
     */
    @Column(name="updated_user")
    protected UUID updatedUser;





}
