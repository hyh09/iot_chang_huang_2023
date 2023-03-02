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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoginRequest {
    private String username;
    private String password;
    private String loginPlatform;
    /**
     * 工厂用户 factoryId 不为空
     * 云端用户：工厂id为空
     */
    private String factoryId;
    //平台：0  ---  租户  type:租户
    //内网：1	  --- 工厂  type: 工厂

    private String appUrl;
//    @JsonCreator
//    public LoginRequest(@JsonProperty("username") String username, @JsonProperty("password") String password) {
//        this.username = username;
//        this.password = password;
//    }


    @JsonCreator
    public LoginRequest(@JsonProperty("username") String username, @JsonProperty("password") String password,
                        @JsonProperty("factoryId") String factoryId, @JsonProperty("loginPlatform") String loginPlatform,
                        @JsonProperty("appUrl") String appUrl
    ) {
        this.username = username;
        this.password = password;
        this.loginPlatform = loginPlatform;
        this.factoryId = factoryId;
        this.appUrl = appUrl;

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
