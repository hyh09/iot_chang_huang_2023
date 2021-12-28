package org.thingsboard.server.dao.sql.role.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.NativeQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

/**
 * @program: thingsboard
 * @description: 执行sql的类
 * @author: HU.YUNHUI
 * @create: 2021-12-22 14:08
 **/
@Slf4j
@Repository
public class JpaSqlTool {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 分页的返回
     * @param sql
     * @param param
     * @param pageable
     * @param resultSetMapping
     * @param <T>
     * @return
     */
    public <T> Page<T> querySql(String sql, Map<String, Object> param,Pageable pageable,String resultSetMapping) {
        boolean enablePage = false;
        if(pageable != null ){
            if(pageable.getPageSize() != Integer.MAX_VALUE){
                enablePage = true;
            }
        }
        String sqlCount = "select count(*) from (" + sql + ") t_count_0";
        Query countQuery = null;
        Query query = null;

            countQuery = entityManager.createNativeQuery(sqlCount).unwrap(NativeQuery.class);
            query= entityManager.createNativeQuery(sql.toString(),resultSetMapping);
        if(param!= null){
            for(Map.Entry<String, ?> entry : param.entrySet()){
                if(sql.indexOf(":" + entry.getKey()) > -1 ){
                    countQuery.setParameter(entry.getKey(), entry.getValue());
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
        }
        Long totalObj = 0l;
        if(enablePage){
            totalObj = Long.parseLong(countQuery.getSingleResult().toString());
            query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize() ).setMaxResults(pageable.getPageSize());
        }
        List<T> list = query.getResultList();
        Page<T> page = new PageImpl<>(list, pageable, totalObj);
        return page;
    }


    /**
     * 查询返回list
     * @param sql
     * @param param
     * @param resultSetMapping
     * @param <T>
     * @return
     */
    public <T> List<T> querySql(String sql,Map<String, Object> param,String resultSetMapping)
    {
        Query query = null;
        query= entityManager.createNativeQuery(sql.toString(),resultSetMapping);
        if(!CollectionUtils.isEmpty(param)) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        List<T> list = query.getResultList();
        return  list;
    }



    /**
     * sql片段  设备的
     * @param queryTsKvVo
     * @param sonSql01
     * @param param
     */
    protected   void sqlPartOnDevice(QueryTsKvVo queryTsKvVo, StringBuffer  sonSql01, Map<String, Object> param)
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
