/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.rule.engine.api.MailService;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.common.data.security.event.UserAuthDataChangedEvent;
import org.thingsboard.server.common.data.security.model.JwtToken;
import org.thingsboard.server.common.data.vo.CustomException;
import org.thingsboard.server.common.data.vo.PasswordVo;
import org.thingsboard.server.common.data.vo.enums.ActivityException;
import org.thingsboard.server.common.data.vo.enums.ErrorMessageEnums;
import org.thingsboard.server.common.data.vo.enums.RoleEnums;
import org.thingsboard.server.common.data.vo.user.CodeVo;
import org.thingsboard.server.common.data.vo.user.UpdateOperationVo;
import org.thingsboard.server.common.data.vo.user.UserVo;
import org.thingsboard.server.common.data.vo.user.enums.CreatorTypeEnum;
import org.thingsboard.server.common.data.vo.user.enums.OperationTypeEums;
import org.thingsboard.server.common.data.vo.user.enums.UserLeveEnums;
import org.thingsboard.server.dao.model.sql.UserEntity;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;
import org.thingsboard.server.dao.sql.role.entity.UserMenuRoleEntity;
import org.thingsboard.server.dao.sql.role.service.UserMenuRoleService;
import org.thingsboard.server.dao.sql.role.service.UserRoleMenuSvc;
import org.thingsboard.server.dao.sql.role.userrole.ResultVo;
import org.thingsboard.server.dao.util.ReflectionUtils;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.auth.jwt.RefreshTokenRepository;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.model.UserPrincipal;
import org.thingsboard.server.service.security.model.token.JwtTokenFactory;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;
import org.thingsboard.server.service.security.system.SystemSecurityService;

