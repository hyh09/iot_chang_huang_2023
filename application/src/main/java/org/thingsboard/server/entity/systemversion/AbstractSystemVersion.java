package org.thingsboard.server.entity.systemversion;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.systemversion.SystemVersion;

import java.util.UUID;

@Data
public abstract class AbstractSystemVersion{

    public UUID id;

    @ApiModelProperty(name = "系统版本号")
    public String version;

    @ApiModelProperty(name = "发版时间")
    public Long publishTime;

    @ApiModelProperty(name = "发版内容")
    public String comment;

    @ApiModelProperty(name = "租户")
    private UUID tenantId;

    @ApiModelProperty("创建人标识")
    public UUID createdUser;
    @ApiModelProperty("创建时间")
    public long createdTime;
    @ApiModelProperty("修改时间")
    public long updatedTime;
    @ApiModelProperty("修改人")
    public UUID updatedUser;

    public AbstractSystemVersion(){}


    public AbstractSystemVersion(SystemVersion systemVersion) {
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
