package org.thingsboard.server.dao.board.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.common.data.vo.TsSqlDayVo;
import org.thingsboard.server.dao.sql.role.dao.JpaSqlTool;
import org.thingsboard.server.dao.sql.role.entity.BoardV3DeviceDitEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: thingsboard
 * @description: 设备字典的查询
 * @author: HU.YUNHUI
 * @create: 2022-03-07 11:25
 **/
@Slf4j
@Repository
public class BoardV3DeviceDictionaryReposutory extends JpaSqlTool {

    private  String  HS_DICT_DEVICE_SQL="select h1.id,h1.code,h1.name  from  hs_dict_device h1 where  1=1 ";



    /**
     *
     * @param vo
     * @return
     */
    public List<BoardV3DeviceDitEntity>  queryDeviceDictionaryByEntityVo(TsSqlDayVo vo){
        Map<String, Object> param = new HashMap<>();
        StringBuffer  sql=new StringBuffer();
        sql.append(HS_DICT_DEVICE_SQL);

        StringBuffer  sonSql01 = new StringBuffer();
        sqlPartOnDevice(vo.toQueryTsKvVo(),sonSql01,param);
        if(StringUtils.isNotEmpty(sonSql01.toString()))
        {
            sql.append(" and  h1.id in (  select  d1.dict_device_id   from device d1  where 1=1  ");
            sql.append(sonSql01);
            sql.append(")");

        }
        List<BoardV3DeviceDitEntity> resultEntity= querySql(sql.toString(),param,"boardV3DeviceDitEntity_map01");
        return  resultEntity;
    }
}
