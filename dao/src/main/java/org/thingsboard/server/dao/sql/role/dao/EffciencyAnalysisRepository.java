package org.thingsboard.server.dao.sql.role.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.TsSqlDayVo;
import org.thingsboard.server.common.data.vo.enums.KeyTitleEnums;
import org.thingsboard.server.common.data.vo.parameter.PcTodayEnergyRaningVo;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.sql.role.entity.CensusSqlByDayEntity;
import org.thingsboard.server.dao.sql.role.entity.EnergyEffciencyNewEntity;
import org.thingsboard.server.dao.sqlts.latest.SearchTsKvLatestRepository;
import org.thingsboard.server.dao.util.StringUtilToll;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @program: thingsboard
 * @description: 效能分析优化后的接口
 * @author: HU.YUNHUI
 * @create: 2021-12-22 10:27
 **/
@Slf4j
@Repository
public class EffciencyAnalysisRepository extends JpaSqlTool {

    @Autowired
    private SearchTsKvLatestRepository searchTsKvLatestRepository;


    /**
     * pc端产能接口
     */
    private String FIND_SON_QUERY = "select t1.entity_id,sum(to_number(capacity_added_value,'99999999999999999999999999999999.9999')) as capacity_added_value" +
            " from hs_statistical_data  t1 where   t1.ts>=:startTime AND t1.ts<=:endTime And  t1.entity_id in ( select  d1.id  from  device  d1 where 1= 1  ";
    public static String SELECT_START_DEVICE = " select d1.id as entity_id,d1.dict_device_id as dictDeviceId, d1.rename as deviceName,d1.picture ,d1.factory_id as factoryId ,d1.workshop_id as workshopId ,d1.production_line_id  as productionLineId  ";
    public static String SELECT_TS_CAP = " ,tb.capacity_added_value  ";
    public static String FROM_QUERY_CAP = "    from   device  d1 left join table1 tb on  d1.id = tb.entity_id  where 1=1 ";


    /***效能接口*/
    private String FIND_SON_QUERY_02 = "select t1.entity_id,sum(to_number(capacity_added_value,'99999999999999999999999999999999.9999')) as capacity_added_value" +
            " ,sum(to_number(water_added_value,'99999999999999999999999999999999.9999')) as water_added_value,sum(to_number(electric_added_value,'99999999999999999999999999999999.9999')) as electric_added_value,sum(to_number(gas_added_value,'99999999999999999999999999999999.9999')) as gas_added_value, " +
            " min(water_first_time) as water_first_time,max(water_last_time) as water_last_time," +
            " min(electric_first_time) as electric_first_time,max(electric_last_time) as electric_last_time," +
            " min(gas_first_time) as gas_first_time,max(gas_last_time) as gas_last_time" +
            " from hs_statistical_data  t1 where   t1.ts>=:startTime AND t1.ts<=:endTime And  t1.entity_id in ( select  d1.id  from  device  d1 where 1= 1  ";
    public static String SELECT_START_DEVICE_02 = " select d1.id as entity_id,d1.dict_device_id as dictDeviceId, d1.rename as deviceName,d1.picture ,d1.factory_id as factoryId ,d1.workshop_id as workshopId ,d1.production_line_id  as productionLineId  ";

    public static String SELECT_TS_CAP_02 = " ,tb.capacity_added_value,tb.water_added_value,tb.electric_added_value,tb.gas_added_value, " +
            "    tb.water_first_time,tb.water_last_time,tb.electric_first_time, tb.electric_last_time,tb.gas_first_time,tb.gas_last_time ";

    public static String FROM_QUERY_CAP_02 = "    from   device  d1 left join table1 tb on  d1.id = tb.entity_id  where 1=1 ";


