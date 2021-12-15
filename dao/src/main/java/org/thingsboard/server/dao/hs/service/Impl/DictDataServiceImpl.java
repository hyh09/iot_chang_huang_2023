package org.thingsboard.server.dao.hs.service.Impl;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.dao.*;
import org.thingsboard.server.dao.hs.entity.po.*;
import org.thingsboard.server.dao.hs.entity.vo.DictDataListQuery;
import org.thingsboard.server.dao.hs.entity.vo.DictDataQuery;
import org.thingsboard.server.dao.hs.service.CommonService;
import org.thingsboard.server.dao.hs.service.DictDataService;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 数据字典接口实现类
 *
 * @author wwj
 * @since 2021.10.18
 */
@Service
@Slf4j
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class DictDataServiceImpl extends AbstractEntityService implements DictDataService, CommonService {

    DictDataRepository dataRepository;

    DictDeviceRepository deviceRepository;
    DictDeviceGroupPropertyRepository groupPropertyRepository;
    DictDeviceComponentRepository componentRepository;
    DictDeviceComponentPropertyRepository componentPropertyRepository;
    DictDeviceStandardPropertyRepository standardPropertyRepository;

    /**
     * 查询数据字典列表
     *
     * @param tenantId          租户Id
     * @param dictDataListQuery 查询条件
     * @param pageLink          分页条件
     * @return 数据字典列表
     */
    @Override
    @SuppressWarnings("Duplicates")
    public PageData<DictData> listPageDictDataByQuery(TenantId tenantId, DictDataListQuery dictDataListQuery, PageLink pageLink) {
        // dynamic query
        Specification<DictDataEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.<UUID>get("tenantId"), tenantId.getId()));
            if (!StringUtils.isBlank(dictDataListQuery.getName()))
                predicates.add(cb.like(root.get("name"), "%" + dictDataListQuery.getName().trim() + "%"));
            if (!StringUtils.isBlank(dictDataListQuery.getCode()))
                predicates.add(cb.like(root.get("code"), "%" + dictDataListQuery.getCode().trim() + "%"));
            if (dictDataListQuery.getDictDataType() != null)
                predicates.add(cb.equal(root.get("type"), dictDataListQuery.getDictDataType().toString()));
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };

        return DaoUtil.toPageData(this.dataRepository.findAll(specification, DaoUtil.toPageable(pageLink)));
    }

    /**
     * 更新或保存数据字典
     *
     * @param tenantId      租户Id
     * @param dictDataQuery 数据字典参数
     */
    @Override
    @Transactional
    public DictDataQuery saveOrUpdateDictData(DictDataQuery dictDataQuery, TenantId tenantId) throws ThingsboardException {
        DictData dictData = new DictData();
        if (!StringUtils.isBlank(dictDataQuery.getId())) {
            dictData = this.dataRepository.findByTenantIdAndId(tenantId.getId(), toUUID(dictDataQuery.getId())).map(DictDataEntity::toData)
                    .orElseThrow(() -> new ThingsboardException("数据字典不存在", ThingsboardErrorCode.GENERAL));

            BeanUtils.copyProperties(dictDataQuery, dictData, "id", "code");
            dictData.setType(dictDataQuery.getType().toString());
        } else {
            BeanUtils.copyProperties(dictDataQuery, dictData);
            dictData.setType(dictDataQuery.getType().toString());
            dictData.setTenantId(tenantId.toString());
        }

        DictDataEntity dictDataEntity = new DictDataEntity(dictData);
        this.dataRepository.save(dictDataEntity);
        BeanUtils.copyProperties(dictDataEntity.toData(), dictDataQuery);
        return dictDataQuery;
    }

    /**
     * 获得数据字典详情
     *
     * @param id 数据字典Id
     */
    @Override
    public DictData getDictDataDetail(String id, TenantId tenantId) throws ThingsboardException {
        return this.dataRepository.findByTenantIdAndId(tenantId.getId(), toUUID(id)).map(DictDataEntity::toData)
                .orElseThrow(() -> new ThingsboardException("数据字典不存在！", ThingsboardErrorCode.GENERAL));
    }

    /**
     * 删除数据字典
     *
     * @param id 数据字典Id
     */
    @Override
    @Transactional
    public void deleteDictDataById(String id, TenantId tenantId) throws ThingsboardException {
        var dictData = this.dataRepository.findByTenantIdAndId(tenantId.getId(), toUUID(id)).map(DictDataEntity::toData)
                .orElseThrow(() -> new ThingsboardException("数据字典不存在！", ThingsboardErrorCode.GENERAL));
        var groupPropertyList = DaoUtil.convertDataList(this.groupPropertyRepository.findAllByDictDataId(toUUID(id)));
        var standardPropertyList = DaoUtil.convertDataList(this.standardPropertyRepository.findAllByDictDataId(toUUID(id)));
        var componentList = DaoUtil.convertDataList(this.componentPropertyRepository.findAllByDictDataId(toUUID(id)));
        
        if (groupPropertyList.isEmpty() && componentList.isEmpty() && standardPropertyList.isEmpty())
            this.dataRepository.deleteById(toUUID(dictData.getId()));
        else {
            Set<UUID> uuids = Sets.newHashSet();
            uuids.addAll(groupPropertyList.stream().map(DictDeviceGroupProperty::getDictDeviceId).map(this::toUUID).collect(Collectors.toList()));
            uuids.addAll(componentList.stream().map(DictDeviceComponentProperty::getDictDeviceId).map(this::toUUID).collect(Collectors.toList()));
            uuids.addAll(standardPropertyList.stream().map(DictDeviceStandardProperty::getDictDeviceId).map(this::toUUID).collect(Collectors.toList()));
            if (uuids.isEmpty())
                throw new ThingsboardException("存在关联的设备字典，但id不存在!请联系管理员!", ThingsboardErrorCode.GENERAL);
            var nameList = DaoUtil.convertDataList(Lists.newArrayList(this.deviceRepository.findAllById(uuids))).stream().map(DictDevice::getName).collect(Collectors.toList());
            throw new ThingsboardException("存在关联的设备字典: " + Joiner.on(", ").join(nameList), ThingsboardErrorCode.GENERAL);
        }
    }

    /**
     * 获得当前可用数据字典编码
     *
     * @param tenantId 租户Id
     * @return 当前可用数据字典编码
     */
    @Override
    public String getAvailableCode(TenantId tenantId) {
        return this.getAvailableCode(this.dataRepository.findAllCodesByTenantId(tenantId.getId()), HSConstants.CODE_PREFIX_DICT_DATA);
    }

    /**
     * 查询全部数据字典
     *
     * @param tenantId 租户Id
     * @return 全部数据字典
     */
    @Override
    public List<DictData> listDictData(TenantId tenantId) {
        return DaoUtil.convertDataList(this.dataRepository.findAllByTenantId(tenantId.getId()));
    }

    /**
     * 查询全部数据字典
     *
     * @param tenantId 租户Id
     * @return 全部数据字典map
     */
    @Override
    public Map<String, DictData> getDictDataMap(TenantId tenantId) {
        return DaoUtil.convertDataList(this.dataRepository.findAllByTenantId(tenantId.getId())).stream().collect(Collectors.toMap(DictData::getId, Function.identity()));
    }

    /**
     * 按keys查询全部数据字典
     *
     * @param tenantId 租户Id
     * @param keys     key列表
     * @return 数据字典map
     */
    @Override
    public Map<String, DictData> getDictDataMapByKeys(TenantId tenantId, List<String> keys) {
        return DaoUtil.convertDataList(this.dataRepository.findAllByTenantIdAndKeys(tenantId.getId(), keys))
                .stream().collect(Collectors.toMap(DictData::getName, Function.identity()));
    }

    @Autowired
    public void setDataRepository(DictDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Autowired
    public void setDeviceRepository(DictDeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Autowired
    public void setGroupPropertyRepository(DictDeviceGroupPropertyRepository groupPropertyRepository) {
        this.groupPropertyRepository = groupPropertyRepository;
    }

    @Autowired
    public void setComponentRepository(DictDeviceComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    @Autowired
    public void setComponentPropertyRepository(DictDeviceComponentPropertyRepository componentPropertyRepository) {
        this.componentPropertyRepository = componentPropertyRepository;
    }

    @Autowired
    public void setStandardPropertyRepository(DictDeviceStandardPropertyRepository standardPropertyRepository) {
        this.standardPropertyRepository = standardPropertyRepository;
    }
}
