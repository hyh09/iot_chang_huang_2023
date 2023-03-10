package org.thingsboard.server.dao.sql.role.userrole.sqldata;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.thingsboard.server.common.data.vo.CustomException;
import org.thingsboard.server.common.data.vo.QueryUserVo;
import org.thingsboard.server.common.data.vo.enums.ActivityException;
import org.thingsboard.server.common.data.vo.user.CodeVo;
import org.thingsboard.server.common.data.vo.user.UserVo;
import org.thingsboard.server.common.data.vo.user.enums.UserLeveEnums;
import org.thingsboard.server.dao.model.CompareType;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.sql.role.service.rolemenu.InMenuByUserVo;
import org.thingsboard.server.dao.sql.role.userrole.SqlSplicingSvc;
//import org.thingsboard.server.dao.sql.role.service.rolemenu.InMenuByUserVo;
//import org.thingsboard.server.service.userrole.SqlSplicingSvc;

import java.util.*;

@Slf4j
@Service
public class SqlSplicingImpl implements SqlSplicingSvc {


    /**
     * 获取租户下菜单数据
     * @param vo
     * @return
     */
    @Override
    public   SqlVo getSqlByVo(InMenuByUserVo vo)
    {
        //返回的字段
        String sqlPre="select m1.lang_key as langkey ,m1.tenant_menu_code as code, cast(m1.is_button as varchar) as button, cast(m1.created_time as varchar) as time," +
                "cast(m1.parent_id as varchar(255)) as pid,cast(m1.id as varchar(255)) as id ," +
                "m1.sys_menu_name as name , cast (count(c1.id) as varchar  ) as mark " +
                "  ";
//        String sqlPre=" select cast(count(c1.id) as varchar ) as mark ,m1.created_time as time1 ,m1.id   " ;


        String fromPre="  from tb_tenant_menu m1  " +
                "   left join tb_tenant_menu_role r1 on m1.id = r1.tenant_menu_id  " +
                "   left join tb_tenant_sys_role c1 on r1.tenant_sys_role_id =c1.id  " +
                "   left join tb_user_menu_role b1  on  c1.id =b1.tenant_sys_role_id " +
                "   left join tb_user  t1  on t1.id=b1.user_id  where 1=1  ";

        StringBuffer whereSql = new StringBuffer().append(sqlPre).append(fromPre);

        Map<String, Object> param= new HashMap<>();
        if(StringUtils.isNoneBlank(vo.getTenantMenuName())){
            whereSql.append(" and m1.tenant_menu_name =:tenantMenuName");
            param.put("tenantMenuName",vo.getTenantMenuName());
        }
        if(StringUtils.isNoneBlank(vo.getMenuType()))
        {
            whereSql.append(" and m1.menu_type =:menuType ");
            param.put("menuType",vo.getMenuType());
        }

//        if((vo.getUserId()) != null){
//            whereSql.append(" and t1.id =:userId");
//            param.put("userId",vo.getUserId());
//        }

        if((vo.getRoleId()) != null){
            whereSql.append(" and b1.tenant_sys_role_id =:roleId");
            param.put("roleId",vo.getRoleId());
        }
        if((vo.getRoleId()) != null){
            whereSql.append(" and b1.tenant_sys_role_id =:roleId");
            param.put("roleId",vo.getRoleId());
        }
        if(vo.getTenantId() != null ){
            whereSql.append(" and m1.tenant_id =:tenantId");
            param.put("tenantId",vo.getTenantId());
        }

        return  new  SqlVo(whereSql.toString()+" group by m1.id ",param);


    }

