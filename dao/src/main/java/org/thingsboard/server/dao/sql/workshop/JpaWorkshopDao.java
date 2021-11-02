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
    public List<WorkshopEntity> findWorkshopListBuyCdn(WorkshopEntity workshopEntity){
        if(workshopEntity != null){
            Specification<WorkshopEntity> specification = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("tenantId"),workshopEntity.getTenantId()));
                if(StringUtils.isNotEmpty(workshopEntity.getName())){
                    predicates.add(cb.like(root.get("name"),"%" + workshopEntity.getName().trim() + "%"));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            };
            return workshopRepository.findAll(specification);
        }
        return new ArrayList<>();
    }

    /**
     * 查询租户下所有车间列表
     * @param tenantId
     * @param factoryId
     * @return
     */
    @Override
    public List<Workshop> findWorkshopListByTenant(UUID tenantId,UUID factoryId){
        List<Workshop> resultWorkshopList = new ArrayList<>();
        Specification<WorkshopEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("tenantId"),tenantId));
            if(factoryId != null){
                predicates.add(cb.equal(root.get("factoryId"),factoryId));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<WorkshopEntity> workshopEntityList = workshopRepository.findAll(specification);
        if(CollectionUtils.isNotEmpty(workshopEntityList)){
            workshopEntityList.forEach(i->{
                resultWorkshopList.add(i.toData());
            });
        }
        return resultWorkshopList;
    }
}
