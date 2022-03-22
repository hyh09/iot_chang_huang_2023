package org.thingsboard.server.dao.sql.tskv.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.thingsboard.server.dao.util.sql.entity.AbstractStatisticalDataEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @program: thingsboard
 * @description: 统计能耗历史的数据表  (小时维度)
 * @author: HU.YUNHUI
 * @create: 2022-01-18 16:08
 **/
@Data
@Entity
@Table(name="hs_energy_hour")
@DynamicInsert
@DynamicUpdate
//@Proxy(lazy = false)
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class EnergyHistoryHourEntity extends AbstractStatisticalDataEntity {



}
