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
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.dao.model.sql.ProductionLineEntity;
import org.thingsboard.server.dao.productionline.ProductionLineDao;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by Valerii Sosliuk on 4/30/2017.
 */
@Component
public class JpaProductionLineDao extends JpaAbstractSearchTextDao<ProductionLineEntity, ProductionLine> implements ProductionLineDao {

    @Autowired
    private ProductionLineRepository productionLineRepository;


    @Override
    protected Class<ProductionLineEntity> getEntityClass() {
        return null;
    }

    @Override
    protected CrudRepository<ProductionLineEntity, UUID> getCrudRepository() {
        return null;
    }

    @Override
    public ProductionLine saveProductionLine(ProductionLine productionLine) {
        ProductionLineEntity productionLineEntity = new ProductionLineEntity(productionLine);
        if (productionLineEntity.getUuid() == null) {
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

    @Override
    public List<ProductionLineEntity> findProductionLineListBuyCdn(ProductionLineEntity productionLineEntity){
        if(productionLineEntity != null){
            Specification<ProductionLineEntity> specification = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("tenantId"),productionLineEntity.getTenantId()));
                if(StringUtils.isNotEmpty(productionLineEntity.getName())){
                    predicates.add(cb.like(root.get("name"),"%" + productionLineEntity.getName().trim() + "%"));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            };
            return productionLineRepository.findAll(specification);
        }
        return new ArrayList<>();
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
    public void delProductionLine(UUID id){
        ProductionLineEntity productionLineEntity = productionLineRepository.findById(id).get();
        productionLineEntity.setDelFlag("D");
        productionLineRepository.save(productionLineEntity);
    }

    /**
     * 根据车间id删除（逻辑删除）
     * @param workshopId
     */
    @Override
    public void delProductionLineByWorkshopId(UUID workshopId){
        productionLineRepository.delProductionLineByWorkshopId(workshopId);
    }

}
