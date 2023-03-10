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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TenantProfileId;
import org.thingsboard.server.common.data.validation.NoXss;

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "租户实体")
public class Tenant extends ContactBased<TenantId> implements HasTenantId {

    private static final long serialVersionUID = 8057243243859922101L;

    @ApiModelProperty("经度【2021-12-01新增字段】")
    private  String longitude;
    @ApiModelProperty("纬度【2021-12-01新增字段】")
    private  String latitude;

    @NoXss
    private String title;
    @NoXss
    private String region;
    private TenantProfileId tenantProfileId;

    public Tenant() {
        super();
    }

    public Tenant(TenantId id) {
        super(id);
    }
    
    public Tenant(Tenant tenant) {
        super(tenant);
        this.title = tenant.getTitle();
        this.region = tenant.getRegion();
        this.tenantProfileId = tenant.getTenantProfileId();
        this.latitude = tenant.getLatitude();
        this.longitude = tenant.getLongitude();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    @JsonIgnore
    public TenantId getTenantId() {
        return getId();
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getName() {
        return title;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public TenantProfileId getTenantProfileId() {
        return tenantProfileId;
    }

    public void setTenantProfileId(TenantProfileId tenantProfileId) {
        this.tenantProfileId = tenantProfileId;
    }


    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public String getSearchText() {
        return getTitle();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tenant [title=");
        builder.append(title);

        builder.append(", longitude=");
        builder.append(longitude);
        builder.append(", latitude=");
        builder.append(latitude);

        builder.append(", region=");
        builder.append(region);
        builder.append(", tenantProfileId=");
        builder.append(tenantProfileId);
        builder.append(", additionalInfo=");
        builder.append(getAdditionalInfo());
        builder.append(", country=");
        builder.append(country);
        builder.append(", state=");
        builder.append(state);
        builder.append(", city=");
        builder.append(city);
        builder.append(", countyLevel=");
        builder.append(countyLevel);
        builder.append(", address=");
        builder.append(address);
        builder.append(", address2=");
        builder.append(address2);
        builder.append(", zip=");
        builder.append(zip);
        builder.append(", phone=");
        builder.append(phone);
        builder.append(", email=");
        builder.append(email);
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

}
