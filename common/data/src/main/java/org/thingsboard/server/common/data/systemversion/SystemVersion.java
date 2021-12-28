package org.thingsboard.server.common.data.systemversion;

import lombok.Data;
import org.thingsboard.server.common.data.id.TenantId;

import java.util.UUID;

@Data
public class SystemVersion {

    protected UUID id;

    private String version;

    private Long publishTime;

    private String comment;

    private UUID tenantId;

    private long createdTime;
    private UUID createdUser;
    private long updatedTime;
    private UUID updatedUser;

    public SystemVersion() {}

    public SystemVersion(UUID id) {
        this.id = id;
    }

    public SystemVersion(TenantId tenantId) {
        if(tenantId != null ){
            this.tenantId = tenantId.getId();
        }
    }
}
