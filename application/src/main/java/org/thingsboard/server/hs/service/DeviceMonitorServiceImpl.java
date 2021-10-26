package org.thingsboard.server.hs.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.device.DeviceProfileService;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.model.sql.DeviceProfileEntity;
import org.thingsboard.server.dao.sql.device.DeviceProfileRepository;
import org.thingsboard.server.hs.dao.*;
import org.thingsboard.server.hs.entity.po.DictData;
import org.thingsboard.server.hs.entity.po.DictDevice;
import org.thingsboard.server.hs.entity.vo.DeviceProfileVO;
import org.thingsboard.server.hs.entity.vo.DictDataListQuery;
import org.thingsboard.server.hs.entity.vo.DictDataQuery;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 设备监控接口实现类
 *
 * @author wwj
 * @since 2021.10.26
 */
@Service
@Slf4j
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class DeviceMonitorServiceImpl extends AbstractEntityService implements DeviceMonitorService {

    @Autowired
    DeviceProfileRepository deviceProfileRepository;

    @Autowired
    DictDeviceRepository dictDeviceRepository;

    @Autowired
    DeviceProfileDictDeviceRepository deviceProfileDictDeviceRepository;

    @Autowired
    DeviceProfileService deviceProfileService;

    /**
     * 获得设备配置列表
     *
     * @param tenantId 租户Id
     * @param name     设备配置名称
     * @param pageLink 分页排序参数
     * @return 设备配置列表
     */
    @Override
    public PageData<DeviceProfile> listDeviceProfile(TenantId tenantId, String name, PageLink pageLink) {
        // 动态条件查询 TODO 增加@Type
        Specification<DeviceProfileEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            var es = cb.equal(root.<UUID>get("tenantId"), tenantId.getId());

            if (!StringUtils.isBlank(name)) {
                predicates.add(cb.like(root.get("name"), "%" + name.trim() + "%"));
            }

            if (predicates.isEmpty())
                return es;
            predicates.add(es);
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 查询数据
        return DaoUtil.toPageData(this.deviceProfileRepository.findAll(specification, DaoUtil.toPageable(pageLink)));
    }

    /**
     * 获得设备配置详情
     *
     * @param tenantId        租户Id
     * @param deviceProfileId 设备配置Id
     * @return 设备详情
     */
    @Override
    public DeviceProfileVO getDeviceProfileDetail(TenantId tenantId, DeviceProfileId deviceProfileId) {
        // 获得设备配置
        var deviceProfile = deviceProfileService.findDeviceProfileById(tenantId, deviceProfileId);

        // 获得绑定的设备字典
        var dictDeviceList = DaoUtil.convertDataList(this.deviceProfileDictDeviceRepository.findAllBindDeviceProfile(deviceProfile.getId().getId()));
        return DeviceProfileVO.builder().deviceProfile(deviceProfile).dictDeviceList(dictDeviceList).build();
    }

    /**
     * 删除绑定的设备字典
     *
     * @param deviceProfileId 设备配置id
     */
    @Override
    @Transactional
    public void deleteBindDictDevice(DeviceProfileId deviceProfileId) {
        this.deviceProfileDictDeviceRepository.deleteByDeviceProfileId(deviceProfileId.getId());
    }

    /**
     * 绑定设备字典到设备配置
     *
     * @param dictDeviceList  设备字典列表
     * @param deviceProfileId 设备配置Id
     */
    @Override
    @Transactional
    public void bindDictDeviceToDeviceProfile(List<DictDevice> dictDeviceList, DeviceProfileId deviceProfileId) {
        this.deleteBindDictDevice(deviceProfileId);
        deviceProfileDictDeviceRepository.saveAll(dictDeviceList.stream().map(e->{
            DeviceProfileDictDeviceEntity entity = new DeviceProfileDictDeviceEntity();
            entity.setDictDeviceId(UUID.fromString(e.getId()));
            entity.setDeviceProfileId(deviceProfileId.getId());
            return entity;
        }).collect(Collectors.toList()));
    }
}
