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
package org.thingsboard.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.oauth2.OAuth2ClientInfo;
import org.thingsboard.server.common.data.oauth2.OAuth2Info;
import org.thingsboard.server.common.data.oauth2.PlatformType;
import org.thingsboard.server.dao.oauth2.OAuth2Configuration;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;
import org.thingsboard.server.utils.MiscUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

@RestController
@TbCoreComponent
@RequestMapping("/api")
@Slf4j
public class OAuth2Controller extends BaseController {

    @Autowired
    private OAuth2Configuration oAuth2Configuration;

    @RequestMapping(value = "/noauth/oauth2Clients", method = RequestMethod.POST)
    @ResponseBody
    public List<OAuth2ClientInfo> getOAuth2Clients(HttpServletRequest request,
                                                   @RequestParam(required = false) String pkgName,
                                                   @RequestParam(required = false) String platform) throws ThingsboardException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Executing getOAuth2Clients: [{}][{}][{}]", request.getScheme(), request.getServerName(), request.getServerPort());
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String header = headerNames.nextElement();
                    log.debug("Header: {} {}", header, request.getHeader(header));
                }
            }
            PlatformType platformType = null;
            if (StringUtils.isNotEmpty(platform)) {
                try {
                    platformType = PlatformType.valueOf(platform);
                } catch (Exception e) {
                }
            }
            return oAuth2Service.getOAuth2Clients(MiscUtils.getScheme(request), MiscUtils.getDomainNameAndPort(request), pkgName, platformType);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/oauth2/config", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public OAuth2Info getCurrentOAuth2Info() throws ThingsboardException {
        try {
            accessControlService.checkPermission(getCurrentUser(), Resource.OAUTH2_CONFIGURATION_INFO, Operation.READ);
            return oAuth2Service.findOAuth2Info();
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * oauth2.0的配置接口
     * 系统管理员界面下的配置
     *
     * @param oauth2Info
     * @return
     * @throws ThingsboardException
     */
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/oauth2/config", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public OAuth2Info saveOAuth2Info(@RequestBody OAuth2Info oauth2Info) throws ThingsboardException {
        try {
            accessControlService.checkPermission(getCurrentUser(), Resource.OAUTH2_CONFIGURATION_INFO, Operation.WRITE);
            oAuth2Service.saveOAuth2Info(oauth2Info);
            return oAuth2Service.findOAuth2Info();
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/oauth2/loginProcessingUrl", method = RequestMethod.GET)
    @ResponseBody
    public String getLoginProcessingUrl() throws ThingsboardException {
        try {
            accessControlService.checkPermission(getCurrentUser(), Resource.OAUTH2_CONFIGURATION_INFO, Operation.READ);
            return "\"" + oAuth2Configuration.getLoginProcessingUrl() + "\"";
        } catch (Exception e) {
            throw handleException(e);
        }
    }
}
