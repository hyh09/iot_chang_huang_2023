package org.thingsboard.server.dao.sql.role.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.dao.sql.role.entity.EffectTsKvEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: thingsboard
 * @description: 效能分析的统计接口（各个设备的属性差值）
 * @author: HU.YUNHUI
 * @create: 2021-11-09 09:28
 **/
@Slf4j
@Repository
public class EffectTsKvRepository {

    @PersistenceContext
    private EntityManager entityManager;


    public  static   String ORDER_BY="ASC";
    public static  String FIND_SON_QUERY="select  " +
            " row_number() OVER (PARTITION BY (CAST (concat(cast(a1.entity_id as VARCHAR ) ,'#',cast(a1.key as varchar) ) as varchar )) ORDER BY ts )  rn, " +
            "CAST (concat(cast(a1.entity_id as VARCHAR ) ,'#',cast(a1.key as varchar) ) as varchar ) id, " +
            "a1.entity_id,a1.key,a1.ts,  substring(concat(a1.long_v,a1.dbl_v,a1.str_v,a1.json_v),E'(\\\\-?\\\\d+\\\\.?\\\\d*)') as valueLast  " +
            "from ts_kv  a1  where  a1.ts >=:startTime  and  a1.ts<= :endTime";


    public static  String FIND_SON_QUERY_02="select  " +
            " row_number() OVER (PARTITION BY (CAST (concat(cast(a1.entity_id as VARCHAR ) ,'#',cast(a1.key as varchar) ) as varchar )) ORDER BY ts desc )  rn, " +
            "CAST (concat(cast(a1.entity_id as VARCHAR ) ,'#',cast(a1.key as varchar) ) as varchar ) id, " +
            "a1.entity_id,a1.key,a1.ts,  substring(concat(a1.long_v,a1.dbl_v,a1.str_v,a1.json_v),E'(\\\\-?\\\\d+\\\\.?\\\\d*)') as valueLast   " +
            "from ts_kv  a1  where  a1.ts >=:startTime  and  a1.ts<= :endTime ";


    public  static  String FIND_WITH_SQL=" ";

    public  static  String  SELECT_START_01=" select   table1.id as onlyKeyId ,table1.entity_id,table1.ts as ts, table1.key as key, ty.key as keyName, " +
            "  d1.name as deviceName,d1.factory_id as factoryId ,d1.workshop_id as workshopId ,d1.production_line_id  as productionLineId," +
         " table1.valueLast as valueLast  ";
//            " table1.bool_v as booleanValue,table1.str_v as strValue, table1.long_v as longValue , table1.dbl_v as doubleValue, table1.json_v as jsonValue  ";
    public  static  String  SELECT_END_02="  table2.ts as ts2,  table2.valueLast as valueLast2" ;
//        "table2.bool_v as bollV2,table2.str_v strV2,table2.long_v as longV2 ," +
//            " table2.dbl_v as  doubleValue2,table2.json_v as jsonValue2 ";
    public  static  String  FROM_QUERY=" from   table1,table2,device d1,ts_kv_dictionary ty  ";
    //添加在设备表中存在
    public  static  String  WHERE_QUERY="  where table1.id=table2.id and  table1.rn = table2.rn and  table1.rn='1' and d1.id =table2.entity_id and ty.key_id = table2.key  ";



    public  static  String QUERY_KEY_ID=" and table2.key in (select  key_id  from  ts_kv_dictionary  ts  where  ts.key = :key ) ";

    public  static  String QUERY_KEYS_id=" and table2.key in (select  key_id  from  ts_kv_dictionary  ts  where  ts.key in( :keys ) ) ";


    //工厂id
    public  static  String QUERY_factory__ID=" and table2.entity_id in ( select id  from  device  where  factory_id =:factoryId )  ";
    public  static  String QUERY_workshop__ID=" and table2.entity_id in ( select id  from  device  where  workshop_id =:workshopId )  ";
    public  static  String QUERY_productionLine__ID=" and table2.entity_id in ( select id  from  device  where  production_line_id = :lineId )  ";


