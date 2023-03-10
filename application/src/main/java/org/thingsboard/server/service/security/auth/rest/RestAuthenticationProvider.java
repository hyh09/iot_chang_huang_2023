/**
 * Copyright © 2016-2021 The Thingsboard Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.service.security.auth.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.common.data.vo.user.enums.CreatorTypeEnum;
import org.thingsboard.server.dao.audit.AuditLogService;
import org.thingsboard.server.dao.customer.CustomerService;
import org.thingsboard.server.dao.sql.factoryUrl.service.FactoryURLAppTableService;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.service.security.auth.ProviderEnums;
import org.thingsboard.server.service.security.exception.UserDoesNotExistException;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.model.UserPrincipal;
import org.thingsboard.server.service.security.system.SystemSecurityService;
import ua_parser.Client;

import java.util.UUID;


@Component
@Slf4j
public class RestAuthenticationProvider implements AuthenticationProvider {

    private final SystemSecurityService systemSecurityService;
    private final UserService userService;
    private final CustomerService customerService;
    private final AuditLogService auditLogService;
    private final FactoryURLAppTableService factoryURLAppTableService;

    @Autowired
    public RestAuthenticationProvider(final UserService userService,
                                      final CustomerService customerService,
                                      final SystemSecurityService systemSecurityService,
                                      final AuditLogService auditLogService,
                                      final FactoryURLAppTableService factoryURLAppTableService
    ) {
        this.userService = userService;
        this.customerService = customerService;
        this.systemSecurityService = systemSecurityService;
        this.auditLogService = auditLogService;
        this.factoryURLAppTableService = factoryURLAppTableService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal)) {
            throw new BadCredentialsException("Authentication Failed. Bad user principal.");
        }
        UserPrincipal userPrincipal = (UserPrincipal) principal;
        if (userPrincipal.getType() == UserPrincipal.Type.USER_NAME) {
            String username = userPrincipal.getValue();
            LoginRequest loginRequest = (LoginRequest) authentication.getCredentials();

            return authenticateByUsernameAndPassword(authentication, userPrincipal, username, loginRequest);
        } else {
            String publicId = userPrincipal.getValue();
            return authenticateByPublicId(userPrincipal, publicId);
        }
    }

    private Authentication authenticateByUsernameAndPassword(Authentication authentication, UserPrincipal userPrincipal, String username, LoginRequest loginRequest) {
        String password = loginRequest.getPassword();
        /** 本地测试去掉的代码 start */
