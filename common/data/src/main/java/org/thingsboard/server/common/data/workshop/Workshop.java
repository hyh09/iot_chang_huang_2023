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

    /**********************************以下是非数据库字段***************************************/
    //工厂名称
    private String factoryName;


    /**********************************以上是非数据库字段***************************************/


    public Workshop() {
        super();
    }

    public Workshop(UUID id) {
        this.id = id;
    }
    public Workshop(UUID id,UUID factoryId,UUID tenantId) {
        this.id = id;
        this.factoryId = factoryId;
        this.tenantId = tenantId;
    }

    public Workshop (Factory factory){
        this.setName(factory.getWorkshopName());
        this.setTenantId(factory.getTenantId());
    }

}
