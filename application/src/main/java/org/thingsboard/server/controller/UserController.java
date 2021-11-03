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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.rule.engine.api.MailService;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
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
import org.thingsboard.server.common.data.vo.PasswordVo;
import org.thingsboard.server.dao.sql.role.entity.UserMenuRoleEntity;
import org.thingsboard.server.dao.sql.role.service.UserMenuRoleService;
import org.thingsboard.server.entity.ResultVo;
import org.thingsboard.server.entity.user.CodeVo;
import org.thingsboard.server.entity.user.UserVo;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.auth.jwt.RefreshTokenRepository;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.model.UserPrincipal;
import org.thingsboard.server.service.security.model.token.JwtTokenFactory;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;
import org.thingsboard.server.service.security.system.SystemSecurityService;
import org.thingsboard.server.service.userrole.UserRoleMemuSvc;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Api(value = "用户管理", tags = {"用户管理接口接口"})
@Slf4j
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class UserController extends BaseController {

    private  static  final String DEFAULT_PASSWORD="123456";//rawPassword

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
    @Autowired  private UserRoleMemuSvc userRoleMemuSvc;
    @Autowired  private UserMenuRoleService userMenuRoleService;



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

            if (Authority.TENANT_ADMIN.equals(getCurrentUser().getAuthority())) {
                user.setTenantId(getCurrentUser().getTenantId());
            }

            checkEntity(user.getId(), user, Resource.USER);

            boolean sendEmail = user.getId() == null && sendActivationMail;
            User savedUser = checkNotNull(userService.saveUser(user));
            if (sendEmail) {
                SecurityUser authUser = getCurrentUser();
                UserCredentials userCredentials = userService.findUserCredentialsByUserId(authUser.getTenantId(), savedUser.getId());
                String baseUrl = systemSecurityService.getBaseUrl(getTenantId(), getCurrentUser().getCustomerId(), request);
                String activateUrl = String.format(ACTIVATE_URL_PATTERN, baseUrl,
                        userCredentials.getActivateToken());
                String email = savedUser.getEmail();
                try {
                    mailService.sendActivationEmail(activateUrl, email);
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
                mailService.sendActivationEmail(activateUrl, email);
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
        System.out.println("当前的入参:"+strUserId);
        checkParameter(USER_ID, strUserId);
        try {
            UserId userId = new UserId(toUUID(strUserId));
            User user = checkUserId(userId, Operation.DELETE);

            List<EdgeId> relatedEdgeIds = findRelatedEdgeIds(getTenantId(), userId);

            userService.deleteUser(getCurrentUser().getTenantId(), userId);

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
            if (Authority.TENANT_ADMIN.equals(currentUser.getAuthority())) {
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
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            return checkNotNull(userService.findTenantAdmins(tenantId, pageLink));
        } catch (Exception e) {
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



    @RequestMapping("/user/test")
    @ResponseBody
    public  String  get()
    {
        return "hello world";
    }


    @ApiOperation(value = "用户管理界面下的修改密码")
    @RequestMapping(value = "/user/changeOthersPassword",method = RequestMethod.POST)
    @ResponseBody
    public Object  changeOthersPassword( @RequestBody PasswordVo vo)
    {
           log.info("【changeOthersPassword】打印当前的入参{}",vo);
           vo.setPassword(passwordEncoder.encode(vo.getPassword()));
           return  userService.changeOthersPassword(vo);
    }


    /**
     * 用户得添加接口
     */
    @ApiOperation(value = "用户管理的添加接口")
    @RequestMapping(value = "/user/save", method = RequestMethod.POST)
    @ResponseBody
    public Object save(@RequestBody User user) throws ThingsboardException {
        try {
            if(user.getId() != null){
                user.setStrId(user.getUuidId().toString());
              return   this.update(user);
            }

            SecurityUser  securityUser =  getCurrentUser();
            TenantId  tenantId  = new TenantId(securityUser.getTenantId().getId());
            user.setTenantId(tenantId);
            log.info("当前的securityUser.getId().toString():{}",securityUser.getId().toString());

            user.setUserCreator(securityUser.getId().toString());
            log.info("当前的登录人:{}",securityUser.getEmail());


            log.info("【用户管理模块.用户添加接口】入参{}", user);
            String  encodePassword =   passwordEncoder.encode(DEFAULT_PASSWORD);
            User savedUser = checkNotNull(userService.save(user,encodePassword));
            userRoleMemuSvc.relationUserBach(user.getRoleIds(),savedUser.getUuidId());
            savedUser.setRoleIds(user.getRoleIds());
            return  savedUser;
        }

        catch (Exception e){
            e.printStackTrace();

            throw  new  ThingsboardException(e.getMessage(),ThingsboardErrorCode.FAIL_VIOLATION);
        }

    }


    @ApiOperation(value = "用户管理界面下的【删除用户接口】")
    @ApiImplicitParams({
            @ApiImplicitParam(name = USER_ID, value = "用户id"),})
    @RequestMapping(value = "/user/delete",method = RequestMethod.DELETE)
    @ResponseBody
    public String    delete(@RequestParam(USER_ID) String strUserId) throws ThingsboardException {
        log.info("【delete User's input parameter ID:{}】",strUserId);
        checkParameter(USER_ID, strUserId);
        UserId userId = new UserId(toUUID(strUserId));
        User user = userService.findUserById(getCurrentUser().getTenantId(), userId);
        checkNotNull(user);
        if(user.getAuthority().equals(Authority.SYS_ADMIN)){
            throw new ThingsboardException("You do not have permission to delete!", ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        userService.deleteUser(getTenantId(),userId);
        userRoleMemuSvc.deleteRoleByUserId(user.getUuidId());
        return "success";
    }

    /**
     * 编辑用户
     */
    @ApiOperation(value = "用户管理的【编辑用户接口】")
    @RequestMapping(value="/user/update",method = RequestMethod.POST)
    @ResponseBody
    public Object update(@RequestBody User user) throws ThingsboardException {
        log.info("打印更新用户的入参:{}",user);
        checkParameter(USER_ID, user.getStrId());
        SecurityUser  securityUser =  getCurrentUser();
        user.setUserCreator(securityUser.getId().toString());
        user.setId( UserId.fromString(user.getStrId()));
        int count =  userService.update(user);
           if(count>0)
           {
               userRoleMemuSvc.updateRoleByUserId(user.getRoleIds(),user.getUuidId());
           }
        user.setRoleIds(user.getRoleIds());
        return  user;
    }

    /**
     * 用户管理的查询接口
     * findAll
     */
    @ApiOperation(value = "查询用户【分页查询】")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "每页大小，默认10"),
            @ApiImplicitParam(name = "page", value = "当前页,起始页，【0开始】"),
            @ApiImplicitParam(name = "textSearch", value = ""),
            @ApiImplicitParam(name = "sortOrder", value = ""),
            @ApiImplicitParam(name = "sortProperty", value = "")



    })
    @RequestMapping(value = "/user/findAll",method = RequestMethod.POST)
    @ResponseBody
    public  Object  findAll(@RequestBody  Map<String, Object> queryParam) throws ThingsboardException {
       int  pageSize =  ((queryParam.get("pageSize"))==null ?10:(int) queryParam.get("pageSize"));
        int  page =  ((queryParam.get("page"))==null ?0:(int) queryParam.get("page"));
        String  textSearch = (String) queryParam.get("textSearch");
        String  sortProperty = (String) queryParam.get("sortProperty");
        String  sortOrder = (String) queryParam.get("sortOrder");
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return userService.findAll( queryParam, pageLink);
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
        SecurityUser  securityUser =  getCurrentUser();
        return  checkSvc.queryCodeNew(vo,securityUser.getTenantId());

    }






}
