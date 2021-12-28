package org.thingsboard.server.dao.sql.role.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.common.data.vo.tskv.parameter.TrendParameterVo;
import org.thingsboard.server.dao.sql.role.entity.EnergyChartOfBoardEntity;

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
public class BoardTrendChartRepositoryNewMethon extends JpaSqlTool {

    private  String  SQL_FRAGMENT_MAIN_TABLE="select t1.entity_id,t1.ts,t1.water_added_value ,t1.water_first_time,t1.water_last_time," +
            "  t1.electric_added_value,t1.electric_first_time,t1.electric_last_time,t1.gas_added_value,t1.gas_first_time,t1.gas_last_time " +
            "  from  TB_ENERGY_CHART t1 WHERE t1.ts>= :startTime and t1.ts<= :endTime ";


    public List<EnergyChartOfBoardEntity> getSolidTrendLine(TrendParameterVo queryVo) {
        Map<String, Object> param = new HashMap<>();
            param.put("startTime",queryVo.getStartTime());
            param.put("endTime",queryVo.getEndTime());
        StringBuffer  sonSql01 = new StringBuffer();
        sqlPartOnDevice(queryVo.toQueryTsKvVo(),sonSql01,param);
        StringBuffer  sql = new StringBuffer();
        sql.append(SQL_FRAGMENT_MAIN_TABLE).append(" and t1.ENTITY_ID IN (  select  d1.id  from  device  d1 where 1= 1 ").append(sonSql01).append(") ");
        List<EnergyChartOfBoardEntity>   list  = querySql(sql.toString(),param, "energyChartOfBoardEntityMap");
        return  list;

    }






}








