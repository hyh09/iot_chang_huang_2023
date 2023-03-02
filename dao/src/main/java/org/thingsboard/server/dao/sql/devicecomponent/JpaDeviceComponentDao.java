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
package org.thingsboard.server.dao.sql.devicecomponent;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.devicecomponent.DeviceComponent;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.devicecomponent.DeviceComponentDao;
import org.thingsboard.server.dao.model.sql.DeviceComponentEntity;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 设备构成Dao
 */
@Component
public class JpaDeviceComponentDao implements DeviceComponentDao {

    @Autowired
    private DeviceComponentRepository deviceComponentRepository;

    /**
     * 批量保存
     * @param deviceComponentList
     * @return
     * @throws ThingsboardException
     */
    @Override
    public List<DeviceComponent> saveDeviceComponentList(List<DeviceComponent> deviceComponentList) throws ThingsboardException {
        List<DeviceComponent> resultList = new ArrayList<DeviceComponent>();
        if(CollectionUtils.isNotEmpty(deviceComponentList)){
            for (DeviceComponent deviceComponent : deviceComponentList) {
                DeviceComponentEntity deviceComponentEntity = new DeviceComponentEntity(deviceComponent);
                UUID uuid = Uuids.timeBased();
                if(deviceComponent.getId() == null){
                    deviceComponentEntity.setUuid(uuid);
                    deviceComponentEntity.setCreatedTime(Uuids.unixTimestamp(uuid));
                }else {
                    deviceComponentEntity.setUpdatedTime(Uuids.unixTimestamp(uuid));
                }
                resultList.add(deviceComponentRepository.save(deviceComponentEntity).toDeviceComponent());
            }
        }
        return resultList;
    }

    /**
     * 保存/修改
     * @param deviceComponent
     * @return
     */
    @Override
    public DeviceComponent saveDeviceComponent(DeviceComponent deviceComponent) {
        DeviceComponentEntity deviceComponentEntity = new DeviceComponentEntity(deviceComponent);
        if (deviceComponentEntity.getUuid() == null) {
            UUID uuid = Uuids.timeBased();
            deviceComponentEntity.setUuid(uuid);
            deviceComponentEntity.setCreatedTime(Uuids.unixTimestamp(uuid));
        }else{
            deviceComponentRepository.deleteById(deviceComponentEntity.getUuid());
            deviceComponentEntity.setUpdatedTime(Uuids.unixTimestamp(Uuids.timeBased()));
        }
        DeviceComponentEntity entity = deviceComponentRepository.save(deviceComponentEntity);
        if(entity != null){
            return (DeviceComponent)entity.toData();
        }
        return null;
    }

    /**
     * 删除设备所有构成
     * @param deviceId
     */
    @Override
    public void delDeviceComponentByDeviceId(UUID deviceId) {
        DeviceComponentEntity entity = new DeviceComponentEntity();
        entity.setDeviceId(deviceId);
        deviceComponentRepository.delete(entity );
    }

    /**
     * 删除指定设备部件
     * @param id
     */
    @Override
    public void delDeviceComponent(UUID id) {
        if(deviceComponentRepository.findById(id) != null){
            deviceComponentRepository.deleteById(id);
        }
    }

    /**
     * 分页查询
     * @param deviceComponent
     * @param pageLink
     * @return
     */
    @Override
    public PageData<DeviceComponent> findDeviceComponentByPage(DeviceComponent deviceComponent, PageLink pageLink) {
        // 动态条件查询
        Specification<DeviceComponentEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(deviceComponent != null) {
                if (deviceComponent.getDeviceId() != null) {
                    predicates.add(cb.equal(root.get("deviceId"), deviceComponent.getDeviceId()));
                }
                if (deviceComponent.getParentId() != null) {
                    predicates.add(cb.equal(root.get("parentId"), deviceComponent.getParentId()));
                }
                if (deviceComponent.getCode() != null) {
                    predicates.add(cb.equal(root.get("code"), deviceComponent.getCode()));
                }
                if (StringUtils.isNotEmpty(deviceComponent.getName())) {
                    predicates.add(cb.like(root.get("name"), "%" + deviceComponent.getName().trim() + "%"));
                }
                if (StringUtils.isNotEmpty(deviceComponent.getType())) {
                    predicates.add(cb.like(root.get("type"), "%" + deviceComponent.getType().trim() + "%"));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = DaoUtil.toPageable(pageLink);
        Page<DeviceComponentEntity> entityPage = deviceComponentRepository.findAll(specification, pageable);
        //转换数据
        List<DeviceComponentEntity> content = entityPage.getContent();

        List<DeviceComponent> deviceComponentList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(content)){
            content.forEach(i->{
                deviceComponentList.add(i.toDeviceComponent());
            });
        }
        PageData<DeviceComponent> resultPage = new PageData<>();
        resultPage = new PageData<DeviceComponent>(deviceComponentList,entityPage.getTotalPages(),entityPage.getTotalElements(),entityPage.hasNext());
        return resultPage;
    }

    /**
     * 多条件列表查询
     * @param deviceComponent
     * @return
     */
    @Override
    public List<DeviceComponent> findDeviceComponentListByCdn(DeviceComponent deviceComponent) {
        return this.commonCondition(deviceComponent);
    }

    private List<DeviceComponent> commonCondition(DeviceComponent deviceComponent){
        List<DeviceComponent> deviceComponentList = new ArrayList<>();
        Specification<DeviceComponentEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(deviceComponent != null) {
                if (deviceComponent.getDeviceId() != null) {
                    predicates.add(cb.equal(root.get("deviceId"), deviceComponent.getDeviceId()));
                }
                if (deviceComponent.getParentId() != null) {
                    predicates.add(cb.equal(root.get("parentId"), deviceComponent.getParentId()));
                }
                if (deviceComponent.getCode() != null) {
                    predicates.add(cb.equal(root.get("code"), deviceComponent.getCode()));
                }
                if (StringUtils.isNotEmpty(deviceComponent.getName())) {
                    predicates.add(cb.like(root.get("name"), "%" + deviceComponent.getName().trim() + "%"));
                }
                if (StringUtils.isNotEmpty(deviceComponent.getType())) {
                    predicates.add(cb.like(root.get("type"), "%" + deviceComponent.getType().trim() + "%"));
                }
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        List<DeviceComponentEntity> entityList = deviceComponentRepository.findAll(specification);
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.forEach(i->{
                deviceComponentList.add(i.toDeviceComponent());
            });
        }
        return deviceComponentList;
    }

    /**
     * 查询部件详情
     * @param id
     * @return
     */
    @Override
    public DeviceComponent getDeviceComponentById(UUID id) {
        DeviceComponentEntity entity = deviceComponentRepository.findById(id).get();
        if (entity != null){
            return entity.toDeviceComponent();
        }
        return null;
    }

    /**
     * 根据设备标识查询设备构成
     * @param deviceId
     * @return
     */
    @Override
    public List<DeviceComponent>  getDeviceComponentByDeviceId(UUID deviceId){
        DeviceComponent deviceComponent = new DeviceComponent();
        deviceComponent.setDeviceId(deviceId);
;        return this.commonCondition(deviceComponent);
    }
}

