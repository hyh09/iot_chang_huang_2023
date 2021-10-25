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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.factory.FactoryId;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractFactoryEntity<T extends Factory> extends BaseSqlEntity<T>{

    @ApiModelProperty("工厂编码")
    @Column(name = "code")
    private String code;

    @ApiModelProperty("工厂名称")
    @Column(name = "name")
    private String name;

    @ApiModelProperty("logo图标")
    @Column(name = "logo_icon")
    private String logoIcon;

    @ApiModelProperty("logo图片")
    @Column(name = "logo_images")
    private String logoImages;

    @ApiModelProperty("工厂地址")
    @Column(name = "adress")
    private String adress;

    @ApiModelProperty("经度")
    @Column(name = "longitude")
    private String longitude;

    @ApiModelProperty("纬度")
    @Column(name = "latitude")
    private String latitude;

    @ApiModelProperty("邮政编码")
    @Column(name = "postal_code")
    private String postalCode;

    @ApiModelProperty("手机号码")
    @Column(name = "mobile")
    private String mobile;

    @ApiModelProperty("邮箱")
    @Column(name = "email")
    private String email;

    @ApiModelProperty("工厂管理员用户标识")
    @Column(name = "admin_user_id")
    private UUID adminUserId;

    @ApiModelProperty("工厂管理员用户标识")
    @Column(name = "admin_user_name")
    private String adminUserName;

    @ApiModelProperty(name = "备注")
    @Column(name = "remark")
    private String remark;

    @ApiModelProperty(name = "租户")
    @Column(name = "tenant_id")
    private UUID tenantId;

    @CreatedDate
    @Column(name = "created_time")
    private long createdTime;

    @CreatedBy
    @Column(name = "created_user")
    private UUID createdUser;

    @CreatedDate
    @Column(name = "updated_time")
    private long updatedTime;

    @CreatedBy
    @Column(name = "updated_user")
    private UUID updatedUser;

    @ApiModelProperty("删除标记（A-未删除；D-已删除）")
    @Column(name = "del_flag")
    private String delFlag = "A";


    public AbstractFactoryEntity() {
        super();
    }

    public AbstractFactoryEntity(AbstractFactoryEntity<T> abstractFactoryEntity){
        if (abstractFactoryEntity.getId() != null) {
            this.setUuid(abstractFactoryEntity.getId());
        }
        this.code = abstractFactoryEntity.getCode();
        this.name = abstractFactoryEntity.getName();
        this.logoIcon = abstractFactoryEntity.getLogoIcon();
        this.logoImages = abstractFactoryEntity.getLogoImages();
        this.adress = abstractFactoryEntity.getAdress();
        this.longitude = abstractFactoryEntity.getLongitude();
        this.latitude = abstractFactoryEntity.getLatitude();
        this.mobile = abstractFactoryEntity.getMobile();
        this.email = abstractFactoryEntity.getEmail();
        this.adminUserId = abstractFactoryEntity.getAdminUserId();
        this.adminUserName = abstractFactoryEntity.getAdminUserName();
        this.remark = abstractFactoryEntity.getRemark();
        this.tenantId = abstractFactoryEntity.getTenantId();
        this.createdTime = abstractFactoryEntity.getUpdatedTime();
        this.createdUser = abstractFactoryEntity.getCreatedUser();
        this.updatedTime = abstractFactoryEntity.getUpdatedTime();
        this.updatedUser = abstractFactoryEntity.getUpdatedUser();
        this.delFlag = abstractFactoryEntity.getDelFlag();
    }

    public AbstractFactoryEntity(Factory factory) {
        if (factory.getId() != null) {
            this.setUuid(factory.getId().getId());
        }
        this.code = factory.getCode();
        this.name = factory.getName();
        this.logoIcon = factory.getLogoIcon();
        this.logoImages = factory.getLogoImages();
        this.adress = factory.getAdress();
        this.longitude = factory.getLongitude();
        this.latitude = factory.getLatitude();
        this.mobile = factory.getMobile();
        this.email = factory.getEmail();
        this.adminUserId = factory.getAdminUserId();
        this.adminUserName = factory.getAdminUserName();
        this.remark = factory.getRemark();
        this.tenantId = factory.getTenantId();
        this.createdTime = factory.getUpdatedTime();
        this.createdUser = factory.getCreatedUser();
        this.updatedTime = factory.getUpdatedTime();
        this.updatedUser = factory.getUpdatedUser();
        this.delFlag = factory.getDelFlag();
    }

    public Factory toFactory(){
        Factory factory = new Factory(new FactoryId(this.getUuid()));
        factory.setCode(code);
        factory.setName(name);
        factory.setLogoIcon(logoIcon);
        factory.setLogoImages(logoImages);
        factory.setAdress(adress);
        factory.setLongitude(longitude);
        factory.setLatitude(latitude);
        factory.setMobile(mobile);
        factory.setEmail(email);
        factory.setAdminUserId(adminUserId);
        factory.setAdminUserName(adminUserName);
        factory.setRemark(remark);
        factory.setTenantId(tenantId);
        factory.setCreatedTime(createdTime);
        factory.setCreatedUser(createdUser);
        factory.setUpdatedTime(updatedTime);
        factory.setUpdatedUser(updatedUser);
        factory.setDelFlag(delFlag);
        return factory;
    }

}
