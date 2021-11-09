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
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.hs.dao.*;
import org.thingsboard.server.dao.hs.entity.po.*;
import org.thingsboard.server.dao.hs.entity.vo.*;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
public class DictDeviceServiceImpl implements DictDeviceService, CommonService {
    // 设备字典Repository
    DictDeviceRepository deviceRepository;

    // 设备字典部件Repository
    DictDeviceComponentRepository componentRepository;

    // 设备字典属性Repository
    DictDevicePropertyRepository propertyRepository;

    // 设备字典分组Repository
    DictDeviceGroupRepository groupRepository;

    // 设备字典分组属性Repository
    DictDeviceGroupPropertyRepository groupPropertyRepository;

    // 设备配置设备字典关系Repository
    DeviceProfileDictDeviceRepository deviceProfileDictDeviceRepository;

    /**
     * 获得当前可用设备字典编码
     *
     * @param tenantId 租户Id
     * @return 可用设备字典编码
     */
    @Override
    public String getAvailableCode(TenantId tenantId) {
        return this.getAvailableCode(this.deviceRepository.findAllCodesByTenantId(tenantId.getId()), "SBZD");
    }

    /**
     * 获得设备字典列表
     *
     * @param dictDeviceListQuery 设备字典列表请求参数
     * @param tenantId            租户Id
     * @return 设备字典列表
     */
    @Override
    @SuppressWarnings("Duplicates")
    public PageData<DictDevice> listDictDeviceByQuery(DictDeviceListQuery dictDeviceListQuery, TenantId tenantId, PageLink pageLink) {
        // dynamic query
        Specification<DictDeviceEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.<UUID>get("tenantId"), tenantId.getId()));

            if (!StringUtils.isBlank(dictDeviceListQuery.getName()))
                predicates.add(cb.like(root.get("name"), "%" + dictDeviceListQuery.getName().trim() + "%"));
            if (!StringUtils.isBlank(dictDeviceListQuery.getCode()))
                predicates.add(cb.like(root.get("code"), "%" + dictDeviceListQuery.getCode().trim() + "%"));
            if (!StringUtils.isBlank(dictDeviceListQuery.getSupplier()))
                predicates.add(cb.like(root.get("supplier"), "%" + dictDeviceListQuery.getSupplier().trim() + "%"));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

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
        var dictDevice = this.deviceRepository.findByTenantIdAndId(tenantId.getId(), toUUID(id)).map(DictDeviceEntity::toData)
                .orElseThrow(()->new ThingsboardException("dict device not exist", ThingsboardErrorCode.GENERAL));

        var propertyList = DaoUtil.convertDataList(this.propertyRepository.findAllByDictDeviceId(toUUID(dictDevice.getId())))
                .stream().map(e -> DictDevicePropertyVO.builder().name(e.getName()).content(e.getContent()).build()).collect(Collectors.toList());

        var groupVOList = this.listDictDeviceGroup(toUUID(dictDevice.getId()));

        List<DictDeviceComponentVO> rList = new ArrayList<>();
        var componentList = DaoUtil.convertDataList(this.componentRepository.findAllByDictDeviceId(toUUID(dictDevice.getId())));
        var componentVOList = componentList.stream()
                .map(e -> {
                    DictDeviceComponentVO vo = new DictDeviceComponentVO();
                    BeanUtils.copyProperties(e, vo);
                    vo.setComponentList(new ArrayList<>());
                    return vo;
                }).collect(Collectors.toList());

        var pMap = componentVOList.stream().collect(Collectors.groupingBy(e -> Optional.ofNullable(e.getParentId()).orElse("null")));

        this.recursionPackageComponent(rList, pMap, "null");

        DictDeviceVO dictDeviceVO = DictDeviceVO.builder()
                .propertyList(propertyList)
                .groupList(groupVOList)
                .componentList(rList).build();
        BeanUtils.copyProperties(dictDevice, dictDeviceVO);
        return dictDeviceVO;
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
        DictDevice dictDevice = this.deviceRepository.findByTenantIdAndId(tenantId.getId(), toUUID(id)).map(DictDeviceEntity::toData)
                .orElseThrow(()->new ThingsboardException("dict device not exist", ThingsboardErrorCode.GENERAL));
        this.deviceRepository.deleteById(toUUID(dictDevice.getId()));

        this.propertyRepository.deleteByDictDeviceId(toUUID(dictDevice.getId()));
        this.componentRepository.deleteByDictDeviceId(toUUID(dictDevice.getId()));
        this.groupRepository.deleteByDictDeviceId(toUUID(dictDevice.getId()));
        this.groupPropertyRepository.deleteByDictDeviceId(toUUID(dictDevice.getId()));
    }

    /**
     * 获得未配置设备配置的设备字典列表
     *
     * @param tenantId 租户Id
     */
    @Override
    public List<DictDevice> listDictDeviceUnused(TenantId tenantId) {
        return DaoUtil.convertDataList(this.deviceRepository.findAllDictDeviceUnusedByTenantId(tenantId.getId()));
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
        DictDevice dictDevice = new DictDevice();
        DictDeviceEntity dictDeviceEntity;
        if (!StringUtils.isBlank(dictDeviceVO.getId())) {
            dictDevice = this.deviceRepository.findByTenantIdAndId(tenantId.getId(), toUUID(dictDeviceVO.getId())).map(DictDeviceEntity::toData)
                    .orElseThrow(()->new ThingsboardException("dict device not exist", ThingsboardErrorCode.GENERAL));;
            BeanUtils.copyProperties(dictDeviceVO, dictDevice, "id", "code");

            this.propertyRepository.deleteByDictDeviceId(toUUID(dictDevice.getId()));
            this.componentRepository.deleteByDictDeviceId(toUUID(dictDevice.getId()));
            this.groupRepository.deleteByDictDeviceId(toUUID(dictDevice.getId()));
            this.groupPropertyRepository.deleteByDictDeviceId(toUUID(dictDevice.getId()));
        } else {
            BeanUtils.copyProperties(dictDeviceVO, dictDevice, "id", "code");
            dictDevice.setTenantId(tenantId.toString());
        }

        dictDeviceEntity = new DictDeviceEntity(dictDevice);
        this.deviceRepository.save(dictDeviceEntity);

        AtomicInteger propertySort = new AtomicInteger();
        var propertyList = dictDeviceVO.getPropertyList().stream()
                .map(e -> {
                    propertySort.addAndGet(1);
                    return DictDeviceProperty.builder()
                            .dictDeviceId(dictDeviceEntity.getId().toString())
                            .name(e.getName())
                            .sort(propertySort.get())
                            .content(e.getContent()).build();
                }).collect(Collectors.toList());
        this.propertyRepository.saveAll(propertyList.stream().map(DictDevicePropertyEntity::new).collect(Collectors.toList()));

        AtomicInteger groupSort = new AtomicInteger();
        AtomicInteger groupPropertySort = new AtomicInteger();
        var groupPropertyList = dictDeviceVO.getGroupList().stream().reduce(new ArrayList<DictDeviceGroupProperty>(), (r, e) -> {
            var dictDeviceGroup = DictDeviceGroup.builder()
                    .dictDeviceId(dictDeviceEntity.getId().toString())
                    .sort(groupSort.get())
                    .name(e.getName()).build();
            groupSort.addAndGet(1);
            var dictDeviceGroupEntity = new DictDeviceGroupEntity(dictDeviceGroup);
            this.groupRepository.save(dictDeviceGroupEntity);

            var dictDeviceGroupPropertyList = e.getGroupPropertyList().stream().map(t -> {
                groupPropertySort.addAndGet(1);
                return DictDeviceGroupProperty.builder()
                        .dictDeviceGroupId(dictDeviceGroupEntity.getId().toString())
                        .dictDeviceId(dictDeviceEntity.getId().toString())
                        .name(t.getName())
                        .sort(groupPropertySort.get())
                        .title(t.getTitle())
                        .content(t.getContent()).build();
            }).collect(Collectors.toList());
            r.addAll(dictDeviceGroupPropertyList);
            return r;
        }, (a, b) -> null);
        this.groupPropertyRepository.saveAll(groupPropertyList.stream().map(DictDeviceGroupPropertyEntity::new).collect(Collectors.toList()));

        if (dictDeviceVO.getComponentList() != null && !dictDeviceVO.getComponentList().isEmpty()) {
            this.recursionSaveComponent(dictDeviceVO.getComponentList(), dictDeviceEntity.getId().toString(), null, 0);
        }
    }

    /**
     * 递归保存部件
     *
     * @param componentList 部件列表
     * @param dictDeviceId  设备字典Id
     * @param parentId      父部件Id
     * @param sort          排序字段
     */
    public void recursionSaveComponent(List<DictDeviceComponentVO> componentList, String dictDeviceId, String parentId, int sort) {
        for (DictDeviceComponentVO componentVO : componentList) {
            DictDeviceComponent dictDeviceComponent = new DictDeviceComponent();
            BeanUtils.copyProperties(componentVO, dictDeviceComponent, "id");
            dictDeviceComponent.setSort(sort).setDictDeviceId(dictDeviceId).setParentId(parentId);
            sort += 1;
            var dictDeviceComponentEntity = new DictDeviceComponentEntity(dictDeviceComponent);
            this.componentRepository.save(dictDeviceComponentEntity);
            if (componentVO.getComponentList() == null || componentVO.getComponentList().isEmpty()) {
                continue;
            }
            this.recursionSaveComponent(componentVO.getComponentList(), dictDeviceId, dictDeviceComponentEntity.getId().toString(), sort);
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

    /**
     * 获得设备字典分组及分组属性
     *
     * @param dictDeviceId 设备字典Id
     */
    @Override
    public List<DictDeviceGroupVO> listDictDeviceGroup(UUID dictDeviceId) {
        var groupList = DaoUtil.convertDataList(this.groupRepository.findAllByDictDeviceId(dictDeviceId));
        var groupUUIDList = groupList.stream().map(e -> toUUID(e.getId())).collect(Collectors.toList());
        List<DictDeviceGroupProperty> groupPropertyList;
        if (groupUUIDList.isEmpty()) {
            groupPropertyList = new ArrayList<>();
        } else {
            groupPropertyList = DaoUtil.convertDataList(this.groupPropertyRepository.findAllInDictDeviceGroupId(groupUUIDList));
        }
        var groupPropertyMap = groupPropertyList.stream()
                .collect(Collectors.groupingBy(DictDeviceGroupProperty::getDictDeviceGroupId));
        return groupList.stream().reduce(new ArrayList<>(), (r, e) -> {
            List<DictDeviceGroupPropertyVO> groupPropertyVOList = new ArrayList<>();
            if (groupPropertyMap.containsKey(e.getId())) {
                groupPropertyVOList = groupPropertyMap.get(e.getId()).stream()
                        .map(g -> DictDeviceGroupPropertyVO.builder()
                                .id(g.getId()).name(g.getName()).content(g.getContent()).title(g.getTitle()).createdTime(g.getCreatedTime())
                                .build()).collect(Collectors.toList());
            }
            r.add(DictDeviceGroupVO.builder().id(e.getId()).name(e.getName()).groupPropertyList(groupPropertyVOList).build());
            return r;
        }, (a, b) -> null);
    }

    /**
     * 获得设备字典分组属性，不包含分组
     *
     * @param dictDeviceId 设备字典Id
     */
    @Override
    public List<DictDeviceGroupPropertyVO> listDictDeviceGroupProperty(UUID dictDeviceId) {
        var groupPropertyList = DaoUtil.convertDataList(this.groupPropertyRepository.findAllByDictDeviceId(dictDeviceId));
        return groupPropertyList.stream()
                .map(g -> DictDeviceGroupPropertyVO.builder()
                        .id(g.getId()).name(g.getName()).content(g.getContent()).title(g.getTitle()).createdTime(g.getCreatedTime())
                        .build()).collect(Collectors.toList());
    }

    /**
     * 批量获得设备字典绑定的设备配置Id
     *
     * @param dictDeviceIdList 设备字典Id列表
     */
    @Override
    public Map<UUID, DeviceProfileId> listDeviceProfileIdsByDictDeviceIdList(List<UUID> dictDeviceIdList) {
        return this.deviceProfileDictDeviceRepository.findAllByDictDeviceIdIn(dictDeviceIdList)
                .stream()
                .collect(Collectors.toMap(DeviceProfileDictDeviceEntity::getDictDeviceId, e -> DeviceProfileId.fromString(e.getDeviceProfileId().toString()), (a, b) -> a));
    }

    /**
     * 获得设备字典绑定的设备配置Id
     *
     * @param dictDeviceId 设备字典Id
     */
    @Override
    public DeviceProfileId getDeviceProfileIdByDictDeviceId(UUID dictDeviceId) {
        var entity = this.deviceProfileDictDeviceRepository.findByDictDeviceId(dictDeviceId);
        return entity.map(deviceProfileDictDeviceEntity -> DeviceProfileId.fromString(deviceProfileDictDeviceEntity.getDeviceProfileId().toString())).orElse(null);
    }

    @Autowired
    public void setDeviceRepository(DictDeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Autowired
    public void setComponentRepository(DictDeviceComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    @Autowired
    public void setPropertyRepository(DictDevicePropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Autowired
    public void setGroupRepository(DictDeviceGroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Autowired
    public void setGroupPropertyRepository(DictDeviceGroupPropertyRepository groupPropertyRepository) {
        this.groupPropertyRepository = groupPropertyRepository;
    }

    @Autowired
    public void setDeviceProfileDictDeviceRepository(DeviceProfileDictDeviceRepository deviceProfileDictDeviceRepository) {
        this.deviceProfileDictDeviceRepository = deviceProfileDictDeviceRepository;
    }
}
