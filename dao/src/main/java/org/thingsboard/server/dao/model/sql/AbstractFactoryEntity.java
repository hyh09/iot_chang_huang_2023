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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.common.data.factory.Factory;
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
public abstract class AbstractFactoryEntity<T extends Factory> extends BaseSqlEntity<T>{

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "logo_icon")
    private String logoIcon;

    @Column(name = "logo_images")
    private String logoImages;

    @Column(name = "address")
    private String address;

    @Column(name = "country")
    private String country;

    @Column(name = "province")
    private String province;

    @Column(name = "city")
    private String city;

    @Column(name = "area")
    private String area;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "email")
    private String email;

    @Column(name = "remark")
    private String remark;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "created_time")
    private long createdTime;

    @Column(name = "created_user")
    private UUID createdUser;

    @Column(name = "updated_time")
    private long updatedTime;

    @Column(name = "updated_user")
    private UUID updatedUser;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "del_flag")
    private String delFlag = "A";

    //车间
    //public List<WorkshopEntity> workshopEntityList;
    @Transient
    private AbstractWorkshopEntity workshopEntityList;


    public AbstractFactoryEntity() {
        super();
    }

    public AbstractFactoryEntity(AbstractFactoryEntity<T> abstractFactoryEntity){
        if (abstractFactoryEntity.getId() != null) {
            this.setId(abstractFactoryEntity.getId());
        }
        this.code = abstractFactoryEntity.getCode();
        this.name = abstractFactoryEntity.getName();
        this.logoIcon = abstractFactoryEntity.getLogoIcon();
        this.logoImages = abstractFactoryEntity.getLogoImages();
        this.address = abstractFactoryEntity.getAddress();
        this.country = abstractFactoryEntity.getCountry();
        this.province = abstractFactoryEntity.getProvince();
        this.city = abstractFactoryEntity.getCity();
        this.area = abstractFactoryEntity.getArea();
        this.longitude = abstractFactoryEntity.getLongitude();
        this.latitude = abstractFactoryEntity.getLatitude();
        this.mobile = abstractFactoryEntity.getMobile();
        this.email = abstractFactoryEntity.getEmail();
        this.postalCode = abstractFactoryEntity.getPostalCode();
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
            this.setId(factory.getId());
        }
        this.code = factory.getCode();
        this.name = factory.getName();
        this.logoIcon = factory.getLogoIcon();
        this.logoImages = factory.getLogoImages();
        this.address = factory.getAddress();
        this.country = factory.getCountry();
        this.province = factory.getProvince();
        this.city = factory.getCity();
        this.area = factory.getArea();
        this.longitude = factory.getLongitude();
        this.latitude = factory.getLatitude();
        this.mobile = factory.getMobile();
        this.email = factory.getEmail();
        this.postalCode = factory.getPostalCode();
        this.remark = factory.getRemark();
        this.tenantId = factory.getTenantId();
        this.createdTime = factory.getCreatedTime();
        this.createdUser = factory.getCreatedUser();
        this.updatedTime = factory.getUpdatedTime();
        this.updatedUser = factory.getUpdatedUser();
        this.delFlag = factory.getDelFlag();
        if(factory.getSort() == null){
            this.sort = 0;
        }else {
            this.sort = factory.getSort();
        }
    }

    public Factory toFactory(){
        Factory factory = new Factory(id);
        factory.setCode(code);
        factory.setName(name);
        factory.setLogoIcon(logoIcon);
        factory.setLogoImages(logoImages);
        factory.setAddress(address);
        factory.setCountry(country);
        factory.setProvince(province);
        factory.setCity(city);
        factory.setArea(area);
        factory.setLongitude(longitude);
        factory.setLatitude(latitude);
        factory.setMobile(mobile);
        factory.setEmail(email);
        factory.setPostalCode(this.postalCode);
        factory.setRemark(remark);
        factory.setTenantId(tenantId);
        factory.setCreatedTime(createdTime);
        factory.setCreatedUser(createdUser);
        factory.setUpdatedTime(updatedTime);
        factory.setUpdatedUser(updatedUser);
        factory.setDelFlag(delFlag);
        factory.setSort(sort);
        return factory;
    }

}
