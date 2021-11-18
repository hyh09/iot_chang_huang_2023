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
package org.thingsboard.server.dao.sql.factory;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.factory.FactoryListVo;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.vo.JudgeUserVo;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.factory.FactoryDao;
import org.thingsboard.server.dao.model.sql.FactoryEntity;
import org.thingsboard.server.dao.productionline.ProductionLineDao;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;
import org.thingsboard.server.dao.workshop.WorkshopDao;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Created by Valerii Sosliuk on 4/30/2017.
 */
@Component
public class JpaFactoryDao extends JpaAbstractSearchTextDao<FactoryEntity, Factory> implements FactoryDao {

    @Autowired
    private FactoryRepository factoryRepository;
    @Autowired
    private WorkshopDao workshopDao;
    @Autowired
    private ProductionLineDao productionLineDao;
    @Autowired
    private DeviceDao deviceDao;

    @Override
    protected Class<FactoryEntity> getEntityClass() {
        return null;
    }

    @Override
    protected CrudRepository<FactoryEntity, UUID> getCrudRepository() {
        return null;
    }


    @Override
    public Factory saveFactory(Factory factory)throws ThingsboardException {
        Boolean create = factory.getId() == null;
        //校验名称重复
        Factory check = new Factory();
        check.setName(factory.getName());
        check.setTenantId(factory.getTenantId());
        List<Factory> factoryList = this.commonCondition(check);
        if(CollectionUtils.isNotEmpty(factoryList)){
            if (create) {
                throw new ThingsboardException("名称重复！", ThingsboardErrorCode.FAIL_VIOLATION);
            }else {
                if(!factoryList.get(0).getId().toString().equals(factory.getId().toString())) {
                    throw new ThingsboardException("名称重复！", ThingsboardErrorCode.FAIL_VIOLATION);
                }
            }
        }
        FactoryEntity factoryEntity = new FactoryEntity(factory);
        if (create) {
            UUID uuid = Uuids.timeBased();
            factoryEntity.setUuid(uuid);
            factoryEntity.setCreatedTime(Uuids.unixTimestamp(uuid));
        }else{
            factoryRepository.deleteById(factoryEntity.getUuid());
            factoryEntity.setUpdatedTime(Uuids.unixTimestamp(Uuids.timeBased()));
        }
        FactoryEntity entity = factoryRepository.save(factoryEntity);
        if(entity != null){
            return entity.toData();
        }
        return null;
    }

