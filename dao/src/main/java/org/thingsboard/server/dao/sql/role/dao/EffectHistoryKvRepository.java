package org.thingsboard.server.dao.sql.role.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SQLQuery;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.vo.QueryTsKvHisttoryVo;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: thingsboard
 * @description: 历史数据的
 * @author: HU.YUNHUI
 * @create: 2021-11-18 14:15
 **/
@Slf4j
@Repository
public class EffectHistoryKvRepository {
    @PersistenceContext
    private EntityManager entityManager;


    private  String  with_sql=" with table1 as ( select (ts / 60000) as time1, entity_id, key, ts,bool_v,long_v,substring(concat(long_v,dbl_v,str_v,json_v),E\'(\\\\-?\\\\d+\\\\.?\\\\d*)\') as value\n" +
            "    from ts_kv   where entity_id = :entityId  and  ts>=:startTime and ts<=:endTime  and  key in  (select  key_id  from  ts_kv_dictionary where key  in (:ids) ) ) ";
    private  String select_sql=" select time1, min(ts) as ts";
    private  String from_sql= " from  table1 t1   group by  t1.time1 order by time1  ";

    public Page<Map> queryEntity(QueryTsKvHisttoryVo queryTsKvVo, Pageable pageable)
    {

        boolean enablePage = false;
        if(pageable != null ){
            if(pageable.getPageSize() != Integer.MAX_VALUE){
                enablePage = true;
            }
        }
        Query countQuery = null;
        Query query = null;
        List<Map> mapList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();

        StringBuffer  sql = new StringBuffer();
        sql.append(with_sql);
        param.put("entityId",queryTsKvVo.getDeviceId());
        param.put("ids",queryTsKvVo.getKeys());
        param.put("startTime",queryTsKvVo.getStartTime());
        param.put("endTime",queryTsKvVo.getEndTime());
        sql.append(select_sql);
        queryTsKvVo.getKeys().stream().forEach(str->{
            sql.append(" , ").append("MAX(case when key= (select  key_id  from ts_kv_dictionary  where  key= ").append("\'").append(str).append( "\' ) then cast(value as VARCHAR) else '0' end) as ").append(str);
        });
        sql.append(from_sql);
        String sqlCount = "select count(*) from (" + sql + ") t_count_0";

        if(StringUtils.isNotEmpty(queryTsKvVo.getSortOrder()))
        {
            sql.append(" "+queryTsKvVo.getSortOrder()+" ");
        }
        countQuery = entityManager.createNativeQuery(sqlCount).unwrap(NativeQuery.class);
        query= entityManager.createNativeQuery(sql.toString());

        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        if(!CollectionUtils.isEmpty(param)) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
                countQuery.setParameter(entry.getKey(), entry.getValue());

            }
        }
        Long totalObj = 0l;
        if(enablePage){
            totalObj = Long.parseLong(countQuery.getSingleResult().toString());
            query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize() ).setMaxResults(pageable.getPageSize());
        }
        List<Map> list = query.getResultList();
        Page<Map> page = new PageImpl<>(list, pageable, totalObj);
        return  page;
    }
}
