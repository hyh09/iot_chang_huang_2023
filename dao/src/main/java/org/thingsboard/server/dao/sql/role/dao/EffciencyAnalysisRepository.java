package org.thingsboard.server.dao.sql.role.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.TsSqlDayVo;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.sql.role.entity.CensusSqlByDayEntity;
import org.thingsboard.server.dao.sql.role.entity.EnergyEffciencyNewEntity;

import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: thingsboard
 * @description: 效能分析优化后的接口
 * @author: HU.YUNHUI
 * @create: 2021-12-22 10:27
 **/
@Slf4j
@Repository
public class EffciencyAnalysisRepository extends JpaSqlTool{


    /**pc端产能接口 */
    private  String FIND_SON_QUERY="select t1.entity_id,sum(to_number(capacity_added_value,'99999999999999999999999999.9999')) as capacity_added_value" +
            " from tb_statistical_data  t1 where   t1.ts>=:startTime AND t1.ts<=:endTime And  t1.entity_id in ( select  d1.id  from  device  d1 where 1= 1  ";
    public  static  String  SELECT_START_DEVICE =" select d1.id as entity_id,d1.dict_device_id as dictDeviceId, d1.name as deviceName,d1.picture ,d1.factory_id as factoryId ,d1.workshop_id as workshopId ,d1.production_line_id  as productionLineId  ";
    public  static  String  SELECT_TS_CAP =" ,tb.capacity_added_value  ";
    public  static  String  FROM_QUERY_CAP="    from   device  d1 left join table1 tb on  d1.id = tb.entity_id  where 1=1 " ;


    /***效能接口*/
    private  String FIND_SON_QUERY_02="select t1.entity_id,sum(to_number(capacity_added_value,'99999999999999999999999999.9999')) as capacity_added_value" +
            " ,sum(to_number(water_added_value,'99999999999999999999999999.9999')) as water_added_value,sum(to_number(electric_added_value,'99999999999999999999999999.9999')) as electric_added_value,sum(to_number(gas_added_value,'99999999999999999999999999.9999')) as gas_added_value, " +
            " min(water_first_time) as water_first_time,max(water_last_time) as water_last_time,"+
            " min(electric_first_time) as electric_first_time,max(electric_last_time) as electric_last_time,"+
            " min(gas_first_time) as gas_first_time,max(gas_last_time) as gas_last_time"+
            " from tb_statistical_data  t1 where   t1.ts>=:startTime AND t1.ts<=:endTime And  t1.entity_id in ( select  d1.id  from  device  d1 where 1= 1  ";
    public  static  String  SELECT_START_DEVICE_02 =" select d1.id as entity_id,d1.dict_device_id as dictDeviceId, d1.name as deviceName,d1.picture ,d1.factory_id as factoryId ,d1.workshop_id as workshopId ,d1.production_line_id  as productionLineId  ";

    public  static  String  SELECT_TS_CAP_02 =" ,tb.capacity_added_value,tb.water_added_value,tb.electric_added_value,tb.gas_added_value, " +
            "    tb.water_first_time,tb.water_last_time,tb.electric_first_time, tb.electric_last_time,tb.gas_first_time,tb.gas_last_time ";

    public  static  String  FROM_QUERY_CAP_02="    from   device  d1 left join table1 tb on  d1.id = tb.entity_id  where 1=1 " ;



     /***  */
     public  static  String SELECT_EVERY_DAY_SUM="select date,sum(to_number(capacity_added_value,'99999999999999999999999999.9999')) increment_capacity," +
             " sum(to_number(t.capacity_value,'99999999999999999999999999.9999')) history_capacity, sum(to_number(t.electric_added_value,'99999999999999999999999999.9999')) increment_electric,\n" +
             "       sum(to_number(t.electric_value,'99999999999999999999999999.9999')) history_electric,  sum(to_number(t.gas_added_value,'99999999999999999999999999.9999')) increment_gas,\n" +
             "       sum(to_number(t.gas_value,'99999999999999999999999999.9999')) history_gas,sum(to_number(t.water_added_value,'99999999999999999999999999.9999')) increment_water,\n" +
             "       sum(to_number(t.water_value,'99999999999999999999999999.9999')) history_water  from  tb_statistical_data t  where t.ts>= :startTime and t.entity_id in ( select id from device d1 where 1=1   \n" ;