    /**
     * 构造查询条件,需要家条件在这里面加
     * @param factory
     * @return
     */
    private List<Factory> commonCondition(Factory factory){
        List<Factory> resultFactorytList = new ArrayList<>();
        Specification<FactoryEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(factory != null){
                if(factory.getTenantId() != null){
                    predicates.add(cb.equal(root.get("tenantId"),factory.getTenantId()));
                }
                if(org.thingsboard.server.common.data.StringUtils.isNotEmpty(factory.getName())){
                    predicates.add(cb.equal(root.get("name"),factory.getName()));
                }
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<FactoryEntity> all = factoryRepository.findAll(specification);
        if(CollectionUtils.isNotEmpty(all)){
            all.forEach(i->{
                resultFactorytList.add(i.toData());
            });
        }
        return resultFactorytList;
    }


    /**
     * 删除(逻辑删除)
     * @param id
     */
    @Override
    public void delFactory(UUID id){

        if(CollectionUtils.isEmpty(workshopDao.findWorkshopListByfactoryId(id))){
           /* 逻辑删除暂时不用
            FactoryEntity factoryEntity = factoryRepository.findById(id).get();
            factoryEntity.setDelFlag("D");
            factoryRepository.save(factoryEntity);*/
            factoryRepository.deleteById(id);
        }
    }


    /**
     * 根据工厂管理员查询
     * @param factoryAdminId
     * @return
     */
    @Override
    public Factory findFactoryByAdmin(UUID factoryAdminId){return factoryRepository.findFactoryByAdmin(factoryAdminId);}

    /**
     * 根据租户查询
     * @param tenantId
     * @return
     */
    @Override
    public List<Factory>  findFactoryByTenantId(UUID tenantId){return factoryRepository.findFactoryByTenantId(tenantId);}


    /**
     * 只查询租户下的第一条工厂
     */
    @Override
    public  FactoryEntity findFactoryByTenantIdFirst(UUID tenantId){
        return factoryRepository.findFactoryByTenantIdFirst(tenantId);
    }


    /**
     * 条件查询工厂列表
     * @param factory
     * @return
     */
    @Override
    public FactoryListVo findFactoryListByCdn(Factory factory, JudgeUserVo judgeUserVo){
        List<FactoryEntity> factoryList = new ArrayList<>();
        List<Workshop> workshopList = new ArrayList<>();
        List<ProductionLine> productionLineList = new ArrayList<>();
        List<Device> deviceList = new ArrayList<>();

        if(factory != null){
            boolean notBlankFactoryName = StringUtils.isNotBlank(factory.getName());
            boolean notBlankWorkshopName = StringUtils.isNotBlank(factory.getWorkshopName());
            boolean notBlankProductionlineName = StringUtils.isNotBlank(factory.getProductionlineName());
            boolean notBlankDeviceName = StringUtils.isNotBlank(factory.getDeviceName());

            /**1.先根据条件查询出所有的 工厂、车间、产线、设备**/
            //查询工厂
            // 动态条件查询
            Specification<FactoryEntity> specification = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("tenantId"),factory.getTenantId()));
                if(notBlankFactoryName){
                    predicates.add(cb.like(root.get("name"),"%" + factory.getName().trim() + "%"));
                }
                if(judgeUserVo != null && judgeUserVo.getFactoryManagementFlag() != null && judgeUserVo.getFactoryManagementFlag()){
                    //工厂管理员/工厂用户，拥有该工厂数据权限
                    predicates.add(cb.equal(root.get("adminUserId"), judgeUserVo.getUserId()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            };
            factoryList = factoryRepository.findAll(specification);

            if(CollectionUtils.isNotEmpty(factoryList)){
                List<UUID> factoryIds = factoryList.stream().map(m->m.getId()).collect(Collectors.toList());
                //查询车间
                workshopList = workshopDao.findWorkshopListByCdn(new Workshop(factory,factoryIds));
                if(CollectionUtils.isNotEmpty(workshopList)){
                    List<UUID> workshopIds = workshopList.stream().map(m->m.getId()).collect(Collectors.toList());
                    //查询产线
                    productionLineList = productionLineDao.findProductionLineListBuyCdn(new ProductionLine(factory,workshopIds));
                    if(CollectionUtils.isNotEmpty(productionLineList)){
                        List<UUID> productionLineIds = productionLineList.stream().map(m->m.getId()).collect(Collectors.toList());
                        //查询设备,过滤掉网关
                        deviceList = deviceDao.findDeviceListBuyCdn(new Device(factory,productionLineIds));
                    }
                }
            }
            /**2.根据查询条件，筛选结果**/
            if(notBlankDeviceName){
                //设备名称不为空，用设备层层往上筛选
                 return this.filterFromDevice(factoryList,workshopList,productionLineList,deviceList);
            }else {
                if(notBlankProductionlineName){
                    //产线名称不为空，用产线层层往上筛选
                   return this.filterFromProductionLine(factoryList,workshopList,productionLineList,deviceList);
                }else {
                    if(notBlankWorkshopName){
                        //车间名称不为空，用车间层层往上筛选
                        return this.filterFromWorkshop(factoryList,workshopList,productionLineList,deviceList);
                    }
                }
            }
        }
        return new FactoryListVo(this.toFactoryList(factoryList),workshopList,productionLineList,deviceList);
    }

    /**
     * List<FactoryEntity>转 List<Factory>
     * @param factoryEntityList
     * @return
     */
    private List<Factory> toFactoryList(List<FactoryEntity> factoryEntityList){
        List<Factory> resultFactory = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(factoryEntityList)){
            factoryEntityList.forEach(i->{
                resultFactory.add(i.toFactory());
            });
        }
        return resultFactory;
    }

