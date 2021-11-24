package org.thingsboard.server.dao.sql.role.service.Imp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.user.DefalutSvc;
import org.thingsboard.server.common.data.vo.CustomException;
import org.thingsboard.server.common.data.vo.JudgeUserVo;
import org.thingsboard.server.common.data.vo.enums.ActivityException;
import org.thingsboard.server.common.data.vo.enums.RoleEnums;
import org.thingsboard.server.common.data.vo.user.CodeVo;
import org.thingsboard.server.common.data.vo.user.UserVo;
import org.thingsboard.server.common.data.vo.user.enums.CreatorTypeEnum;
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;
import org.thingsboard.server.dao.sql.role.entity.UserMenuRoleEntity;
import org.thingsboard.server.dao.sql.role.service.CheckSvc;
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
public class UserRoleMenuImpl  implements UserRoleMenuSvc, DefalutSvc {


    private final BCryptPasswordEncoder passwordEncoder;

    public UserRoleMenuImpl() {
        passwordEncoder= new BCryptPasswordEncoder();
    }

    @Autowired  private UserService  userService;
    @Autowired  private TenantSysRoleService tenantSysRoleService;
    @Autowired  private UserMenuRoleService userMenuRoleService;
    @Autowired protected CheckSvc checkSvc;


    {

    }

    @Override
    public Boolean isTENANT(UUID userId) {
        List<TenantSysRoleEntity>  tenantSysRoleEntities =  tenantSysRoleService.queryRoleByUserId(userId);
        if(CollectionUtils.isEmpty(tenantSysRoleEntities)){
            return false;
        }
        long count=   tenantSysRoleEntities.stream().filter(p1 -> p1.getRoleCode().equals(RoleEnums.TENANT_ADMIN.getRoleCode())).count();
        if(count>0)
        {
            return  true;
        }

        return  false;
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
        if(isTENANT(user.getUuidId()))
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
        if(user.getFactoryId() == null)
        {
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"创建工厂管理员 工厂id不能为空!");
        }

        if(user1.getTenantId() ==  null)
        {
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"创建工厂管理员 所属租户不能为空!");
        }

        UserVo  vo1 = new UserVo();
        vo1.setEmail(user.getEmail());
        if(checkSvc.checkValueByKey(vo1)){
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"邮箱 ["+user.getEmail()+"]已经被占用!");
        }
        UserVo  vo2 = new UserVo();
        vo2.setPhoneNumber(user.getPhoneNumber());
        if(checkSvc.checkValueByKey(vo2)){
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"手机号["+user.getPhoneNumber()+"]已经被占用!!");
        }

        //用户编码如果不传就
        if(StringUtils.isNotEmpty(user.getUserCode()))
        {
            CodeVo  codeVo =  new CodeVo();
            codeVo.setKey("1");
         String    str = (String) checkSvc.queryCodeNew(codeVo,user1.getTenantId());
          user.setUserCode(str);
        }

        String  encodePassword =   passwordEncoder.encode(DEFAULT_PASSWORD);
        user.setTenantId(user1.getTenantId());
        user.setUserCreator(user1.getUuidId().toString());
        user.setType(CreatorTypeEnum.FACTORY_MANAGEMENT.getCode());

         user.setAuthority(Authority.FACTORY_MANAGEMENT);

        User  rmUser= userService.save(user,encodePassword);


      TenantSysRoleEntity entityBy=  tenantSysRoleService.queryEntityBy(RoleEnums.FACTORY_ADMINISTRATOR.getRoleCode(),user1.getTenantId().getId());
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
        entity.setTenantId(user1.getTenantId().getId());
        entity.setFactoryId(user.getFactoryId());
        entity.setType(user.getType());
        entity.setSystemTab("1");
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
