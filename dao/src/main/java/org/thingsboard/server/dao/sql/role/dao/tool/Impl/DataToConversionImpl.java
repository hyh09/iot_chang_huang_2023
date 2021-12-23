package org.thingsboard.server.dao.sql.role.dao.tool.Impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;
import org.thingsboard.server.dao.model.sql.ProductionLineEntity;
import org.thingsboard.server.dao.model.sql.WorkshopEntity;
import org.thingsboard.server.dao.sql.productionline.ProductionLineRepository;
import org.thingsboard.server.dao.sql.role.dao.tool.DataToConversionSvc;
import org.thingsboard.server.dao.sql.role.entity.EnergyEffciencyNewEntity;
import org.thingsboard.server.dao.sql.workshop.WorkshopRepository;
import org.thingsboard.server.dao.util.StringUtilToll;

import java.math.BigDecimal;
import java.util.*;

/**
 * @program: thingsboard
 * @description: 效能分析的对象转换接口
 * @author: HU.YUNHUI
 * @create: 2021-12-22 15:03
 **/
@Service
@Slf4j
public class DataToConversionImpl implements DataToConversionSvc {

    @Autowired
    private WorkshopRepository workshopRepository;
    @Autowired private ProductionLineRepository productionLineRepository;



    /**
     * pc端的产能列表的数据返回的处理
     * @param entityList
     * @return
     */
    @Override
    public List<AppDeviceCapVo> resultProcessingByCapacityPc(List<EnergyEffciencyNewEntity> entityList, TenantId tenantId) {
        List<AppDeviceCapVo> appDeviceCapVoList = new ArrayList<>();

        entityList.stream().forEach(entity->{
            AppDeviceCapVo  capVo = new AppDeviceCapVo();
            capVo.setValue(StringUtils.isEmpty(entity.getCapacityAddedValue())?"0":entity.getCapacityAddedValue());
            capVo.setDeviceId(entity.getEntityId().toString());
            capVo.setDeviceName(entity.getDeviceName());
            capVo.setFlg(entity.getFlg());
            if(entity.getWorkshopId() != null) {
                Optional<WorkshopEntity> workshop = workshopRepository.findByTenantIdAndId(tenantId.getId(), entity.getWorkshopId());
                capVo.setWorkshopName(workshop.isPresent()?workshop.get().getName():"");
            }

            if(entity.getProductionLineId() != null) {
                Optional<ProductionLineEntity> productionLine = productionLineRepository.findByTenantIdAndId(tenantId.getId(), entity.getProductionLineId());
                capVo.setProductionName(productionLine.isPresent()?productionLine.get().getName():"");
            }
            appDeviceCapVoList.add(capVo);

        });

        return  appDeviceCapVoList;
    }

    /**
     * 计算总产能的
     * @param effectTsKvEntities
     * @return
     */
    @Override
    public String getTotalValue(List<EnergyEffciencyNewEntity> effectTsKvEntities) {
        if(CollectionUtils.isEmpty(effectTsKvEntities))
        {
            return "0";
        }
        BigDecimal invoiceAmount = effectTsKvEntities.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getCapacityAddedValue()))
                .map(EnergyEffciencyNewEntity::getCapacityAddedValue).map(BigDecimal::new).reduce(BigDecimal.ZERO,
                BigDecimal::add);
        return   StringUtilToll.roundUp(invoiceAmount.stripTrailingZeros().toPlainString());
    }

    /**
     *
     * @param pageList  具体的数据
     * @param tenantId  租户id
     * @param mapNameToVo  描述 和 单位 等
     * @return
     */
    @Override
    public List<Map> resultProcessingByEnergyPc(List<EnergyEffciencyNewEntity> pageList, TenantId tenantId, Map<String, DictDeviceGroupPropertyVO> mapNameToVo) {
       return  null;
    }
}
