package org.thingsboard.server.dao.sql.role.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.enums.EfficiencyEnums;
import org.thingsboard.server.common.data.vo.enums.KeyTitleEnums;
import org.thingsboard.server.common.data.vo.tskv.ConsumptionTodayVo;
import org.thingsboard.server.common.data.vo.tskv.MaxTsVo;
import org.thingsboard.server.common.data.vo.tskv.consumption.TkTodayVo;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;
import org.thingsboard.server.dao.hs.service.DeviceDictPropertiesSvc;
import org.thingsboard.server.dao.sql.role.dao.EffectMaxValueKvRepository;
import org.thingsboard.server.dao.sql.role.dao.EffectTsKvRepository;
import org.thingsboard.server.dao.sql.role.entity.EffectTsKvEntity;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: thingsboard
 * @description: 看板的相关接口
 * @author: HU.YUNHUI
 * @create: 2021-12-07 10:52
 **/
@Slf4j
@Data
@Service
public class BulletinBoardImpl implements BulletinBoardSvc {


    @Autowired private EffectMaxValueKvRepository effectMaxValueKvRepository;
    @Autowired private DeviceDictPropertiesSvc deviceDictPropertiesSvc;
    @Autowired private EffectTsKvRepository effectTsKvRepository;




    @Override
    public ConsumptionTodayVo energyConsumptionToday(QueryTsKvVo vo, UUID tenantId) {
        List<String>  keys1 = new ArrayList<>();
        keys1=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.ENERGY_002.getgName());
        vo.setKeys(keys1);
        Map<String, DictDeviceGroupPropertyVO>  mapNameToVo  = deviceDictPropertiesSvc.getMapPropertyVo();
          List<EffectTsKvEntity>  effectTsKvEntities =  effectTsKvRepository.queryEntityByKeys(vo,vo.getKeys());
        log.info("查询到的数据{}",effectTsKvEntities);
        Map<UUID,List<EffectTsKvEntity>> map = effectTsKvEntities.stream().collect(Collectors.groupingBy(EffectTsKvEntity::getEntityId));
        return   getEntityKeyValue(map,tenantId,mapNameToVo);
    }

    /**
     * 历史的产能的总和
     * @param factoryId
     * @return
     * @throws ThingsboardException
     */
    public   String getHistoryCapValue(String factoryId,UUID tenantId)   {
        MaxTsVo  vo = new MaxTsVo();
        List<String> nameKey=  deviceDictPropertiesSvc.findAllByName(null, EfficiencyEnums.CAPACITY_001.getgName());
        String keyName=  nameKey.get(0);
        vo.setKey(keyName);
        if(StringUtils.isNotBlank(factoryId))
        {
            vo.setFactoryId(UUID.fromString(factoryId));//工厂维度
        }
        vo.setTenantId(tenantId);
        vo.setCapSign(true);
        return this.historySumByKey(vo);
    }

    /**
     * 查询历史key维度的 设备总和
     *
     * @return
     */
    @Override
    public String historySumByKey(MaxTsVo maxTsVo) {
        return  effectMaxValueKvRepository.querySum(maxTsVo);

    }



    /**
     * @param listMap
     * @return
     */
    public  ConsumptionTodayVo getEntityKeyValue(Map<UUID,List<EffectTsKvEntity>> listMap, UUID tenantId, Map<String, DictDeviceGroupPropertyVO>  mapNameToVo)
    {
        ConsumptionTodayVo appVo = new  ConsumptionTodayVo();
        List<TkTodayVo> waterList = new ArrayList<>();
        List<TkTodayVo> electricList = new ArrayList<>();
        List<TkTodayVo> gasList = new ArrayList<>();

        listMap.forEach((entityId,value)->{
            TkTodayVo  tkTodayVo  =  new TkTodayVo();

            tkTodayVo.setDeviceId(entityId.toString());
            EffectTsKvEntity  entity1 =value.get(0);
            if(entity1 != null) {
                tkTodayVo.setDeviceName(entity1.getDeviceName());
                tkTodayVo.setTotalValue(entity1.getLocalValue());
                value.stream().forEach(effectTsKvEntity -> {
                    DictDeviceGroupPropertyVO dictVO=  mapNameToVo.get(effectTsKvEntity.getKeyName());
                    if(dictVO.getTitle().equals(KeyTitleEnums.key_cable.getgName()))
                    {
                        tkTodayVo.setValue(effectTsKvEntity.getValueLast2());
                        electricList.add(tkTodayVo);
                    }
                    if(dictVO.getTitle().equals(KeyTitleEnums.key_gas.getgName()))
                    {
                        tkTodayVo.setValue(effectTsKvEntity.getValueLast2());
                        gasList.add(tkTodayVo);
                    }
                    if(dictVO.getTitle().equals(KeyTitleEnums.key_water.getgName()))
                    {
                        tkTodayVo.setValue(effectTsKvEntity.getValueLast2());
                        waterList.add(tkTodayVo);
                    }
                });

            }

        });
        appVo.setElectricList(compareToMaxToMin(electricList));
        appVo.setWaterList(compareToMaxToMin(waterList));
        appVo.setGasList(compareToMaxToMin(gasList));
        return  appVo;
    }


    /**大到小*/
    public static List<TkTodayVo> compareToMaxToMin(List<TkTodayVo> list){
        return list.stream().sorted((s1, s2) -> new BigDecimal(s2.getTotalValue()).compareTo(new BigDecimal(s1.getTotalValue()))).collect(Collectors.toList());
    }


}
