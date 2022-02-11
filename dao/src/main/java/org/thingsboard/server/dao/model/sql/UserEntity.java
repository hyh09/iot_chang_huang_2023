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
package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.SearchTextEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 4/21/2017.
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.USER_PG_HIBERNATE_COLUMN_FAMILY_NAME)
public class UserEntity extends BaseSqlEntity<User> implements SearchTextEntity<User> {

    @Column(name = ModelConstants.USER_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @Column(name = ModelConstants.USER_CUSTOMER_ID_PROPERTY)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = ModelConstants.USER_AUTHORITY_PROPERTY)
    private Authority authority;

    @Column(name = ModelConstants.USER_EMAIL_PROPERTY, unique = true)
    private String email;

    /**
     * 添加一个手机号
     */
    @Column(name = ModelConstants.USER_PHONE_NUMBER_PROPERTY, unique = true)
    private  String phoneNumber;
    /**
     * 启用状态
     */
    @Column(name = ModelConstants.USER_ACTIVE_STATUS_PROPERTY)
    private  String  activeStatus="1";
    /**
     * 用户编码
     */
    @Column(name = ModelConstants.USER_USERCODE_PROPERTY)
    private  String userCode;
    /**
     * 用户名称
     */
    @Column(name = ModelConstants.USER_USER_NAME_PROPERTY)
    private  String userName;

    /**
     * 用户创建者
     */
    @Column(name = ModelConstants.USER_USER_CREATOR_PROPERTY)
    private String userCreator;

    /**
     * 被创建者的类型:
     */
    @Column(name = ModelConstants.USER_USER_TYPE)
    private  String type;

    /**
     * 0为默认
     * 1为工厂管理员角色
     * 3为租户管理员角色
     * 4为 用户系统管理员
     */
    @Column(name = ModelConstants.USER_USER_LEVEL)
    private  int userLevel=0;

    /**
     * 工厂id
     */
    @Column(name = ModelConstants.USER_USER_FACTORY_ID)
    private UUID  factoryId;

    /**
     *  0是默认值  正常逻辑
     *  1 是只允许修改密码 （不可以编辑修改， 删除)
     */
    @Column(name = ModelConstants.USER_OPERATION_TYPE,columnDefinition="varchar(25) default '0'")
    private  Integer  operationType=0;




    @Column(name = ModelConstants.SEARCH_TEXT_PROPERTY)
    private String searchText;

    @Column(name = ModelConstants.USER_FIRST_NAME_PROPERTY)
    private String firstName;

    @Column(name = ModelConstants.USER_LAST_NAME_PROPERTY)
    private String lastName;

    @Type(type = "json")
    @Column(name = ModelConstants.USER_ADDITIONAL_INFO_PROPERTY)
    private JsonNode additionalInfo;

    public UserEntity() {
    }

    public UserEntity(User user) {
        if (user.getId() != null) {
            this.setUuid(user.getId().getId());
        }
        this.setCreatedTime(user.getCreatedTime());
        this.authority = user.getAuthority();
        if (user.getTenantId() != null) {
            this.tenantId = user.getTenantId().getId();
        }
        if (user.getCustomerId() != null) {
            this.customerId = user.getCustomerId().getId();
        }
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.additionalInfo = user.getAdditionalInfo();

        this.phoneNumber = user.getPhoneNumber();
        this.activeStatus =user.getActiveStatus();
        this.userCode = user.getUserCode();
        this.userName = user.getUserName();
        this.userCreator = user.getUserCreator();
        this.type  = user.getType();
        this.factoryId = user.getFactoryId();
        this.userLevel = user.getUserLevel();
        this.operationType = user.getOperationType();

    }

    @Override
    public String getSearchTextSource() {
        return email;
    }

    @Override
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    /**
     * 将实体类转换前后端交互的数据类
     * @return
     */
    @Override
    public User toData() {
        User user = new User(new UserId(this.getUuid()));
        user.setCreatedTime(createdTime);
        user.setAuthority(authority);
        if (tenantId != null) {
            user.setTenantId(new TenantId(tenantId));
        }
        if (customerId != null) {
            user.setCustomerId(new CustomerId(customerId));
        }
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAdditionalInfo(additionalInfo);

        user.setPhoneNumber(phoneNumber);
        user.setUserName(userName);
        user.setUserCode(userCode);
        user.setActiveStatus(activeStatus);
        user.setUserCreator(userCreator);

        user.setFactoryId(factoryId);
        user.setUserLevel(userLevel);
        if(type != null)
        {
            user.setType(type);

        }
        user.setOperationType(operationType);

        return user;
    }


    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }
}
