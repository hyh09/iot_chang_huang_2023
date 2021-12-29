package org.thingsboard.server.dao.sql.role.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.common.data.vo.DeviceCapacityVo;
import org.thingsboard.server.common.data.vo.tskv.parameter.TrendParameterVo;
import org.thingsboard.server.dao.sql.role.entity.EnergyChartOfBoardEntity;
import org.thingsboard.server.dao.util.StringUtilToll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: thingsboard
 * @description: 看板趋势图-实线（天维度）
 * @author: HU.YUNHUI
 * @create: 2021-12-15 13:17
 **/
@Slf4j
@Repository
public class BoardTrendChartRepositoryNewMethon extends JpaSqlTool {

    private  String  SQL_FRAGMENT_MAIN_TABLE="select t1.entity_id,t1.ts,t1.water_added_value ,t1.water_first_time,t1.water_last_time," +
            "  t1.electric_added_value,t1.electric_first_time,t1.electric_last_time,t1.gas_added_value,t1.gas_first_time,t1.gas_last_time " +
            "  from  TB_ENERGY_CHART t1 WHERE t1.ts>= :startTime and t1.ts<= :endTime ";




    public List<EnergyChartOfBoardEntity> getSolidTrendLine(TrendParameterVo queryVo) {
        Map<String, Object> param = new HashMap<>();
            param.put("startTime",queryVo.getStartTime());
            param.put("endTime",queryVo.getEndTime());
        StringBuffer  sonSql01 = new StringBuffer();
        sqlPartOnDevice(queryVo.toQueryTsKvVo(),sonSql01,param);
        StringBuffer  sql = new StringBuffer();
        sql.append(SQL_FRAGMENT_MAIN_TABLE).append(" and t1.ENTITY_ID IN (  select  d1.id  from  device  d1 where 1= 1 ").append(sonSql01).append(") ");
        List<EnergyChartOfBoardEntity>   list  = querySql(sql.toString(),param, "energyChartOfBoardEntityMap");
        return  list;

    }



    private  final  String  DEVICE_CAPACITY_VALUE_SQL="WITH table01 AS ( SELECT entity_id ,max(ts) maxTime, min(ts) minTime FROM ts_kv WHERE entity_id =:entity_id and key=:keyId AND " +
            " ts>= :startTime and ts<=:endTime  GROUP BY entity_id ) select t1.entity_id ," +
            "(select  concat(long_v,dbl_v,str_v,json_v)  from  ts_kv  where entity_id = t1.entity_id and key=:keyId and   ts=t1.maxTime limit 1 )  maxValue01,"+
            "(select  concat(long_v,dbl_v,str_v,json_v)  from  ts_kv  where entity_id = t1.entity_id and key=:keyId and   ts=t1.minTime limit 1 )  minValue02  from table01 t1 ";



    /**
     * 查询设备的产能
     */
     public String getCapacityValueByDeviceIdAndInTime(DeviceCapacityVo  vo,int keyId)
     {
         Map<String, Object> param = new HashMap<>();
         param.put("startTime",vo.getStartTime());
         param.put("endTime",vo.getEndTime());
         param.put("entity_id",vo.getId());
         param.put("keyId",keyId);

         StringBuffer  sql = new StringBuffer();
         sql.append(DEVICE_CAPACITY_VALUE_SQL);
         List<EnergyChartOfBoardEntity>   list  = querySql(sql.toString(),param, "getCapacityValueByDeviceIdAndInTime");
         if(CollectionUtils.isNotEmpty(list))
         {
           String str =   list.stream().map(m1->{
                 String  value =    StringUtilToll.sub(m1.getMaxValue01(),m1.getMinValue02());
                 return  value;
             }).collect(Collectors.joining());
           return  str;
         }
         return  "0";
     }





}








