package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.vo.enums.key.KeyNameEnums;
import org.thingsboard.server.dao.attribute.AttributeCullingSvc;
import org.thingsboard.server.dao.kanban.service.svc.KanbanDeviceOneOutSvc;
import org.thingsboard.server.dao.kanban.vo.KanbanDeviceVo;
import org.thingsboard.server.dao.kanban.vo.inside.ComponentDataDTO;
import org.thingsboard.server.dao.kanban.vo.inside.DataDTO;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Project Name: thingsboard
 * @File Name: KanbanDeviceOneOutController
 * @Date: 2022/11/1 14:11
 * @author: wb04
 * 业务中文描述: 第三方接口看板
 * Copyright (c) 2022,All Rights Reserved.
 */
@Api(value = "第三方看板接口 获取设备的信息看板", tags = {"第三方看板接口 获取设备的信息看板"})
@RestController
@TbCoreComponent
@RequestMapping("/api/three/kanban/")
public class KanbanDeviceOneOutController extends BaseController {

    @Autowired
    private KanbanDeviceOneOutSvc kanbanDeviceOneOutSvc;
    @Autowired
    private AttributeCullingSvc attributeCullingSvc;

    /**
     * 需要改动的：
     * 参考逻辑： 设备监控
     * 效果：参数合并
     * ***************参数名 使用配置的名称
     * ***************值用配置的分隔符拼接
     * ***************配置的位置：设备管理-数据关联- （用设备字典来查询）
     * ###2023-02-21 返回的参数合并之后的数据重复了，
     * ###2023-02-22 提出 缺少 整机，能耗，产量的； 也需要合并;
     * ###2023-03-01 提出 能耗的需要翻译  能耗 下的数据 对应的key:  翻译的枚举：KeyNameEnums  和 工厂维度 --> 云端维度
     *
     * @param deviceId
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation(value = "获取设备的信息看板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", required = true, paramType = "form", value = "设备id", example = "ac0297b1-5656-11ec-a240-955d7c1497e4"),
    })
    @GetMapping(value = "/integratedDeviceInterface")
    public KanbanDeviceVo integratedDeviceInterface(@RequestParam("deviceId") UUID deviceId) throws ThingsboardException {
        KanbanDeviceVo kanbanDeviceVo = kanbanDeviceOneOutSvc.integratedDeviceInterface(getTenantId(), deviceId);
        if (kanbanDeviceVo != null && CollectionUtils.isNotEmpty(kanbanDeviceVo.getComponentData())) {
            List<ComponentDataDTO> componentDataDTOList = kanbanDeviceVo.getComponentData();
            List<ComponentDataDTO> dataDTOList = attributeCullingSvc.componentData(componentDataDTOList, getTenantId(), deviceId, isFactoryUser());
            List<ComponentDataDTO> dataDTOList1 = exchangeValue(dataDTOList);
            kanbanDeviceVo.setComponentData(dataDTOList1);

        }
        return kanbanDeviceVo;
    }

    /**
     * 将tableName不为空的赋值给key
     * ##2023-03-01 添加对能耗 -产量的翻译
     *
     * @param dataDTOList
     * @return
     */
    private List<ComponentDataDTO> exchangeValue(List<ComponentDataDTO> dataDTOList) {
        List<ComponentDataDTO> resultSet = new ArrayList<>();
        dataDTOList.forEach(m1 -> {
            List<DataDTO> dataDTOList1 = m1.getData();
            dataDTOList1.forEach(m2 -> {
                String tableName = m2.getTableName();
                if (StringUtils.isNotEmpty(tableName)) {
                    m2.setKey(tableName);
                } else {
                    m2.setKey(KeyNameEnums.translateCode(m2.getKey()));/** 添加对产量，能耗的中文翻译 ；2023-03-01*/
                }

            });
            m1.setData(dataDTOList1);
            resultSet.add(m1);
        });
        return resultSet;
    }


}
