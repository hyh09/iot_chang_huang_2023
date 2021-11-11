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
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.factory.FactoryListVo;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.vo.JudgeUserVo;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.factory.FactoryDao;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sql.FactoryEntity;
import org.thingsboard.server.dao.model.sql.ProductionLineEntity;
import org.thingsboard.server.dao.model.sql.WorkshopEntity;
import org.thingsboard.server.dao.productionline.ProductionLineDao;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;
import org.thingsboard.server.dao.sql.role.service.UserRoleMenuSvc;
import org.thingsboard.server.dao.workshop.WorkshopDao;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


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
    @Autowired
    private UserRoleMenuSvc userRoleMenuSvc;

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
        FactoryEntity factoryEntity = new FactoryEntity(factory);
        if (factoryEntity.getUuid() == null) {
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
     * 删除后刷新值(逻辑删除)
     * @param id
     */
    @Override
    public void delFactory(UUID id){
        FactoryEntity factoryEntity = factoryRepository.findById(id).get();
        factoryEntity.setDelFlag("D");
        factoryRepository.save(factoryEntity);
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
    public FactoryListVo findFactoryListBuyCdn(Factory factory, JudgeUserVo judgeUserVo){
        List<FactoryEntity> factoryEntityList = new ArrayList<>();
        List<WorkshopEntity> workshopEntityList = new ArrayList<>();
        List<ProductionLineEntity> productionLineEntityList = new ArrayList<>();
        List<DeviceEntity> deviceEntityList = new ArrayList<>();

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
            factoryEntityList = factoryRepository.findAll(specification);

            //查询车间
            if(notBlankWorkshopName){
                workshopEntityList = workshopDao.findWorkshopListBuyCdn(new WorkshopEntity(factory));
            }else {
                workshopEntityList = workshopDao.findWorkshopListBuyCdn(new WorkshopEntity(factory.getTenantId()));
            }
            //查询产线
            if(notBlankProductionlineName){
                productionLineEntityList = productionLineDao.findProductionLineListBuyCdn(new ProductionLineEntity(factory));
            }else {
                productionLineEntityList = productionLineDao.findProductionLineListBuyCdn(new ProductionLineEntity(factory.getTenantId()));
            }
            //查询设备
            if(notBlankDeviceName){
                deviceEntityList = deviceDao.findDeviceListBuyCdn(new DeviceEntity(factory));
            }else {
                deviceEntityList = deviceDao.findDeviceListBuyCdn(new DeviceEntity(factory.getTenantId()));
            }


            //搜索条件组合判断
            //处理数据
            //如果设备不为空
            if(notBlankDeviceName){

                //判断产线数据是否为空
                if(notBlankProductionlineName){
                    //设备不为空-产线不为空，拿设备数据筛选产线
                    productionLineEntityList = this.filterProductionLineByDevice(deviceEntityList,productionLineEntityList);

                    //判断车间数据是否为空
                    if(notBlankWorkshopName){
                        //设备不为空-产线不为空-车间不为空，拿产线数据筛选车间
                        workshopEntityList = this.filterWorkshopByProductionLine(productionLineEntityList,workshopEntityList);

                        //判断工厂数据是否为空
                        if(notBlankFactoryName){
                            //设备不为空-产线不为空-车间不为空-工厂不为空，拿车间数据筛选工厂
                            factoryEntityList = this.filterFactoryByWorkshop(workshopEntityList,factoryEntityList);
                        }
                    }else {
                        //判断工厂数据是否为空
                        if(notBlankFactoryName){
                            //设备不为空-产线不为空-车间为空-工厂不为空，拿产线数据筛选工厂
                            factoryEntityList = this.filterFactoryByProductionLine(productionLineEntityList,workshopEntityList,factoryEntityList);
                        }
                    }
                }else {
                    //判断车间数据是否为空
                    if(notBlankWorkshopName){
                        //设备不为空-产线为空，车间不为空，拿设备数据筛选车间
                        workshopEntityList = this.filterWorkshopByDevice(deviceEntityList,productionLineEntityList,workshopEntityList);

                        //判断工厂数据是否为空
                        if(notBlankFactoryName){
                            //设备不为空-产线为空-车间不为空，拿车间数据筛选工厂
                            factoryEntityList = this.filterFactoryByWorkshop(workshopEntityList,factoryEntityList);
                        }
                    }else {
                        //判断工厂数据是否为空
                        if(notBlankFactoryName){
                            //设备不为空-产线为空-车间为空，拿设备数据筛选工厂
                            factoryEntityList = this.filterFactoryByDevice(deviceEntityList,productionLineEntityList,workshopEntityList,factoryEntityList);
                        }

                    }
                }
            }else {
                //判断产线是否为空
                if(notBlankProductionlineName){

                    //判断车间是否为空
                    if(notBlankWorkshopName){
                        //设备为空-产线不为空-车间不为空，拿产线数据筛选车间
                        workshopEntityList = this.filterWorkshopByProductionLine(productionLineEntityList,workshopEntityList);

                        //判断工厂是否为空
                        if(notBlankFactoryName){
                            //设备为空-产线不为空-车间不为空-工厂不为空，拿车间数据筛选工厂
                            factoryEntityList = this.filterFactoryByWorkshop(workshopEntityList,factoryEntityList);
                        }
                    }else {
                        //判断工厂是否为空
                        if(notBlankFactoryName){
                            //设备为空-产线不为空-车间为空-工厂不为空，拿产线数据筛选工厂
                            factoryEntityList = this.filterFactoryByProductionLine(productionLineEntityList,workshopEntityList,factoryEntityList);
                        }
                    }
                }else {
                    //判断车间是否为空
                    if(notBlankWorkshopName){
                        //判断工厂是否为空
                        if(notBlankFactoryName){
                            //设备为空-产线为空-车间不为空-工厂不为空，拿车间数据筛选工厂
                            factoryEntityList = this.filterFactoryByWorkshop(workshopEntityList,factoryEntityList);
                        }
                    }
                }

            }
        }
        return this.toFactoryListVo(factoryEntityList,workshopEntityList,productionLineEntityList,deviceEntityList);
    }

    private FactoryListVo toFactoryListVo(List<FactoryEntity> factoryEntityList, List<WorkshopEntity> workshopEntityList, List<ProductionLineEntity> productionLineEntityList, List<DeviceEntity> deviceEntityList) {
        List<Factory> factoryList = new ArrayList<>();
        List<Workshop> workshopList = new ArrayList<>();
        List<ProductionLine> productionLineList = new ArrayList<>();
        List<Device> deviceList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(factoryEntityList)) {
            factoryEntityList.forEach(i -> {
                factoryList.add(i.toFactory());
            });

        }
        if (CollectionUtils.isNotEmpty(workshopEntityList)) {
            workshopEntityList.forEach(i -> {
                workshopList.add(i.toWorkshop());
            });

        }
        if (CollectionUtils.isNotEmpty(productionLineEntityList)) {
            productionLineEntityList.forEach(i -> {
                productionLineList.add(i.toProductionLine());
            });

        }
        if (CollectionUtils.isNotEmpty(deviceEntityList)) {
            deviceEntityList.forEach(i -> {
                deviceList.add(i.toData());
            });

        }
        return new FactoryListVo(factoryList,workshopList,productionLineList,deviceList);
    }

    /**
     * 根据设备过滤产线
     * @param deviceEntityList
     * @param productionLineEntityList
     * @return
     */
    private List<ProductionLineEntity> filterProductionLineByDevice(List<DeviceEntity> deviceEntityList,List<ProductionLineEntity> productionLineEntityList){
        deviceEntityList.forEach(i->{
            Iterator<ProductionLineEntity> it = productionLineEntityList.iterator();
            while (it.hasNext()){
                ProductionLineEntity entity = it.next();
                if(!entity.getUuid().toString().equals(i.getProductionLineId().toString())){
                    it.remove();
                }
            }
        });
        return productionLineEntityList;
    }

    /**
     * 根据产线过滤车间
     * @param productionLineEntityList
     * @param workshopEntityList
     * @return
     */
    private List<WorkshopEntity> filterWorkshopByProductionLine(List<ProductionLineEntity> productionLineEntityList,List<WorkshopEntity> workshopEntityList){
        productionLineEntityList.forEach(i->{
            Iterator<WorkshopEntity> it = workshopEntityList.iterator();
            while (it.hasNext()){
                WorkshopEntity entity = it.next();
                if(!entity.getUuid().toString().equals(i.getWorkshopId().toString())){
                    it.remove();
                }
            }
        });
        return workshopEntityList;
    }

    /**
     * 根据车间过滤工厂
     * @param workshopEntityList
     * @param factoryEntityList
     * @return
     */
    private List<FactoryEntity> filterFactoryByWorkshop(List<WorkshopEntity> workshopEntityList,List<FactoryEntity> factoryEntityList){
        workshopEntityList.forEach(i->{
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
     * @param productionLineEntityList
     * @param workshopEntityList
     * @param factoryEntityList
     * @return
     */
    private List<FactoryEntity> filterFactoryByProductionLine(List<ProductionLineEntity> productionLineEntityList,List<WorkshopEntity> workshopEntityList,List<FactoryEntity> factoryEntityList ){
        productionLineEntityList.forEach(i->{
            Iterator<WorkshopEntity> it = workshopEntityList.iterator();
            while (it.hasNext()){
                WorkshopEntity entity = it.next();
                if(!entity.getUuid().toString().equals(i.getWorkshopId().toString())){
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
     * @param deviceEntityList
     * @param productionLineEntityList
     * @param workshopEntityList
     * @return
     */
    private List<WorkshopEntity> filterWorkshopByDevice(List<DeviceEntity> deviceEntityList,List<ProductionLineEntity> productionLineEntityList,List<WorkshopEntity> workshopEntityList){
        deviceEntityList.forEach(i->{
            Iterator<ProductionLineEntity> it = productionLineEntityList.iterator();
            while (it.hasNext()){
                ProductionLineEntity entity = it.next();
                if(!entity.getUuid().toString().equals(i.getProductionLineId().toString())){
                    it.remove();
                }else {
                    Iterator<WorkshopEntity> itWorkshop = workshopEntityList.iterator();
                    while (itWorkshop.hasNext()){
                        WorkshopEntity workshopEntity = itWorkshop.next();
                        if(!workshopEntity.getUuid().toString().equals(entity.getWorkshopId().toString())){
                            it.remove();
                        }
                    }
                }
            }
        });

        return workshopEntityList;
    }

    /**
     * 根据设备过滤工厂
     * @param deviceEntityList
     * @param productionLineEntityList
     * @param workshopEntityList
     * @param factoryEntityList
     * @return
     */
    private List<FactoryEntity> filterFactoryByDevice(List<DeviceEntity> deviceEntityList,List<ProductionLineEntity> productionLineEntityList,List<WorkshopEntity> workshopEntityList,List<FactoryEntity> factoryEntityList){
        deviceEntityList.forEach(i->{
            Iterator<ProductionLineEntity> it = productionLineEntityList.iterator();
            while (it.hasNext()){
                ProductionLineEntity entity = it.next();
                if(!entity.getUuid().toString().equals(i.getProductionLineId().toString())){
                    it.remove();
                }else {
                    Iterator<WorkshopEntity> itWorkshop = workshopEntityList.iterator();
                    while (itWorkshop.hasNext()){
                        WorkshopEntity workshopEntity = itWorkshop.next();
                        if(!workshopEntity.getUuid().toString().equals(entity.getWorkshopId().toString())){
                            it.remove();
                        }else {
                            Iterator<FactoryEntity> itFactory = factoryEntityList.iterator();
                            while (itFactory.hasNext()){
                                FactoryEntity factoryEntity = itFactory.next();
                                if(!factoryEntity.getUuid().toString().equals(workshopEntity.getFactoryId().toString())){
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
}

