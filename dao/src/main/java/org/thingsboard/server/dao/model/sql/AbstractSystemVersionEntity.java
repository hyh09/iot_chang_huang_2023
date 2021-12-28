package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.common.data.systemversion.SystemVersion;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractSystemVersionEntity <T extends SystemVersion> extends BaseSqlEntity<T> {

    @Column(name = "version")
    private String version;

    @Column(name = "publish_time")
    private Long publishTime;

    @Column(name = "comment")
    private String comment;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "created_time")
    private long createdTime;

    @Column(name = "created_user")
    private UUID createdUser;

    @Column(name = "updated_time")
    private long updatedTime;

    @Column(name = "updated_user")
    private UUID updatedUser;


    public AbstractSystemVersionEntity(){}

    public AbstractSystemVersionEntity(AbstractSystemVersionEntity<T> abstractSystemVersionEntity) {
        if(abstractSystemVersionEntity.getId() != null){
            this.id = abstractSystemVersionEntity.getId();
        }
        this.version = abstractSystemVersionEntity.getVersion();
        this.publishTime = abstractSystemVersionEntity.getPublishTime();
        this.comment = abstractSystemVersionEntity.getComment();
        this.tenantId = abstractSystemVersionEntity.getTenantId();
        this.createdTime = abstractSystemVersionEntity.getCreatedTime();
        this.createdUser = abstractSystemVersionEntity.getCreatedUser();
        this.updatedTime = abstractSystemVersionEntity.getUpdatedTime();
        this.updatedUser = abstractSystemVersionEntity.getUpdatedUser();
    }

    public AbstractSystemVersionEntity(SystemVersion systemVersion) {
        if(systemVersion.getId() != null){
            this.id = systemVersion.getId();
        }
        this.version = systemVersion.getVersion();
        this.publishTime = systemVersion.getPublishTime();
        this.comment = systemVersion.getComment();
        this.tenantId = systemVersion.getTenantId();
        this.createdTime = systemVersion.getCreatedTime();
        this.createdUser = systemVersion.getCreatedUser();
        this.updatedTime = systemVersion.getUpdatedTime();
        this.updatedUser = systemVersion.getUpdatedUser();
    }


    public SystemVersion toSystemVersion(){
        SystemVersion systemVersion = new SystemVersion(id);
        systemVersion.setVersion(version);
        systemVersion.setPublishTime(publishTime);
        systemVersion.setComment(comment);
        systemVersion.setTenantId(tenantId);
        systemVersion.setCreatedTime(createdTime);
        systemVersion.setCreatedUser(createdUser);
        systemVersion.setUpdatedTime(updatedTime);
        systemVersion.setUpdatedUser(updatedUser);
        return systemVersion;
    }
}
