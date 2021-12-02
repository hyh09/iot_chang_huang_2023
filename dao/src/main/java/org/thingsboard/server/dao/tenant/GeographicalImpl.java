package org.thingsboard.server.dao.tenant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.svc.tenant.GeographicalSvc;

/**
 * @program: thingsboard
 * @description: 租户的经纬度保存
 * @author: HU.YUNHUI
 * @create: 2021-12-02 14:53
 **/
@Slf4j
@Service
public class GeographicalImpl implements GeographicalSvc {

    @Autowired
    private TenantDao tenantDao;

    @Override
    public void saveCoordinate(Tenant savedTenant) {
        log.info("查询当前的租户的经度维度的接口:{}",savedTenant);
       String parameter=  getDetailedAddress(savedTenant);

        tenantDao.updateTenantSetLatitudeAndLongitude("","",savedTenant.getUuidId());

    }



    private String getDetailedAddress(Tenant savedTenant)
    {
        if(StringUtils.isNotEmpty(savedTenant.getAddress()))
        {
            return  savedTenant.getAddress();
        }
        //国家 城市 省份 县 拼接地址
        StringBuffer  strB = new StringBuffer();
        strB.append(savedTenant.getCountry());
        strB.append(savedTenant.getState());
        strB.append(savedTenant.getCity());
        strB.append(savedTenant.getCountyLevel());
        return strB.toString();
    }
}