    /**
     * 如果设备id为空，就排除产能配置的false
     * @return
     */
    public Page<EnergyEffciencyNewEntity> queryCapacity(QueryTsKvVo queryTsKvVo, PageLink pageLink)
    {

        Query query = null;
        Map<String, Object> param = new HashMap<>();
        param.put("startTime",queryTsKvVo.getStartTime());
        param.put("endTime",queryTsKvVo.getEndTime());
        StringBuffer  sonSql = new StringBuffer();

        StringBuffer  sonSql01 = new StringBuffer();
        sqlPartOnDevice(queryTsKvVo,sonSql01,param);
        if(queryTsKvVo.getDeviceId()  == null) {
            sonSql01.append(" and  d1.flg = true");
        }
        sonSql.append(sonSql01).append("  ) group by  t1.entity_id ");
        StringBuffer  sql = new StringBuffer();
        String sqlpre =" with table1  as ( "+FIND_SON_QUERY+sonSql+ " )";
        sql.append(sqlpre);
        sql.append(SELECT_START_DEVICE).append(SELECT_TS_CAP).append(FROM_QUERY_CAP);
        sql.append(sonSql01);

        Page<EnergyEffciencyNewEntity>   page = querySql(sql.toString(),param, DaoUtil.toPageable(pageLink),"energyEffciencyNewEntity_01");
    return page;



    }


    /**
     * 能耗的统计
     *   包含了产能
     *        能耗
     *            水
     *           电
     *           气
     * @param queryTsKvVo
     * @param pageLink
     * @return
     */
    public Page<EnergyEffciencyNewEntity> queryEnergy(QueryTsKvVo queryTsKvVo, PageLink pageLink)
    {
        Query query = null;
        Map<String, Object> param = new HashMap<>();
        param.put("startTime",queryTsKvVo.getStartTime());
        param.put("endTime",queryTsKvVo.getEndTime());
        StringBuffer  sonSql = new StringBuffer();

        StringBuffer  sonSql01 = new StringBuffer();
        sqlPartOnDevice(queryTsKvVo,sonSql01,param);
        sonSql.append(sonSql01).append("  ) group by  t1.entity_id ");

        StringBuffer  sql = new StringBuffer();
        String sqlpre =" with table1  as ( "+FIND_SON_QUERY_02+sonSql+ " )";
        sql.append(sqlpre);
        sql.append(SELECT_START_DEVICE_02).append(SELECT_TS_CAP_02).append(FROM_QUERY_CAP_02);
        sql.append(sonSql01);
        Page<EnergyEffciencyNewEntity>   page = querySql(sql.toString(),param, DaoUtil.toPageable(pageLink),"energyEffciencyNewEntity_02");
        return  page;
    }


    /**
     * 统计各个
     *   ###2021-12-24 用于统计昨天 今天 历史的数据
     * @param vo
     * @return
     */
    public List<CensusSqlByDayEntity> queryCensusSqlByDay(TsSqlDayVo vo)
    {
        Query query = null;
        Map<String, Object> param = new HashMap<>();
        param.put("startTime",vo.getStartTime());
        StringBuffer  sonSql = new StringBuffer();

        StringBuffer  sonSql01 = new StringBuffer();
        sqlPartOnDevice(vo.toQueryTsKvVo(),sonSql01,param);
        sonSql.append(sonSql01).append("  ) ");

        if(vo.getEndTime() != null )
        {
            sonSql01.append(" and  t.endTime = :endTime");
            param.put("endTime", vo.getEndTime());
        }

        StringBuffer  sql = new StringBuffer();
        sql.append(SELECT_EVERY_DAY_SUM).append(sonSql).append("   group by  date ");
        List<CensusSqlByDayEntity>   list  = querySql(sql.toString(),param, "censusSqlByDayEntity_01");
        return  list;
    }


    /**
     * sql片段
     * @param queryTsKvVo
     * @param sonSql01
     * @param param
     */
    private  void sqlPartOnDevice(QueryTsKvVo queryTsKvVo,StringBuffer  sonSql01,Map<String, Object> param)
    {
        if(queryTsKvVo.getTenantId() != null)
        {
            sonSql01.append(" and  d1.tenant_id = :tenantId");
            param.put("tenantId", queryTsKvVo.getTenantId());
            sonSql01.append("  and position('\"gateway\":true' in d1.additional_info)=0" );

        }
        if(queryTsKvVo.getFactoryId() != null)
        {
            sonSql01.append(" and  d1.factory_id = :factoryId");
            param.put("factoryId", queryTsKvVo.getFactoryId());
        }
        if(queryTsKvVo.getWorkshopId() != null)
        {
            sonSql01.append(" and  d1.workshop_id = :workshopId");
            param.put("workshopId", queryTsKvVo.getWorkshopId());
        }
        if(queryTsKvVo.getProductionLineId() != null)
        {
            sonSql01.append(" and  d1.production_line_id = :productionLineId");
            param.put("productionLineId", queryTsKvVo.getProductionLineId());
        }
        if(queryTsKvVo.getDeviceId()  != null) {
            sonSql01.append(" and  d1.id = :did");
            param.put("did", queryTsKvVo.getDeviceId());
        }
     }











}