    /**
     * 设备名称不为空，用设备层层往上筛选
     * @param factoryEntityList
     * @param workshops
     * @param lines
     * @param devices
     * @return
     */
    private FactoryListVo filterFromDevice(List<FactoryEntity> factoryEntityList, List<Workshop> workshops, List<ProductionLine> lines, List<Device> devices){
        FactoryListVo result = new FactoryListVo();
        List<Device> resultDeviceList = devices;
        List<ProductionLine> resultLineList = new ArrayList<>();
        List<Workshop> resultWorkshopList = new ArrayList<>();
        List<Factory> resultFactoryList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(resultDeviceList)){
            //设备去重
            resultDeviceList = resultDeviceList.stream().distinct().collect(Collectors.toList());
            /**1.用设备筛选 -产线**/
            for (Device device : resultDeviceList){
                //产线
                for(ProductionLine productionLine : lines){
                    if(device.getProductionLineId().toString().equals(productionLine.getId().toString())){
                        resultLineList.add(productionLine);
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(resultLineList)){
                //产线去重
                resultLineList = resultLineList.stream().distinct().collect(Collectors.toList());
            }
            /**2.用设备筛选出的产线，去筛选车间**/
            if(CollectionUtils.isNotEmpty(resultLineList) && CollectionUtils.isNotEmpty(workshops)){
                for (ProductionLine line : resultLineList){
                    //车间
                    for (Workshop workshop : workshops){
                        if(line.getWorkshopId().toString().equals(workshop.getId().toString())){
                            resultWorkshopList.add(workshop);
                        }
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(resultWorkshopList)){
                //车间去重
                resultWorkshopList = resultWorkshopList.stream().distinct().collect(Collectors.toList());
            }
            /**3.用上一步（设备筛选出产线，再用产线去筛选车间）筛选出的车间，去筛选工厂，**/
            if(CollectionUtils.isNotEmpty(resultWorkshopList) && CollectionUtils.isNotEmpty(factoryEntityList)){
                for (Workshop workshop :resultWorkshopList){
                    //工厂
                    for (FactoryEntity factoryEntity :factoryEntityList){
                        if(workshop.getFactoryId().toString().equals(factoryEntity.getId().toString())){
                            resultFactoryList.add(factoryEntity.toFactory());
                        }
                    }
                }
            }
            //工厂去重
            if(CollectionUtils.isNotEmpty(resultFactoryList)){
                resultFactoryList = resultFactoryList.stream().distinct().collect(Collectors.toList());
            }
        }

        result.setFactoryEntityList(resultFactoryList);
        result.setWorkshopEntityList(resultWorkshopList);
        result.setProductionLineEntityList(resultLineList);
        result.setDeviceEntityList(resultDeviceList);
        return result;
    }

    /**
     * 产线名称不为空，用产线层层往上筛选,同时也要往下筛选设备
     * @param factoryEntityList
     * @param workshops
     * @param lines
     * @return
     */
    private FactoryListVo filterFromProductionLine(List<FactoryEntity> factoryEntityList, List<Workshop> workshops, List<ProductionLine> lines, List<Device> devices) {
        FactoryListVo result = new FactoryListVo();
        List<Device> resultDeviceList = new ArrayList<>();
        List<ProductionLine> resultLineList = lines;
        List<Workshop> resultWorkshopList = new ArrayList<>();
        List<Factory> resultFactoryList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(lines)){
            //产线去重
            resultLineList = resultLineList.stream().distinct().collect(Collectors.toList());
            for (ProductionLine line : resultLineList){
                /**1.产线筛选-设备**/
                if(CollectionUtils.isNotEmpty(devices)){
                    for (Device device:devices){
                        if(device.getProductionLineId().toString().equals(line.getId().toString())){
                            resultDeviceList.add(device);
                        }
                    }
                }
                /**2.产线筛选-车间**/
                for (Workshop workshop:workshops){
                    if(line.getWorkshopId().toString().equals(workshop.getId().toString())){
                        resultWorkshopList.add(workshop);
                    }
                }

            }
            //设备去重
            if(CollectionUtils.isNotEmpty(resultDeviceList)){
                resultDeviceList = resultDeviceList.stream().distinct().collect(Collectors.toList());
            }
            if(CollectionUtils.isNotEmpty(resultWorkshopList)){
                //车间去重
                resultWorkshopList = resultWorkshopList.stream().distinct().collect(Collectors.toList());
            }

            /**用产线筛选出的车间，去筛选工厂**/
            if(CollectionUtils.isNotEmpty(resultWorkshopList) && CollectionUtils.isNotEmpty(factoryEntityList)){
                for (Workshop workshop :resultWorkshopList){
                    for (FactoryEntity factoryEntity:factoryEntityList){
                        if(workshop.getFactoryId().toString().equals(factoryEntity.getId().toString())){
                            resultFactoryList.add(factoryEntity.toFactory());
                        }
                    }
                }
            }
            /**工厂去重**/
            if(CollectionUtils.isNotEmpty(resultFactoryList)){
                resultFactoryList = resultFactoryList.stream().distinct().collect(Collectors.toList());
            }
        }
        result.setFactoryEntityList(resultFactoryList);
        result.setWorkshopEntityList(resultWorkshopList);
        result.setProductionLineEntityList(resultLineList);
        result.setDeviceEntityList(resultDeviceList);
        return result;

    }

    /**
     * 车间名称不为空，用车间往上筛选工厂，同时也要往下筛选产线、设备
     * @param factoryEntityList
     * @param workshops
     * @return
     */
    private FactoryListVo filterFromWorkshop(List<FactoryEntity> factoryEntityList, List<Workshop> workshops,List<ProductionLine> lines, List<Device> devices) {
        FactoryListVo result = new FactoryListVo();
        List<Device> resultDeviceList = new ArrayList<>();
        List<ProductionLine> resultLineList = new ArrayList<>();
        List<Workshop> resultWorkshopList = workshops;
        List<Factory> resultFactoryList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(resultWorkshopList)){
            //车间去重
            resultWorkshopList = resultWorkshopList.stream().distinct().collect(Collectors.toList());
            for (Workshop workshop :resultWorkshopList){
                /**用车间筛选-工厂**/
                for (FactoryEntity factoryEntity : factoryEntityList){
                    if(workshop.getFactoryId().toString().equals(factoryEntity.getId().toString())){
                        resultFactoryList.add(factoryEntity.toFactory());
                    }
                }
                /**用车间筛选-产线**/
                for (ProductionLine productionLine:lines){
                    if(workshop.getId().toString().equals(productionLine.getWorkshopId().toString())){
                        resultLineList.add(productionLine);
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(resultFactoryList)){
                //工厂去重
                resultFactoryList = resultFactoryList.stream().distinct().collect(Collectors.toList());
            }
            if(CollectionUtils.isNotEmpty(resultLineList)){
                //产线去重
                resultLineList = resultLineList.stream().distinct().collect(Collectors.toList());
            }
            /**用车间筛选出的产线去筛选-设备**/
            if(CollectionUtils.isNotEmpty(resultLineList) && CollectionUtils.isNotEmpty(devices)){
                for (ProductionLine productionLine:resultLineList){
                    for (Device device:devices){
                        if(productionLine.getId().toString().equals(device.getProductionLineId().toString())){
                            resultDeviceList.add(device);
                        }
                    }
                }
            }

        }

        result.setFactoryEntityList(resultFactoryList);
        result.setWorkshopEntityList(resultWorkshopList);
        result.setProductionLineEntityList(resultLineList);
        result.setDeviceEntityList(resultDeviceList);
        return result;
    }


    /**
     * 查询详情
     * @param id
     * @return
     */
    @Override
    public Factory findById(UUID id){
        Optional<FactoryEntity> optional = factoryRepository.findById(id);
        if(!optional.isEmpty()){
            FactoryEntity entity = optional.get();
            if(entity != null){
                return entity.toData();
            }
        }

        return null;
    }

    /**
     * 批量查询
     * @param ids
     * @return
     */
    @Override
    public List<Factory> getFactoryByIdList(List<UUID> ids){
        List<Factory> resultList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(ids)){
            Specification<FactoryEntity> specification = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                // 下面是一个 IN查询
                CriteriaBuilder.In<UUID> in = cb.in(root.get("id"));
                ids.forEach(in::value);
                predicates.add(in);
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            };
            List<FactoryEntity> all = factoryRepository.findAll(specification);
            if(CollectionUtils.isNotEmpty(all)){
                all.forEach(i->{
                    resultList.add(i.toFactory());
                });
            }
        }
        return resultList;
    }

    /**
     * 根据条件查询工厂信息
     * @param factory
     * @return
     */
    @Override
    public List<Factory> findAllByCdn(Factory factory){
        return this.commonCondition(factory);
    }
}

