package org.thingsboard.server.dao.sql.deviceoeeeveryhour;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.deviceoeeeveryhour.DeviceOeeEveryHour;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.deviceoeeeveryhour.DeviceOeeEveryHourDao;
import org.thingsboard.server.dao.model.sql.DeviceOeeEveryHourEntity;
import org.thingsboard.server.dao.sql.productioncalender.ProductionCalenderRepository;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class JpaDeviceOeeEveryHourDao implements DeviceOeeEveryHourDao {

    @Autowired
    private DeviceOeeEveryHourRepository deviceOeeEveryHourRepository;

    @Autowired
    private ProductionCalenderRepository productionCalenderRepository;

    @Autowired
    private DeviceDao deviceDao;


    @Override
    public void save(DeviceOeeEveryHour deviceOeeEveryHour) throws ThingsboardException {
        DeviceOeeEveryHourEntity entity = new DeviceOeeEveryHourEntity(deviceOeeEveryHour);
        if (entity.getId() == null) {
            UUID uuid = Uuids.timeBased();
            entity.setId(uuid);
            entity.setCreatedTime(Uuids.unixTimestamp(uuid));
        }
        if (deviceOeeEveryHour.getFactoryId() == null) {
            Device deviceInfo = deviceDao.findById(deviceOeeEveryHour.getDeviceId());
            entity.setFactoryAndWorkshopAndProductionLine(deviceInfo.getFactoryId(), deviceInfo.getWorkshopId(), deviceInfo.getProductionLineId());
        }
        //查询设备当前时间是否已存在数据，存在则覆盖
        List<DeviceOeeEveryHour> byCdn = this.findAllByCdn(new DeviceOeeEveryHour(deviceOeeEveryHour.getDeviceId(),deviceOeeEveryHour.getTs()), null, null);
        if (CollectionUtils.isNotEmpty(byCdn)) {
            entity.setId(byCdn.get(0).getId());
        }
        entity.setOeeValue(entity.getOeeValue().multiply(new BigDecimal(100)));
        deviceOeeEveryHourRepository.save(entity);
    }

    /**
     * 根据条件查询
     * @param deviceOeeEveryHour
     * @param orderByFile  字段
     * @param orderByType  DESC/ASC
     * @return
     */
    @Override
    public List<DeviceOeeEveryHour> findAllByCdn(DeviceOeeEveryHour deviceOeeEveryHour,String orderByFile,String orderByType) {
        List<DeviceOeeEveryHour> result = new ArrayList<>();
        // 动态条件查询
        Specification<DeviceOeeEveryHourEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Order orderByCdn = null;
            if (deviceOeeEveryHour != null) {
                if(deviceOeeEveryHour.getTenantId() != null){
                    predicates.add(cb.equal(root.get("tenantId"), deviceOeeEveryHour.getTenantId()));
                }
                if(deviceOeeEveryHour.getFactoryId() != null){
                    predicates.add(cb.equal(root.get("factoryId"), deviceOeeEveryHour.getFactoryId()));
                }
                if(deviceOeeEveryHour.getWorkshopId() != null){
                    predicates.add(cb.equal(root.get("workshopId"), deviceOeeEveryHour.getWorkshopId()));
                }
                if(deviceOeeEveryHour.getProductionLineId() != null){
                    predicates.add(cb.equal(root.get("productionLineId"), deviceOeeEveryHour.getProductionLineId()));
                }
                if (deviceOeeEveryHour.getDeviceId() != null) {
                    predicates.add(cb.equal(root.get("deviceId"), deviceOeeEveryHour.getDeviceId()));
                }
                if (deviceOeeEveryHour.getTs() != null) {
                    predicates.add(cb.equal(root.get("ts"), deviceOeeEveryHour.getTs()));
                }
                if(deviceOeeEveryHour.getStartTime() != null && deviceOeeEveryHour.getEndTime() != null){
                    predicates.add(cb.greaterThanOrEqualTo(root.get("ts"), deviceOeeEveryHour.getStartTime()));
                    predicates.add(cb.lessThanOrEqualTo(root.get("ts"), deviceOeeEveryHour.getEndTime()));
                }
                if(StringUtils.isNotEmpty(orderByFile) && StringUtils.isNotEmpty(orderByType)){
                    if("DESC".equals(orderByType)){
                        orderByCdn = cb.desc(root.get(orderByFile));
                    }else {
                        orderByCdn = cb.asc(root.get(orderByFile));
                    }
                }
            }
            if(orderByCdn == null){
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
            return query.orderBy(orderByCdn).where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        };
        List<DeviceOeeEveryHourEntity> entityList = deviceOeeEveryHourRepository.findAll(specification);
        if (CollectionUtils.isNotEmpty(entityList)) {
            entityList.forEach(s -> {
                result.add(s.toData());
            });
        }
        return result;
    }

}
