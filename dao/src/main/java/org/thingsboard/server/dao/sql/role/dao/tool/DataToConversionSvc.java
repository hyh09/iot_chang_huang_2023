package org.thingsboard.server.dao.sql.role.dao.tool;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;
import org.thingsboard.server.dao.sql.role.entity.EnergyEffciencyNewEntity;

import java.util.List;

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
     * 计算总产能的接口
     * @param effectTsKvEntities
     * @return
     */
    String getTotalValue(List<EnergyEffciencyNewEntity> effectTsKvEntities);

}
