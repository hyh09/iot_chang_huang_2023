package org.thingsboard.server.service.userrole.sqldata;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.thingsboard.server.entity.rolemenu.InMenuByUserVo;
import org.thingsboard.server.service.userrole.SqlSplicingSvc;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SqlSplicingImpl implements SqlSplicingSvc {


    @Override
    public   SqlVo getSqlByVo(InMenuByUserVo vo)
    {
        //返回的字段
        String sqlPre="select  cast(m1.id as varchar(255)) as id ,m1.sys_menu_name as sysMenuName ,(case when c1.id is not null then '1' else '0' end) as mark   ";
        String fromPre="  from  tb_user  t1     " +
                "   left join tb_user_menu_role b1  on t1.id=b1.user_id     " +
                "   left join tb_tenant_sys_role c1 on  c1.id =b1.tenant_sys_role_id    " +
                "   left join  tb_tenant_menu_role r1  on r1.tenant_sys_role_id =c1.id                  " +
                "   left join  tb_tenant_menu m1   on m1.id = r1.tenant_menu_id where 1=1  ";
        StringBuffer whereSql  = new StringBuffer().append(sqlPre).append(fromPre);

        Map<String, Object> param= new HashMap<>();
        if(StringUtils.isNoneBlank(vo.getTenantMenuName())){
            whereSql.append(" and m1.tenant_menu_name =:tenantMenuName");
            param.put("tenantMenuName",vo.getTenantMenuName());
        }
        if(StringUtils.isNoneBlank(vo.getMenuType())){
            whereSql.append(" and m1.menu_type =:menuType");
            param.put("menuType",vo.getMenuType());
        }
        //用于本地测试
        if((vo.getUserId()) != null){
            //cast(usermenuro0_.user_id as varchar(255))
            whereSql.append(" and t1.id =:userId");  //cast(t1.id as varchar(255))
            param.put("userId",vo.getUserId());
        }

        return  new  SqlVo(whereSql.toString(),param);


    }
}
