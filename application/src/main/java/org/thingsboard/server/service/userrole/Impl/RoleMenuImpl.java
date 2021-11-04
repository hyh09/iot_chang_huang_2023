package org.thingsboard.server.service.userrole.Impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.common.data.vo.menu.QueryMenuByRoleVo;
import org.thingsboard.server.dao.sql.role.entity.TenantMenuRoleEntity;
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;
import org.thingsboard.server.dao.sql.role.service.TenantMenuRoleService;
import org.thingsboard.server.dao.sql.role.service.TenantSysRoleService;
import org.thingsboard.server.dao.tenantmenu.TenantMenuService;
import org.thingsboard.server.entity.ResultVo;
import org.thingsboard.server.entity.rolemenu.InMenuByUserVo;
import org.thingsboard.server.entity.rolemenu.OutMenuByUserVo;
import org.thingsboard.server.entity.rolemenu.RoleMenuVo;
import org.thingsboard.server.service.userrole.RoleMenuSvc;
import org.thingsboard.server.service.userrole.SqlSplicingSvc;
import org.thingsboard.server.service.userrole.sqldata.SqlVo;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleMenuImpl implements RoleMenuSvc {

    @Autowired  private TenantMenuRoleService tenantMenuRoleService;
    @Autowired private TenantSysRoleService roleService;

    @Autowired private TenantMenuService  menuService;//租户菜单接口
    @Autowired  private  SqlSplicingSvc splicingSvc;


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
        SqlVo sqlVo= splicingSvc.getSqlByVo(vo);
        List<OutMenuByUserVo> list= tenantMenuRoleService.queryAllListSqlLocal(sqlVo.getSql(),sqlVo.getParam(), OutMenuByUserVo.class);
        return toList(list);
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


    private List<QueryMenuByRoleVo> toList(List<OutMenuByUserVo> voList)
    {
        List<QueryMenuByRoleVo> result = voList.stream().map(temp -> {

            QueryMenuByRoleVo meVo = new QueryMenuByRoleVo();

            meVo.setChecked((StringUtils.isNotBlank(temp.getMark())&&temp.getMark().equals("1") )?true:false);
            meVo.setCreatedTime(StringUtils.isNoneBlank(temp.getTime())?(Long.parseLong(temp.getTime())):0);
            meVo.setId(temp.getId());
            meVo.setParentId(temp.getPid());
            meVo.setLangKey(temp.getLangkey());
            meVo.setTenantMenuName(temp.getName());
            meVo.setTenantMenuCode(temp.getCode());
            if(!StringUtils.isEmpty(temp.getButton() ))
            {
               if(temp.getButton().equalsIgnoreCase("true"))
               {
                   meVo.setIsButton(true);
               }
            }
            return meVo;

        }).collect(Collectors.toList());

        return  result;
    }



}
