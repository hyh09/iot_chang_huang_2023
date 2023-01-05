package org.thingsboard.server.common.data;

import lombok.Data;

import java.util.UUID;

@Data
public class MesDeviceRelation {

    private UUID deviceId;

    protected UUID mesDeviceId;

    private UUID factoryId;

    private UUID workshopId;

    private UUID productionLineId;

    private UUID tenantId;


}
