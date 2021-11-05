package org.thingsboard.server.dao.hs.service;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.attributes.AttributesService;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.hs.entity.dto.DeviceBaseDTO;
import org.thingsboard.server.dao.hs.entity.dto.DeviceListAffiliationDTO;
import org.thingsboard.server.dao.hs.entity.vo.FactoryDeviceQuery;
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.model.sql.*;
import org.thingsboard.server.dao.sql.attributes.AttributeKvRepository;
import org.thingsboard.server.dao.sql.device.DeviceRepository;
import org.thingsboard.server.dao.sql.factory.FactoryRepository;
import org.thingsboard.server.dao.sql.productionline.ProductionLineRepository;
import org.thingsboard.server.dao.sql.workshop.WorkshopRepository;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 二方库接口实现类
 *
 * @author wwj
 * @since 2021.11.1
 */
@Service
@Slf4j
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class ClientServiceImpl extends AbstractEntityService implements ClientService {

    // 工厂Repository
    FactoryRepository factoryRepository;

    // 车间Repository
    WorkshopRepository workshopRepository;

    // 产线Repository
    ProductionLineRepository productionLineRepository;

    // 设备Repository
    DeviceRepository deviceRepository;

    // 属性Repository
    AttributeKvRepository attributeKvRepository;

    // 属性Service
    AttributesService attributesService;

    /**
     * 查询设备基本信息、工厂、车间、产线、设备等
     *
     * @param t extends FactoryDeviceQuery
     */
    @Override
    public <T extends FactoryDeviceQuery> DeviceBaseDTO getDeviceBase(TenantId tenantId, T t) {
        return null;
    }

    /**
     * 查询设备列表
     *
     * @param tenantId 租户Id
     * @param t        extends FactoryDeviceQuery
     */
    @Override
    public <T extends FactoryDeviceQuery> List<Device> listDeviceByQuery(TenantId tenantId, T t) {
        return null;
    }

    /**
     * 分页查询设备列表
     *
     * @param tenantId 租户Id
     * @param t        extends FactoryDeviceQuery
     * @param pageLink 分页参数
     */
    @Override
    public <T extends FactoryDeviceQuery> PageData<Device> listDevicePageByQuery(TenantId tenantId, T t, PageLink pageLink) {
        return null;
    }

    /**
     * 查询全部设备的在线情况
     *
     * @param allDeviceIdList 设备的UUID列表
     */
    @Override
    public Map<String, Boolean> listAllDeviceOnlineStatus(List<UUID> allDeviceIdList) {
        return null;
    }

    /**
     * 查询全部设备的工厂、车间、产线信息
     *
     * @param deviceList 设备列表
     */
    @Override
    public DeviceListAffiliationDTO getDeviceListAffiliation(List<Device> deviceList) {
        return null;
    }

    /**
     * 组装设备请求 specification
     *
     * @param tenantId 租户Id
     * @param t        extends FactoryDeviceQuery
     */
    public <T extends FactoryDeviceQuery> Specification<DeviceEntity> getDeviceQuerySpecification(TenantId tenantId, T t) {
        return null;
    }

    @Autowired
    public void setFactoryRepository(FactoryRepository factoryRepository) {
        this.factoryRepository = factoryRepository;
    }

    @Autowired
    public void setWorkshopRepository(WorkshopRepository workshopRepository) {
        this.workshopRepository = workshopRepository;
    }

    @Autowired
    public void setProductionLineRepository(ProductionLineRepository productionLineRepository) {
        this.productionLineRepository = productionLineRepository;
    }

    @Autowired
    public void setDeviceRepository(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Autowired
    public void setAttributeKvRepository(AttributeKvRepository attributeKvRepository) {
        this.attributeKvRepository = attributeKvRepository;
    }

    @Autowired
    public void setAttributesService(AttributesService attributesService) {
        this.attributesService = attributesService;
    }
}
