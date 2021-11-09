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
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractWorkshopEntity<T extends Workshop> extends BaseSqlEntity<T>{

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

    //删除标记（A-未删除；D-已删除）
    @Column(name = "del_flag")
    private String delFlag = "A";

    //产线
    //public List<ProductionLineEntity> productionLineEntityList;
    @Transient
    private AbstractProductionLineEntity productionLineEntityList;


    public AbstractWorkshopEntity() {
        super();
    }

    public AbstractWorkshopEntity(AbstractWorkshopEntity<T> abstractWorkshopEntity){
        if (abstractWorkshopEntity.getId() != null) {
            this.setId(abstractWorkshopEntity.getId());
        }
        this.factoryId = abstractWorkshopEntity.getFactoryId();
        this.code = abstractWorkshopEntity.getCode();
        this.name = abstractWorkshopEntity.getName();
        this.logoIcon = abstractWorkshopEntity.getLogoIcon();
        this.logoImages = abstractWorkshopEntity.getLogoImages();
        this.bgImages = abstractWorkshopEntity.getBgImages();
        this.remark = abstractWorkshopEntity.getRemark();
        this.tenantId = abstractWorkshopEntity.getTenantId();
        this.createdTime = abstractWorkshopEntity.getUpdatedTime();
        this.createdUser = abstractWorkshopEntity.getCreatedUser();
        this.updatedTime = abstractWorkshopEntity.getUpdatedTime();
        this.updatedUser = abstractWorkshopEntity.getUpdatedUser();
        this.delFlag = abstractWorkshopEntity.getDelFlag();
    }

    public AbstractWorkshopEntity(Workshop workshop) {
        if (workshop.getId() != null) {
            this.setId(workshop.getId());
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
        Workshop workshop = new Workshop(id);
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
