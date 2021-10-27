package org.thingsboard.server.common.data.productionline;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.SearchTextBasedWithAdditionalInfo;
import org.thingsboard.server.common.data.id.productionline.ProductionLineId;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductionLine extends SearchTextBasedWithAdditionalInfo<ProductionLineId> {

    private UUID workshopId;

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

    public ProductionLine() {
        super();
    }

    public ProductionLine(ProductionLineId id) {
        super(id);
    }

    @Override
    public String getSearchText() {
        return null;
    }

}
