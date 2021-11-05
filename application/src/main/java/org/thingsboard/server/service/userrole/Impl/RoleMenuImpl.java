package org.thingsboard.server.service.userrole.Impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.common.data.vo.enums.MenuCheckEnum;
import org.thingsboard.server.common.data.vo.menu.QueryMenuByRoleVo;
import org.thingsboard.server.common.data.vo.menu.TenantMenuVo;
import org.thingsboard.server.dao.model.sql.TenantMenuEntity;
import org.thingsboard.server.dao.sql.role.entity.TenantMenuRoleEntity;
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;
import org.thingsboard.server.dao.sql.role.service.TenantMenuRoleService;
import org.thingsboard.server.dao.sql.role.service.TenantSysRoleService;
import org.thingsboard.server.dao.tenantmenu.TenantMenuService;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.entity.ResultVo;
import org.thingsboard.server.entity.rolemenu.InMenuByUserVo;
import org.thingsboard.server.entity.rolemenu.OutMenuByUserVo;
import org.thingsboard.server.entity.rolemenu.RoleMenuVo;
import org.thingsboard.server.service.userrole.RoleMenuSvc;
import org.thingsboard.server.service.userrole.SqlSplicingSvc;
import org.thingsboard.server.service.userrole.sqldata.SqlVo;

import java.util.ArrayList;
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
    @Autowired private UserService userService;



    @Override
    public void  binding(RoleMenuVo vo) throws ThingsboardException {
        log.info("角色绑定菜单的入参:{}",vo);
        TenantSysRoleEntity roleEntity = roleService.findById(vo.getRoleId());
        if (roleEntity == null) {
            throw new ThingsboardException(" Role ID does not exist !", ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        tenantMenuRoleService.deleteByTenantSysRoleId(vo.getRoleId());
        binding(vo.getRoleId(),vo.getMenuVoList(),MenuCheckEnum.SELECT_ALL);
        binding(vo.getRoleId(),vo.getSemiSelectList(),MenuCheckEnum.SEMI_SELECTION);
    }


    public void  binding( UUID  roleId,List<UUID> ids,MenuCheckEnum checkEnum) {
        log.info("开始剔除库里面不存在得菜单id");
        List<TenantMenu>  menus =  menuService.findByIdIn(ids);
        if(CollectionUtils.isEmpty(menus))
        {
            log.info("[角色绑定菜单接口]由入参ids{}查询到为空",ids);
            return ;
        }
        //能查询到就用查询到的id来
        List<UUID> idsNew = menus.stream().map(TenantMenu::getUuidId).collect(Collectors.toList());
        log.info("在系统中存在的ids条数:{} 传入的条数{}",idsNew.size(),ids.size());
         bindingData(roleId,idsNew,checkEnum);
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


    @Override
    public List<TenantMenuVo> queryAllNew(InMenuByUserVo vo) throws Exception {
        try {
            log.info("1.先查询租户下的所有菜单入参{}", vo);
            List<TenantMenu> menus = menuService.getTenantMenuListByTenantId(vo.getMenuType(), vo.getTenantId());
            List<TenantMenuVo> vos = listToVo(menus);
            log.info("2.先查询租户下的所有菜单入参{}返回的结果{}", vo, menus);
            if (CollectionUtils.isEmpty(menus)) {
                return vos;
            }
            //2.用当前的角色查询所绑定的菜单：  tb_tenant_menu_role
            TenantMenuRoleEntity entity = new TenantMenuRoleEntity();
            entity.setTenantSysRoleId(vo.getRoleId());
            List<TenantMenuRoleEntity> entityList = tenantMenuRoleService.findAllByTenantMenuRoleEntity(entity);
            log.info("3.先查询租户下的所有菜单入参{}返回的结果{}", vo, entityList);
            if (CollectionUtils.isEmpty(entityList)) {
                return vos;
            }
            for (TenantMenuVo menu : vos) {
                for (TenantMenuRoleEntity entity1 : entityList) {
                    if (menu.getId().equals(entity1.getTenantMenuId()) && entity1.getFlg().equals(MenuCheckEnum.SELECT_ALL.getRoleCode())) {
                        menu.setChecked(true);
                    }
                }


            }
            log.info("4.先查询租户下的所有菜单入参{}返回的结果{}", vo, menus);
            return vos;
        }catch (Exception e)
        {
            e.printStackTrace();
            return  null;
        }
    }

    @Override
    public List<TenantMenuVo>queryByUser(InMenuByUserVo vo, TenantId tenantId, UserId userId) throws Exception {
        User user = userService.findUserById(tenantId, userId);

        log.info("=user===>{}",user);
        if(user.getAuthority() == Authority.SYS_ADMIN)
        {
            //返回系统菜单;
            return null;
        }
        if(user.getAuthority() == Authority.TENANT_ADMIN && StringUtils.isEmpty(user.getUserCode()))
        {
            List<TenantMenu>  menus =   menuService.getTenantMenuListByTenantId(vo.getMenuType(),vo.getTenantId());
            return listToVo(menus);
        }
        List<TenantMenuVo>  menusd = new ArrayList<>();
        log.info("获取当前登录的人菜单:{}",vo);
        List<TenantMenuRoleEntity> entityList =  tenantMenuRoleService.queryMenuIdByRole(vo.getUserId());
        //如果是系统管理员 或者租户管理员呢？ //先不考虑
        log.info("获取当前登录的人菜单查询到的角色数据:{}",entityList);
        if(CollectionUtils.isEmpty(entityList))
        {
            return menusd;
        }
        List<UUID> uuids= entityList.stream().map(TenantMenuRoleEntity::getId).collect(Collectors.toList());
        List<TenantMenu>  menus1= menuService.getTenantMenuListByIds(vo.getMenuType(),vo.getTenantId(),uuids);
        log.info("获取当前登录的人菜单TenantMenu:{}",menus1);

        List<TenantMenuVo>  vos=   listToVo(menus1);
        log.info("获取当前登录的人菜单TenantMenuVo:{}",vos);

        return  vos;
    }

    //具体的绑定的入库
    @Transactional
    public void bindingData(UUID roleId, List<UUID> voList, MenuCheckEnum checkEnum) {
        if(CollectionUtils.isEmpty(voList)){
            return ;

        }
//        tenantMenuRoleService.deleteByTenantSysRoleId(roleId);
        voList.forEach(id ->{
            TenantMenuRoleEntity  entity  = new TenantMenuRoleEntity();
            entity.setTenantSysRoleId(roleId);
            entity.setTenantMenuId(id);
            entity.setFlg(checkEnum.getRoleCode());
            tenantMenuRoleService.saveEntity(entity);

        });
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
            meVo.setName(temp.getName());
            meVo.setLangKey(temp.getLangkey());
            meVo.setPath(temp.getPath());
//            meVo.setTenantMenuName(temp.getName());
//            meVo.setTenantMenuCode(temp.getCode());
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



    private  List<TenantMenuVo> listToVo(List<TenantMenu>   entities)
    {
        List<TenantMenuVo> tenantMenuList = new ArrayList<>();

        if(!org.springframework.util.CollectionUtils.isEmpty(entities)){
            entities.forEach(tenantMenuEntity->{
                if(tenantMenuEntity != null){
                    tenantMenuList.add(tenantMenuEntity.toTenantMenuVo(tenantMenuEntity));
                }
            });

        }

        return  tenantMenuList;
    }



}
