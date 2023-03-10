package org.thingsboard.server.common.data.productionline;

import lombok.Data;
import org.thingsboard.server.common.data.HasCustomerId;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.HasOtaPackage;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.OtaPackageId;

import java.util.List;
import java.util.UUID;

@Data
public class ProductionLine implements HasName,HasCustomerId, HasOtaPackage {

    private UUID id;

    private UUID workshopId;

    private UUID factoryId;

    private String code;

    private String name;

    private String logoIcon;

    private String logoImages;

    private String bgImages;

    private String adress;

    private String longitude;

    private String latitude;

    private String postalCode;

    private String mobile;

    private String email;

    private UUID adminUserId;

    private String adminUserName;

    private String remark;

    private UUID tenantId;
    private long createdTime;
    private UUID createdUser;
    private long updatedTime;
    private UUID updatedUser;
    private String delFlag;
    private Integer sort;

    /**********************************以下是非数据库字段***************************************/
    //工厂名称
    private String factoryName;
    //车间名称
    private String workshopName;
    private List<UUID> workshopIds;

    /**********************************以上是非数据库字段***************************************/



    public ProductionLine(){
    }
    public ProductionLine(UUID id) {
        this.id = id;
    }
    public ProductionLine (Factory factory,List<UUID> workshopIds){
        this.setWorkshopIds(workshopIds);
        this.setTenantId(factory.getTenantId());
        this.setName(factory.getProductionLineName());
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
