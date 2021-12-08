package org.thingsboard.server.dao.sql.role.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SQLQuery;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.tskv.MaxTsVo;
import org.thingsboard.server.dao.model.sqlts.timescale.ts.TimescaleTsKvCompositeKey;
import org.thingsboard.server.dao.sql.role.entity.EffectMaxValueKvEntity;
import org.thingsboard.server.dao.sql.role.entity.EffectTsKvEntity;
import org.thingsboard.server.dao.sql.role.service.TenantSysRoleService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2021-12-07 11:42
 **/
@Slf4j
@Repository
public class EffectMaxValueKvRepository {


    @PersistenceContext
    private EntityManager entityManager;




    private  String SELECT_SQL_10="with tabl1 as ( " +
            "select (to_number(max(substring(concat(long_v, " +
            "                                           dbl_v," +
            "                                           str_v," +
            "                                           json_v)," +
            "                                    E'(\\\\-?\\\\d+\\\\.?\\\\d*)')),'9999999.99' ))  as value1 from  ts_kv " +
            "where 1=1 ";


    private  String SUMSQL="    group  by  entity_id) " +
            "            select cast((value1) as VARCHAR) as maxValue from  tabl1  ";



    public  String querySum(MaxTsVo maxTsVo)
    {

        Query query = null;
        StringBuffer  sql = new StringBuffer();
        Map<String, Object> param = new HashMap<>();
        sql.append(SELECT_SQL_10);
        sql.append(" and  entity_id in ( select id   from  device d1  where 1=1 ");
        if(maxTsVo.getTenantId() != null)
        {
            sql.append(" and  d1.tenant_id = :tenantId");
            param.put("tenantId",maxTsVo.getTenantId());
        }
        if(maxTsVo.getFactoryId() != null)
        {
            sql.append(" and  d1.factory_id = :factoryId");
            param.put("factoryId",maxTsVo.getFactoryId());
        }
        if(maxTsVo.getWorkshopId() != null)
        {
            sql.append(" and  d1.workshop_id = :workshopId");
            param.put("workshopId",maxTsVo.getWorkshopId());
        }
        if(maxTsVo.getProductionLineId() != null)
        {
            sql.append(" and  d1.production_line_id = :productionLineId");
            param.put("productionLineId",maxTsVo.getProductionLineId());
        }
        if(!maxTsVo.getCapSign() ) {
            sql.append(" and  d1.flg = :flg");
            param.put("flg",maxTsVo.getCapSign());
        }
        sql.append(" ) ");
        sql.append(" and  key  in (select  key_id  from  ts_kv_dictionary  ts  where  ts.key = :key ) ");
        param.put("key",maxTsVo.getKey());


        sql.append(SUMSQL);

        query= entityManager.createNativeQuery(sql.toString(),"effectMaxValueKvEntityMap");
        System.out.println("==param==>"+param);
        if(!CollectionUtils.isEmpty(param)) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        List<EffectMaxValueKvEntity> entityList=query.getResultList();
        log.info("打印的结果entityListentityList:{}",entityList);
        if(CollectionUtils.isEmpty(entityList))
        {
            return  "0";
        }
        return getTotalValue(entityList);

    }

    private  String getTotalValue(List<EffectMaxValueKvEntity> effectTsKvEntities)
    {

        BigDecimal invoiceAmount = effectTsKvEntities.stream().map(EffectMaxValueKvEntity::getMaxValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                BigDecimal::add);
        return  invoiceAmount.stripTrailingZeros().toPlainString();
    }



}
