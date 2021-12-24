package org.thingsboard.server.common.data.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.TenantId;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 统计每天的数据sum
 * @author: HU.YUNHUI
 * @create: 2021-12-24 10:27
 **/
@Data

@ApiModel(value = "查询产能的入参实体")
public class TsSqlDayVo extends  AbstractDeviceVo{

    //目前只取昨天的0点数据
    private  Long startTime;

    private  Long endTime;




    public   QueryTsKvVo  toQueryTsKvVo()
    {
        QueryTsKvVo  vo = new QueryTsKvVo();
        vo.setTenantId(this.tenantId);
        vo.setFactoryId(this.factoryId);
        vo.setWorkshopId(this.workshopId);
        vo.setProductionLineId(this.productionLineId);
        vo.setDeviceId(this.deviceId);
        return  vo;

    }

    /**
     * 由入参的String构造 实体
     * @return
     */
    public static TsSqlDayVo constructionTsSqlDayVo(
            String factoryId,String workshopId,String productionLineId,String deviceId
    )
    {
        TsSqlDayVo  vo =  new TsSqlDayVo();
        if(StringUtils.isNotEmpty(factoryId)) {
            vo.setFactoryId(UUID.fromString(factoryId));
        }
        if(StringUtils.isNotEmpty(workshopId)) {
            vo.setWorkshopId(UUID.fromString(workshopId));
        }
        if(StringUtils.isNotEmpty(productionLineId)) {
            vo.setProductionLineId(UUID.fromString(productionLineId));
        }
        if(StringUtils.isNotEmpty(deviceId)) {
            vo.setDeviceId(UUID.fromString(deviceId));
        }

        return vo;

    }



    public  static  TsSqlDayVo  constructionByQueryTsKvVo(QueryTsKvVo queryTsKvVo, TenantId tenantId)
    {
        TsSqlDayVo vo = new TsSqlDayVo();
        vo.setTenantId(tenantId.getId());
        vo.setFactoryId(queryTsKvVo.getFactoryId());
        vo.setWorkshopId(queryTsKvVo.getWorkshopId());
        vo.setProductionLineId(queryTsKvVo.getProductionLineId());
        vo.setDeviceId(queryTsKvVo.getDeviceId());
        return  vo;

    }


    @Override
    public String toString() {
        return "TsSqlDayVo{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", deviceId=" + deviceId +
                ", productionLineId=" + productionLineId +
                ", workshopId=" + workshopId +
                ", factoryId=" + factoryId +
                ", tenantId=" + tenantId +
                '}';
    }
}
