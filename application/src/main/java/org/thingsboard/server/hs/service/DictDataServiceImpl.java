package org.thingsboard.server.hs.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.hs.dao.DictDataEntity;
import org.thingsboard.server.hs.dao.DictDataRepository;
import org.thingsboard.server.hs.entity.po.DictData;
import org.thingsboard.server.hs.entity.vo.DictDataListQuery;
import org.thingsboard.server.hs.entity.vo.DictDataQuery;

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
    DictDataRepository dictDataRepository;

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
            var es = cb.equal(root.<UUID>get("tenantId"), UUID.fromString(tenantId.toString()));

            if (dictDataListQuery != null) {
                if (!StringUtils.isBlank(dictDataListQuery.getName())) {
                    predicates.add(cb.like(root.get("name"), "%" + dictDataListQuery.getName().trim() + "%"));
                }
                if (!StringUtils.isBlank(dictDataListQuery.getCode())) {
                    predicates.add(cb.like(root.get("code"), "%" + dictDataListQuery.getCode().trim() + "%"));
                }
                if (!StringUtils.isBlank(dictDataListQuery.getDictDataType())) {
                    predicates.add(cb.equal(root.get("type"), dictDataListQuery.getDictDataType().trim()));
                }
            }
            if (predicates.size() == 0)
                return es;
            predicates.add(es);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
//        Pageable pageable = PageRequest.of(pageLink.getPage(), pageLink.getPageSize(), pageLink.getSortOrder());
//        var t = this.dictDataRepository.findAll(specification, pageable);
        return null;
    }

    /**
     * 更新或保存数据字典
     *
     * @param tenantId 租户Id
     * @param dictDataQuery 数据字典参数
     */
    @Override
    @Transactional
    public void updateOrSaveDictData(DictDataQuery dictDataQuery, TenantId tenantId) throws ThingsboardException {
        DictData dictData;
        if (!StringUtils.isBlank(dictDataQuery.getId())) {
            // 修改
            dictData = this.dictDataRepository.findById(UUID.fromString(dictDataQuery.getId())).get().toData();
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

            UUID timeBased = Uuids.timeBased();
            dictData.setId(timeBased.toString());
            dictData.setCreatedTime(Uuids.unixTimestamp(timeBased));
            dictData.setUpdatedTime(Uuids.unixTimestamp(timeBased));
            dictData.setTenantId(tenantId.toString());

            dictData.setName(dictDataQuery.getName());
            dictData.setComment(dictDataQuery.getComment());
            dictData.setIcon(dictDataQuery.getIcon());
            dictData.setType(dictDataQuery.getType().toString());
            dictData.setUnit(dictDataQuery.getUnit());
            dictData.setComment(dictDataQuery.getComment());

            // 设置编码
            if (!StringUtils.isBlank(dictData.getCode())) {
                if (!dictData.getCode().startsWith("SJZD")) {
                    throw new ThingsboardException("编码不符合规则", ThingsboardErrorCode.GENERAL);
                }
                dictData.setCode(dictData.getCode());
            } else {
                var codes = this.dictDataRepository.findCodes();
                if (codes.isEmpty()) {
                    dictData.setCode("SJZD0001");
                } else {
                    var ints = codes.stream().map(e -> Integer.valueOf(e.split("SJZD")[1])).sorted().collect(Collectors.toList());
                    int start = 0;
                    while (true) {
                        if (ints.size() - 1 == start) {
                            dictData.setCode("SJZD" + String.format("%04d", start + 2));
                            break;
                        }
                        if (!ints.get(start).equals(start + 1)) {
                            dictData.setCode("SJZD" + String.format("%04d", start + 1));
                            break;
                        }
                        start += 1;
                    }
                }
            }
        }

        this.dictDataRepository.save(new DictDataEntity(dictData));
    }

    /**
     * 获得数据字典详情
     *
     * @param id 数据字典Id
     */
    @Override
    public DictData getDictDataDetail(String id, TenantId tenantId) throws ThingsboardException {
        var s = this.dictDataRepository.findById(UUID.fromString(id));
        DictData dictData = this.dictDataRepository.findById(UUID.fromString(id)).get().toData();
        if (!dictData.getTenantId().equals(tenantId.toString())) {
            throw new ThingsboardException("租户Id不相等", ThingsboardErrorCode.GENERAL);
        }
        return this.dictDataRepository.findById(UUID.fromString(id)).get().toData();
    }

    /**
     * 删除数据字典
     *
     * @param id 数据字典Id
     */
    @Override
    @Transactional
    public void deleteDictDataById(String id, TenantId tenantId) throws ThingsboardException {
        DictData dictData = this.dictDataRepository.findById(UUID.fromString(id)).get().toData();
        if (!dictData.getTenantId().equals(tenantId.toString())) {
            throw new ThingsboardException("租户Id不相等", ThingsboardErrorCode.GENERAL);
        }
        this.dictDataRepository.deleteById(UUID.fromString(id));
    }
}