//        if (StringUtils.isNotBlank(loginRequest.getAppUrl())) {
//            FactoryURLAppTableEntity factoryURLAppTableEntity = factoryURLAppTableService.queryAllByAppUrl(loginRequest.getAppUrl());
//            if (factoryURLAppTableEntity == null) {
//                log.info("查询不到配置表信息【FACTORY_URL_APP_TABLE】入参为{}", loginRequest.getAppUrl());
//                throw new UserDoesNotExistException(" user does not exist ");
//            }
//            loginRequest.setFactoryId(factoryURLAppTableEntity.getFactoryId());
//        }
        /** 本地测试去掉的代码 start */


        User user = new User();
        if (isEmail(username)) {
            user = userService.findUserByEmailAndfactoryId(TenantId.SYS_TENANT_ID, username, loginRequest.getFactoryId());
            //checkUserLogin(user, loginRequest);
        } else {
            user = userService.findByPhoneNumberAndFactoryId(username, loginRequest.getFactoryId());
        }
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        try {

            UserCredentials userCredentials = userService.findUserCredentialsByUserId(TenantId.SYS_TENANT_ID, user.getId());
            if (userCredentials == null) {
                throw new UsernameNotFoundException("User credentials not found");
            }

            try {
                systemSecurityService.validateUserCredentials(user.getTenantId(), userCredentials, username, password);
            } catch (LockedException e) {
                logLoginAction(user, authentication, ActionType.LOCKOUT, null);
                throw e;
            }

            if (user.getAuthority() == null)
                throw new InsufficientAuthenticationException("User has no authority assigned");

            SecurityUser securityUser = new SecurityUser(user, userCredentials.isEnabled(), userPrincipal);
            logLoginAction(user, authentication, ActionType.LOGIN, null);
//            log.info("====securityUser.getType();=>"+securityUser.getType());
            return new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        } catch (Exception e) {
            logLoginAction(user, authentication, ActionType.LOGIN, e);
            throw e;
        }
    }

    private Authentication authenticateByPublicId(UserPrincipal userPrincipal, String publicId) {
        CustomerId customerId;
        try {
            customerId = new CustomerId(UUID.fromString(publicId));
        } catch (Exception e) {
            throw new BadCredentialsException("Authentication Failed. Public Id is not valid.");
        }
        Customer publicCustomer = customerService.findCustomerById(TenantId.SYS_TENANT_ID, customerId);
        if (publicCustomer == null) {
            throw new UsernameNotFoundException("Public entity not found: " + publicId);
        }
        if (!publicCustomer.isPublic()) {
            throw new BadCredentialsException("Authentication Failed. Public Id is not valid.");
        }
        User user = new User(new UserId(EntityId.NULL_UUID));
        user.setTenantId(publicCustomer.getTenantId());
        user.setCustomerId(publicCustomer.getId());
        user.setEmail(publicId);
        user.setAuthority(Authority.CUSTOMER_USER);
        user.setFirstName("Public");
        user.setLastName("Public");

        SecurityUser securityUser = new SecurityUser(user, true, userPrincipal);

        return new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    private void logLoginAction(User user, Authentication authentication, ActionType actionType, Exception e) {
        String clientAddress = "Unknown";
        String browser = "Unknown";
        String os = "Unknown";
        String device = "Unknown";
        if (authentication != null && authentication.getDetails() != null) {
            if (authentication.getDetails() instanceof RestAuthenticationDetails) {
                RestAuthenticationDetails details = (RestAuthenticationDetails) authentication.getDetails();
                clientAddress = details.getClientAddress();
                if (details.getUserAgent() != null) {
                    Client userAgent = details.getUserAgent();
                    if (userAgent.userAgent != null) {
                        browser = userAgent.userAgent.family;
                        if (userAgent.userAgent.major != null) {
                            browser += " " + userAgent.userAgent.major;
                            if (userAgent.userAgent.minor != null) {
                                browser += "." + userAgent.userAgent.minor;
                                if (userAgent.userAgent.patch != null) {
                                    browser += "." + userAgent.userAgent.patch;
                                }
                            }
                        }
                    }
                    if (userAgent.os != null) {
                        os = userAgent.os.family;
                        if (userAgent.os.major != null) {
                            os += " " + userAgent.os.major;
                            if (userAgent.os.minor != null) {
                                os += "." + userAgent.os.minor;
                                if (userAgent.os.patch != null) {
                                    os += "." + userAgent.os.patch;
                                    if (userAgent.os.patchMinor != null) {
                                        os += "." + userAgent.os.patchMinor;
                                    }
                                }
                            }
                        }
                    }
                    if (userAgent.device != null) {
                        device = userAgent.device.family;
                    }
                }
            }
        }
        auditLogService.logEntityAction(
                user.getTenantId(), user.getCustomerId(), user.getId(),
                user.getName(), user.getId(), null, actionType, e, clientAddress, browser, os, device);
    }


    private static boolean isEmail(String string) {
        return true;
//        log.info("【isEmail】打印当前得入参:{}",string);
//        if (string == null)
//            return false;
//        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
//        Pattern p;
//        Matcher m;
//        p = Pattern.compile(regEx1);
//        m = p.matcher(string);
//        if (m.matches())
//            return true;
//        else
//            return false;
    }


    private void checkUserLogin(User user, LoginRequest loginRequest) {
        if (user == null) {
            throw new UserDoesNotExistException(" user does not exist ");
        }

        if (user.getAuthority().equals(Authority.SYS_ADMIN)) {
            return;
        }


//        if(StringUtils.isNotBlank(loginRequest.getAppUrl()))
//        {
//            FactoryURLAppTableEntity  factoryURLAppTableEntity =  factoryURLAppTableService.queryAllByAppUrl(loginRequest.getAppUrl());
//            if(factoryURLAppTableEntity == null)
//            {
//                log.info("查询不到配置表信息【FACTORY_URL_APP_TABLE】入参为{}",loginRequest.getAppUrl());
//               throw  new UserDoesNotExistException(" user does not exist ");
//            }
//            loginRequest.setFactoryId(factoryURLAppTableEntity.getFactoryId());
//        }

        String userType = user.getType();
        String factoryId = loginRequest.getFactoryId();
        if (StringUtils.isNotBlank(loginRequest.getLoginPlatform()) && loginRequest.getLoginPlatform().equals(ProviderEnums.Intranet_1.getCode())) {
            if (!userType.equals(CreatorTypeEnum.FACTORY_MANAGEMENT.getCode())) {
                log.info("(内网)登录的邮箱[内网的]只能工厂类型登录:{}", user.getEmail());
                throw new UserDoesNotExistException("User not found: " + user.getEmail());
            }

            if (StringUtils.isEmpty(factoryId)) {
                log.info("(工厂为空)登录的邮箱[内网的]只能工厂类型登录:{}", user.getEmail());
                throw new UserDoesNotExistException("User not found: " + user.getEmail());
            }

            if (!user.getFactoryId().equals(UUID.fromString(factoryId))) {
                log.info("(工厂不等)登录的邮箱[内网的]只能工厂类型登录:{}", user.getEmail());
                throw new UserDoesNotExistException("User not found: " + user.getEmail());
            }

        } else {
            //默认是平台的
            if (!userType.equals(CreatorTypeEnum.TENANT_CATEGORY.getCode())) {
                log.info("(默认是平台的)登录的邮箱[内网的]只能工厂类型登录:{}", user.getEmail());
                throw new UserDoesNotExistException("User not found: " + user.getEmail());
            }
        }


    }
}