    /***  今天 昨天 历史的 总和统计*/
    public static String SELECT_EVERY_DAY_SUM = "select date,sum(to_number(capacity_added_value,'99999999999999999999999999999999.9999')) increment_capacity," +
            " sum(to_number(t.capacity_value,'99999999999999999999999999999999.9999')) history_capacity, sum(to_number(t.electric_added_value,'99999999999999999999999999999999.9999')) increment_electric,\n" +
            "       sum(to_number(t.electric_value,'99999999999999999999999999999999.9999')) history_electric,  sum(to_number(t.gas_added_value,'99999999999999999999999999999999.9999')) increment_gas,\n" +
            "       sum(to_number(t.gas_value,'99999999999999999999999999999999.9999')) history_gas,sum(to_number(t.water_added_value,'99999999999999999999999999999999.9999')) increment_water,\n" +
            "       sum(to_number(t.water_value,'99999999999999999999999999999999.9999')) history_water  from  hs_statistical_data t  where t.ts>= :startTime and t.entity_id in ( select id from device d1 where 1=1   \n";


    /***今日排行*/
    public static String TODAY_SQL_02 = " tb.water_value,tb.water_added_value,tb.electric_added_value,tb.electric_value,tb.gas_added_value,tb.gas_value ,tb.ts ";
    public static String FROM_SQL_02 = "    from   device  d1 left join hs_statistical_data tb on  d1.id = tb.entity_id  and  tb.ts>=:startTime and tb.ts<:endTime  where 1=1 ";


//    private  static  String  DEVICE_FLG_TRUE="  and  EXISTS  (select 1  from   hs_order_plan  p1  where  p1.device_id=d1.id   and enabled =true )";


    /**
     * 如果设备id为空，就排除产能配置的false
     *
     * @return
     */
    public List<EnergyEffciencyNewEntity> queryCapacityALL(QueryTsKvVo queryTsKvVo, PageLink pageLink) {

        Query query = null;
        Map<String, Object> param = new HashMap<>();
        param.put("startTime", queryTsKvVo.getStartTime());
        param.put("endTime", queryTsKvVo.getEndTime());
        StringBuffer sonSql = new StringBuffer();

        StringBuffer sonSql01 = new StringBuffer();
        sqlPartOnDevice(queryTsKvVo, sonSql01, param);
        if (queryTsKvVo.getDeviceId() == null) {
//            sonSql01.append(" and  d1.flg = true");
//             sonSql01.append(DEVICE_FLG_TRUE);
        }
        sonSql.append(sonSql01).append("  ) group by  t1.entity_id ");
        StringBuffer sql = new StringBuffer();
        String sqlpre = " with table1  as ( " + FIND_SON_QUERY + sonSql + " )";
        sql.append(sqlpre);
        sql.append(SELECT_START_DEVICE).append(SELECT_TS_CAP).append(FROM_QUERY_CAP);
        sql.append(sonSql01);

        List<EnergyEffciencyNewEntity> page = querySql(sql.toString(), param, "energyEffciencyNewEntity_01");
        return page;

    }


    /**
     * 能耗的统计
     * 包含了产能
     * 能耗
     * 水
     * 电
     * 气
     *
     * @param queryTsKvVo
     * @param pageLink
     * @return
     */
    public List<EnergyEffciencyNewEntity> queryEnergyListAll(QueryTsKvVo queryTsKvVo, PageLink pageLink) {
        Query query = null;
        Map<String, Object> param = new HashMap<>();
        param.put("startTime", queryTsKvVo.getStartTime());
        param.put("endTime", queryTsKvVo.getEndTime());
        StringBuffer sonSql = new StringBuffer();

        StringBuffer sonSql01 = new StringBuffer();
        sqlPartOnDevice(queryTsKvVo, sonSql01, param);
        sonSql.append(sonSql01).append("  ) group by  t1.entity_id ");

        StringBuffer sql = new StringBuffer();
        String sqlpre = " with table1  as ( " + FIND_SON_QUERY_02 + sonSql + " )";
        sql.append(sqlpre);
        sql.append(SELECT_START_DEVICE_02).append(SELECT_TS_CAP_02).append(FROM_QUERY_CAP_02);
        sql.append(sonSql01);
        sql.append(" ORDER BY  d1.sort ");
        List<EnergyEffciencyNewEntity> page = querySql(sql.toString(), param, "energyEffciencyNewEntity_02");
//        System.out.println("===page==>" + page);
        return page;
    }


