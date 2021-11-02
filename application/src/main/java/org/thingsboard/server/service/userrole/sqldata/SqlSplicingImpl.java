package org.thingsboard.server.service.userrole.sqldata;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.thingsboard.server.entity.rolemenu.InMenuByUserVo;
import org.thingsboard.server.entity.user.CodeVo;
import org.thingsboard.server.entity.user.UserVo;
import org.thingsboard.server.common.data.vo.CustomException;
import org.thingsboard.server.common.data.vo.enums.ActivityException;
import org.thingsboard.server.service.userrole.SqlSplicingSvc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
            whereSql.append(" and t1.id =:userId");  //cast(t1.id as varchar(255))
            param.put("userId",vo.getUserId());
        }

        return  new  SqlVo(whereSql.toString(),param);


    }

    @Override
    public SqlVo getCountUserSqlByVo(UserVo vo) {
        String  sqlCount ="select count(1) from  tb_user t1 where 1=1 ";
        StringBuffer whereSql  = new StringBuffer();
        Map<String, Object> param= new HashMap<>();
        if(StringUtils.isNoneBlank(vo.getEmail()))
        {
            whereSql.append(" and t1.email =:email ");
            param.put("email",vo.getEmail());
        }
        if(StringUtils.isNoneBlank(vo.getPhoneNumber()))
        {
            whereSql.append(" and t1.phone_number =:phoneNumber ");
            param.put("phoneNumber",vo.getPhoneNumber());
        }

        if(StringUtils.isNoneBlank(vo.getUserCode()))
        {
            whereSql.append(" and t1.user_code =:userCode ");
            param.put("userCode",vo.getUserCode());
        }
        if(StringUtils.isNoneBlank(vo.getUserId()))
        {

            whereSql.append(" and t1.id !=:id ");
            param.put("id", UUID.fromString(vo.getUserId()));
        }
        if(StringUtils.isEmpty(whereSql))
        {
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"入参不能为空");
        }
        return  new  SqlVo(sqlCount+whereSql.toString(),param);
    }


    /**
     * 查询编码
     * @param vo
     * @return
     */
    @Override
    public SqlVo getUserCode(CodeVo vo) {
        Map<String, Object> param= new HashMap<>();



        if(vo.getKey().equals("1"))
        {
            String  sql2 = " select  user_code as code from  tb_user where created_time in(  " +
                    "select max(created_time) from   tb_user  " +
                    " )  ";
            return new  SqlVo(sql2.toString(),param);

        }

        if(vo.getKey().equals("2"))
        {
            String  sql = " select  role_code  as code  from  TB_TENANT_SYS_ROLE where created_time in(  " +
                    "select max(created_time) from   TB_TENANT_SYS_ROLE " +
                    " )  ";
            return new  SqlVo(sql.toString(),param);

        }

        throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"入参不能为空");
    }

    @Override
    public SqlVo getUserByInRole() {
        String  sqlPre="";
        String sql="";
        return null;
    }


}
