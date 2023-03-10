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
package org.thingsboard.server.dao.sql.productionline;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.model.sql.ProductionLineEntity;
import org.thingsboard.server.dao.productionline.ProductionLineDao;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;
import org.thingsboard.server.dao.workshop.WorkshopDao;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
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
public class JpaProductionLineDao extends JpaAbstractSearchTextDao<ProductionLineEntity, ProductionLine> implements ProductionLineDao {

    @Autowired
    private ProductionLineRepository productionLineRepository;
    @Autowired
    private DeviceDao deviceDao;
    @Autowired
    private WorkshopDao workshopDao;

    @Override
    protected Class<ProductionLineEntity> getEntityClass() {
        return null;
    }

    @Override
    protected CrudRepository<ProductionLineEntity, UUID> getCrudRepository() {
        return null;
    }

    @Override
    public ProductionLine saveProductionLine(ProductionLine productionLine) throws ThingsboardException{
        Boolean create = productionLine.getId() == null;
        //校验名称重复
        ProductionLine check = new ProductionLine();
        check.setName(productionLine.getName());
        check.setTenantId(productionLine.getTenantId());
        check.setFactoryId(productionLine.getFactoryId());
        check.setWorkshopId(productionLine.getWorkshopId());
        List<ProductionLine> factoryList = this.commonCondition(check);
        if(CollectionUtils.isNotEmpty(factoryList)){
            if (create) {
                throw new ThingsboardException("名称重复！", ThingsboardErrorCode.FAIL_VIOLATION);
            }else {
                if(!factoryList.get(0).getId().toString().equals(productionLine.getId().toString())) {
                    throw new ThingsboardException("名称重复！", ThingsboardErrorCode.FAIL_VIOLATION);
                }
            }
        }
        ProductionLineEntity productionLineEntity = new ProductionLineEntity(productionLine);
        if (create) {
            UUID uuid = Uuids.timeBased();
            productionLineEntity.setUuid(uuid);
            productionLineEntity.setCreatedTime(Uuids.unixTimestamp(uuid));
        }else{
            productionLineRepository.deleteById(productionLineEntity.getUuid());
            productionLineEntity.setUpdatedTime(Uuids.unixTimestamp(Uuids.timeBased()));
        }
        ProductionLineEntity entity = productionLineRepository.save(productionLineEntity);
        if(entity != null){
            return entity.toData();
        }
        return null;
    }
    /**
     * 构造查询条件,需要加条件在这里面加
     * @param productionLine
     * @return
     */
    private List<ProductionLine> commonCondition(ProductionLine productionLine){
        List<ProductionLine> resultWorkshop = new ArrayList<>();
        Specification<ProductionLineEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("tenantId"),productionLine.getTenantId()));
            if(StringUtils.isNotEmpty(productionLine.getName())){
                predicates.add(cb.like(root.get("name"),"%" + productionLine.getName().trim() + "%"));
            }
            if(productionLine.getFactoryId() != null && StringUtils.isNotEmpty(productionLine.getFactoryId().toString())){
                predicates.add(cb.equal(root.get("factoryId"),productionLine.getFactoryId()));
            }
            if(productionLine.getWorkshopId() != null && StringUtils.isNotEmpty(productionLine.getWorkshopId().toString())){
                predicates.add(cb.equal(root.get("workshopId"),productionLine.getWorkshopId()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<ProductionLineEntity> all = productionLineRepository.findAll(specification);
        if(CollectionUtils.isNotEmpty(all)){
            all.forEach(i->{
                resultWorkshop.add(i.toData());
            });
        }
        return resultWorkshop;
    }


    @Override
    public List<ProductionLine> findProductionLineListBuyCdn(ProductionLine productionLine){
        List<ProductionLine> resultList = new ArrayList<>();
        if(productionLine != null){
            Specification<ProductionLineEntity> specification = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("tenantId"),productionLine.getTenantId()));
                if(StringUtils.isNotEmpty(productionLine.getName())){
                    predicates.add(cb.like(root.get("name"),"%" + productionLine.getName().trim() + "%"));
                }
                if(CollectionUtils.isNotEmpty(productionLine.getWorkshopIds())){
                    // 下面是一个 IN查询
                    CriteriaBuilder.In<UUID> in = cb.in(root.get("workshopId"));
                    productionLine.getWorkshopIds().forEach(in::value);
                    predicates.add(in);
                }
                /**
                 * order By
                 */
                Order sort = cb.asc(root.get("sort"));
                return  query.orderBy(sort).where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            };
            List<ProductionLineEntity> all = productionLineRepository.findAll(specification);
            if(CollectionUtils.isNotEmpty(all)){
                all.forEach(i->{
                    resultList.add(i.toProductionLine());
                });
            }
        }
        return this.getParentNameByList(resultList);
    }

    /**
     * 询租户/工厂/车间下所有生产线列表
     * @param tenantId
     * @param workshopId
     * @param factoryId
     * @return
     */
    @Override
    public List<ProductionLine> findProductionLineList(UUID tenantId,UUID workshopId,UUID factoryId){
        List<ProductionLine> productionLineList = new ArrayList<>();
        Specification<ProductionLineEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(tenantId != null){
                predicates.add(cb.equal(root.get("tenantId"),tenantId));
            }
            if(workshopId != null){
                predicates.add(cb.equal(root.get("workshopId"),workshopId));
            }
            if(factoryId != null){
                predicates.add(cb.equal(root.get("factoryId"),factoryId));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<ProductionLineEntity> productionLineEntityList = productionLineRepository.findAll(specification);
        if(CollectionUtils.isNotEmpty(productionLineEntityList)){
            productionLineEntityList.forEach(i->{
                productionLineList.add(i.toData());
            });
        }
        return productionLineList;

    }

    /**
     * 根据id删除（逻辑删除）
     * @param id
     */
    @Override
    public void delProductionLine(UUID id) throws ThingsboardException{
        Device device = new Device();
        device.setProductionLineId(id);
        if(CollectionUtils.isEmpty(deviceDao.findDeviceListByCdn(device,null,null))){
            /*逻辑删除暂时不用
             ProductionLineEntity productionLineEntity = productionLineRepository.findById(id).get();
            productionLineEntity.setDelFlag("D");
            productionLineRepository.save(productionLineEntity);*/
            productionLineRepository.deleteById(id);
        }else {
            throw new ThingsboardException("产线下有设备不能删除！",ThingsboardErrorCode.GENERAL);
        }
    }

    /**
     * 根据车间id删除（逻辑删除）
     * @param workshopId
     */
    @Override
    public void delProductionLineByWorkshopId(UUID workshopId){
        productionLineRepository.delProductionLineByWorkshopId(workshopId);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public ProductionLine findById(UUID id){
        Optional<ProductionLineEntity> optional = productionLineRepository.findById(id);
        if(!optional.isEmpty()){
            ProductionLineEntity entity = optional.get();
            if(entity != null){
                ProductionLine productionLine = entity.toData();
                if(productionLine.getWorkshopId() != null && StringUtils.isNotEmpty(productionLine.getWorkshopId().toString())){
                    Workshop byId = workshopDao.findById(productionLine.getWorkshopId());
                    if(byId != null){
                        productionLine.setFactoryName(byId.getFactoryName());
                        productionLine.setWorkshopName(byId.getName());
                    }
                }
                return productionLine;
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
    public List<ProductionLine> getProductionLineByIdList(List<UUID> ids){
        List<ProductionLine> resultList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(ids)){
            Specification<ProductionLineEntity> specification = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                // 下面是一个 IN查询
                CriteriaBuilder.In<UUID> in = cb.in(root.get("id"));
                ids.forEach(in::value);
                predicates.add(in);

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            };
            List<ProductionLineEntity> all = productionLineRepository.findAll(specification);
            if(CollectionUtils.isNotEmpty(all)){
                //查询车间名称
                List<UUID> workshopIds = all.stream().map(s -> s.getWorkshopId()).collect(Collectors.toList());
                if(!org.springframework.util.CollectionUtils.isEmpty(workshopIds)){
                    workshopIds = workshopIds.stream().distinct().collect(Collectors.toList());
                }
                List<Workshop> workshopList = workshopDao.getWorkshopByIdList(workshopIds);
                all.forEach(i->{
                    ProductionLine productionLine = i.toProductionLine();
                    if(CollectionUtils.isNotEmpty(workshopList)){
                        workshopList.forEach(j->{
                            if(i.getWorkshopId() != null && i.getWorkshopId().toString().equals(j.getId().toString())){
                                productionLine.setFactoryName(j.getFactoryName());
                                productionLine.setWorkshopName(j.getName());
                            }
                        });
                    }
                    resultList.add(productionLine);
                });
            }
        }
        return resultList;
    }

    /**
     * 获取父级名称
     * @param productionLineList
     * @return
     */
    public List<ProductionLine> getParentNameByList(List<ProductionLine> productionLineList){
        if(CollectionUtils.isNotEmpty(productionLineList)){
            //查询车间名称
            List<UUID> workshopIds = productionLineList.stream().map(s -> s.getWorkshopId()).collect(Collectors.toList());
            if(!org.springframework.util.CollectionUtils.isEmpty(workshopIds)){
                workshopIds = workshopIds.stream().distinct().collect(Collectors.toList());
            }
            List<Workshop> workshopList = workshopDao.getWorkshopByIdList(workshopIds);
            productionLineList.forEach(i->{
                if(CollectionUtils.isNotEmpty(workshopList)){
                    workshopList.forEach(j->{
                        if(i.getWorkshopId() != null && i.getWorkshopId().toString().equals(j.getId().toString())){
                            i.setFactoryName(j.getFactoryName());
                            i.setWorkshopName(j.getName());
                        }
                    });
                }
            });
        }
        return productionLineList;
    }

    @Override
    public ListenableFuture<ProductionLine> findProductionLineByIdAsync(TenantId tenantId, UUID id) {
        return service.submit(() -> DaoUtil.getData(productionLineRepository.findById(id)));
    }

}
