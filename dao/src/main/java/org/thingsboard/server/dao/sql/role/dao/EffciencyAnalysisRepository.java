package org.thingsboard.server.dao.sql.role.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.sql.role.entity.EnergyEffciencyNewEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
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

    @PersistenceContext
    private EntityManager entityManager;
    private  String FIND_SON_QUERY="select *  from tb_statistical_data  t1 where   t1.entity_id in ( select  d1.id  from  device  d1 where 1= 1  ";
    public  static  String  SELECT_START_DEVICE =" select d1.id as entity_id,d1.dict_device_id as dictDeviceId, d1.name as deviceName,d1.picture ,d1.factory_id as factoryId ,d1.workshop_id as workshopId ,d1.production_line_id  as productionLineId  ";
    public  static  String  SELECT_TS_CAP =" ,tb.capacity_added_value  ";

    public  static  String  FROM_QUERY_CAP="    from   device  d1 left join table1 tb on  d1.id = tb.entity_id  where 1=1 " ;


    /**
     * 如果设备id为空，就排除产能配置的false
     * @return
     */
    public Page<EnergyEffciencyNewEntity> queryCapacity(QueryTsKvVo queryTsKvVo, PageLink pageLink)
    {

        Query query = null;
        Map<String, Object> param = new HashMap<>();
        StringBuffer  sonSql = new StringBuffer();

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
            sonSql01.append("  and position('\"gateway\":true' in d1.additional_info)=0" );
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
            sonSql01.append(" and  d1.flg = true");
        }else {
            sonSql01.append(" and  d1.id = :did");
            param.put("did", queryTsKvVo.getDeviceId());
        }
        sonSql.append(sonSql01).append(" )");
        StringBuffer  sql = new StringBuffer();
        String sqlpre =" with table1  as ( "+FIND_SON_QUERY+sonSql+ " )";
        sql.append(sqlpre);
        sql.append(SELECT_START_DEVICE).append(SELECT_TS_CAP).append(FROM_QUERY_CAP);
        sql.append(sonSql01);

        Page<EnergyEffciencyNewEntity>   page = querySql(sql.toString(),param, DaoUtil.toPageable(pageLink),"energyEffciencyNewEntity_01");
    return page;



    }
}
