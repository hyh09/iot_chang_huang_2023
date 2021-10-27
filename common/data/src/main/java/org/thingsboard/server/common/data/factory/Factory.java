package org.thingsboard.server.common.data.factory;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.SearchTextBasedWithAdditionalInfo;
import org.thingsboard.server.common.data.id.factory.FactoryId;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class Factory extends SearchTextBasedWithAdditionalInfo<FactoryId> {

//    @ApiModelProperty("工厂标识")
//    private UUID id;

    private String code;

    private String name;

    private String logoIcon;

    private String logoImages;

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

    public Factory() {
        super();
    }

    public Factory(FactoryId id) {
        super(id);
    }

    @Override
    public String getSearchText() {
        return null;
    }

}
