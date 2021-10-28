package org.thingsboard.server.service.userrole;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.dao.sql.role.entity.TenantMenuRoleEntity;
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;
import org.thingsboard.server.dao.sql.role.service.TenantMenuRoleService;
import org.thingsboard.server.dao.sql.role.service.TenantSysRoleService;
import org.thingsboard.server.dao.tenantmenu.TenantMenuService;
import org.thingsboard.server.entity.ResultVo;
import org.thingsboard.server.entity.rolemenu.InMenuByUserVo;
import org.thingsboard.server.entity.rolemenu.InputMenuVo;
import org.thingsboard.server.entity.rolemenu.OutMenuByUserVo;
import org.thingsboard.server.entity.rolemenu.RoleMenuVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleMenuImpl implements  RoleMenuSvc {

    @Autowired  private TenantMenuRoleService tenantMenuRoleService;
    @Autowired private TenantSysRoleService roleService;

    @Autowired private TenantMenuService  menuService;//租户菜单接口



    @Override
    public Object binding(RoleMenuVo vo) {
        log.info("角色绑定菜单的入参:{}",vo);
        TenantSysRoleEntity roleEntity = roleService.findById(vo.getRoleId());
        if (roleEntity == null) {
            return ResultVo.getFail("当前传入得角色id[roleId]错误,请检查!");
        }
        List<UUID> ids = vo.getMenuVoList();
        if(CollectionUtils.isEmpty(ids))
        {
            tenantMenuRoleService.deleteByTenantSysRoleId(vo.getRoleId());
            return ResultVo.getSuccessFul(null,"解绑数据成功!");
        }

        log.info("开始剔除库里面不存在得菜单id");
        List<TenantMenu>  menus =  menuService.findByIdIn(ids);
        if(CollectionUtils.isEmpty(menus))
        {
            log.info("[角色绑定菜单接口]由入参ids{}查询到为空",ids);
            return ResultVo.getSuccessFul(null,"菜单已经更新,请重新绑定!");
        }
        //能查询到就用查询到的id来
        List<UUID> idsNew = menus.stream().map(TenantMenu::getUuidId).collect(Collectors.toList());
        log.info("在系统中存在的ids条数:{} 传入的条数{}",idsNew.size(),ids.size());
         return binding(vo.getRoleId(),idsNew);
    }

    /**
     * 查询菜单列表
     * @param vo
     * @return
     */
    @Override
    public Object queryAll(InMenuByUserVo vo) {
        log.info("调用查询菜单列表的入参{}",vo);
        SqlVo  sqlVo= this.getSqlByVo(vo);
        List<OutMenuByUserVo> list= tenantMenuRoleService.queryAllListSqlLocal(sqlVo.getSql(),sqlVo.getParam(), OutMenuByUserVo.class);
        return ResultVo.getSuccessFul(list);
    }


    //具体的绑定的入库
    @Transactional
    public Object binding(UUID roleId, List<UUID> voList) {
        if(CollectionUtils.isEmpty(voList)){
            return ResultVo.getFail("传入的菜单为空!");

        }
        tenantMenuRoleService.deleteByTenantSysRoleId(roleId);
        voList.forEach(id ->{
            TenantMenuRoleEntity  entity  = new TenantMenuRoleEntity();
            entity.setTenantSysRoleId(roleId);
            entity.setTenantMenuId(id);
            tenantMenuRoleService.saveEntity(entity);

        });
        return ResultVo.getSuccessFul(null);
    }


    private  SqlVo getSqlByVo(InMenuByUserVo vo)
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
