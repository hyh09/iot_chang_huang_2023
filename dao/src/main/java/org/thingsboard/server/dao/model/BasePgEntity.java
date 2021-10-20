package org.thingsboard.server.dao.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public abstract class BasePgEntity<D> extends BaseSqlEntity<D>{
    /**
     * 创建人
     */
    @Column(name = ModelConstants.GENERAL_CREATED_USER)
    protected String createdUser;

    /**
     * 创建时间
     */
    @Column(name = ModelConstants.GENERAL_UPDATED_TIME)
    protected long updatedTime;

    /**
     * 更新人
     */
    @Column(name = ModelConstants.GENERAL_UPDATED_USER)
    protected String updatedUser;
}
