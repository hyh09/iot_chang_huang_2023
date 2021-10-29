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
package org.thingsboard.server.entity.factory;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.factory.FactoryId;

import java.util.UUID;

@Data
public abstract class AbstractFactory{

    @ApiModelProperty("工厂标识")
    public UUID id;

    @ApiModelProperty("工厂编码")
    public String code;

    @ApiModelProperty("工厂名称")
    public String name;

    @ApiModelProperty("logo图标")
    public String logoIcon;

    @ApiModelProperty("logo图片")
    public String logoImages;

    @ApiModelProperty("工厂地址")
    public String address;

    @ApiModelProperty("经度")
    public String longitude;

    @ApiModelProperty("纬度")
    public String latitude;

    @ApiModelProperty("邮政编码")
    public String postalCode;

    @ApiModelProperty("手机号码")
    public String mobile;

    @ApiModelProperty("邮箱")
    public String email;

    @ApiModelProperty("工厂管理员用户标识")
    public UUID adminUserId;

    @ApiModelProperty("工厂管理员用户标识")
    public String adminUserName;

    @ApiModelProperty(name = "备注")
    public String remark;

    @ApiModelProperty(name = "租户")
    public UUID tenantId;

    @ApiModelProperty("创建人标识")
    public UUID createdUser;
    @ApiModelProperty("创建时间")
    public long createdTime;
    @ApiModelProperty("修改时间")
    public long updatedTime;
    @ApiModelProperty("修改人")
    public UUID updatedUser;
    @ApiModelProperty("删除标记（A-未删除；D-已删除）")
    public String delFlag = "A";


    public AbstractFactory() {
        super();
    }


    public AbstractFactory(Factory factory) {
        if (factory.getId() != null) {
            this.setId(factory.getId().getId());
        }
        this.code = factory.getCode();
        this.name = factory.getName();
        this.logoIcon = factory.getLogoIcon();
        this.logoImages = factory.getLogoImages();
        this.address = factory.getAddress();
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
        Factory factory = new Factory(new FactoryId(this.getId()));
        factory.setCode(code);
        factory.setName(name);
        factory.setLogoIcon(logoIcon);
        factory.setLogoImages(logoImages);
        factory.setAddress(address);
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
