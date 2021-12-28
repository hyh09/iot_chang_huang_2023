package org.thingsboard.server.entity.systemversion.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.common.data.systemversion.SystemVersion;
import org.thingsboard.server.entity.systemversion.AbstractSystemVersion;

@Data
@ApiModel(value = "SystemVersionVo",description = "系统版本")
public class SystemVersionVo extends AbstractSystemVersion {

    public SystemVersionVo(){}

    public SystemVersionVo(String version, long publishTime){
        this.version = version;
        this.publishTime = publishTime;
    }
    public SystemVersionVo(SystemVersion systemVersion){
        super(systemVersion);
    }
}
