package org.thingsboard.server.hs.service;

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
import org.thingsboard.server.hs.dao.*;
import org.thingsboard.server.hs.entity.po.*;
import org.thingsboard.server.hs.entity.vo.*;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 设备字典接口实现类
 *
 * @author wwj
 * @since 2021.10.21
 */
@Service
@Slf4j
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class DictDeviceServiceImpl implements DictDeviceService {
    @Autowired
    DictDeviceRepository deviceRepository;

    @Autowired
    DictDeviceComponentRepository componentRepository;

    @Autowired
    DictDevicePropertyRepository propertyRepository;

    @Autowired
    DictDeviceGroupRepository groupRepository;

    @Autowired
    DictDeviceGroupPropertyRepository groupPropertyRepository;

    /**
     * 获得当前可用设备字典编码
     *
     * @param tenantId 租户Id
     * @return 可用设备字典编码
     */
    @Override
    public String getAvailableCode(TenantId tenantId) {
        var codes = this.deviceRepository.findAllCodesByTenantId(tenantId.getId());
        if (codes.isEmpty()) {
            return "SBZD0001";
        } else {
            var ints = codes.stream().map(e -> Integer.valueOf(e.split("SBZD")[1])).sorted().collect(Collectors.toList());
            int start = 0;
            while (true) {
                if (ints.size() - 1 == start) {
                    return "SBZD" + String.format("%04d", start + 2);
                }
                if (!ints.get(start).equals(start + 1)) {
                    return "SBZD" + String.format("%04d", start + 1);
                }
                start += 1;
            }
        }
    }

    /**
     * 获得设备字典列表
     *
     * @param dictDeviceListQuery 设备字典列表请求参数
     * @param tenantId            租户Id
     * @return 设备字典列表
     */
    @Override
    public PageData<DictDevice> listDictDeviceByQuery(DictDeviceListQuery dictDeviceListQuery, TenantId tenantId, PageLink pageLink) {
        // 动态条件查询
        Specification<DictDeviceEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            var es = cb.equal(root.<UUID>get("tenantId"), UUID.fromString(tenantId.toString()));

            if (dictDeviceListQuery != null) {
                if (!StringUtils.isBlank(dictDeviceListQuery.getName())) {
                    predicates.add(cb.like(root.get("name"), "%" + dictDeviceListQuery.getName().trim() + "%"));
                }
                if (!StringUtils.isBlank(dictDeviceListQuery.getCode())) {
                    predicates.add(cb.like(root.get("code"), "%" + dictDeviceListQuery.getCode().trim() + "%"));
                }
                if (!StringUtils.isBlank(dictDeviceListQuery.getSupplier())) {
                    predicates.add(cb.like(root.get("supplier"), "%" + dictDeviceListQuery.getSupplier().trim() + "%"));
                }
            }
            if (predicates.isEmpty())
                return es;
            predicates.add(es);
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 查询数据
        return DaoUtil.toPageData(this.deviceRepository.findAll(specification, DaoUtil.toPageable(pageLink)));
    }

    /**
     * 获得设备字典详情
     *
     * @param id       设备字典id
     * @param tenantId 租户Id
     * @return 设备字典详情
     */
    @Override
    public DictDeviceVO getDictDeviceDetail(String id, TenantId tenantId) throws ThingsboardException {
        var dictDevice = this.deviceRepository.findById(UUID.fromString(id)).get().toData();
        if (!dictDevice.getTenantId().equals(tenantId.toString())) {
            throw new ThingsboardException("租户Id不相等", ThingsboardErrorCode.GENERAL);
        }

        // 获得属性列表
        var propertyList = DaoUtil.convertDataList(this.propertyRepository.findAllByDictDeviceId(UUID.fromString(dictDevice.getId())))
                .stream().map(e -> DictDevicePropertyVO.builder().name(e.getName()).content(e.getContent()).build()).collect(Collectors.toList());
        // 获得分组及分组属性列表
        var groupList = DaoUtil.convertDataList(this.groupRepository.findAllByDictDeviceId(UUID.fromString(dictDevice.getId())));
        var groupUUIDList = groupList.stream().map(e->UUID.fromString(e.getId())).collect(Collectors.toList());
        List<DictDeviceGroupProperty> groupPropertyList;
        if (groupUUIDList.isEmpty()) {
            groupPropertyList = new ArrayList<>();
        } else {
            groupPropertyList = DaoUtil.convertDataList(this.groupPropertyRepository.findAllInDictDeviceGroupId(groupUUIDList));
        }
        var groupPropertyMap = groupPropertyList.stream()
                .collect(Collectors.groupingBy(DictDeviceGroupProperty::getDictDeviceGroupId));
        var groupVOList = groupList.stream().reduce(new ArrayList<DictDeviceGroupVO>(), (r, e) -> {
            List<DictDeviceGroupPropertyVO> groupPropertyVOList = new ArrayList<>();
            if (groupPropertyMap.containsKey(e.getId())) {
                groupPropertyVOList = groupPropertyMap.get(e.getId()).stream()
                        .map(g -> DictDeviceGroupPropertyVO.builder().name(g.getName()).content(g.getContent()).build()).collect(Collectors.toList());
            }
            r.add(DictDeviceGroupVO.builder().name(e.getName()).groupPropertyList(groupPropertyVOList).build());
            return r;
        }, (a, b) -> null);

        // 获得部件信息
        List<DictDeviceComponentVO> rList = new ArrayList<>();
        var componentList = DaoUtil.convertDataList(this.componentRepository.findAllByDictDeviceId(UUID.fromString(dictDevice.getId())));
        var componentVOList = componentList.stream().map(e -> DictDeviceComponentVO.builder()
                .id(e.getId())
                .parentId(e.getParentId())
                .code(e.getCode())
                .name(e.getName())
                .type(e.getType())
                .supplier(e.getSupplier())
                .model(e.getModel())
                .version(e.getVersion())
                .warrantyPeriod(e.getWarrantyPeriod())
                .picture(e.getPicture())
                .icon(e.getIcon())
                .componentList(new ArrayList<>()).build()
        ).collect(Collectors.toList());

        var pMap = componentVOList.stream().collect(Collectors.groupingBy(e->e.getParentId() == null?"null":e.getParentId()));

        // 开始递归组装数据
        this.recursionPackageComponent(rList, pMap, "null");

        //返回数据
        return DictDeviceVO.builder()
                .id(dictDevice.getId())
                .code(dictDevice.getCode())
                .name(dictDevice.getName())
                .type(dictDevice.getType())
                .supplier(dictDevice.getSupplier())
                .model(dictDevice.getModel())
                .version(dictDevice.getVersion())
                .warrantyPeriod(dictDevice.getWarrantyPeriod())
                .picture(dictDevice.getPicture())
                .icon(dictDevice.getIcon())
                .propertyList(propertyList)
                .groupList(groupVOList)
                .componentList(rList).build();
    }

    /**
     * 删除设备字典
     *
     * @param id       设备字典id
     * @param tenantId 租户Id
     */
    @Override
    @Transactional
    public void deleteDictDevice(String id, TenantId tenantId) throws ThingsboardException {
        DictDevice dictDevice = this.deviceRepository.findById(UUID.fromString(id)).get().toData();
        if (!dictDevice.getTenantId().equals(tenantId.toString())) {
            throw new ThingsboardException("租户Id不相等", ThingsboardErrorCode.GENERAL);
        }
        // 删除设备
        this.deviceRepository.deleteById(UUID.fromString(id));

        // 删除其余旧数据
        this.propertyRepository.deleteByDictDeviceId(UUID.fromString(dictDevice.getId()));
        this.componentRepository.deleteByDictDeviceId(UUID.fromString(dictDevice.getId()));
        this.groupRepository.deleteByDictDeviceId(UUID.fromString(dictDevice.getId()));
        this.groupPropertyRepository.deleteByDictDeviceId(UUID.fromString(dictDevice.getId()));
    }

    /**
     * 新增或修改设备字典
     *
     * @param dictDeviceVO 设备字典入参
     * @param tenantId     租户Id
     */
    @Override
    @Transactional
    public void updateOrSaveDictDevice(DictDeviceVO dictDeviceVO, TenantId tenantId) throws ThingsboardException {
        // 设备字典基础
        DictDevice dictDevice;
        DictDeviceEntity dictDeviceEntity;
        if (!StringUtils.isBlank(dictDeviceVO.getId())) {
            // 修改
            dictDevice = this.deviceRepository.findById(UUID.fromString(dictDeviceVO.getId())).get().toData();
            if (!dictDevice.getTenantId().equals(tenantId.toString())) {
                throw new ThingsboardException("租户Id不相等", ThingsboardErrorCode.GENERAL);
            }
            dictDevice.setCode(dictDeviceVO.getCode())
                    .setName(dictDeviceVO.getName())
                    .setType(dictDeviceVO.getType())
                    .setSupplier(dictDeviceVO.getSupplier())
                    .setModel(dictDeviceVO.getModel())
                    .setVersion(dictDeviceVO.getVersion())
                    .setWarrantyPeriod(dictDeviceVO.getWarrantyPeriod())
                    .setPicture(dictDeviceVO.getPicture())
                    .setIcon(dictDeviceVO.getIcon());

            // 删除其余旧数据
            this.propertyRepository.deleteByDictDeviceId(UUID.fromString(dictDevice.getId()));
            this.componentRepository.deleteByDictDeviceId(UUID.fromString(dictDevice.getId()));
            this.groupRepository.deleteByDictDeviceId(UUID.fromString(dictDevice.getId()));
            this.groupPropertyRepository.deleteByDictDeviceId(UUID.fromString(dictDevice.getId()));
        } else {
            // 新增
            dictDevice = DictDevice.builder()
                    .id(dictDeviceVO.getId())
                    .code(dictDeviceVO.getCode())
                    .name(dictDeviceVO.getName())
                    .type(dictDeviceVO.getType())
                    .supplier(dictDeviceVO.getSupplier())
                    .model(dictDeviceVO.getModel())
                    .version(dictDeviceVO.getVersion())
                    .warrantyPeriod(dictDeviceVO.getWarrantyPeriod())
                    .picture(dictDeviceVO.getPicture())
                    .icon(dictDeviceVO.getIcon())
                    .tenantId(tenantId.toString()).build();

        }

        // 保存设备字典
        dictDeviceEntity = new DictDeviceEntity(dictDevice);
        this.deviceRepository.save(dictDeviceEntity);

        // 设备字典属性及保存
        var propertyList = dictDeviceVO.getPropertyList().stream()
                .map(e -> DictDeviceProperty.builder()
                        .dictDeviceId(dictDeviceEntity.getId().toString())
                        .name(e.getName())
                        .content(e.getContent()).build()).collect(Collectors.toList());
        this.propertyRepository.saveAll(propertyList.stream().map(DictDevicePropertyEntity::new).collect(Collectors.toList()));

        // 设备字典分组、分组属性及保存
        var groupPropertyList = dictDeviceVO.getGroupList().stream().reduce(new ArrayList<DictDeviceGroupProperty>(), (r, e) -> {
            var dictDeviceGroup = DictDeviceGroup.builder()
                    .dictDeviceId(dictDeviceEntity.getId().toString())
                    .name(e.getName()).build();
            var dictDeviceGroupEntity = new DictDeviceGroupEntity(dictDeviceGroup);
            this.groupRepository.save(dictDeviceGroupEntity);

            var dictDeviceGroupPropertyList = e.getGroupPropertyList().stream().map(t -> DictDeviceGroupProperty.builder()
                    .dictDeviceGroupId(dictDeviceGroupEntity.getId().toString())
                    .dictDeviceId(dictDeviceEntity.getId().toString())
                    .name(t.getName())
                    .content(t.getContent()).build()).collect(Collectors.toList());
            r.addAll(dictDeviceGroupPropertyList);
            return r;
        }, (a, b) -> null);
        this.groupPropertyRepository.saveAll(groupPropertyList.stream().map(DictDeviceGroupPropertyEntity::new).collect(Collectors.toList()));

        // 设备字典部件及保存
        if (dictDeviceVO.getComponentList() != null && !dictDeviceVO.getComponentList().isEmpty()) {
            this.recursionSaveComponent(dictDeviceVO.getComponentList(), dictDeviceEntity.getId().toString(), null);
        }
    }

    /**
     * 递归保存部件
     *
     * @param componentList 部件列表
     * @param dictDeviceId  设备字典Id
     * @param parentId      父部件Id
     */
    public void recursionSaveComponent(List<DictDeviceComponentVO> componentList, String dictDeviceId, String parentId) {
        for (DictDeviceComponentVO componentVO : componentList) {
            var dictDeviceComponent = DictDeviceComponent.builder()
                    .code(componentVO.getCode())
                    .name(componentVO.getName())
                    .type(componentVO.getType())
                    .supplier(componentVO.getSupplier())
                    .model(componentVO.getModel())
                    .version(componentVO.getVersion())
                    .warrantyPeriod(componentVO.getWarrantyPeriod())
                    .picture(componentVO.getPicture())
                    .parentId(parentId)
                    .dictDeviceId(dictDeviceId)
                    .icon(componentVO.getIcon()).build();
            var dictDeviceComponentEntity = new DictDeviceComponentEntity(dictDeviceComponent);
            this.componentRepository.save(dictDeviceComponentEntity);
            if (componentVO.getComponentList() == null || componentVO.getComponentList().isEmpty()) {
                continue;
            }
            this.recursionSaveComponent(componentVO.getComponentList(), dictDeviceId, dictDeviceComponentEntity.getId().toString());
        }
    }

    /**
     * 递归包装部件
     *
     * @param rList    部件列表
     * @param pMap     父节点部件map
     * @param parentId 父节点Id
     */
    public void recursionPackageComponent(List<DictDeviceComponentVO> rList,
                                          Map<String, List<DictDeviceComponentVO>> pMap,
                                          String parentId) {
        if (pMap.get(parentId) != null && !pMap.get(parentId).isEmpty()) {
            pMap.get(parentId).forEach(e -> {
                rList.add(e);
                recursionPackageComponent(e.getComponentList(), pMap, e.getId());
            });
        }
    }
}