    /**
     * 产能
     * @param queryTsKvVo
     * @return
     */
    public  List<EffectTsKvEntity>  queryEntity(QueryTsKvVo queryTsKvVo)
    {

        StringBuffer  sql = new StringBuffer();
        String sqlpre =" with table1  as ( "+FIND_SON_QUERY+ " ), table2  as ( "+FIND_SON_QUERY_02+ " )";
        sql.append(sqlpre);
        sql.append(SELECT_START_01+" , ");
        sql.append(SELECT_END_02);
        sql.append(FROM_QUERY);
        sql.append(WHERE_QUERY);

        Query query = null;
        Map<String, Object> param = new HashMap<>();

        if(StringUtils.isNotBlank(queryTsKvVo.getKey()))
        {
            sql.append(QUERY_KEY_ID);
            param.put("key", queryTsKvVo.getKey());
        }
        if(queryTsKvVo.getDeviceId() != null)
        {
            sql.append(" and table1.entity_id = :entity_id  ");
            param.put("entity_id", queryTsKvVo.getDeviceId());
        }
        if(queryTsKvVo.getFactoryId() != null)
        {
            sql.append(QUERY_factory__ID);
            param.put("factoryId", queryTsKvVo.getFactoryId());
        }

        if(queryTsKvVo.getWorkshopId() != null)
        {
            sql.append(QUERY_workshop__ID);
            param.put("workshopId", queryTsKvVo.getWorkshopId());
        }

        if(queryTsKvVo.getProductionLineId() != null)
        {
            sql.append(QUERY_productionLine__ID);
            param.put("lineId", queryTsKvVo.getProductionLineId());
        }
        param.put("startTime",queryTsKvVo.getStartTime());
        param.put("endTime",queryTsKvVo.getEndTime());


        query= entityManager.createNativeQuery(sql.toString(),"result001");
        System.out.println("==param==>"+param);
        if(!CollectionUtils.isEmpty(param)) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }

        List<EffectTsKvEntity> entityList=query.getResultList();
        log.debug("==打印的结果:==>"+entityList);
        entityList.stream().forEach(EffectTsKvEntity -> {
            if(EffectTsKvEntity.isNotEmpty())
            {
                EffectTsKvEntity.subtraction();

            }

        });

        return  entityList;


    }




    /**
     * 产能
     * @param queryTsKvVo
     * @return
     */
    public  List<EffectTsKvEntity>  queryEntityByKeys(QueryTsKvVo queryTsKvVo,List<String> key )
    {

        StringBuffer  sql = new StringBuffer();
        String sqlpre =" with table1  as ( "+FIND_SON_QUERY+ " ), table2  as ( "+FIND_SON_QUERY_02+ " )";
        sql.append(sqlpre);
        sql.append(SELECT_START_01+" , ");
        sql.append(SELECT_END_02);
        sql.append(FROM_QUERY);
        sql.append(WHERE_QUERY);

        Query query = null;
        Map<String, Object> param = new HashMap<>();

        if(!CollectionUtils.isEmpty(key))
        {
            sql.append(QUERY_KEYS_id);
            param.put("keys", key);
        }
        if(queryTsKvVo.getDeviceId() != null)
        {
            sql.append(" and table1.entity_id = :entity_id  ");
            param.put("entity_id", queryTsKvVo.getDeviceId());
        }
        if(queryTsKvVo.getFactoryId() != null)
        {
            sql.append(QUERY_factory__ID);
            param.put("factoryId", queryTsKvVo.getFactoryId());
        }

        if(queryTsKvVo.getWorkshopId() != null)
        {
            sql.append(QUERY_workshop__ID);
            param.put("workshopId", queryTsKvVo.getWorkshopId());
        }

        if(queryTsKvVo.getProductionLineId() != null)
        {
            sql.append(QUERY_productionLine__ID);
            param.put("lineId", queryTsKvVo.getProductionLineId());
        }
        param.put("startTime",queryTsKvVo.getStartTime());
        param.put("endTime",queryTsKvVo.getEndTime());


        query= entityManager.createNativeQuery(sql.toString(),"result001");
        System.out.println("==param==>"+param);
        if(!CollectionUtils.isEmpty(param)) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }

        List<EffectTsKvEntity> entityList=query.getResultList();
        log.debug("==打印的结果:==>"+entityList);
        entityList.stream().forEach(EffectTsKvEntity -> {
            if(EffectTsKvEntity.isNotEmpty())
            {
                EffectTsKvEntity.subtraction();

            }

        });

        return  entityList;


    }






}
