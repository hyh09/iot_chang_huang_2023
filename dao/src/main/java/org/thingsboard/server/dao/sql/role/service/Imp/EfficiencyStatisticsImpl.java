package org.thingsboard.server.dao.sql.role.service.Imp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.ResultCapAppVo;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sql.ProductionLineEntity;
import org.thingsboard.server.dao.model.sql.WorkshopEntity;
import org.thingsboard.server.dao.productionline.ProductionLineDao;
import org.thingsboard.server.dao.sql.factory.FactoryRepository;
import org.thingsboard.server.dao.sql.role.dao.EffectTsKvRepository;
import org.thingsboard.server.dao.sql.role.entity.EffectTsKvEntity;
import org.thingsboard.server.dao.sql.role.service.EfficiencyStatisticsSvc;
import org.thingsboard.server.dao.util.StringUtilToll;
import org.thingsboard.server.dao.workshop.WorkshopDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @program: thingsboard
 * @description: 效能分析接口业务层
 * @author: HU.YUNHUI
 * @create: 2021-11-09 11:16
 **/
@Service
@Slf4j
public class EfficiencyStatisticsImpl implements EfficiencyStatisticsSvc {

    @Autowired  private EffectTsKvRepository effectTsKvRepository;
    @Autowired  private DeviceDao deviceDao;
    @Autowired  private FactoryRepository factoryRepository;
    @Autowired  private WorkshopDao workshopDao;
    @Autowired  private ProductionLineDao productionLineDao;


    /**
     * app的产能接口
     * @return
     */
    @Override
    public ResultCapAppVo queryCapApp(QueryTsKvVo vo, TenantId tenantId) {
        ResultCapAppVo  resultCapAppVo = new ResultCapAppVo();
        log.info("app的产能分析接口入参:{}",vo);
        List<EffectTsKvEntity> effectTsKvEntities = effectTsKvRepository.queryEntity(vo);
        if(CollectionUtils.isEmpty(effectTsKvEntities))
        {
            return   new ResultCapAppVo();
        }
        List<EffectTsKvEntity>  pageList =  effectTsKvEntities.stream().skip((vo.getPage())*vo.getPageSize()).limit(vo.getPageSize()).
                collect(Collectors.toList());
        log.info("当前的分页之后的数据:{}",pageList);
        List<UUID> ids = pageList.stream().map(EffectTsKvEntity::getEntityId).collect(Collectors.toList());
        log.info("当前的分页之后的数据之设备id的汇总:{}",ids);
        List<DeviceEntity>  entities =  deviceDao.queryAllByIds(ids);
        log.info("查询到的设备信息:{}",entities);
        entities.stream().forEach(e->{
             System.out.println("====>"+e.getId()+"===>"+e.getName());
        });
        Map<UUID,DeviceEntity> map1 = entities.stream().collect(Collectors.toMap(DeviceEntity::getId,DeviceEntity->DeviceEntity));
        log.info("查询到的设备信息map1:{}",map1);
        List<AppDeviceCapVo> appDeviceCapVoList = new ArrayList<>();
        pageList.stream().forEach(entity->{
            AppDeviceCapVo  capVo = new AppDeviceCapVo();
            DeviceEntity  entity1 = map1.get(entity.getEntityId());
            //会存在为空的
            capVo.setValue(getValueByEntity(entity));
            capVo.setDeviceId(entity.getEntityId().toString());
            capVo.setDeviceName(entity1.getName());

            if(entity1.getWorkshopId() != null) {
                Workshop workshop = workshopDao.findById(tenantId, entity1.getWorkshopId());
                capVo.setWorkshopName(workshop.getName());
            }

            if(entity1.getProductionLineId() != null) {
                ProductionLine productionLine = productionLineDao.findById(tenantId, entity1.getProductionLineId());
                capVo.setProductionName(productionLine.getName());
            }
            appDeviceCapVoList.add(capVo);

        });
        resultCapAppVo.setTotalValue(getTotalValue(effectTsKvEntities));
        resultCapAppVo.setAppDeviceCapVoList(appDeviceCapVoList);
        return resultCapAppVo;
    }








    private  String  getValueByEntity(EffectTsKvEntity entity)
    {
        if(entity.getSubtractDouble()>0)
        {
            return  entity.getSubtractDouble().toString();
        }
        if(entity.getSubtractLong()>0)
        {
            return  entity.getSubtractLong().toString();

        }
        return "0";
    }


    private  String getTotalValue(List<EffectTsKvEntity> effectTsKvEntities)
    {

        Double  totalSku =
                effectTsKvEntities.stream().mapToDouble(EffectTsKvEntity::getSubtractDouble).sum();

        Long  totalSku2 =
                effectTsKvEntities.stream().mapToLong(EffectTsKvEntity::getSubtractLong).sum();
        double dvalue =  StringUtilToll.add(totalSku.toString(),totalSku2.toString());
        return dvalue+"";
    }

}
