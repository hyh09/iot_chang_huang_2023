package org.thingsboard.server.dao.repository;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.common.data.vo.TsSqlDayVo;
import org.thingsboard.server.dao.sql.role.dao.JpaSqlTool;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: demo-all
 * @description: 看板区间产能【租户，和工厂】
 * @author: HU.YUNHUI
 * @create: 2022-04-13 11:09
 **/
@Slf4j
@Repository
public class KanbanInervalCapacityRepository extends JpaSqlTool {




   public String capacitySumValue(TsSqlDayVo vo, Long startTime, Long endTime)
    {
        Map<String, Object> param = new HashMap<>();

        StringBuffer sql = new StringBuffer();
        sql.append(" select  sum(to_number(actual_capacity,'99999999999999999999999999.9999'))   from hs_order_plan h1 where  h1.enabled =true  ");
        StringBuffer deviveSql = new StringBuffer();
        sqlPartOnDevice(vo.toQueryTsKvVo(),deviveSql,param);
        if(StringUtils.isNotEmpty(deviveSql.toString()))
        {
            sql.append(" and  h1.device_id in (  select id  from  device  d1 where  1=1  ");
            sql.append(deviveSql.toString());
            sql.append(" ) ");
        }
        if(startTime != null)
        {
            sql.append(" and  h1.actual_end_time >= :startTime");
            param.put("startTime", startTime);
        }
        if( endTime != null)
        {
            sql.append(" and  h1.actual_start_time <= :endTime");
            param.put("endTime", endTime );
        }
        String sumValue =  queryResultStr(sql.toString(),param);
        return  sumValue;

    }
}
