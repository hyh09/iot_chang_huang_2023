package org.thingsboard.server.common.data.svc.tenant;

import org.thingsboard.server.common.data.Tenant;

/**
 * @program: thingsboard
 * @description: 租户的经纬度
 * @author: HU.YUNHUI
 * @create: 2021-12-02 14:36
 **/
public interface GeographicalSvc {

     void saveCoordinate(Tenant savedTenant );
}
