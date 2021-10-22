package org.thingsboard.server.hs.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.hs.dao.*;
import org.thingsboard.server.hs.entity.vo.DictDeviceVO;

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
     * 新增或修改设备字典
     *
     * @param dictDeviceVO 设备字典入参
     */
    @Override
    @Transactional
    public void updateOrSaveDictDevice(DictDeviceVO dictDeviceVO) {

    }
}