import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Api(value = "用户管理", tags = {"用户管理接口接口"})
@Slf4j
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class UserController extends BaseController  {


    public static final String USER_ID = "userId";
    public static final String YOU_DON_T_HAVE_PERMISSION_TO_PERFORM_THIS_OPERATION = "You don't have permission to perform this operation!";
    public static final String ACTIVATE_URL_PATTERN = "%s/api/noauth/activate?activateToken=%s";

    @Value("${security.user_token_access_enabled}")
    @Getter
    private boolean userTokenAccessEnabled;

    private final BCryptPasswordEncoder passwordEncoder;

    private final MailService mailService;
    private final JwtTokenFactory tokenFactory;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SystemSecurityService systemSecurityService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired  private UserRoleMenuSvc  nuSvc;
    @Autowired  private UserMenuRoleService userMenuRoleService;



    @ApiOperation(value = "提供app端获取用户的信息【当前登录人的信息 无参请求】")
    @RequestMapping(value = "/app/user/", method = RequestMethod.GET)
    @ResponseBody
    public  User  getAppUserById() throws ThingsboardException {
        SecurityUser authUser = getCurrentUser();
        User user =   this.getUserById(authUser.getUuidId().toString());
        Boolean aBoolean = user.getType().equals(CreatorTypeEnum.TENANT_CATEGORY.getCode());//nuSvc.isTENANT(user.getUuidId());
        if(aBoolean)
        {
            user.setUserName(StringUtils.isEmpty(user.getUserName())?(user.getFirstName()+user.getLastName()):user.getUserName());
            Tenant  tenant =  tenantService.findTenantById(user.getTenantId());
            user.setTenantTitle(tenant != null? tenant.getTitle():"");
        }else {

            Factory  factory =  factoryService.findById(user.getFactoryId());
            user.setFactoryName(factory!=null?factory.getName():"");

        }
        user.setSystemUser(configureRoles(user));
    return  user;
    }



//    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public User getUserById(@PathVariable(USER_ID) String strUserId) throws ThingsboardException {
        checkParameter(USER_ID, strUserId);
        try {
            UserId userId = new UserId(toUUID(strUserId));
            User user = checkUserId(userId, Operation.READ);

            List<UserMenuRoleEntity> entities =    userMenuRoleService.queryRoleIdByUserId(toUUID(strUserId));
            if(!CollectionUtils.isEmpty(entities))
            {
                List<UUID> roleIds = entities.stream().map(UserMenuRoleEntity::getTenantSysRoleId).collect(Collectors.toList());
                user.setRoleIds(roleIds);
            }
            if(user.getAdditionalInfo().isObject()) {
                ObjectNode additionalInfo = (ObjectNode) user.getAdditionalInfo();
                processDashboardIdFromAdditionalInfo(additionalInfo, DEFAULT_DASHBOARD);
                processDashboardIdFromAdditionalInfo(additionalInfo, HOME_DASHBOARD);
                UserCredentials userCredentials = userService.findUserCredentialsByUserId(user.getTenantId(), user.getId());
                if(userCredentials.isEnabled() && !additionalInfo.has("userCredentialsEnabled")) {
                    additionalInfo.put("userCredentialsEnabled", true);
                }
            }
//            SecurityUser securityUser = getCurrentUser();
            return user;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

//    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/user/tokenAccessEnabled", method = RequestMethod.GET)
    @ResponseBody
    public boolean isUserTokenAccessEnabled() {
        return userTokenAccessEnabled;
    }

//    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/user/{userId}/token", method = RequestMethod.GET)
    @ResponseBody
    public JsonNode getUserToken(@PathVariable(USER_ID) String strUserId) throws ThingsboardException {
        checkParameter(USER_ID, strUserId);
        try {
            if (!userTokenAccessEnabled) {
                throw new ThingsboardException(YOU_DON_T_HAVE_PERMISSION_TO_PERFORM_THIS_OPERATION,
                        ThingsboardErrorCode.PERMISSION_DENIED);
            }
            UserId userId = new UserId(toUUID(strUserId));
            SecurityUser authUser = getCurrentUser();
            User user = checkUserId(userId, Operation.READ);
            UserPrincipal principal = new UserPrincipal(UserPrincipal.Type.USER_NAME, user.getEmail());
            UserCredentials credentials = userService.findUserCredentialsByUserId(authUser.getTenantId(), userId);
            SecurityUser securityUser = new SecurityUser(user, credentials.isEnabled(), principal);
            JwtToken accessToken = tokenFactory.createAccessJwtToken(securityUser);
            JwtToken refreshToken = refreshTokenRepository.requestRefreshToken(securityUser);
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode tokenObject = objectMapper.createObjectNode();
            tokenObject.put("token", accessToken.getToken());
            tokenObject.put("refreshToken", refreshToken.getToken());
            return tokenObject;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

//    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    @ResponseBody
    public User saveUser(@RequestBody User user,
                         @RequestParam(required = false, defaultValue = "true") boolean sendActivationMail,
                         HttpServletRequest request) throws ThingsboardException {
        try {

            if (Authority.SYS_ADMIN.equals(getCurrentUser().getAuthority())) {
                user.setType(CreatorTypeEnum.TENANT_CATEGORY.getCode());
                user.setUserLevel(3);
            }


            checkEntity(user.getId(), user, Resource.USER);

            boolean sendEmail = user.getId() == null && sendActivationMail;

            if(StringUtils.isEmpty(user.getUserName()))
            {
                String  userName =user.getFirstName()+user.getLastName();
                user.setUserName(userName);
            }
            User savedUser = checkNotNull(userService.saveUser(user));
            if (Authority.SYS_ADMIN.equals(getCurrentUser().getAuthority())) {
                saveRole(savedUser);
            }


            if (sendEmail) {
                SecurityUser authUser = getCurrentUser();
                UserCredentials userCredentials = userService.findUserCredentialsByUserId(authUser.getTenantId(), savedUser.getId());
                String baseUrl = systemSecurityService.getBaseUrl(getTenantId(), getCurrentUser().getCustomerId(), request);
                String activateUrl = String.format(ACTIVATE_URL_PATTERN, baseUrl,
                        userCredentials.getActivateToken());
                String email = savedUser.getEmail();
                try {
                    mailService.sendActivationEmail(activateUrl, email,user.getAdditionalInfo());
                } catch (ThingsboardException e) {
                    userService.deleteUser(authUser.getTenantId(), savedUser.getId());
                    throw e;
                }
            }

            logEntityAction(savedUser.getId(), savedUser,
                    savedUser.getCustomerId(),
                    user.getId() == null ? ActionType.ADDED : ActionType.UPDATED, null);

            sendEntityNotificationMsg(getTenantId(), savedUser.getId(),
                    user.getId() == null ? EdgeEventActionType.ADDED : EdgeEventActionType.UPDATED);

            return savedUser;
        } catch (Exception e) {

            logEntityAction(emptyId(EntityType.USER), user,
                    null, user.getId() == null ? ActionType.ADDED : ActionType.UPDATED, e);

            throw handleException(e);
        }
    }

//    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/user/sendActivationMail", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void sendActivationEmail(
            @RequestParam(value = "email") String email,
            HttpServletRequest request) throws ThingsboardException {
        try {
            User user = checkNotNull(userService.findUserByEmail(getCurrentUser().getTenantId(), email));

            accessControlService.checkPermission(getCurrentUser(), Resource.USER, Operation.READ,
                    user.getId(), user);

            UserCredentials userCredentials = userService.findUserCredentialsByUserId(getCurrentUser().getTenantId(), user.getId());
            if (!userCredentials.isEnabled() && userCredentials.getActivateToken() != null) {
                String baseUrl = systemSecurityService.getBaseUrl(getTenantId(), getCurrentUser().getCustomerId(), request);
                String activateUrl = String.format(ACTIVATE_URL_PATTERN, baseUrl,
                        userCredentials.getActivateToken());
                mailService.sendActivationEmail(activateUrl, email,user.getAdditionalInfo());
            } else {
                throw new ThingsboardException("User is already activated!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

//    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/user/{userId}/activationLink", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    public String getActivationLink(
            @PathVariable(USER_ID) String strUserId,
            HttpServletRequest request) throws ThingsboardException {
        checkParameter(USER_ID, strUserId);
        try {
            UserId userId = new UserId(toUUID(strUserId));
            User user = checkUserId(userId, Operation.READ);
            SecurityUser authUser = getCurrentUser();
            UserCredentials userCredentials = userService.findUserCredentialsByUserId(authUser.getTenantId(), user.getId());
            if (!userCredentials.isEnabled() && userCredentials.getActivateToken() != null) {
                String baseUrl = systemSecurityService.getBaseUrl(getTenantId(), getCurrentUser().getCustomerId(), request);
                String activateUrl = String.format(ACTIVATE_URL_PATTERN, baseUrl,
                        userCredentials.getActivateToken());
                return activateUrl;
            } else {
                throw new ThingsboardException("User is already activated!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

//    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteUser(@PathVariable(USER_ID) String strUserId) throws ThingsboardException {
        checkParameter(USER_ID, strUserId);
        try {
            UserId userId = new UserId(toUUID(strUserId));
            User user = checkUserId(userId, Operation.DELETE);

            List<EdgeId> relatedEdgeIds = findRelatedEdgeIds(getTenantId(), userId);

            userService.deleteUser(getCurrentUser().getTenantId(), userId);
            userRoleMemuSvc.deleteRoleByUserId(userId.getId());
            logEntityAction(userId, user,
                    user.getCustomerId(),
                    ActionType.DELETED, null, strUserId);

            sendDeleteNotificationMsg(getTenantId(), userId, relatedEdgeIds);

        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.USER),
                    null,
                    null,
                    ActionType.DELETED, e, strUserId);
            throw handleException(e);
        }
    }

//    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/users", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<User> getUsers(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        try {
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            SecurityUser currentUser = getCurrentUser();
             if(nuSvc.isTENANT(currentUser.getUuidId())){
//            if (Authority.TENANT_ADMIN.equals(currentUser.getAuthority())) {
                return checkNotNull(userService.findUsersByTenantId(currentUser.getTenantId(), pageLink));
            } else {
                return checkNotNull(userService.findCustomerUsers(currentUser.getTenantId(), currentUser.getCustomerId(), pageLink));
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

//    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/tenant/{tenantId}/users", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<User> getTenantAdmins(
            @PathVariable("tenantId") String strTenantId,
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        checkParameter("tenantId", strTenantId);
        try {
            TenantId tenantId = new TenantId(toUUID(strTenantId));
//            Field field = UserEntity.class.getDeclaredField(sortProperty);// 暴力获取private修饰的成员变量
//            Column annotation = field.getAnnotation(Column.class);
            Field field=  ReflectionUtils.getAccessibleField(new UserEntity(),sortProperty);
            Column annotation = field.getAnnotation(Column.class);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, annotation.name(), sortOrder);
            return checkNotNull(userService.findTenantAdmins(tenantId, pageLink));
        } catch (Exception e) {
            e.printStackTrace();
            log.info("查询/tenant/{tenantId}/users接口报错:{}",e);
            throw handleException(e);
        }
    }

//    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/{customerId}/users", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<User> getCustomerUsers(
            @PathVariable("customerId") String strCustomerId,
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        checkParameter("customerId", strCustomerId);
        try {
            CustomerId customerId = new CustomerId(toUUID(strCustomerId));
            checkCustomerId(customerId, Operation.READ);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            TenantId tenantId = getCurrentUser().getTenantId();
            return checkNotNull(userService.findCustomerUsers(tenantId, customerId, pageLink));
        } catch (Exception e) {
            e.printStackTrace();
            throw handleException(e);
        }
    }

//    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/user/{userId}/userCredentialsEnabled", method = RequestMethod.POST)
    @ResponseBody
    public void setUserCredentialsEnabled(@PathVariable(USER_ID) String strUserId,
                                          @RequestParam(required = false, defaultValue = "true") boolean userCredentialsEnabled) throws ThingsboardException {
        checkParameter(USER_ID, strUserId);
        try {
            UserId userId = new UserId(toUUID(strUserId));
            User user = checkUserId(userId, Operation.WRITE);
            TenantId tenantId = getCurrentUser().getTenantId();
            userService.setUserCredentialsEnabled(tenantId, userId, userCredentialsEnabled);

            if (!userCredentialsEnabled) {
                eventPublisher.publishEvent(new UserAuthDataChangedEvent(userId));
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "用户管理界面下的修改密码")
    @RequestMapping(value = "/user/changeOthersPassword",method = RequestMethod.POST)
    @ResponseBody
    public Object  changeOthersPassword( @RequestBody PasswordVo vo)
    {
           log.info("【changeOthersPassword】打印当前的入参{}",vo);
           vo.setPassword(passwordEncoder.encode(vo.getPassword()));
//            eventPublisher.publishEvent(new UserAuthDataChangedEvent( UserId.fromString(vo.getUserId())));
           return  userService.changeOthersPassword(vo);
    }


    /**
     * 用户得添加接口
     */
    @ApiOperation(value = "用户管理的添加接口")
    @RequestMapping(value = "/user/save", method = RequestMethod.POST)
    @ResponseBody
    public User save(@RequestBody User user) throws ThingsboardException {
         DataValidator.validateEmail(user.getEmail());
         DataValidator.validateCode(user.getUserCode());

        SecurityUser  securityUser =  getCurrentUser();
//        if(user.getFactoryId() == null)
//        {
//            user.setFactoryId(securityUser.getFactoryId());
//        }
        log.info("打印当前的管理人的信息:{}",securityUser);
        log.info("打印当前的管理人的信息工厂id:{},创建者类别{}，用户的等级:{}",securityUser.getFactoryId(),securityUser.getType(),securityUser.getUserLevel());

        try {
            if(user.getId() != null){
                user.setStrId(user.getUuidId().toString());
              return   this.update(user);
            }
            UserVo  vo0 = new UserVo();
            vo0.setTenantId(securityUser.getTenantId().getId());
            vo0.setUserCode(user.getUserCode());
            if(checkSvc.checkValueByKey(vo0)){
                throw  new CustomException(ActivityException.FAILURE_ERROR.getCode()," 用户编码 ["+user.getUserCode()+"]已经被占用!");
            }

            UserVo  vo1 = new UserVo();
            vo1.setTenantId(securityUser.getTenantId().getId());
            vo1.setEmail(user.getEmail());
            vo1.setFactoryId(getCurrentFactoryId(user.getFactoryId()));
            if(checkSvc.checkValueByKey(vo1)){
                throw  new CustomException(ActivityException.FAILURE_ERROR.getCode()," 邮箱 ["+user.getEmail()+"]已经被占用!");
            }
            UserVo  vo2 = new UserVo();
            vo2.setTenantId(securityUser.getTenantId().getId());
            vo2.setPhoneNumber(user.getPhoneNumber());
            vo2.setFactoryId(getCurrentFactoryId(user.getFactoryId()));
            if(checkSvc.checkValueByKey(vo2)){
                throw  new CustomException(ActivityException.FAILURE_ERROR.getCode()," 手机号["+user.getPhoneNumber()+"]已经被占用!!");
            }
            TenantId  tenantId  = new TenantId(securityUser.getTenantId().getId());
            user.setTenantId(tenantId);
            user.setUserCreator(securityUser.getId().toString());

            if(user.getFactoryId()!= null )
            {
                log.info("当前保存的是工厂管理员角色用户:{}",user);
                user.setType(CreatorTypeEnum.FACTORY_MANAGEMENT.getCode());
                user.setUserLevel(1);
                user.setOperationType(OperationTypeEums.USER_DEFAULT.getValue());
                TenantSysRoleEntity  tenantSysRoleEntity= tenantSysRoleService.queryAllByFactoryId(RoleEnums.FACTORY_ADMINISTRATOR.getRoleCode(),tenantId.getId(),user.getFactoryId());
                List<UUID> roleIds = new ArrayList<>();
                roleIds.add(tenantSysRoleEntity.getId());
                user.setRoleIds(roleIds);

            }else {
                user.setType(securityUser.getType());
                user.setFactoryId(securityUser.getFactoryId());
                if(securityUser.getUserLevel() == UserLeveEnums.TENANT_ADMIN.getCode()){
                    user.setOperationType(OperationTypeEums.ROLE_NON_EDITABLE.getValue());
                    user.setUserLevel(UserLeveEnums.USER_SYSTEM_ADMIN.getCode());
                }

            }

           log.info("【用户管理模块.用户添加接口】入参{}", user);
            String  encodePassword =   passwordEncoder.encode(DataConstants.DEFAULT_PASSWORD);
            User savedUser = checkNotNull(userService.save(user,encodePassword));
            userRoleMemuSvc.relationUserBach(user.getRoleIds(),savedUser.getUuidId(),getTenantId());
            savedUser.setRoleIds(user.getRoleIds());
            return  savedUser;
        }

        catch (Exception e){
            log.error("创建用户的异常日志:入参为{}异常日志{}",user,e);
            throw  new  ThingsboardException(e.getMessage(),ThingsboardErrorCode.FAIL_VIOLATION);
        }

    }


    @ApiOperation(value = "用户管理界面下的【删除用户接口】")
    @ApiImplicitParams({
            @ApiImplicitParam(name = USER_ID, value = "用户id"),})
    @RequestMapping(value = "/user/delete/{userId}",method = RequestMethod.DELETE)
    @ResponseBody
    public String    delete(@PathVariable("userId") String strUserId) throws ThingsboardException {
        log.info("【delete User's input parameter ID:{}】",strUserId);
        checkParameter(USER_ID, strUserId);
        UserId userId = new UserId(toUUID(strUserId));
        User user = userService.findUserById(getCurrentUser().getTenantId(), userId);
        checkNotNull(user);
        if(user.getAuthority().equals(Authority.SYS_ADMIN)){
            throw new ThingsboardException("You do not have permission to delete!", ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        try {
            userService.deleteUser(getTenantId(), userId);
            userRoleMemuSvc.deleteRoleByUserId(user.getUuidId());
            return "success";
        }catch (EmptyResultDataAccessException e)
        {
            log.info("打印当前的异常信息###正常异常:{}",e);
            return  "success";
        }
    }

    /**
     * 编辑用户
     */
    @ApiOperation(value = "用户管理的【编辑用户接口】")
    @RequestMapping(value="/user/update",method = RequestMethod.POST)
    @ResponseBody
    public User update(@RequestBody User user) throws ThingsboardException {
        SecurityUser  securityUser =  getCurrentUser();

        log.info("打印更新用户的入参:{}",user);
        checkEmailAndPhone(user);
        UserVo  vo0 = new UserVo();
        vo0.setUserCode(user.getUserCode());
        vo0.setUserId(user.getUuidId().toString());
        vo0.setTenantId(securityUser.getTenantId().getId());
        if(checkSvc.checkValueByKey(vo0)){
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode()," 用户编码 ["+user.getUserCode()+"]已经被占用!");
        }

        UserVo  vo1 = new UserVo();
        vo1.setEmail(user.getEmail());
        vo1.setUserId(user.getUuidId().toString());
        vo1.setFactoryId(getCurrentFactoryId(user.getFactoryId()));
        if(checkSvc.checkValueByKey(vo1)){
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode()," 邮箱 ["+user.getEmail()+"]已经被占用!");
        }
        UserVo  vo2 = new UserVo();
        vo2.setPhoneNumber(user.getPhoneNumber());
        vo2.setUserId(user.getUuidId().toString());
        vo2.setFactoryId(getCurrentFactoryId(user.getFactoryId()));
        if(checkSvc.checkValueByKey(vo2)){
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode()," 手机号["+user.getPhoneNumber()+"]已经被占用!!");
        }

        checkParameter(USER_ID, user.getStrId());
        user.setUserCreator(securityUser.getId().toString());
        user.setId( UserId.fromString(user.getStrId()));
        int count =  userService.update(user);
        userService.updateEnableByUserId(user.getUuidId(),((user.getActiveStatus().equals("1"))?true:false));
        log.info("user.getFactoryId():工厂管理员个人角色那个是空的;所以不更新:{}",user.getFactoryId() );
           if(count>0  && UserLeveEnums.getEnableCanByCode(user.getUserLevel()))
           {
               userRoleMemuSvc.updateRoleByUserId(user.getRoleIds(),user.getUuidId(),getTenantId());
           }
        user.setRoleIds(user.getRoleIds());
        return  user;
    }



    @GetMapping("/user/findFactoryManagers")
    public PageData<User> findTenantAdmins(@RequestParam(value = "factoryId",required = false) UUID factoryId,
                                           @RequestParam(value = "userCode",required = false) String userCode,
                                           @RequestParam(value = "userName",required = false) String userName,
                                           @RequestParam int pageSize,
                                           @RequestParam int page,
                                           @RequestParam(required = false) String textSearch,
                                           @RequestParam(required = false) String sortProperty,
                                           @RequestParam(required = false) String sortOrder
    ) throws ThingsboardException {
        try {
            Field field=  ReflectionUtils.getAccessibleField(new UserEntity(),sortProperty);
            Column annotation = field.getAnnotation(Column.class);
            SecurityUser authUser = getCurrentUser();
            PageLink pageLink = createPageLink(pageSize, page, textSearch, annotation.name(), sortOrder);
             return userService.findFactoryAdmins(authUser.getTenantId(),factoryId,userCode,userName,pageLink);
        }catch (Exception e)
        {
            e.printStackTrace();
            return  null;
        }
    }




    @ApiOperation(value = "查询用户 【分页查询】")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "每页大小，默认10"),
            @ApiImplicitParam(name = "page", value = "当前页,起始页，【0开始】"),
            @ApiImplicitParam(name = "textSearch", value = ""),
            @ApiImplicitParam(name = "sortOrder", value = ""),
            @ApiImplicitParam(name = "sortProperty", value = ""),
            @ApiImplicitParam(name = "userCode", value = "用户编码"),
            @ApiImplicitParam(name = "sortProperty", value = "用户名称")


    })
    @RequestMapping(value = "/user/findAll",method = RequestMethod.GET)
    @ResponseBody
    public Object pageQuery(
            @RequestParam(value = "userCode",required = false) String userCode,
            @RequestParam(value = "userName",required = false) String userName,
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
         try {
             Map<String, Object> queryParam = new HashMap<>();
             if (!StringUtils.isEmpty(userCode)) {
                 queryParam.put("userCode", userCode);
             }
             if (!StringUtils.isEmpty(userName)) {
                 queryParam.put("userName", userName);
             }
             PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
             SecurityUser securityUser = getCurrentUser();
             UUID  userId = securityUser.getUuidId();
             queryParam.put("tenantId", securityUser.getTenantId().getId());
             if (securityUser.getType().equals(CreatorTypeEnum.FACTORY_MANAGEMENT.getCode())) {
                 log.info("如果当前用户如果是工厂类别的,就查询当前工厂下的数据:{}", securityUser.getFactoryId());
                 queryParam.put("factoryId", securityUser.getFactoryId());
             }
             queryParam.put("type", securityUser.getType());
             queryParam.put("operationType",null);
             setParametersByUserLevel(queryParam);
             PageData<User>  userPageData =  userService.findAll(queryParam, pageLink);
             return  userPageData;
         }catch (Exception  e)
         {
             e.printStackTrace();
             log.info("查询用户 【分页查询】打印当前异常:{}",e);
             throw new ThingsboardException("查询用户异常!", ThingsboardErrorCode.GENERAL);
         }
    }

    @ApiOperation(value = "用户管理得 用户得重复数据校验")
    @RequestMapping(value = "/user/check",method = RequestMethod.POST)
    public  Object check(@RequestBody @Valid UserVo vo)
    {
          return ResultVo.getSuccessFul( checkSvc.checkValueByKey(vo));
    }


    @ApiOperation(value = "用户管理得 {用户编码 或角色编码}得生成获取")
    @RequestMapping(value = "/user/getCode",method = RequestMethod.POST)
    public  Object check(@RequestBody @Valid CodeVo vo) throws ThingsboardException {

            TenantId  tenantId = null;
            SecurityUser securityUser = getCurrentUser();
            tenantId =securityUser.getTenantId();
            if(securityUser.getAuthority() == Authority.SYS_ADMIN )
            {
                if(vo.getTenantId() == null){
                    String message=   getMessageByUserId(ErrorMessageEnums.PARAMETER_NOT_NULL);
                    throw new ThingsboardException(message+"[TenantId]", ThingsboardErrorCode.GENERAL);
                }
                tenantId = new TenantId(vo.getTenantId());
                log.info("当前是系统用户");
            }
            return checkSvc.queryCodeNew(vo, tenantId);


    }



    /**
     * 编辑用户
     */
    @ApiOperation(value = "用户管理-系统开关的更新接口")
    @RequestMapping(value="/user/updateOperationType",method = RequestMethod.POST)
    @ResponseBody
    public UpdateOperationVo updateOperationType(@RequestBody @Valid UpdateOperationVo vo) throws ThingsboardException {
       return   userService.updateOperationType(vo);
    }

    private  void checkEmailAndPhone(User  user)
    {
        UserVo  vo1 = new UserVo();
        vo1.setUserId(user.getUuidId().toString());
        vo1.setEmail(user.getEmail());
        vo1.setFactoryId(user.getFactoryId());
        if(checkSvc.checkValueByKey(vo1)){
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode()," 这个邮箱 ["+user.getEmail()+"]已经被占用!");
        }
        UserVo  vo2 = new UserVo();
        vo2.setUserId(user.getUuidId().toString());
        vo2.setEmail(user.getPhoneNumber());
        vo2.setFactoryId(user.getFactoryId());
        if(checkSvc.checkValueByKey(vo2)){
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"这个手机号:["+user.getPhoneNumber()+"]已经被占用!!");
        }
    }


    private  void saveRole(User user) throws ThingsboardException {
        TenantSysRoleEntity entityBy=  tenantSysRoleService.queryEntityBy(RoleEnums.TENANT_ADMIN.getRoleCode(),user.getTenantId().getId());
         if(entityBy == null)
         {
             TenantSysRoleEntity entity = new TenantSysRoleEntity();
             entity.setCreatedUser(getCurrentUser().getUuidId());
             entity.setUpdatedUser(getCurrentUser().getUuidId());
             entity.setRoleCode(RoleEnums.TENANT_ADMIN.getRoleCode());
             entity.setRoleName(RoleEnums.TENANT_ADMIN.getRoleName());
             entity.setTenantId(user.getTenantId().getId());
             entity.setFactoryId(user.getFactoryId());
             entity.setType(user.getType());
             entity.setSystemTab("1");

             TenantSysRoleEntity rmEntity=  tenantSysRoleService.saveEntity(entity);

             UserMenuRoleEntity entityRR = new UserMenuRoleEntity();
             entityRR.setUserId(user.getUuidId());
             entityRR.setTenantSysRoleId(rmEntity.getId());
             entityRR.setTenantId(user.getTenantId().getId());
             userMenuRoleService.saveEntity(entityRR);
             return;
         }


        UserMenuRoleEntity entityRR = new UserMenuRoleEntity();
        entityRR.setUserId(user.getUuidId());
        entityRR.setTenantSysRoleId(entityBy.getId());
        entityRR.setTenantId(user.getTenantId().getId());
        userMenuRoleService.saveEntity(entityRR);

    }




    private  Boolean  configureRoles(User user) throws ThingsboardException {
        List<UUID> rolesId =user.getRoleIds();
        if(CollectionUtils.isEmpty(rolesId))
        {
            return false;
        }

        AdminSettings  settings =  adminSettingsService.findAdminSettingsByKey(getTenantId(),getTenantId().getId().toString());
        if(settings == null)
        {
            return  false;
        }

         JsonNode jsonConfig = settings.getJsonValue();
        String  systemRoleId = jsonConfig.get("systemRoleId").asText();
        log.info("打印systemRoleId：{}",systemRoleId);
        log.info("打印rolesId：{}",rolesId);

     Long  count=   rolesId.stream().filter(id-> id.toString().equals(systemRoleId)).count();
     if(count>0)
     {
         return  true;
     }
     return false;
    }



    private  UUID  getCurrentFactoryId(UUID  factoryId) throws ThingsboardException {
        if(factoryId !=null)
        {
            return  factoryId;
        }
        return   getCurrentUser().getFactoryId();
    }






}
