package org.thingsboard.server.dao.sql.role.dao.tool;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.enums.KeyTitleEnums;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.AppDeviceEnergyVo;
import org.thingsboard.server.common.data.vo.tskv.ConsumptionTodayVo;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;
import org.thingsboard.server.dao.sql.role.entity.EnergyEffciencyNewEntity;
import org.thingsboard.server.dao.sql.tskv.entity.EnergyHistoryMinuteEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 效能分析的对象转换接口
 * @author: HU.YUNHUI
 * @create: 2021-12-22 15:02
 **/

public interface DataToConversionSvc {

    /**
     * 数据的处理  pc端的 产能返回
     * @param entityList
     * @return
     */
    public  List<AppDeviceCapVo>  resultProcessingByCapacityPc (List<EnergyEffciencyNewEntity> entityList, TenantId tenantId);

    /**
     * 数据的处理： 看板的数据返回处理 今日排行
     */
    ConsumptionTodayVo   resultProcessByEntityList(List<EnergyEffciencyNewEntity> entityList, TenantId tenantId);

    ConsumptionTodayVo   todayUntiEnergyByEntityList(List<EnergyEffciencyNewEntity> entityList, TenantId tenantId, QueryTsKvVo vo);


    /**
     * 计算总产能的接口
     * @param effectTsKvEntities
     * @return
     */
    String getTotalValue(List<EnergyEffciencyNewEntity> effectTsKvEntities);

    /**
     * 图片的处理 设备的图片
     * @param appDeviceCapVoList
     * @param tenantId
     * @return
     */
    List<AppDeviceCapVo>  fillDevicePicture(List<AppDeviceCapVo> appDeviceCapVoList,TenantId tenantId);


    List<AppDeviceEnergyVo>   resultProcessingByEnergyApp(List<EnergyEffciencyNewEntity> pageList, Map<String, DictDeviceGroupPropertyVO> mapNameToVo,TenantId tenantId);


    /**
     * 查询历史能耗的数据
        数据返回的处理
     * @param energyHistoryMinuteEntities
     * @param name 设备名称
     * @return
     */
    List<Map> resultProcessByEnergyHistoryMinuteEntity(List<EnergyHistoryMinuteEntity> energyHistoryMinuteEntities,String name);


    /**
     * 获取标准能耗的数据
     * @return
     */
    public String queryStandardEnergyValue(UUID dictDeviceId, KeyTitleEnums enums);

}
