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
import java.util.Iterator;
import java.util.List;
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
                        //查询设备
                        deviceList = deviceDao.findDeviceListBuyCdn(new Device(factory,productionLineIds));
                    }
                }
            }


            //搜索条件组合判断
            //处理数据
            //如果设备不为空
            if(notBlankDeviceName){

                //判断产线数据是否为空
                if(notBlankProductionlineName){
                    //设备不为空-产线不为空，拿设备数据筛选产线
                    productionLineList = this.filterProductionLineByDevice(deviceList,productionLineList);

                    //判断车间数据是否为空
                    if(notBlankWorkshopName){
                        //设备不为空-产线不为空-车间不为空，拿产线数据筛选车间
                        workshopList = this.filterWorkshopByProductionLine(productionLineList,workshopList);

                        //判断工厂数据是否为空
                        if(notBlankFactoryName){
                            //设备不为空-产线不为空-车间不为空-工厂不为空，拿车间数据筛选工厂
                            factoryList = this.filterFactoryByWorkshop(workshopList,factoryList);
                        }
                    }else {
                        //判断工厂数据是否为空
                        if(notBlankFactoryName){
                            //设备不为空-产线不为空-车间为空-工厂不为空，拿产线数据筛选工厂
                            factoryList = this.filterFactoryByProductionLine(productionLineList,workshopList,factoryList);
                        }
                    }
                }else {
                    //判断车间数据是否为空
                    if(notBlankWorkshopName){
                        //设备不为空-产线为空，车间不为空，拿设备数据筛选车间
                        workshopList = this.filterWorkshopByDevice(deviceList,productionLineList,workshopList);

                        //判断工厂数据是否为空
                        if(notBlankFactoryName){
                            //设备不为空-产线为空-车间不为空，拿车间数据筛选工厂
                            factoryList = this.filterFactoryByWorkshop(workshopList,factoryList);
                        }
                    }else {
                        //判断工厂数据是否为空
                        if(notBlankFactoryName){
                            //设备不为空-产线为空-车间为空，拿设备数据筛选工厂
                            factoryList = this.filterFactoryByDevice(deviceList,productionLineList,workshopList,factoryList);
                        }

                    }
                }
            }else {
                //判断产线是否为空
                if(notBlankProductionlineName){

                    //判断车间是否为空
                    if(notBlankWorkshopName){
                        //设备为空-产线不为空-车间不为空，拿产线数据筛选车间
                        workshopList = this.filterWorkshopByProductionLine(productionLineList,workshopList);

                        //判断工厂是否为空
                        if(notBlankFactoryName){
                            //设备为空-产线不为空-车间不为空-工厂不为空，拿车间数据筛选工厂
                            factoryList = this.filterFactoryByWorkshop(workshopList,factoryList);
                        }
                    }else {
                        //判断工厂是否为空
                        if(notBlankFactoryName){
                            //设备为空-产线不为空-车间为空-工厂不为空，拿产线数据筛选工厂
                            factoryList = this.filterFactoryByProductionLine(productionLineList,workshopList,factoryList);
                        }
                    }
                }else {
                    //判断车间是否为空
                    if(notBlankWorkshopName){
                        //判断工厂是否为空
                        if(notBlankFactoryName){
                            //设备为空-产线为空-车间不为空-工厂不为空，拿车间数据筛选工厂
                            factoryList = this.filterFactoryByWorkshop(workshopList,factoryList);
                        }
                    }
                }

            }
        }
        return this.toFactoryListVo(factoryList,workshopList,productionLineList,deviceList);
    }

    private FactoryListVo toFactoryListVo(List<FactoryEntity> factoryEntityList, List<Workshop> workshops, List<ProductionLine> lineList, List<Device> devices) {
        List<Factory> factoryList = new ArrayList<>();
        List<Workshop> workshopList = new ArrayList<>();
        List<ProductionLine> productionLineList = new ArrayList<>();
        List<Device> deviceList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(factoryEntityList)) {
            factoryEntityList.forEach(i -> {
                factoryList.add(i.toFactory());
            });

        }
        if (CollectionUtils.isNotEmpty(workshops)) {
            workshops.forEach(i -> {
                workshopList.add(i);
            });

        }
        if (CollectionUtils.isNotEmpty(lineList)) {
            lineList.forEach(i -> {
                productionLineList.add(i);
            });

        }
        if (CollectionUtils.isNotEmpty(devices)) {
            devices.forEach(i -> {
                deviceList.add(i);
            });

        }
        return new FactoryListVo(factoryList,workshopList,productionLineList,deviceList);
    }

    /**
     * 根据设备过滤产线
     * @param deviceList
     * @param productionLineList
     * @return
     */
    private List<ProductionLine> filterProductionLineByDevice(List<Device> deviceList,List<ProductionLine> productionLineList){
        deviceList.forEach(i->{
            Iterator<ProductionLine> it = productionLineList.iterator();
            while (it.hasNext()){
                ProductionLine entity = it.next();
                if(!entity.getId().toString().equals(i.getProductionLineId().toString())){
                    it.remove();
                }
            }
        });
        return productionLineList;
    }

    /**
     * 根据产线过滤车间
     * @param productionLineList
     * @param workshopList
     * @return
     */
    private List<Workshop> filterWorkshopByProductionLine(List<ProductionLine> productionLineList,List<Workshop> workshopList){
        productionLineList.forEach(i->{
            Iterator<Workshop> it = workshopList.iterator();
            while (it.hasNext()){
                Workshop entity = it.next();
                if(!entity.getId().toString().equals(i.getWorkshopId().toString())){
                    it.remove();
                }
            }
        });
        return workshopList;
    }

    /**
     * 根据车间过滤工厂
     * @param workshopList
     * @param factoryEntityList
     * @return
     */
    private List<FactoryEntity> filterFactoryByWorkshop(List<Workshop> workshopList,List<FactoryEntity> factoryEntityList){
        workshopList.forEach(i->{
            Iterator<FactoryEntity> it = factoryEntityList.iterator();
            while (it.hasNext()){
                FactoryEntity entity = it.next();
                if(!entity.getUuid().toString().equals(i.getFactoryId().toString())){
                    it.remove();
                }
            }
        });
        return factoryEntityList;
    }

    /**
     * 根据产线过滤工厂
     * @param productionLineList
     * @param workshopList
     * @param factoryEntityList
     * @return
     */
    private List<FactoryEntity> filterFactoryByProductionLine(List<ProductionLine> productionLineList,List<Workshop> workshopList,List<FactoryEntity> factoryEntityList ){
        productionLineList.forEach(i->{
            Iterator<Workshop> it = workshopList.iterator();
            while (it.hasNext()){
                Workshop entity = it.next();
                if(!entity.getId().toString().equals(i.getWorkshopId().toString())){
                    it.remove();
                }else {
                    Iterator<FactoryEntity> itFactory = factoryEntityList.iterator();
                    while (itFactory.hasNext()){
                        FactoryEntity factoryEntity = itFactory.next();
                        if(!factoryEntity.getUuid().toString().equals(entity.getFactoryId().toString())){
                            it.remove();
                        }
                    }
                }
            }
        });
        return factoryEntityList;
    }

    /**
     * 根据设备过滤车间
     * @param deviceList
     * @param productionLineList
     * @param workshopList
     * @return
     */
    private List<Workshop> filterWorkshopByDevice(List<Device> deviceList,List<ProductionLine> productionLineList,List<Workshop> workshopList){
        deviceList.forEach(i->{
            Iterator<ProductionLine> it = productionLineList.iterator();
            while (it.hasNext()){
                ProductionLine entity = it.next();
                if(!entity.getId().toString().equals(i.getProductionLineId().toString())){
                    it.remove();
                }else {
                    Iterator<Workshop> itWorkshop = workshopList.iterator();
                    while (itWorkshop.hasNext()){
                        Workshop workshop = itWorkshop.next();
                        if(!workshop.getId().toString().equals(entity.getWorkshopId().toString())){
                            it.remove();
                        }
                    }
                }
            }
        });

        return workshopList;
    }

    /**
     * 根据设备过滤工厂
     * @param deviceList
     * @param productionLineList
     * @param workshopList
     * @param factoryEntityList
     * @return
     */
    private List<FactoryEntity> filterFactoryByDevice(List<Device> deviceList,List<ProductionLine> productionLineList,List<Workshop> workshopList,List<FactoryEntity> factoryEntityList){
        deviceList.forEach(i->{
            Iterator<ProductionLine> it = productionLineList.iterator();
            while (it.hasNext()){
                ProductionLine entity = it.next();
                if(!entity.getId().toString().equals(i.getProductionLineId().toString())){
                    it.remove();
                }else {
                    Iterator<Workshop> itWorkshop = workshopList.iterator();
                    while (itWorkshop.hasNext()){
                        Workshop workshop = itWorkshop.next();
                        if(!workshop.getId().toString().equals(entity.getWorkshopId().toString())){
                            it.remove();
                        }else {
                            Iterator<FactoryEntity> itFactory = factoryEntityList.iterator();
                            while (itFactory.hasNext()){
                                FactoryEntity factoryEntity = itFactory.next();
                                if(!factoryEntity.getUuid().toString().equals(workshop.getFactoryId().toString())){
                                    it.remove();
                                }
                            }
                        }
                    }
                }
            }
        });
        return factoryEntityList;
    }
    /**
     * 查询详情
     * @param id
     * @return
     */
    @Override
    public Factory findById(UUID id){
        FactoryEntity entity = factoryRepository.findById(id).get();
        if(entity != null){
            return entity.toData();
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