    /**
     * 设备的排行
     * 单纯的设备维度； 不需要统计的
     *
     * @param queryTsKvVo
     * @return
     */
    public List<EnergyEffciencyNewEntity> queryEnergy(QueryTsKvVo queryTsKvVo) {
        Map<String, Object> param = new HashMap<>();
        param.put("startTime", queryTsKvVo.getStartTime());
        param.put("endTime", queryTsKvVo.getEndTime());
        StringBuffer sonSql01 = new StringBuffer();
        sqlPartOnDevice(queryTsKvVo, sonSql01, param);
        StringBuffer sql = new StringBuffer();
        sql.append("  select ")
                .append(" d1.id as entity_id,max(d1.\"rename\") as deviceName,sum(to_number(tb.water_added_value,'99999999999999999999999999.9999')) as water_added_value,")
                .append(" sum(to_number(tb.electric_added_value,'99999999999999999999999999.9999')) as electric_added_value, ")
                .append(" sum(to_number(tb.gas_added_value,'99999999999999999999999999.9999')) as gas_added_value, ")
                .append(" sum(to_number(tb.capacity_added_value,'99999999999999999999999999.9999')) as capacity_added_value, ")
                .append("  max(tb.ts) as  ts  ")
                .append(FROM_SQL_02);
        sql.append(sonSql01);
        sql.append(" GROUP BY  d1.id ");
        List<EnergyEffciencyNewEntity> entityList = querySql(sql.toString(), param, "energyEffciencyNewEntity_03");
        return entityList;
    }


    /**
     * 统计各个
     * ###2021-12-24 用于统计昨天 今天 历史的数据
     *
     * @param vo
     * @return
     */
    public List<CensusSqlByDayEntity> queryCensusSqlByDay(TsSqlDayVo vo, boolean isCap) {
        Query query = null;
        Map<String, Object> param = new HashMap<>();
        param.put("startTime", vo.getStartTime());
        StringBuffer sonSql = new StringBuffer();

        StringBuffer sonSql01 = new StringBuffer();
        sqlPartOnDevice(vo.toQueryTsKvVo(), sonSql01, param);
        if (isCap) {
//            sonSql01.append(" and  d1.flg = true");
//             sonSql01.append(DEVICE_FLG_TRUE);
        }

        sonSql.append(sonSql01).append("  ) ");

        if (vo.getEndTime() != null) {
            sonSql.append(" and  t.ts <= :endTime");
            param.put("endTime", vo.getEndTime());
        }

        StringBuffer sql = new StringBuffer();
        sql.append(SELECT_EVERY_DAY_SUM).append(sonSql).append("   group by  date ");
        List<CensusSqlByDayEntity> list = querySql(sql.toString(), param, "censusSqlByDayEntity_01");
        return list;
    }


