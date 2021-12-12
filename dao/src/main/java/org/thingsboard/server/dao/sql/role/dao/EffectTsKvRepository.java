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



    public  static  String  SELECT_START_01=" select   table1.id as onlyKeyId ,table1.entity_id,table1.ts as ts, table1.key as key, ty.key as keyName, " +
            "  d1.name as deviceName,d1.picture ,d1.factory_id as factoryId ,d1.workshop_id as workshopId ,d1.production_line_id  as productionLineId," +
         " table1.valueLast as valueLast  ";
    public  static  String  SELECT_END_02="  table2.ts as ts2,  table2.valueLast as valueLast2" ;

    public  static  String  FROM_QUERY=" from   table1,table2,device d1,ts_kv_dictionary ty  ";
    //添加在设备表中存在
    public  static  String  WHERE_QUERY="  where table1.id=table2.id and  table1.rn = table2.rn and  table1.rn='1' and d1.id =table2.entity_id and ty.key_id = table2.key  ";




    /****产能的sql */
    public  static  String  SELECT_START_DEVICE =" select d1.id as entity_id,d1.flg ,d1.name as deviceName,d1.picture ,d1.factory_id as factoryId ,d1.workshop_id as workshopId ,d1.production_line_id  as productionLineId  ";
    public  static  String  SELECT_START_CAP=" table3.key as key,(select key from ts_kv_dictionary  where  key_id= table3.key ) as keyName," +
            " table3.onlyKeyId as onlyKeyId, table3.ts1 as ts, table3.valueLast1 as valueLast, " +
            "     table3.ts2 as ts2, table3.valueLast2  as valueLast2  ";

    public  static  String  FROM_QUERY_CAP="    from   device  d1 left join  (select table1.id  as onlyKeyId, table1.entity_id ," +
            "        table1.key as key," +
            "        table1.valueLast      as valueLast1," +
            "        table2.valueLast      as valueLast2," +
            "        table1.ts as ts1," +
            "        table2.ts  as ts2" +
            "        from  table2 ," +
            "        table1 where table1.id = table2.id and table1.rn = table2.rn and  table1.rn = '1' ) " +
            "        as table3  on  d1.id = table3.entity_id  where  1=1  ";



    /**
     * 产能
     * @param queryTsKvVo
     * @return
     */
    public  List<EffectTsKvEntity>  queryEntity(QueryTsKvVo queryTsKvVo)
    {



        Query query = null;
        Map<String, Object> param = new HashMap<>();
        StringBuffer  sonSql = new StringBuffer();

        if(StringUtils.isNotBlank(queryTsKvVo.getKey()))
        {
            sonSql.append(" and a1.key in (select  key_id  from  ts_kv_dictionary  ts  where  ts.key = :key ) ");
            param.put("key", queryTsKvVo.getKey());
        }
        sonSql.append("   and  a1.entity_id in ( select  d1.id  from  device  d1 where 1= 1  ");

        StringBuffer  sonSql01 = new StringBuffer();

        if(queryTsKvVo.getTenantId() != null)
        {
            sonSql01.append(" and  d1.tenant_id = :tenantId");
            param.put("tenantId", queryTsKvVo.getTenantId());
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
        if(queryTsKvVo.getDeviceId() == null)
        {
         //   sonSql01.append(" and  d1.flg = true");
        }else {
            sonSql01.append(" and  d1.id = :did");
            param.put("did", queryTsKvVo.getDeviceId());
        }
        sonSql.append(sonSql01).append(" )");

        StringBuffer  sql = new StringBuffer();
        String sqlpre =" with table1  as ( "+FIND_SON_QUERY+sonSql+ " ), table2  as ( "+FIND_SON_QUERY_02+ sonSql+" )";
        sql.append(sqlpre);
//        sql.append(SELECT_START_01+" , ");
//        sql.append(SELECT_END_02);
        sql.append(SELECT_START_DEVICE+ " , ").append(SELECT_START_CAP);
        sql.append(FROM_QUERY_CAP);
        sql.append(sonSql01);
        sql.append("  order by  ts2 ");
//        sql.append(WHERE_QUERY);

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
                EffectTsKvEntity.subtraction();
        });

        return  entityList;


    }




    /**
     * 能耗
     * @param queryTsKvVo
     * @return
     */
    public  List<EffectTsKvEntity>  queryEntityByKeys(QueryTsKvVo queryTsKvVo,List<String> key )
    {

        Query query = null;
        Map<String, Object> param = new HashMap<>();
        StringBuffer  sonSql = new StringBuffer();

        if(!CollectionUtils.isEmpty(key))
        {
            sonSql.append(" and a1.key in (select  key_id  from  ts_kv_dictionary  ts  where  ts.key  in (:keys)   ) ");
            param.put("keys", key);
        }
        sonSql.append("   and  a1.entity_id in ( select  d1.id  from  device  d1 where 1= 1  ");
        if(queryTsKvVo.getTenantId() != null)
        {
            sonSql.append(" and  d1.tenant_id = :tenantId");
            param.put("tenantId", queryTsKvVo.getTenantId());
        }
        if(queryTsKvVo.getFactoryId() != null)
        {
            sonSql.append(" and  d1.factory_id = :factoryId");
            param.put("factoryId", queryTsKvVo.getFactoryId());
        }
        if(queryTsKvVo.getWorkshopId() != null)
        {
            sonSql.append(" and  d1.workshop_id = :workshopId");
            param.put("workshopId", queryTsKvVo.getWorkshopId());
        }
        if(queryTsKvVo.getProductionLineId() != null)
        {
            sonSql.append(" and  d1.production_line_id = :productionLineId");
            param.put("productionLineId", queryTsKvVo.getProductionLineId());
        }

        if(queryTsKvVo.getDeviceId() != null){
            sonSql.append(" and  d1.id = :did");
            param.put("did", queryTsKvVo.getDeviceId());
        }
        sonSql.append(" )");

        StringBuffer  sql = new StringBuffer();
        String sqlpre =" with table1  as ( "+FIND_SON_QUERY+sonSql+ " ), table2  as ( "+FIND_SON_QUERY_02+ sonSql+" )";
        sql.append(sqlpre);
        sql.append(SELECT_START_01+" , ");
        sql.append(SELECT_END_02);
        sql.append(FROM_QUERY);
        sql.append(WHERE_QUERY);

        param.put("startTime",queryTsKvVo.getStartTime());
        param.put("endTime",queryTsKvVo.getEndTime());


        query= entityManager.createNativeQuery(sql.toString(),"result001");
        if(!CollectionUtils.isEmpty(param)) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }

        List<EffectTsKvEntity> entityList=query.getResultList();
        entityList.stream().forEach(EffectTsKvEntity -> {
                EffectTsKvEntity.subtraction();
        });

        return  entityList;


    }






}
