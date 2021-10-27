package org.thingsboard.server.service.userrole;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.dao.sql.role.entity.TenantMenuRoleEntity;
import org.thingsboard.server.dao.sql.role.service.TenantMenuRoleService;
import org.thingsboard.server.entity.ResultVo;
import org.thingsboard.server.entity.rolemenu.InputMenuVo;
import org.thingsboard.server.entity.rolemenu.RoleMenuVo;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class RoleMenuImpl implements  RoleMenuSvc {

    @Autowired  private TenantMenuRoleService tenantMenuRoleService;



    @Override
    public Object binding(RoleMenuVo vo) {
        log.info("角色绑定菜单的入参:{}",vo);
        return binding(vo.getRoleId(),vo.getMenuVoList());
    }




    private Object binding(UUID roleId, List<InputMenuVo> voList) {
        if(CollectionUtils.isEmpty(voList)){
            return ResultVo.getFail("传入的菜单为空!");

        }
        voList.forEach(menuVo ->{
            TenantMenuRoleEntity  entity  = new TenantMenuRoleEntity();
            entity.setTenantSysRoleId(roleId);
            entity.setTenantMenuId(menuVo.getMenuId());
            entity.setRemark(menuVo.getRemark());
            tenantMenuRoleService.saveEntity(entity);

        });
        return ResultVo.getSuccessFul(null);
    }
}
