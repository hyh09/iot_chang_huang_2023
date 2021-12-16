package org.thingsboard.server.dao.sql.role.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.user.DefalutSvc;
import org.thingsboard.server.common.data.vo.tskv.parameter.TrendParameterVo;
import org.thingsboard.server.dao.sql.role.entity.EffectTsKvEntity;
import org.thingsboard.server.dao.sql.role.entity.SolidTrendLineEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: thingsboard
 * @description: 看板趋势图-实线（天维度）
 * @author: HU.YUNHUI
 * @create: 2021-12-15 13:17
 **/
@Slf4j
@Repository
public class BoardTrendChartRepository implements DefalutSvc {
    @PersistenceContext
    private EntityManager entityManager;

    private  String SELECT_SQL_01="with table1 as (\n" +
            "    select to_char(to_timestamp(k1.ts / 1000) AT TIME ZONE 'UTC-8', 'yyyy-MM-dd HH24:MI:SS') as time\n" +
            "         , floor(k1.ts / (1000 * 60 * 60 * 24))                                              as days\n" +
            "         , k1.*\n" +
            "    from ts_kv k1  where  k1.ts>= :startTime and k1.ts<=:endTime ";

    private  String  SQL_STEP_02=" )";


   private  String SELECT_SQL_03="select days,sum(va1) as sumValue ,max(ts2)  as time01,min(ts01) as mints ,max(time) as time02 from (\n" +
           "                   select entity_id, days, max(ts) as ts2,  min(ts) as ts01,(max(long_v) - min(long_v)) as va1, to_char(to_timestamp(max(ts) / 1000) AT TIME ZONE 'UTC-8', 'yyyy-MM-dd HH24:MI:SS')  as time\n" +
           "                   from table1    group by entity_id, days\n" +
           "               ) as Table2   group by  days ;";



   public List<SolidTrendLineEntity>  getSolidTrendLine(TrendParameterVo  queryVo)
   {
       Query query = null;
       Map<String, Object> param = new HashMap<>();
       StringBuffer  sonSql = new StringBuffer();
       if(StringUtils.isNotBlank(queryVo.getKey()))
       {
           sonSql.append(" and k1.key in (select  key_id  from  ts_kv_dictionary  ts  where  ts.key = :key ) ");
           param.put("key", queryVo.getKey());
       }
       sonSql.append("   and  k1.entity_id in ( select  d1.id  from  device  d1 where 1= 1  ");

       StringBuffer  sonSql01 = new StringBuffer();

       if(queryVo.getTenantId() != null)
       {
           sonSql01.append(" and  d1.tenant_id = :tenantId");
           param.put("tenantId", queryVo.getTenantId());
       }
       if(queryVo.getFactoryId() != null)
       {
           sonSql01.append(" and  d1.factory_id = :factoryId");
           param.put("factoryId", queryVo.getFactoryId());
       }
       if(queryVo.getWorkshopId() != null)
       {
           sonSql01.append(" and  d1.workshop_id = :workshopId");
           param.put("workshopId", queryVo.getWorkshopId());
       }
       sonSql.append(sonSql01).append(" )");

       StringBuffer  actualSql = new StringBuffer();
       actualSql.append(SELECT_SQL_01).append(sonSql).append(SQL_STEP_02).append(SELECT_SQL_03);
       param.put("startTime",queryVo.getStartTime());
       param.put("endTime",queryVo.getEndTime());
       query= entityManager.createNativeQuery(actualSql.toString(),"solidTrendLineEntityMap");
       System.out.println("==param==>"+param);
       if(!CollectionUtils.isEmpty(param)) {
           for (Map.Entry<String, Object> entry : param.entrySet()) {
               query.setParameter(entry.getKey(), entry.getValue());
           }
       }
       List<SolidTrendLineEntity> entityList=query.getResultList();
       return  entityList;



   }


    public List<SolidTrendLineEntity>  getDottedTrendLine(TrendParameterVo  vo){
       String sql="with  table01 as (     select    floor(t.ts / (1000 * 60 * 60 * 24)) as days,     t.*      from     tb_enery_time_gap t   where " +
               "    t.ts >= :startTime and t.ts <= :endTime and time_gap > :timeGap and t.key_name=:keyName ) select  days, sum(time_gap) as time01  from table01  group by   days";
        Query query = null;
        Map<String, Object> param = new HashMap<>();
        param.put("startTime",vo.getStartTime());
        param.put("endTime",vo.getEndTime());
        param.put("keyName",vo.getKey());
        param.put("timeGap",ENERGY_TIME_GAP);
        query= entityManager.createNativeQuery(sql.toString(),"dottedLineTrendLineEntityMap");
        System.out.println("==param==>"+param);
        if(!CollectionUtils.isEmpty(param)) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        List<SolidTrendLineEntity> entityList=query.getResultList();
        return  entityList;

   }















}
