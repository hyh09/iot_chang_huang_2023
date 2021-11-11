package org.thingsboard.server.dao.sql.role.service.Imp;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.vo.CustomException;
import org.thingsboard.server.common.data.vo.JudgeUserVo;
import org.thingsboard.server.common.data.vo.enums.ActivityException;
import org.thingsboard.server.common.data.vo.enums.RoleEnums;
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;
import org.thingsboard.server.dao.sql.role.entity.UserMenuRoleEntity;
import org.thingsboard.server.dao.sql.role.service.TenantSysRoleService;
import org.thingsboard.server.dao.sql.role.service.UserMenuRoleService;
import org.thingsboard.server.dao.sql.role.service.UserRoleMenuSvc;
import org.thingsboard.server.dao.user.UserService;

import java.util.List;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 用户角色菜单业务相关接口
 * @author: HU.YUNHUI
 * @create: 2021-11-02 10:29
 **/
@Slf4j
@Service
public class UserRoleMenuImpl  implements UserRoleMenuSvc {

    private  static  final String DEFAULT_PASSWORD="123456";//rawPassword

    private final BCryptPasswordEncoder passwordEncoder;

    public UserRoleMenuImpl() {
        passwordEncoder= new BCryptPasswordEncoder();
    }

    @Autowired  private UserService  userService;
    @Autowired  private TenantSysRoleService tenantSysRoleService;
    @Autowired  private UserMenuRoleService userMenuRoleService;


    {

    }

    /**
     * 查询当前人的是否是 工厂管理角色 /组合角色
     * @param userId
     * @return
     */
    @Override
    public JudgeUserVo decideUser(UserId userId) {
        User  user =  userService.findUserById(null,userId);
        JudgeUserVo  judgeUserVo =  new JudgeUserVo();
        if(user == null)
        {
            return  judgeUserVo;
//           throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"用户id:"+userId+"查询不到");
        }
        

        ///暂时的
        if(user.getAuthority() == Authority.TENANT_ADMIN && StringUtils.isEmpty(user.getUserCode()))
        {
            judgeUserVo.setTenantFlag(true);
            return judgeUserVo;
        }

        //当前用户查询
        List<TenantSysRoleEntity>  factorySysRoleEntities =  tenantSysRoleService.queryRoleByUserId(user.getUuidId());
        if(CollectionUtils.isEmpty(factorySysRoleEntities))
        {
            return  new JudgeUserVo();
//            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"用户id:"+userId+"查询未分配角色");

        }
        //当前创建人的角色
        List<TenantSysRoleEntity>  tenantSysRoleEntities1 =  tenantSysRoleService.queryRoleByUserId(UUID.fromString(user.getUserCreator()));
        JudgeUserVo vo = getJudeUserVoById(tenantSysRoleEntities1,UUID.fromString(user.getUserCreator())) ;
        if(vo != null)
        {
            return  vo;
        }
        JudgeUserVo vo1 = getJudeUserVoById(factorySysRoleEntities,user.getUuidId()) ;
        if(vo1 != null)
        {
            return  vo1;
        }
        return  new JudgeUserVo(false,false,null);

    }

    @Override
    public User save(User user, User user1)  {
        String  encodePassword =   passwordEncoder.encode(DEFAULT_PASSWORD);
        user.setTenantId(user1.getTenantId());
        user.setUserCreator(user1.getUuidId().toString());
        User  rmUser= userService.save(user,encodePassword);


      TenantSysRoleEntity entityBy=  tenantSysRoleService.queryEntityBy(RoleEnums.FACTORY_ADMINISTRATOR.getRoleCode());
        if(entityBy != null)
        {
            UserMenuRoleEntity entityRR = new UserMenuRoleEntity();
            entityRR.setUserId(rmUser.getUuidId());
            entityRR.setTenantSysRoleId(entityBy.getId());
            userMenuRoleService.saveEntity(entityRR);
            return rmUser;
        }

        TenantSysRoleEntity entity = new TenantSysRoleEntity();
        entity.setCreatedUser(user1.getUuidId());
        entity.setUpdatedUser(user1.getUuidId());
        entity.setRoleCode(RoleEnums.FACTORY_ADMINISTRATOR.getRoleCode());
        entity.setRoleName(RoleEnums.FACTORY_ADMINISTRATOR.getRoleName());
        TenantSysRoleEntity rmEntity=  tenantSysRoleService.saveEntity(entity);

        UserMenuRoleEntity entityRR = new UserMenuRoleEntity();
        entityRR.setUserId(rmUser.getUuidId());
        entityRR.setTenantSysRoleId(rmEntity.getId());
        userMenuRoleService.saveEntity(entityRR);
        return rmUser;
    }


    private  JudgeUserVo  getJudeUserVoById(List<TenantSysRoleEntity>  tenantSysRoleEntities1,UUID id)
    {
        if(!CollectionUtils.isEmpty(tenantSysRoleEntities1))
        {
            long count=   tenantSysRoleEntities1.stream().filter(p1 -> p1.getRoleCode().equals(RoleEnums.TENANT_ADMIN.getRoleCode())).count();
            if(count>0)
            {
                return  new JudgeUserVo(true,false,id);
            }

            long fCount=   tenantSysRoleEntities1.stream().filter(p1 -> p1.getRoleCode().equals(RoleEnums.FACTORY_ADMINISTRATOR.getRoleCode())).count();
            if(fCount>0)
            {
                return  new JudgeUserVo(false,true,id);
            }
        }
        return  null;
    }
}
