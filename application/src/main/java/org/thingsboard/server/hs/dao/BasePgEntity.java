package org.thingsboard.server.hs.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.ToData;

import javax.persistence.*;
import java.util.UUID;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BasePgEntity<D>{

    /**
     * 主键Id
     */
    @Id
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @GeneratedValue(generator = "uuid-gen")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @Column(name = ModelConstants.ID_PROPERTY, columnDefinition = "uuid")
    protected UUID id;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = ModelConstants.CREATED_TIME_PROPERTY)
    protected long createdTime;

    /**
     * 创建人
     */
    @CreatedBy
    @Column(name = ModelConstants.GENERAL_CREATED_USER)
    protected String createdUser;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = ModelConstants.GENERAL_UPDATED_TIME)
//    @JsonIgnore
    protected long updatedTime;

    /**
     * 更新人
     */
    @LastModifiedBy
    @Column(name = ModelConstants.GENERAL_UPDATED_USER)
    protected String updatedUser;

    /**
     * 设置创建时间
     *
     * @param createdTime 创建时间
     */
    public void setCreatedTime(long createdTime) {
        if (createdTime > 0) {
            this.createdTime = createdTime;
        }
    }
//    public void setUpdatedTime(long updatedTime) {
//        if (updatedTime > 0) {
//            this.updatedTime = updatedTime;
//        }
//    }
}
