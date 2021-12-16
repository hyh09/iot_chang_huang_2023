package org.thingsboard.server.dao.sql.energyTime.entity;	
	
import lombok.Data;	
import org.hibernate.annotations.DynamicInsert;	
import org.hibernate.annotations.DynamicUpdate;	
import org.hibernate.annotations.GenericGenerator;	
import org.hibernate.annotations.CreationTimestamp;	
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;	
import org.thingsboard.server.dao.util.sql.entity.TenantBaseEntity;	
	
import javax.persistence.*;	
import java.util.Date;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.*;

/**	
  创建时间: 2021-12-16 11:17:13	
  创建人: HU.YUNHUI	
  对应的数据库表: ENERY_TIME_GAP	
   描述: 【能耗超过30分钟的时间的遥测时间差保存】 对应的实体	
*/	
@Data	
@Entity	
@Table(name="TB_ENERY_TIME_GAP")
@DynamicInsert	
@DynamicUpdate	
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class EneryTimeGapEntity extends  TenantBaseEntity{


    /**	
     *中文描述: 设备id	
     */

    @Column(name = ENTITY_ID_COLUMN, columnDefinition = "uuid")
    private UUID entityId;
    /**	
     *中文描述: 遥测的时间ts	
     */
    @Column(name = KEY_COLUMN)
    private Long ts;
    /**	
     *中文描述: 遥测上传的key	
     */
    @Column(name = TS_COLUMN)
    private String key;
    /**	
     *中文描述: 遥测的时间差	
     */	
    @Column(name="time_gap")	
    private Long timeGap;

 }	
