package org.thingsboard.server.dao.hs.service;

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
import org.thingsboard.server.dao.hs.dao.DictDataEntity;
import org.thingsboard.server.dao.hs.dao.DictDataRepository;
import org.thingsboard.server.dao.hs.entity.po.DictData;
import org.thingsboard.server.dao.hs.entity.vo.DictDataListQuery;
import org.thingsboard.server.dao.hs.entity.vo.DictDataQuery;

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
    public PageData<DictData> listDictDataByQuery(TenantId tenantId, DictDataListQuery dictDataListQuery, PageLink pageLink) {
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
    public DictDataQuery updateOrSaveDictData(DictDataQuery dictDataQuery, TenantId tenantId) throws ThingsboardException {
        DictData dictData = new DictData();
        if (!StringUtils.isBlank(dictDataQuery.getId())) {
            // Modify
            dictData = this.dataRepository.findByTenantIdAndId(tenantId.getId(), toUUID(dictDataQuery.getId())).map(DictDataEntity::toData)
                    .orElseThrow(()->new ThingsboardException("dict data not exist", ThingsboardErrorCode.GENERAL));;
            BeanUtils.copyProperties(dictDataQuery, dictData, "id", "code");
            dictData.setType(dictDataQuery.getType().toString());
        } else {
            // Save
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
                .orElseThrow(()->new ThingsboardException("dict data not exist", ThingsboardErrorCode.GENERAL));
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
                .orElseThrow(()->new ThingsboardException("dict data not exist", ThingsboardErrorCode.GENERAL));
        this.dataRepository.deleteById(toUUID(dictData.getId()));
    }

    /**
     * 获得当前可用数据字典编码
     *
     * @param tenantId 租户Id
     * @return 当前可用数据字典编码
     */
    @Override
    public String getAvailableCode(TenantId tenantId) {
        return this.getAvailableCode(this.dataRepository.findAllCodesByTenantId(tenantId.getId()), "SJZD");
    }

    /**
     * 查询全部数据字典
     *
     * @param tenantId 租户Id
     * @return 全部数据字典
     */
    @Override
    public List<DictData> listAllDictData(TenantId tenantId) {
        return DaoUtil.convertDataList(this.dataRepository.findAllByTenantId(tenantId.getId()));
    }

    /**
     * 按keys查询全部数据字典
     *
     * @param tenantId 租户Id
     * @param keys     key列表
     * @return 数据字典map
     */
    @Override
    public Map<String, DictData> listDictDataByKeys(TenantId tenantId, List<String> keys) {
        return DaoUtil.convertDataList(this.dataRepository.findAllByTenantIdAndKeys(tenantId.getId(), keys))
                .stream().collect(Collectors.toMap(DictData::getName, Function.identity()));
    }

    @Autowired
    public void setDataRepository(DictDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }
}
