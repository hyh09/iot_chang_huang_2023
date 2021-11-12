package org.thingsboard.server.common.data.workshop;

import lombok.Data;
import org.thingsboard.server.common.data.factory.Factory;

import java.util.UUID;

@Data
public class Workshop{

    private UUID id;
    private UUID factoryId;
    
    private String code;

    private String name;

    private String logoIcon;

    private String logoImages;

    private String bgImages;

    private String remark;

    private UUID tenantId;
    private long createdTime;
    private UUID createdUser;
    private long updatedTime;
    private UUID updatedUser;

    private String delFlag;

    public Workshop() {
        super();
    }

    public Workshop(UUID id) {
        this.id = id;
    }

    public Workshop (Factory factory){
        this.setName(factory.getWorkshopName());
        this.setTenantId(factory.getTenantId());
    }

}
