package org.thingsboard.server.dao.sql.factoryUrl.entity;	
	
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.thingsboard.server.dao.util.sql.entity.TenantBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
	
/**	
  创建时间: 2022-01-20 12:28:03	
  创建人: HU.YUNHUI	
  对应的数据库表: FACTORY_U_R_L_APP_TABLE	
   描述: 【当天的产能能耗的增量数据和当天历史数据】 对应的实体	
*/	
@Data	
@Entity	
@Table(name="FACTORY_U_R_L_APP_TABLE")	
@DynamicInsert	
@DynamicUpdate	
//@Proxy(lazy = false)	
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})	
public class FactoryURLAppTableEntity  extends TenantBaseEntity {	
	
	
	
    /**	
     *中文描述: 工厂id	
     */	
    @Column(name="FACTORY_ID")	
    private String factoryId;	
    /**	
     *中文描述: app的请求url配置	
     */	
    @Column(name="APP_URL")	
    private String appUrl;	
    /**	
     *中文描述: 备注说明	
     */	
    @Column(name="NOTES")	
    private String notes;	

 }	
