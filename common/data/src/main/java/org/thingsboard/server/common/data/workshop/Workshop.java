package org.thingsboard.server.common.data.workshop;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.SearchTextBasedWithAdditionalInfo;
import org.thingsboard.server.common.data.id.workshop.WorkshopId;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class Workshop extends SearchTextBasedWithAdditionalInfo<WorkshopId> {

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

    public Workshop(WorkshopId id) {
        super(id);
    }

    @Override
    public String getSearchText() {
        return null;
    }

}
