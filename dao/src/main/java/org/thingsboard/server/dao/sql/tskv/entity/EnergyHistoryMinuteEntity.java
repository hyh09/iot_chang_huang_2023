package org.thingsboard.server.dao.sql.tskv.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.thingsboard.server.common.data.vo.resultvo.cap.CapacityHistoryVo;
import org.thingsboard.server.dao.util.sql.entity.AbstractStatisticalDataEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: thingsboard
 * @description: 统计能耗历史的数据表  (分钟维度)
 * @author: HU.YUNHUI
 * @create: 2022-01-18 16:08
 **/
@Data
@Entity
@Table(name="hs_energy_minute")
@DynamicInsert
@DynamicUpdate
//@Proxy(lazy = false)
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class EnergyHistoryMinuteEntity extends AbstractStatisticalDataEntity {






    public static List<CapacityHistoryVo> toCapacityHistoryVo(List<EnergyHistoryMinuteEntity> entityList,String deviceName)
    {
        List<CapacityHistoryVo>  capacityHistoryVoList  = new ArrayList<>();
        if(CollectionUtils.isEmpty(entityList))
         {
             return  capacityHistoryVoList;
         }
      return   entityList.stream().map(m->{
            CapacityHistoryVo  vo = new CapacityHistoryVo();
            vo.setDeviceName(deviceName);
            vo.setDeviceId(m.getEntityId().toString());
            vo.setValue(StringUtils.isNotEmpty(m.getCapacityValue())?m.getCapacityValue():"0");
            vo.setCreatedTime(m.getTs());
            return  vo;
        }).collect(Collectors.toList());

    }
}
