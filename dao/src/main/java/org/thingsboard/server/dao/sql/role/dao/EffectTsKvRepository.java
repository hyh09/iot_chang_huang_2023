package org.thingsboard.server.dao.sql.role.dao;

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
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 效能分析的统计接口（各个设备的属性差值）
 * @author: HU.YUNHUI
 * @create: 2021-11-09 09:28
 **/
//@Component
@Repository
//@TimescaleDBTsOrTsLatestDao
public class EffectTsKvRepository {

    @PersistenceContext
    private EntityManager entityManager;


    public  static   String ORDER_BY="ASC";
    public static  String FIND_SON_QUERY="select  " +
            " row_number() OVER (PARTITION BY (CAST (concat(cast(a1.entity_id as VARCHAR ) ,'#',cast(a1.key as varchar) ) as varchar )) ORDER BY ts )  rn, " +
            "CAST (concat(cast(a1.entity_id as VARCHAR ) ,'#',cast(a1.key as varchar) ) as varchar ) id, " +
            "a1.entity_id,a1.key,a1.ts,a1.long_v ,a1.dbl_v,a1.json_v,a1.bool_v, a1.str_v  " +
            "from ts_kv  a1";


    public static  String FIND_SON_QUERY_02="select  " +
            " row_number() OVER (PARTITION BY (CAST (concat(cast(a1.entity_id as VARCHAR ) ,'#',cast(a1.key as varchar) ) as varchar )) ORDER BY ts desc )  rn, " +
            "CAST (concat(cast(a1.entity_id as VARCHAR ) ,'#',cast(a1.key as varchar) ) as varchar ) id, " +
            "a1.entity_id,a1.key,a1.ts,a1.long_v ,a1.dbl_v,a1.json_v,a1.bool_v, a1.str_v  " +
            "from ts_kv  a1 ";


    public  static  String FIND_WITH_SQL=" ";

    public  static  String  SELECT_START_01=" select table1.id as onlyKeyId ,table1.entity_id,table1.ts as ts, table1.key as key," +
            " table1.bool_v as booleanValue,table1.str_v as strValue, table1.long_v as longValue , table1.dbl_v as doubleValue, table1.json_v as jsonValue  ";
    public  static  String  SELECT_END_02="  table2.ts as ts2, table2.bool_v as bollV2,table2.str_v strV2,table2.long_v as longV2 ," +
            " table2.dbl_v as  doubleValue2,table2.json_v as jsonValue2 ";
    public  static  String  FROM_QUERY=" from   table1,table2 ";
    public  static  String  WHERE_QUERY="  where table1.id=table2.id and  table1.rn = table2.rn and  table1.rn='1' ";


    public  List<EffectTsKvEntity>  queryEntity(QueryTsKvVo vo)
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
        sql.append(" and table1.entity_id = :entity_id  ");
        sql.append(" and table1.key = 17  ");
        System.out.println("====>"+sql);
        param.put("entity_id", UUID.fromString("9689acb0-2c9f-11ec-a563-6f6ff066531c"));

        query= entityManager.createNativeQuery(sql.toString(),"result001");
        System.out.println("==param==>"+param);
        if(!CollectionUtils.isEmpty(param)) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }

        List<EffectTsKvEntity> entityList=query.getResultList();
        System.out.println("==打印的结果:==>"+entityList);
        entityList.stream().forEach(EffectTsKvEntity -> {
            if(EffectTsKvEntity.isNotEmpty())
            {
                EffectTsKvEntity.subtraction();

            }
            System.out.println("====>" + EffectTsKvEntity.getOnlyKeyId() + "##############" + EffectTsKvEntity.getEntityId()+"@@@@数字差:"+EffectTsKvEntity.getSubtractDouble()+"Long:====>"+EffectTsKvEntity.getSubtractLong());

        });

        return  entityList;


    }






}