    /**
     * 查询 能耗  产能历史数据
     *
     * @param vo
     * @param isCap 是否是产能的查询
     * @param type
     * @return
     */
    public String queryHistoricalTelemetryData(TsSqlDayVo vo, boolean isCap, String type) {

        StringBuffer sonSql01 = new StringBuffer();
        Map<String, Object> param = new HashMap<>();
        sqlPartOnDevice(vo.toQueryTsKvVo(), sonSql01, param);
        if (isCap) {
//            sonSql01.append(" and  d1.flg = true");
//              sonSql01.append(DEVICE_FLG_TRUE);
        }
        StringBuffer sql = new StringBuffer();
        sql.append("select  d1.id as entity_id  from  device d1 where  1=1 ");
        sql.append(sonSql01);
        List<CensusSqlByDayEntity> list = querySql(sql.toString(), param, "censusSqlByDayEntity_device");
        List<UUID> uuidList = list.stream().map(CensusSqlByDayEntity::getEntityId).collect(Collectors.toList());
        List<TsKvEntry> tsKvEntryList = DaoUtil.convertDataList(searchTsKvLatestRepository.findAllByEntityIdAndKey(uuidList, queryKeyName(type)));
        if (CollectionUtils.isEmpty(tsKvEntryList)) {
            return "0";
        }
        BigDecimal sum = tsKvEntryList.stream().filter(m1 -> m1.getValue() != null).map(m1 -> m1.getValue().toString()).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
        return StringUtilToll.roundUp(sum.stripTrailingZeros().toPlainString());

    }


    /**
     * 接口描述:  PC端的能耗今日排行版数据
     *
     * @param vo
     * @return
     */
    public List<CensusSqlByDayEntity> queryTodayEffceency(PcTodayEnergyRaningVo vo) {
        Map<String, Object> param = new HashMap<>();
        param.put("todayDate", vo.getDate());
        StringBuffer sql = new StringBuffer();
        StringBuffer sonSql01 = new StringBuffer();
        sqlPartOnDevice(vo.toQueryTsKvVo(), sonSql01, param);
        StringBuffer orderSql = new StringBuffer();
        sql.append(" select h1.date,h1.entity_id,d1.rename,h1.water_added_value,h1.gas_added_value,h1.electric_added_value,h1.capacity_added_value  ")
                .append(" from  hs_statistical_data h1 ,device d1")
                .append("  where h1.entity_id =d1.id  ")
                .append(" and h1.\"date\" =:todayDate")
                .append(sonSql01);
        if (vo.getType().equals("0")) {
//            sql.append(DEVICE_FLG_TRUE);
//            sql.append(" and  d1.flg = true");
            sql.append(" ORDER BY to_number(h1.capacity_added_value,'99999999999999999999999999.9999') DESC ");
            orderSql.append(" ORDER BY t2.capacity_added_value ");
        } else {
            KeyTitleEnums enums = KeyTitleEnums.getEnumsByPCCode(vo.getKeyNum());
            if (enums == KeyTitleEnums.key_water) {
                sql.append(" ORDER BY to_number(h1.water_added_value,'99999999999999999999999999.9999') DESC ");
                orderSql.append(" ORDER BY t2.water_added_value ");
            }
            if (enums == KeyTitleEnums.key_cable) {
                sql.append(" ORDER BY to_number(h1.electric_added_value,'99999999999999999999999999.9999') DESC ");
                orderSql.append(" ORDER BY t2.electric_added_value ");

            }
            if (enums == KeyTitleEnums.key_gas) {
                sql.append(" ORDER BY to_number(h1.gas_added_value,'99999999999999999999999999.9999') DESC ");
                orderSql.append(" ORDER BY t2.gas_added_value ");

            }
        }
        sql.append("  LIMIT 10 ");
        StringBuffer sqlAll = new StringBuffer();
        sqlAll.append(" with table01 as (").append(sql).append(")")
                .append(" select d1.id as entity_id ,d1.rename ,t2.date,t2.water_added_value,t2.gas_added_value,t2.electric_added_value,t2.capacity_added_value")
                .append(" from device d1 left JOIN table01 t2  on d1.id=t2.entity_id where 1=1 ");
        sqlAll.append(sonSql01);
        if (vo.getType().equals("0")) {
//            sqlAll.append(DEVICE_FLG_TRUE);
        }
        sqlAll.append(orderSql).append(" limit 10");
        List<CensusSqlByDayEntity> list = querySql(sqlAll.toString(), param, "censusSqlByDayEntity_02");
        return list;
    }


}
