package org.thingsboard.server.dao.attribute;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.device.DictDeviceDataVo;
import org.thingsboard.server.common.data.vo.device.RunningStateVo;
import org.thingsboard.server.dao.hs.service.DictDeviceService;
import org.thingsboard.server.dao.hsms.entity.enums.DictDevicePropertySwitchEnum;
import org.thingsboard.server.dao.hsms.entity.vo.DictDevicePropertySwitchVO;
import org.thingsboard.server.dao.kanban.vo.inside.ComponentDataDTO;
import org.thingsboard.server.dao.kanban.vo.inside.DataDTO;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Project Name: thingsboard
 * @File Name: AttributeCullingImpl
 * @Date: 2022/12/21 11:12
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Slf4j
@Service
public class AttributeCullingImpl implements AttributeCullingSvc {
    @Autowired
    private DictDeviceService dictDeviceService;


    @Override
    public List<RunningStateVo> queryKeyToSwitch(List<RunningStateVo> resultList, TenantId tenantId, UUID deviceId, boolean isFactoryUser) {
        if (CollectionUtils.isEmpty(resultList)) {
            return resultList;
        }
        List<DictDevicePropertySwitchVO> devicePropertySwitchVOList = getSwitchList(tenantId, deviceId);
        if (CollectionUtils.isEmpty(devicePropertySwitchVOList)) {
            return resultList;
        }

        Map<String, DictDevicePropertySwitchEnum> map1 = devicePropertySwitchVOList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getPropertyName()))
                .filter(m1 -> m1.getPropertySwitch() != null)
                .collect(Collectors.toMap(DictDevicePropertySwitchVO::getPropertyName, DictDevicePropertySwitchVO::getPropertySwitch));
        List<RunningStateVo> resultList3 = new ArrayList<>();
        for (RunningStateVo m3 : resultList) {
            List<String> keysNameS = m3.getAttributeNames();
            if (isAddList(keysNameS, map1)) {
                resultList3.add(m3);
            }
        }
        return resultList3;
    }


    /**
     * 1.优先由 attributeNames 来判断，
     * 2.再由name 来判断
     *
     * @param map
     * @param tenantId
     * @param deviceId
     * @return
     */
    @Override
    public Map toMakeToMap(Map<String, List<DictDeviceDataVo>> map, TenantId tenantId, UUID deviceId, boolean isFactoryUser) {
        List<DictDevicePropertySwitchVO> devicePropertySwitchVOList = getSwitchList(tenantId, deviceId);
        if (CollectionUtils.isEmpty(devicePropertySwitchVOList)) {
            return map;
        }

        Map<String, DictDevicePropertySwitchEnum> map1 = devicePropertySwitchVOList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getPropertyName()))
                .filter(m1 -> m1.getPropertySwitch() != null)
                .collect(Collectors.toMap(DictDevicePropertySwitchVO::getPropertyName, DictDevicePropertySwitchVO::getPropertySwitch));
        Map<String, List<DictDeviceDataVo>> result = new HashMap<>();
        map.forEach((k1, v1) -> {
            List<DictDeviceDataVo> deviceDataVoList = v1;
            List<DictDeviceDataVo> resultList3 = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(deviceDataVoList)) {
                for (DictDeviceDataVo vo : deviceDataVoList) {
                    List<String> keysNameS = vo.getAttributeNames();

                    if (CollectionUtils.isNotEmpty(keysNameS)) {
                        if (isAddList(keysNameS, map1)) {
                            resultList3.add(vo);
                        }
                    }

                    if (StringUtils.isNotEmpty(vo.getName())) {
                        if (isAddListStr(vo.getName(), map1)) {
                            resultList3.add(vo);
                        }
                    }
                }
            }
            result.put(k1, resultList3);
        });


        return result;

    }


    /**
     * 2022-12-23
     * 2023-02-21 新增， 将data.tableName  赋值给 data.key
     *
     * @param componentDataDTOList 部件的list
     * @param tenantId             租户id
     * @param deviceId             设备id
     * @param isFactoryUser        是否是工厂管理员
     * @return
     */
    @Override
    public List<ComponentDataDTO> componentData(List<ComponentDataDTO> componentDataDTOList, TenantId tenantId, UUID deviceId, boolean isFactoryUser) {
        if (CollectionUtils.isEmpty(componentDataDTOList)) {
            return componentDataDTOList;
        }
        /**
         * 查询设备开关的属性
         */
        List<DictDevicePropertySwitchVO> devicePropertySwitchVOList = getSwitchList(tenantId, deviceId);
        if (CollectionUtils.isEmpty(devicePropertySwitchVOList)) {
            return componentDataDTOList;
        }
        Map<String, DictDevicePropertySwitchEnum> map1 = devicePropertySwitchVOList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getPropertyName()))
                .filter(m1 -> m1.getPropertySwitch() != null)
                .collect(Collectors.toMap(DictDevicePropertySwitchVO::getPropertyName, DictDevicePropertySwitchVO::getPropertySwitch));

        List<ComponentDataDTO> componentDataDTOListResults = new ArrayList<>();
        for (ComponentDataDTO v1 : componentDataDTOList) {
            List<DataDTO> dataDTOList = v1.getData();
            if (CollectionUtils.isNotEmpty(dataDTOList)) {
                dataDTOList.forEach(m1 -> {
                    m1.setFlg(isAddListStr3(m1, map1));
                });
                if (isFactoryUser) {
                    /** 如果是工厂维度的用户 只返回 true， 【在 设备管理--> 管理参数权限 下配置的 ‘工厂可见’ 为false的 工厂看不到】 --2023-03-02*/
                    List<DataDTO> dataDTOList1 = dataDTOList.stream().filter(d1 -> d1.getFlg()).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(dataDTOList1)) {
                        v1.setData(dataDTOList1);
                        componentDataDTOListResults.add(v1);
                    }
                } else {

                    v1.setData(dataDTOList);
                    componentDataDTOListResults.add(v1);
                }
            }

        }
        return componentDataDTOListResults;
    }

    /**
     * 2023-1-4 新增  工厂账号不显示这些参数，租户还是要全部显得
     *
     * @param tenantId
     * @param deviceId
     * @return
     */
    private List<DictDevicePropertySwitchVO> getSwitchList(TenantId tenantId, UUID deviceId) {
        List<DictDevicePropertySwitchVO> list = new ArrayList<>();
        try {
            list = dictDeviceService.listDictDeviceSwitches(tenantId, deviceId.toString());
        } catch (ThingsboardException e1) {
            log.info("查询[dictDeviceService.listDictDeviceSwitches]接口异常异常日志:{}", e1);
        } catch (Exception e2) {
            log.info("查询[dictDeviceService.listDictDeviceSwitches]接口异常异常日志:{}", e2);
        }
        return list;
    }


    private boolean isAddList(List<String> keysNameS, Map<String, DictDevicePropertySwitchEnum> map1) {
        if (CollectionUtils.isEmpty(keysNameS)) {
            return true;
        }
        for (String st1 : keysNameS) {
            if (map1.get(st1) == DictDevicePropertySwitchEnum.HIDE) {
                return false;
            }
        }
        return true;

    }


    private boolean isAddListStr(String st1, Map<String, DictDevicePropertySwitchEnum> map1) {
        if (StringUtils.isEmpty(st1)) {
            return true;
        }
        if (map1.get(st1) == DictDevicePropertySwitchEnum.HIDE) {
            return false;
        }

        return true;

    }


    private boolean isAddListStr3(DataDTO d1, Map<String, DictDevicePropertySwitchEnum> map1) {
        if (d1 == null) {
            return true;
        }
        if (map1.get(d1.getKey()) == DictDevicePropertySwitchEnum.HIDE) {
            return false;
        }

        return true;

    }
}
