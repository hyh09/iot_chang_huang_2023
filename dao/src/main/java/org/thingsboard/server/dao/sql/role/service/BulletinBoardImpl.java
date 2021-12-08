package org.thingsboard.server.dao.sql.role.service;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.vo.enums.EfficiencyEnums;
import org.thingsboard.server.common.data.vo.tskv.MaxTsVo;
import org.thingsboard.server.dao.hs.service.DeviceDictPropertiesSvc;
import org.thingsboard.server.dao.sql.role.dao.EffectMaxValueKvRepository;

import java.util.List;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 看板的相关接口
 * @author: HU.YUNHUI
 * @create: 2021-12-07 10:52
 **/
@Data
@Service
public class BulletinBoardImpl implements BulletinBoardSvc {


    @Autowired private EffectMaxValueKvRepository effectMaxValueKvRepository;
    @Autowired private DeviceDictPropertiesSvc deviceDictPropertiesSvc;



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
}
