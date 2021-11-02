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
package org.thingsboard.server.entity.productionline;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.id.productionline.ProductionLineId;
import org.thingsboard.server.common.data.productionline.ProductionLine;

import java.util.UUID;

@Data
public abstract class AbstractProductionLine{

    @ApiModelProperty("产线标识")
    public UUID id;

    @ApiModelProperty("车间标识")
    public UUID workshopId;

    @ApiModelProperty("工厂标识")
    private UUID factoryId;

    @ApiModelProperty("生产线编码")
    public String code;

    @ApiModelProperty("生产线名称")
    public String name;

    @ApiModelProperty("logo图标")
    public String logoIcon;

    @ApiModelProperty("logo图片")
    public String logoImages;

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

    public AbstractProductionLine() {
        super();
    }

    public AbstractProductionLine(ProductionLine productionLine) {
        if (productionLine.getId() != null) {
            this.setId(productionLine.getId().getId());
        }
        this.workshopId = productionLine.getWorkshopId();
        this.factoryId = productionLine.getFactoryId();
        this.code = productionLine.getCode();
        this.name = productionLine.getName();
        this.logoIcon = productionLine.getLogoIcon();
        this.logoImages = productionLine.getLogoImages();
        this.remark = productionLine.getRemark();
        this.tenantId = productionLine.getTenantId();
        this.createdTime = productionLine.getUpdatedTime();
        this.createdUser = productionLine.getCreatedUser();
        this.updatedTime = productionLine.getUpdatedTime();
        this.updatedUser = productionLine.getUpdatedUser();
        this.delFlag = productionLine.getDelFlag();
    }

    public ProductionLine toProductionLine(){
        ProductionLine productionLine = new ProductionLine(new ProductionLineId(this.getId()));
        productionLine.setWorkshopId(workshopId);
         productionLine.setFactoryId(factoryId);
        productionLine.setCode(code);
        productionLine.setName(name);
        productionLine.setLogoIcon(logoIcon);
        productionLine.setLogoImages(logoImages);
        productionLine.setRemark(remark);
        productionLine.setTenantId(tenantId);
        productionLine.setCreatedTime(createdTime);
        productionLine.setCreatedUser(createdUser);
        productionLine.setUpdatedTime(updatedTime);
        productionLine.setUpdatedUser(updatedUser);
        productionLine.setDelFlag(delFlag);
        return productionLine;
    }

}
