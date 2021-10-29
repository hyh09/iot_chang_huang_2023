package org.thingsboard.server.hs.dao;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.hs.entity.po.BasePO;

import javax.persistence.*;
import java.util.UUID;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BasePgEntity<D> {

    /**
     * 主键Id
     */
    @Id
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @GeneratedValue(generator = "uuid-gen")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @Column(name = HsModelConstants.GENERAL_ID, columnDefinition = "uuid")
    protected UUID id;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = HsModelConstants.GENERAL_CREATED_TIME)
    protected long createdTime;

    /**
     * 创建人
     */
    @CreatedBy
    @Column(name = HsModelConstants.GENERAL_CREATED_USER)
    protected String createdUser;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = HsModelConstants.GENERAL_UPDATED_TIME)
    protected long updatedTime;

    /**
     * 更新人
     */
    @LastModifiedBy
    @Column(name = HsModelConstants.GENERAL_UPDATED_USER)
    protected String updatedUser;

    /**
     * 回填数据
     */
    public <T extends BasePO> void setCreatedTimeAndCreatedUser(T t) {
        if (t.getCreatedTime() != null && t.getCreatedTime() > 0)
            this.createdTime = t.getCreatedTime();
        if (t.getCreatedUser() != null && !StringUtils.isBlank(t.getCreatedUser())) {
            this.createdUser = t.getCreatedUser();
        }
    }
}
