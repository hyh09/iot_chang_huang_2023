package org.thingsboard.server.common.data.productionline;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductionLine{

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



    public ProductionLine() {
        super();
    }

}
