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
package org.thingsboard.server.entity.workshop;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.id.workshop.WorkshopId;
import org.thingsboard.server.common.data.workshop.Workshop;

import java.util.UUID;

@Data
public abstract class AbstractWorkshop{

    @ApiModelProperty("车间标识")
    private UUID id;

    @ApiModelProperty("工厂标识")
    private UUID factoryId;

    @ApiModelProperty("车间编码")
    private String code;

    @ApiModelProperty("车间名称")
    private String name;

    @ApiModelProperty("logo图标")
    private String logoIcon;

    @ApiModelProperty("logo图片")
    private String logoImages;

    @ApiModelProperty("看板背景图片")
    private String bgImages;

    @ApiModelProperty(name = "备注")
    private String remark;
    @ApiModelProperty(name = "租户")
    private UUID tenantId;
    @ApiModelProperty("创建人标识")
    public UUID createdUser;
    @ApiModelProperty("创建时间")
    public long createdTime;
    @ApiModelProperty("修改时间")
    public long updatedTime;
    @ApiModelProperty("修改人")
    public UUID updatedUser;
    @ApiModelProperty("删除标记（A-未删除；D-已删除）")
    private String delFlag = "A";


    public AbstractWorkshop() {
        super();
    }

    public AbstractWorkshop(Workshop workshop) {
        if (workshop.getId() != null) {
            this.setId(workshop.getId().getId());
        }
        this.factoryId = workshop.getFactoryId();
        this.code = workshop.getCode();
        this.name = workshop.getName();
        this.logoIcon = workshop.getLogoIcon();
        this.logoImages = workshop.getLogoImages();
        this.bgImages = workshop.getBgImages();
        this.remark = workshop.getRemark();
        this.tenantId = workshop.getTenantId();
        this.createdTime = workshop.getUpdatedTime();
        this.createdUser = workshop.getCreatedUser();
        this.updatedTime = workshop.getUpdatedTime();
        this.updatedUser = workshop.getUpdatedUser();
        this.delFlag = workshop.getDelFlag();
    }

    public Workshop toWorkshop(){
        Workshop workshop = new Workshop(new WorkshopId(this.getId()));
        workshop.setFactoryId(factoryId);
        workshop.setCode(code);
        workshop.setName(name);
        workshop.setLogoIcon(logoIcon);
        workshop.setLogoImages(logoImages);
        workshop.setBgImages(bgImages);
        workshop.setRemark(remark);
        workshop.setTenantId(tenantId);
        workshop.setCreatedTime(createdTime);
        workshop.setCreatedUser(createdUser);
        workshop.setUpdatedTime(updatedTime);
        workshop.setUpdatedUser(updatedUser);
        workshop.setDelFlag(delFlag);
        return workshop;
    }

}
