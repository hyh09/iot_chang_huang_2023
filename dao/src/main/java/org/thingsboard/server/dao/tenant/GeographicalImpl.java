package org.thingsboard.server.dao.tenant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.baidumap.BaiduMaps;
import org.thingsboard.common.util.baidumap.BaiduMapsResponseEnum;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.svc.tenant.GeographicalSvc;

import java.util.Map;

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

    private  String status="status";
    //经度
    private static final String LONGITUDE = "longitude";
    //纬度
    private static final String LATITUDE = "latitude";

    @Override
    public void saveCoordinate(Tenant savedTenant) {
        log.info("查询当前的租户的经度维度的接口:{}",savedTenant);
       String parameter=  getDetailedAddress(savedTenant);
        Map<String,String> map =  BaiduMaps.getCoordinate(parameter);
        if(map.get(status).equals(BaiduMapsResponseEnum.SUCCESS.getCode()+"")) {
            String  latitude = map.get(LATITUDE);
            String  longitude = map.get(LONGITUDE);
            tenantDao.updateTenantSetLatitudeAndLongitude(latitude, longitude, savedTenant.getUuidId());
        }

    }


    /**
     * 地址拼接 #http://api.map.baidu.com/geocoding/v3/?address=北京市海淀区上地十街10号&output=json&ak=您的ak&callback=showLocation
     * @param savedTenant
     * @return
     */
    private String getDetailedAddress(Tenant savedTenant)
    {
//        if(StringUtils.isNotEmpty(savedTenant.getAddress()))
//        {
//            return  savedTenant.getAddress();
//        }
        //国家 城市 省份 县 拼接地址
        StringBuffer  strB = new StringBuffer();
//        if(StringUtils.isNotEmpty(savedTenant.getCountry())) {
//            strB.append(savedTenant.getCountry());
//        }
        if(StringUtils.isNotEmpty(savedTenant.getState())) {
            strB.append(savedTenant.getState());
        }
        if(StringUtils.isNotEmpty(savedTenant.getCity())) {
            strB.append(savedTenant.getCity());
        }
        if(StringUtils.isNotEmpty(savedTenant.getCountyLevel())) {
            strB.append(savedTenant.getCountyLevel());
        }
        if(StringUtils.isNotEmpty(savedTenant.getAddress())) {
            strB.append(savedTenant.getAddress());
        }
        return strB.toString();
    }
}
