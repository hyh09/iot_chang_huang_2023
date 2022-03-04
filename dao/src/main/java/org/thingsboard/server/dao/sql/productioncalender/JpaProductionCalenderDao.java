package org.thingsboard.server.dao.sql.productioncalender;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.factory.FactoryDao;
import org.thingsboard.server.dao.model.sql.ProductionCalenderEntity;
import org.thingsboard.server.dao.productioncalender.ProductionCalenderDao;
import org.thingsboard.server.dao.sql.role.dao.JpaSqlTool;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.util.*;

@Component
public class JpaProductionCalenderDao implements ProductionCalenderDao {

    @Autowired
    private ProductionCalenderRepository productionCalenderRepository;
    @Autowired
    private FactoryDao factoryDao;
    @Autowired
    private JpaSqlTool jpaSqlTool;


    private Page<ProductionCalenderEntity> queryWhereSql(ProductionCalender productionCalender,Pageable pageable){
        Map<String, Object> param = new HashMap<>();
        StringBuffer  sonSql01 = new StringBuffer();
        sonSql01.append(" select t1.* from hs_production_calendar as t1 " +
                " inner join (" +
                "    SELECT device_id,max(end_time)as end_time FROM hs_production_calendar " +
                "    group by device_id " +
                " )as t2 " +
                " on t1.device_id = t2.device_id and t1.end_time = t2.end_time " +
                " where 1 = 1  " );

        if(productionCalender != null){
            if(productionCalender.getFactoryId() != null){
                sonSql01.append(" and t1.factory_id = :factoryId");
                param.put("factoryId",productionCalender.getFactoryId());
            }
            if(StringUtils.isNotEmpty(productionCalender.getDeviceName())){
                sonSql01.append(" and t1.device_name like :deviceName");
                param.put("deviceName","%" + productionCalender.getDeviceName() + "%");
            }
            if(productionCalender.getStartTime() != null){
                sonSql01.append(" and t1.start_time < :startTime");
                param.put("startTime",productionCalender.getStartTime());
            }
            if(productionCalender.getEndTime() != null){
                sonSql01.append(" and t1.end_time > :endTime");
                param.put("endTime",productionCalender.getEndTime());
            }
        }
        return jpaSqlTool.querySql(sonSql01.toString(), param, pageable, "productionCalendarEntity_01");
    }


    /**
     * 新增/修改
     * @param productionCalender
     * @throws ThingsboardException
     */
    @Override
    public void saveProductionCalender(ProductionCalender productionCalender) throws ThingsboardException {
        ProductionCalenderEntity productionCalenderEntity = new ProductionCalenderEntity(productionCalender);
        if (productionCalenderEntity.getUuid() == null) {
            UUID uuid = Uuids.timeBased();
            productionCalenderEntity.setId(uuid);
        }
        if(productionCalender.getFactoryId() != null){
            Factory byId = factoryDao.findById(productionCalender.getFactoryId());
            if(byId != null){
                productionCalenderEntity.setFactoryName(byId.getName());
            }
        }
        productionCalenderRepository.save(productionCalenderEntity);
    }

    /**
     * 单条删除
     * @param id
     * @throws ThingsboardException
     */
    @Override
    public void delProductionCalender(UUID id) throws ThingsboardException {
        productionCalenderRepository.deleteById(id);
    }

    /**
     * 查单条记录
     * @param id
     * @return
     */
    @Override
    public ProductionCalender findById(UUID id) {
        Optional<ProductionCalenderEntity> byId = productionCalenderRepository.findById(id);
        if(byId != null && byId.get() != null){
            return byId.get().toData();
        }
        return null;
    }

    /**
     * 分页查询
     * @param pageLink
     * @return
     */
    @Override
    public PageData<ProductionCalender> findProductionCalenderPage(ProductionCalender productionCalender, PageLink pageLink){
        List<ProductionCalender> result = new ArrayList<>();
        Pageable pageable1 = DaoUtil.toPageable(pageLink);
        Page<ProductionCalenderEntity> page = queryWhereSql(productionCalender,pageable1);// productionCalenderRepository.findPage(productionCalender.factoryId, productionCalender.getDeviceName(), productionCalender.getStartTime(), productionCalender.getEndTime(),pageable1);
        //转换数据
        List<ProductionCalenderEntity> content = page.getContent();

        if(CollectionUtils.isNotEmpty(content)){
            content.forEach(i->{
                result.add(i.toData());
            });
        }
        PageData<ProductionCalender> resultPage = new PageData<>();
        resultPage = new PageData<ProductionCalender>(result,page.getTotalPages(),page.getTotalElements(),page.hasNext());
        return resultPage;
    }

    /**
     * 查询历史
     * @param deviceId
     * @return
     */
    @Override
    public List<ProductionCalender> getHistoryById(UUID deviceId) {

        List<ProductionCalender> result = new ArrayList<>();
        // 动态条件查询
        Specification<ProductionCalenderEntity> specification = dynamicCondition(new ProductionCalender(deviceId));

        List<ProductionCalenderEntity> entityList = productionCalenderRepository.findAll(specification);
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.forEach(s->{
                result.add(s.toData());
            });
        }
        return result;
    }

    /**
     * 动态条件
     * @param productionCalender
     * @return
     */
    public Specification<ProductionCalenderEntity> dynamicCondition(ProductionCalender productionCalender){
        return  (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(productionCalender != null){
                if(productionCalender.getTenantId() != null){
                    predicates.add(cb.equal(root.get("tenantId"),productionCalender.getTenantId()));
                }
                if(productionCalender.getFactoryId() != null){
                    predicates.add(cb.equal(root.get("factoryId"),productionCalender.getFactoryId()));
                }
                if(StringUtils.isNotEmpty(productionCalender.getDeviceName())){
                    predicates.add(cb.like(root.get("deviceName"),productionCalender.getDeviceName()));
                }
            }
            /**
             * order By
             */
            Order createdTime = cb.desc(root.get("createdTime"));
            return  query.orderBy(createdTime).where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        };
    }



}