    @Override
    public SqlVo getCountUserSqlByVo(UserVo vo) {
        STGroup stg = new STGroupFile(ModelConstants.STG_YIE_ID_DATA);
        ST sqlST = stg.getInstanceOf(ModelConstants.USER_SQL_VO);
        List<String> columnList = new LinkedList<String>();
        columnList.add(ModelConstants.count("1")+ModelConstants.as("count"));
        sqlST.add("columns", columnList);
        sqlST.add("model",vo );
        sqlST.add("tableName",ModelConstants.USER_PG_HIBERNATE_COLUMN_FAMILY_NAME );
        sqlST.add("CompareType",new CompareType());
        String cql =sqlST.render();
        return  new  SqlVo(cql,null);
//        String  sqlCount ="select count(1) from  tb_user t1 where 1=1 ";
//        StringBuffer whereSql  = new StringBuffer();
//        Map<String, Object> param= new HashMap<>();
//        if(StringUtils.isNoneBlank(vo.getEmail()))
//        {
//            whereSql.append(" and t1.email =:email ");
//            param.put("email",vo.getEmail());
//        }
//        if(StringUtils.isNoneBlank(vo.getPhoneNumber()))
//        {
//            whereSql.append(" and t1.phone_number =:phoneNumber ");
//            param.put("phoneNumber",vo.getPhoneNumber());
//        }
//
//        if(StringUtils.isNoneBlank(vo.getUserCode()))
//        {
//            whereSql.append(" and t1.user_code =:userCode ");
//            param.put("userCode",vo.getUserCode());
//        }
//        if((vo.getTenantId()) != null)
//        {
//            whereSql.append(" and t1.tenant_id =:tenantId ");
//            param.put("tenantId",vo.getTenantId());
//        }
//
//
//        if(StringUtils.isNoneBlank(vo.getUserId()))
//        {
//
//            whereSql.append(" and t1.id !=:id ");
//            param.put("id", UUID.fromString(vo.getUserId()));
//        }
//        if(StringUtils.isEmpty(whereSql))
//        {
//            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"入参不能为空");
//        }
//        return  new  SqlVo(sqlCount+whereSql.toString(),param);
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
    public SqlVo getUserByInRole(QueryUserVo vo) {
        Map<String, Object> param= new HashMap<>();

        StringBuffer sql  = new StringBuffer();
//        cast(t1.created_time as varchar(255))
        sql.append("select cast(t1.id as varchar(255)) as id ,t1.phone_number as phoneNumber, t1.active_status as activeStatus,t1.user_code as userCode ,t1.user_creator as userCreator,cast(t1.created_time as varchar(255))  as time, " +
                "   t1.email as email, t1.authority as authority, cast(t1.tenant_id as varchar(255)) as tenantId ,t1.user_name as userName    " +
                "from  tb_user  t1  ");
        sql.append(" left join tb_user_menu_role b1  on t1.id=b1.user_id  where 1=1 ");


        StringBuffer whereSql  = new StringBuffer();
        if(vo.getRoleId() != null)
        {
            whereSql.append(" and b1.tenant_sys_role_id =:roleId ");
            param.put("roleId", vo.getRoleId());
        }
        if(StringUtils.isNoneBlank(vo.getUserName()))
        {
            whereSql.append(" and t1.user_name =:userName ");
            param.put("userName", vo.getUserName());
        }
        if(StringUtils.isNoneBlank(vo.getUserCode()))
        {
            whereSql.append(" and t1.user_code =:userCode ");
            param.put("userCode", vo.getUserCode());
        }

        return  new  SqlVo(sql+whereSql.toString(),param);

    }

    @Override
    public SqlVo getUserByNotInRole(QueryUserVo vo) {
        Map<String, Object> param= new HashMap<>();

        StringBuffer sql  = new StringBuffer();

        sql.append("select cast(t1.id as varchar(255)) as id ,t1.phone_number as phoneNumber, t1.active_status as activeStatus,t1.user_code as userCode ,t1.user_creator as userCreator," +
                "   cast(t1.created_time as varchar(255))  as time, t1.email as email, t1.authority as authority, cast(t1.tenant_id as varchar(255)) as tenantId ,t1.user_name as userName  " +
                "from  tb_user  t1  where  1=1  ");
        if(vo.getUserLevel() == UserLeveEnums.TENANT_ADMIN.getCode()
                || vo.getUserLevel() == UserLeveEnums.USER_SYSTEM_ADMIN.getCode()
                || vo.getUserLevel() == UserLeveEnums.DEFAULT_VALUE.getCode()
          )
        {
            sql.append(" and t1.user_level in (0,4) ");
        }

       StringBuffer whereSql  = new StringBuffer();
        if(vo.getRoleId() != null)
        {

            whereSql.append(" and  t1.id not in  (select b1.user_id  from tb_user_menu_role b1 where b1.tenant_sys_role_id =:roleId  ) ");
            param.put("roleId", vo.getRoleId());
        }
        if(StringUtils.isNoneBlank(vo.getUserName()))
        {
            whereSql.append(" and t1.user_name like :userName ");
            param.put("userName", "%"+vo.getUserName()+"%");
        }
        if(StringUtils.isNoneBlank(vo.getUserCode()))
        {
            whereSql.append(" and t1.user_code like :userCode ");
            param.put("userCode","%"+vo.getUserCode()+"%");
        }
        if((vo.getTenantId() !=null ))
        {
            whereSql.append(" and t1.tenant_id =:tenantId ");
            param.put("tenantId", vo.getTenantId());
        }

        if(vo.getFactoryId() != null)
        {
            whereSql.append(" and t1.factory_Id =:factoryId ");
            param.put("factoryId", vo.getFactoryId());
        }

        if(StringUtils.isNotBlank(vo.getType()))
        {
            whereSql.append(" and t1.type =:type ");
            param.put("type", vo.getType());
        }


//        if((vo.getCreateId() !=null ))
//        {
//            whereSql.append(" and t1.user_creator =:userCreator ");
//            param.put("userCreator", vo.getCreateId().toString());
//        }

        return  new  SqlVo(sql+whereSql.toString(),param);
    }


}
