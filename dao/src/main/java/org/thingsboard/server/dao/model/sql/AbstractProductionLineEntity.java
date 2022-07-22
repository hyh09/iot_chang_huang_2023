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

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.common.data.productionline.ProductionLine;
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
public abstract class AbstractProductionLineEntity<T extends ProductionLine> extends BaseSqlEntity<T>{

    @Column(name = "workshop_id")
    private UUID workshopId;

    @Column(name = "factory_id")
    private UUID factoryId;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "logo_icon")
    private String logoIcon;

    @Column(name = "logo_images")
    private String logoImages;

    @Column(name = "bg_images")
    private String bgImages;

    @Column(name = "remark")
    private String remark;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @CreatedDate
    @Column(name = "created_time")
    private long createdTime;

    @Column(name = "created_user")
    private UUID createdUser;

    @LastModifiedDate
    @Column(name = "updated_time")
    private long updatedTime;

    @Column(name = "updated_user")
    private UUID updatedUser;

    @Column(name = "del_flag")
    private String delFlag = "A";

    @Column(name = "sort")
    private Integer sort;

    public AbstractProductionLineEntity() {
        super();
    }

    public AbstractProductionLineEntity(AbstractProductionLineEntity<T> abstractProdutionLineEntity){
        if (abstractProdutionLineEntity.getId() != null) {
            this.setId(abstractProdutionLineEntity.getId());
        }
        this.workshopId = abstractProdutionLineEntity.getWorkshopId();
        this.factoryId = abstractProdutionLineEntity.getFactoryId();
        this.code = abstractProdutionLineEntity.getCode();
        this.name = abstractProdutionLineEntity.getName();
        this.logoIcon = abstractProdutionLineEntity.getLogoIcon();
        this.logoImages = abstractProdutionLineEntity.getLogoImages();
        this.bgImages = abstractProdutionLineEntity.getBgImages();
        this.remark = abstractProdutionLineEntity.getRemark();
        this.tenantId = abstractProdutionLineEntity.getTenantId();
        this.createdTime = abstractProdutionLineEntity.getUpdatedTime();
        this.createdUser = abstractProdutionLineEntity.getCreatedUser();
        this.updatedTime = abstractProdutionLineEntity.getUpdatedTime();
        this.updatedUser = abstractProdutionLineEntity.getUpdatedUser();
        this.delFlag = abstractProdutionLineEntity.getDelFlag();
    }

    public AbstractProductionLineEntity(ProductionLine productionLine) {
        if (productionLine.getId() != null) {
            this.setId(productionLine.getId());
        }
        this.workshopId = productionLine.getWorkshopId();
        this.factoryId = productionLine.getFactoryId();
        this.code = productionLine.getCode();
        this.name = productionLine.getName();
        this.logoIcon = productionLine.getLogoIcon();
        this.logoImages = productionLine.getLogoImages();
        this.bgImages = productionLine.getBgImages();
        this.remark = productionLine.getRemark();
        this.tenantId = productionLine.getTenantId();
        this.createdTime = productionLine.getUpdatedTime();
        this.createdUser = productionLine.getCreatedUser();
        this.updatedTime = productionLine.getUpdatedTime();
        this.updatedUser = productionLine.getUpdatedUser();
        this.delFlag = productionLine.getDelFlag();
        if(productionLine.getSort() == null){
            this.sort = 0;
        }else {
            this.sort = productionLine.getSort();
        }
    }

    public ProductionLine toProductionLine(){
        ProductionLine productionLine = new ProductionLine();
        productionLine.setId(id);
        productionLine.setWorkshopId(workshopId);
        productionLine.setFactoryId(factoryId);
        productionLine.setCode(code);
        productionLine.setName(name);
        productionLine.setLogoIcon(logoIcon);
        productionLine.setLogoImages(logoImages);
        productionLine.setBgImages(bgImages);
        productionLine.setRemark(remark);
        productionLine.setTenantId(tenantId);
        productionLine.setCreatedTime(createdTime);
        productionLine.setCreatedUser(createdUser);
        productionLine.setUpdatedTime(updatedTime);
        productionLine.setUpdatedUser(updatedUser);
        productionLine.setDelFlag(delFlag);
        productionLine.setSort(sort);
        return productionLine;
    }

}
