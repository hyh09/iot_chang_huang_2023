package org.thingsboard.server.dao.hs.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
public class DictDataServiceImpl extends AbstractEntityService implements DictDataService {

    @Autowired
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
    public PageData<DictData> listDictDataByQuery(TenantId tenantId, DictDataListQuery dictDataListQuery, PageLink pageLink) {
        // 动态条件查询
        Specification<DictDataEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            var es = cb.equal(root.<UUID>get("tenantId"), tenantId.getId());

            if (dictDataListQuery != null) {
                if (!StringUtils.isBlank(dictDataListQuery.getName())) {
                    predicates.add(cb.like(root.get("name"), "%" + dictDataListQuery.getName().trim() + "%"));
                }
                if (!StringUtils.isBlank(dictDataListQuery.getCode())) {
                    predicates.add(cb.like(root.get("code"), "%" + dictDataListQuery.getCode().trim() + "%"));
                }
                if (dictDataListQuery.getDictDataType() != null) {
                    predicates.add(cb.equal(root.get("type"), dictDataListQuery.getDictDataType().toString()));
                }
            }
            if (predicates.isEmpty())
                return es;
            predicates.add(es);
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 查询数据
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
    public void updateOrSaveDictData(DictDataQuery dictDataQuery, TenantId tenantId) throws ThingsboardException {
        DictData dictData;
        if (!StringUtils.isBlank(dictDataQuery.getId())) {
            // 修改
            dictData = this.dataRepository.findById(UUID.fromString(dictDataQuery.getId())).get().toData();
            if (!dictData.getTenantId().equals(tenantId.toString())) {
                throw new ThingsboardException("租户Id不相等", ThingsboardErrorCode.GENERAL);
            }
            dictData.setName(dictDataQuery.getName());
            dictData.setComment(dictDataQuery.getComment());
            dictData.setIcon(dictDataQuery.getIcon());
            dictData.setType(dictDataQuery.getType().toString());
            dictData.setComment(dictDataQuery.getComment());
        } else {
            // 新增
            dictData = new DictData();

            dictData.setTenantId(tenantId.toString());
            dictData.setName(dictDataQuery.getName());
            dictData.setCode(dictDataQuery.getCode());
            dictData.setComment(dictDataQuery.getComment());
            dictData.setIcon(dictDataQuery.getIcon());
            dictData.setType(dictDataQuery.getType().toString());
            dictData.setUnit(dictDataQuery.getUnit());
            dictData.setComment(dictDataQuery.getComment());
        }
        this.dataRepository.save(new DictDataEntity(dictData));
    }

    /**
     * 获得数据字典详情
     *
     * @param id 数据字典Id
     */
    @Override
    public DictData getDictDataDetail(String id, TenantId tenantId) throws ThingsboardException {
        var s = this.dataRepository.findById(UUID.fromString(id));
        DictData dictData = this.dataRepository.findById(UUID.fromString(id)).get().toData();
        if (!dictData.getTenantId().equals(tenantId.toString())) {
            throw new ThingsboardException("租户Id不相等", ThingsboardErrorCode.GENERAL);
        }
        return this.dataRepository.findById(UUID.fromString(id)).get().toData();
    }

    /**
     * 删除数据字典
     *
     * @param id 数据字典Id
     */
    @Override
    @Transactional
    public void deleteDictDataById(String id, TenantId tenantId) throws ThingsboardException {
        DictData dictData = this.dataRepository.findById(UUID.fromString(id)).get().toData();
        if (!dictData.getTenantId().equals(tenantId.toString())) {
            throw new ThingsboardException("租户Id不相等", ThingsboardErrorCode.GENERAL);
        }
        this.dataRepository.deleteById(UUID.fromString(id));
    }

    /**
     * 获得当前可用数据字典编码
     *
     * @param tenantId 租户Id
     * @return 当前可用数据字典编码
     */
    @Override
    public String getAvailableCode(TenantId tenantId) {
        var codes = this.dataRepository.findAllCodesByTenantId(tenantId.getId());
        if (codes.isEmpty()) {
            return "SJZD0001";
        } else {
            var ints = codes.stream().map(e -> Integer.valueOf(e.split("SJZD")[1])).sorted().collect(Collectors.toList());
            int start = 0;
            while (true) {
                if (ints.size() - 1 == start) {
                    return "SJZD" + String.format("%04d", start + 2);
                }
                if (!ints.get(start).equals(start + 1)) {
                    return "SJZD" + String.format("%04d", start + 1);
                }
                start += 1;
            }
        }
    }
}
