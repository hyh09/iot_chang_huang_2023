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
package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.security.Authority;

import org.thingsboard.server.common.data.validation.NoXss;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.UUID;

@ToString
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "用户的请求参数实体类")
public class User extends SearchTextBasedWithAdditionalInfo<UserId> implements HasName, HasTenantId, HasCustomerId {

    private static final long serialVersionUID = 8250339805336035966L;

    @ApiModelProperty(value = "数据字典Id,【用于接收编辑接口的用户id】")
    private  String strId;

    private TenantId tenantId;
    private CustomerId customerId;
    private String email;

    /**
     * 添加一个手机号
     */
    @ApiModelProperty(value = "手机号")
    private  String phoneNumber;
    /**
     * 启用状态
     *  1是启用
     *  0是未启用
     */
    @ApiModelProperty(value = "启用状态{1是启用,0是未启用}")
    private  String  activeStatus="1";
    /**
     * 用户编码
     */
    @ApiModelProperty(value = "用户编码")
    private  String userCode;
    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    private  String userName;

    private String userCreator;

    /**
     * 创建者的类别 :租户类别  工厂类别
     */
    @ApiModelProperty(value = "如果是工厂管理员新增字段【必传写死 FACTORY_MANAGEMENT】 #不用传了;此值后端赋值")
    private  String type="";

    /**
     * 0为默认
     * 1为工厂管理员角色
     * 3为租户管理员角色
     */
    private  int userLevel=0;

    /**
     * 工厂id
     */
    @ApiModelProperty(value = "如果是工厂管理员新增字段【必传】")
    private UUID  factoryId;


    private  Integer operationType;



    //用户角色id
    @ApiModelProperty(value = "用户所需要绑定的角色【角色di】 更新的时候为空会清理此角色")
    private  List<UUID> roleIds;


    private Authority authority;
    @NoXss
    private String firstName;
    @NoXss
    private String lastName;

    private  String tenantTitle;
    private  String factoryName;


    public User() {
        super();
    }

    public User(UserId id) {
        super(id);
    }

    public User(User user) {
        super(user);
        this.tenantId = user.getTenantId();
        this.customerId = user.getCustomerId();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.activeStatus  = user.getActiveStatus();
        this.userCode = user.getUserCode();
        this.userName = user.getUserName();
        this.userCreator=user.getUserCreator();
        this.type = user.getType();
        this.factoryId = user.getFactoryId();
        this.userLevel = user.getUserLevel();
        this.operationType =user.getOperationType();
        this.authority = user.getAuthority();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getName() {
        return email;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    @Override
    public String getSearchText() {
        return getEmail();
    }



    //    @Override
//    public String toString() {
//        StringBuilder builder = new StringBuilder();
//        builder.append("User [tenantId=");
//        builder.append(tenantId);
//        builder.append(", customerId=");
//        builder.append(customerId);
//        builder.append(", email=");
//        builder.append(email);
//        builder.append(", phoneNumber=");
//        builder.append(phoneNumber);
//        builder.append(", userCreator=");
//        builder.append(userCreator);
//        builder.append(", authority=");
//        builder.append(authority);
//        builder.append(", firstName=");
//        builder.append(firstName);
//        builder.append(", lastName=");
//        builder.append(lastName);
//        builder.append(", additionalInfo=");
//        builder.append(getAdditionalInfo());
//        builder.append(", createdTime=");
//        builder.append(createdTime);
//        builder.append(", id=");
//        builder.append(id);
//        builder.append("]");
//        return builder.toString();
//    }

    @JsonIgnore
    public boolean isSystemAdmin() {
        return tenantId == null || EntityId.NULL_UUID.equals(tenantId.getId());
    }

    @JsonIgnore
    public boolean isTenantAdmin() {
        return !isSystemAdmin() && (customerId == null || EntityId.NULL_UUID.equals(customerId.getId()));
    }

    @JsonIgnore
    public boolean isCustomerUser() {
        return !isSystemAdmin() && !isTenantAdmin();
    }
}
