package org.thingsboard.server.dao.sql.role.entity;	
	
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

/**	
  创建时间: 2021-10-26 18:16:59	
  创建人: HU.YUNHUI	
  对应的数据库表: TENANT_MENU_ROLE	
   描述: 【角色-菜单 关系数据】 对应的实体	
*/	
@Data	
@Entity	
@Table(name="TB_TENANT_MENU_ROLE")
@DynamicInsert	
@DynamicUpdate	
//@Proxy(lazy = false)	
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})	
public class TenantMenuRoleEntity  extends TenantBaseEntity {	
	
	
	
    /**	
     *中文描述: 租户菜单标识	
     */	
    @Column(name="tenant_menu_id")	
    private UUID tenantMenuId;
    /**	
     *中文描述: 租户系统角色标识【角色表主键】	
     */	
    @Column(name="tenant_sys_role_id")
    private UUID tenantSysRoleId;
    /**	
     *中文描述: 备注	
     */	
    @Column(name="remark")	
    private String remark;	

 }	
