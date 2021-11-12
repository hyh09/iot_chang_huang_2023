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
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.model.sql.WorkshopEntity;
import org.thingsboard.server.dao.productionline.ProductionLineDao;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;
import org.thingsboard.server.dao.workshop.WorkshopDao;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by Valerii Sosliuk on 4/30/2017.
 */
@Component
public class JpaWorkshopDao extends JpaAbstractSearchTextDao<WorkshopEntity, Workshop> implements WorkshopDao {

    @Autowired
    private WorkshopRepository workshopRepository;
    @Autowired
    private ProductionLineDao productionLineDao;

    @Override
    protected Class<WorkshopEntity> getEntityClass() {
        return null;
    }

    @Override
    protected CrudRepository<WorkshopEntity, UUID> getCrudRepository() {
        return null;
    }


    @Override
    public Workshop saveWorkshop(Workshop workshop){
        WorkshopEntity workshopEntity = new WorkshopEntity(workshop);
        if (workshopEntity.getUuid() == null) {
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
    public List<Workshop> findWorkshopListBuyCdn(Workshop workshop){
        return this.commonCondition(workshop);
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
            WorkshopEntity workshopEntity = workshopRepository.findById(id).get();
            workshopEntity.setDelFlag("D");
            workshopRepository.save(workshopEntity);
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
}
