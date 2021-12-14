package org.thingsboard.server.common.data.workshop;

import lombok.Data;
import org.thingsboard.server.common.data.HasCustomerId;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.HasOtaPackage;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.OtaPackageId;
import org.thingsboard.server.common.data.id.TenantId;

import java.util.List;
import java.util.UUID;

@Data
public class Workshop implements HasName,HasCustomerId, HasOtaPackage {

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
    private List<UUID> factoryIds;


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

    public Workshop (Factory factory,List<UUID> factoryIds){
        this.setFactoryIds(factoryIds);
        this.setTenantId(factory.getTenantId());
        this.setName(factory.getWorkshopName());
    }

    @Override
    public CustomerId getCustomerId() {
        return null;
    }

    @Override
    public OtaPackageId getFirmwareId() {
        return null;
    }

    @Override
    public OtaPackageId getSoftwareId() {
        return null;
    }

}
