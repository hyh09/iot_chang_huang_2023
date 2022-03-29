package org.thingsboard.server.dao.sql.productioncalender;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.device.DeviceDao;
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
    private DeviceDao deviceDao;
    @Autowired
    private JpaSqlTool jpaSqlTool;


    private Page<ProductionCalenderEntity> queryWhereSql(ProductionCalender productionCalender,Pageable pageable){
        Map<String, Object> param = new HashMap<>();
        StringBuffer  sonSql01 = new StringBuffer();
        sonSql01.append(" select a1.id as device_id,a1.name as device_name,a2.id as factory_id ,a2.name as factory_name,a3.start_time,a3.end_time from device as a1 " +
                " left join hs_factory as a2 on a1.factory_id = a2.id " +
                " left join (" +
                "    select t1.* from hs_production_calendar as t1 " +
                "    inner join ( " +
                "       SELECT device_id,max(end_time)as end_time FROM hs_production_calendar " +
                "       group by device_id  " +
                "    )as t2  " +
                "    on t1.device_id = t2.device_id and t1.end_time = t2.end_time " +
                " ) as a3 on a3.device_id = a1.id " +
                " where 1 = 1  and position('\"gateway\":true' in a1.additional_info)=0 " );

        if(productionCalender != null){
            if(productionCalender.getFactoryName() != null){
                sonSql01.append(" and a2.name like :factoryName");
                param.put("factoryName","%" + productionCalender.getFactoryName() + "%");
            }
            if(StringUtils.isNotEmpty(productionCalender.getDeviceName())){
                sonSql01.append(" and a1.name like :deviceName");
                param.put("deviceName","%" + productionCalender.getDeviceName() + "%");
            }
            if(productionCalender.getTenantId() != null){
                sonSql01.append(" and a1.tenant_id = :tenantId");
                param.put("tenantId", productionCalender.getTenantId());
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
        if (productionCalender.getDeviceId() != null) {
            List<ProductionCalenderEntity> allByDeviceIdAndStartTimeAndEndTime = productionCalenderRepository.findAllByDeviceIdAndStartTimeAndEndTime(productionCalender.getDeviceId(), productionCalender.getStartTime(), productionCalender.getEndTime());
            if(CollectionUtils.isNotEmpty(allByDeviceIdAndStartTimeAndEndTime)){
                throw new ThingsboardException("设备【"+productionCalender.getDeviceName()+"】日历时间有重叠！", ThingsboardErrorCode.GENERAL);
            }
        }
        ProductionCalenderEntity productionCalenderEntity = new ProductionCalenderEntity(productionCalender);
        //校验，同一个设备的生产日历时间不允许存在交叉
        if (productionCalenderEntity.getId() == null) {
            UUID uuid = Uuids.timeBased();
            productionCalenderEntity.setId(uuid);
        }
        if(StringUtils.isEmpty(productionCalender.getFactoryName()) && productionCalender.getFactoryId() != null){
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
        //统计生产日历设备分组信息
        Page<ProductionCalenderEntity> page = queryWhereSql(productionCalender,pageable1);
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
     * 设备生产日历历史记录分页列表
     * @param deviceId
     * @param pageLink
     * @return
     */
    @Override
    public PageData<ProductionCalender> getHistoryPageByDeviceId(UUID deviceId, PageLink pageLink) {

        List<ProductionCalender> result = new ArrayList<>();
        // 动态条件查询
        Specification<ProductionCalenderEntity> specification = dynamicCondition(new ProductionCalender(deviceId));
        Pageable pageable = DaoUtil.toPageable(pageLink);
        Page<ProductionCalenderEntity> page = productionCalenderRepository.findAll(specification, pageable);

        List<ProductionCalenderEntity> entityList =page.getContent();
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.forEach(s->{
                result.add(s.toData());
            });
        }
        PageData<ProductionCalender> resultPage = new PageData<>();
        resultPage = new PageData<ProductionCalender>(result,page.getTotalPages(),page.getTotalElements(),page.hasNext());
        return resultPage;
    }

    /**
     * 设备生产日历历史记录列表
     * @param deviceId
     * @return
     */
    @Override
    public List<ProductionCalender> getHistoryByDeviceId(UUID deviceId) {

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
                if(productionCalender.getDeviceId() != null){
                    predicates.add(cb.equal(root.get("deviceId"),productionCalender.getDeviceId()));
                }
            }
            /**
             * order By
             */
            Order createdTime = cb.desc(root.get("createdTime"));
            return  query.orderBy(createdTime).where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        };
    }

    /**
     * 查询设备当天班次
     * @param deviceId
     * @return
     */
    @Override
    public List<ProductionCalender> getDeviceByTimenterval(UUID deviceId,long startTime,long endTime) {
        List<ProductionCalender> result = new ArrayList<>();
        // 动态条件查询
        Specification<ProductionCalenderEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deviceId"), deviceId));
            predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), startTime));
            predicates.add(cb.lessThanOrEqualTo(root.get("endTime"), endTime));
            Order orderByEndTime = cb.desc(root.get("endTime"));
            return query.orderBy(orderByEndTime).where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        };
        List<ProductionCalenderEntity> entityList = productionCalenderRepository.findAll(specification);
        if (CollectionUtils.isNotEmpty(entityList)) {
            entityList.forEach(s -> {
                result.add(s.toData());
            });
        }
        return result;
    }

    /**
     * 查询时间范围有交集的实生产日历
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<ProductionCalender> findAllByDeviceIdAndStartTimeAndEndTime(UUID deviceId,long startTime,long endTime){
        List<ProductionCalender> result = new ArrayList<>();
        List<ProductionCalenderEntity> entityList = productionCalenderRepository.findAllByDeviceIdAndStartTimeAndEndTime(deviceId, startTime, endTime);
        if (CollectionUtils.isNotEmpty(entityList)) {
            entityList.forEach(s -> {
                result.add(s.toData());
            });
        }
        return result;
    }


}
