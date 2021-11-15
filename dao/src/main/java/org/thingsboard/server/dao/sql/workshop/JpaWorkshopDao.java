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
package org.thingsboard.server.dao.sql.workshop;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.factory.FactoryDao;
import org.thingsboard.server.dao.model.sql.WorkshopEntity;
import org.thingsboard.server.dao.productionline.ProductionLineDao;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;
import org.thingsboard.server.dao.workshop.WorkshopDao;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Created by Valerii Sosliuk on 4/30/2017.
 */
@Component
public class JpaWorkshopDao extends JpaAbstractSearchTextDao<WorkshopEntity, Workshop> implements WorkshopDao {

    @Autowired
    private WorkshopRepository workshopRepository;
    @Autowired
    private ProductionLineDao productionLineDao;
    @Autowired
    private FactoryDao factoryDao;

    @Override
    protected Class<WorkshopEntity> getEntityClass() {
        return null;
    }

    @Override
    protected CrudRepository<WorkshopEntity, UUID> getCrudRepository() {
        return null;
    }


    @Override
    public Workshop saveWorkshop(Workshop workshop)throws ThingsboardException {
        Boolean create = workshop.getId() == null;
        //校验名称重复
        Workshop check = new Workshop();
        check.setName(workshop.getName());
        check.setTenantId(workshop.getTenantId());
        check.setFactoryId(workshop.getFactoryId());
        List<Workshop> workshopList = this.commonCondition(check);
        if(CollectionUtils.isNotEmpty(workshopList)){
            if (create) {
                throw new ThingsboardException("名称重复！", ThingsboardErrorCode.FAIL_VIOLATION);
            }else {
                if(!workshopList.get(0).getId().toString().equals(workshop.getId().toString())) {
                    throw new ThingsboardException("名称重复！", ThingsboardErrorCode.FAIL_VIOLATION);
                }
            }
        }
        WorkshopEntity workshopEntity = new WorkshopEntity(workshop);
        if (create) {
            UUID uuid = Uuids.timeBased();
            workshopEntity.setUuid(uuid);
            workshopEntity.setCreatedTime(Uuids.unixTimestamp(uuid));
        }else{
            workshopRepository.deleteById(workshopEntity.getUuid());
            workshopEntity.setUpdatedTime(Uuids.unixTimestamp(Uuids.timeBased()));
        }
        WorkshopEntity entity = workshopRepository.save(workshopEntity);
        if(entity != null){
            return entity.toData();
        }
        return null;
    }

    @Override
    public List<Workshop> findWorkshopListByCdn(Workshop workshop){
        return this.getParentNameByList(this.commonCondition(workshop));
    }

    @Override
    public List<Workshop> findWorkshopListByfactoryId(UUID factoryId){
        Workshop workshop = new Workshop();
        workshop.setFactoryId(factoryId);
        return this.commonCondition(workshop);
    }


    /**
     * 查询租户下所有车间列表
     * @param tenantId
     * @param factoryId
     * @return
     */
    @Override
    public List<Workshop> findWorkshopListByTenant(UUID tenantId,UUID factoryId){
        Workshop workshop = new Workshop();
        workshop.setTenantId(tenantId);
        workshop.setFactoryId(factoryId);
        return this.commonCondition(workshop);
    }

    /**
     * 删除(逻辑删除)
     * @param id
     */
    @Override
    public void delWorkshop(UUID id){
        //判断下面有没有产线
        if(CollectionUtils.isEmpty(productionLineDao.findProductionLineList(null,id,null))){
            /* 逻辑删除暂时不用
             WorkshopEntity workshopEntity = workshopRepository.findById(id).get();
            workshopEntity.setDelFlag("D");
            workshopRepository.save(workshopEntity);*/
            workshopRepository.deleteById(id);
        }
    }

    /**
     * 根据工厂删除(逻辑删除)
     * @param factoryId
     */
    @Override
    public void delWorkshopByFactoryId(UUID factoryId){
        workshopRepository.delWorkshopByFactoryId(factoryId);
    }


    /**
     * 构造查询条件,需要加条件在这里面加
     * @param workshop
     * @return
     */
    private List<Workshop> commonCondition(Workshop workshop){
        List<Workshop> resultWorkshop = new ArrayList<>();
        Specification<WorkshopEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("tenantId"),workshop.getTenantId()));
            if(StringUtils.isNotEmpty(workshop.getName())){
                predicates.add(cb.like(root.get("name"),"%" + workshop.getName().trim() + "%"));
            }
            if(workshop.getFactoryId() != null && StringUtils.isNotEmpty(workshop.getFactoryId().toString())){
                predicates.add(cb.equal(root.get("factoryId"),workshop.getFactoryId()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<WorkshopEntity> all = workshopRepository.findAll(specification);
        if(CollectionUtils.isNotEmpty(all)){
            all.forEach(i->{
                resultWorkshop.add(i.toData());
            });
        }
        return resultWorkshop;
    }

    /**
     * 批量查询
     * @param ids
     * @return
     */
    @Override
    public List<Workshop> getWorkshopByIdList(List<UUID> ids){
        List<Workshop> resultList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(ids)){
            Specification<WorkshopEntity> specification = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.in(root.get("id").in(ids)));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            };
            List<WorkshopEntity> all = workshopRepository.findAll(specification);
            if(CollectionUtils.isNotEmpty(all)){
                //查询工厂名称
                List<UUID> factoryIds = all.stream().distinct().map(s -> s.getFactoryId()).collect(Collectors.toList());
                List<Factory> factoryByIdList = factoryDao.getFactoryByIdList(factoryIds);
                all.forEach(i->{
                    Workshop workshop = i.toWorkshop();
                    if(CollectionUtils.isNotEmpty(factoryByIdList)){
                        factoryByIdList.forEach(j->{
                            if(i.getFactoryId() != null && i.getFactoryId().toString().equals(j.getId())){
                                workshop.setFactoryName(j.getName());
                            }
                        });
                    }
                    resultList.add(workshop);
                });
            }
        }
        return resultList;
    }

    /**
     * 获取父级名称
     * @param workshopList
     * @return
     */
    public List<Workshop> getParentNameByList(List<Workshop> workshopList){
        if(CollectionUtils.isNotEmpty(workshopList)){
            //查询工厂名称
            List<UUID> factoryIds = workshopList.stream().distinct().map(s -> s.getFactoryId()).collect(Collectors.toList());
            List<Factory> factoryByIdList = factoryDao.getFactoryByIdList(factoryIds);
            workshopList.forEach(i->{
                if(CollectionUtils.isNotEmpty(factoryByIdList)){
                    factoryByIdList.forEach(j->{
                        if(i.getFactoryId() != null && i.getFactoryId().toString().equals(j.getId())){
                            i.setFactoryName(j.getName());
                        }
                    });
                }
            });
        }
        return workshopList;
    }
}
