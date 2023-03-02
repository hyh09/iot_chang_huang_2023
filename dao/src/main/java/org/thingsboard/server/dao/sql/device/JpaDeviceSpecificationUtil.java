package org.thingsboard.server.dao.sql.device;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.thingsboard.server.dao.model.sql.DeviceEntity;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Project Name: thingsboard
 * @File Name: JpaDeviceSpecificationUtil
 * @Date: 2022/11/28 10:50
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
public class JpaDeviceSpecificationUtil {

    public static JpaDeviceSpecificationUtil build = new JpaDeviceSpecificationUtil();

    /**
     * 精确查询
     *
     * @param entity
     * @return
     */
    public Specification<DeviceEntity> specification(DeviceEntity entity) {
        Specification<DeviceEntity> specification = new Specification<DeviceEntity>() {

            @Override
            public Predicate toPredicate(Root<DeviceEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> pList = new ArrayList<Predicate>();
                if (entity.getTenantId() != null) {
                    pList.add(cb.equal(root.get("tenantId").as(UUID.class), entity.getTenantId()));
                }
                if (entity.getFactoryId() != null) {
                    pList.add(cb.equal(root.get("factoryId").as(UUID.class), entity.getFactoryId()));
                }
                if (entity.getWorkshopId() != null) {
                    pList.add(cb.equal(root.get("workshopId").as(UUID.class), entity.getWorkshopId()));
                }
                if (entity.getProductionLineId() != null) {
                    pList.add(cb.equal(root.get("productionLineId").as(UUID.class), entity.getProductionLineId()));
                }
                if (entity.getId() != null) {
                    pList.add(cb.equal(root.get("id").as(UUID.class), entity.getId()));
                }
                Predicate[] pArr = new Predicate[pList.size()];
                return cb.and(pList.toArray(pArr));
            }
        };
        return specification;
    }
}
