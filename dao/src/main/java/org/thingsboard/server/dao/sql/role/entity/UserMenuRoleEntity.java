package org.thingsboard.server.dao.sql.role.entity;

import lombok.Data;	
import org.hibernate.annotations.DynamicInsert;	
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.thingsboard.server.dao.util.sql.entity.TenantBaseEntity;

import javax.persistence.*;
import java.util.UUID;

/**	
  创建时间: 2021-10-25 17:39:26
  创建人: HU.YUNHUI	
  对应的数据库表: USER_MENU_ROLE
   描述: 【用户角色关系数据】 对应的实体
*/	
@Data	
@Entity	
@Table(name="TB_USER_MENU_ROLE")
@DynamicInsert	
@DynamicUpdate	
//@Proxy(lazy = false)
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class UserMenuRoleEntity  extends TenantBaseEntity {
	


    /**	
     *中文描述: 用户标识
     */	
    @Column(name="user_id")	
    private UUID userId;
    /**	
     *中文描述: 租户系统角色标识
     */	
    @Column(name="tenant_sys_role_id")	
    private UUID tenantSysRoleId;
    /**	
     *中文描述: 备注
     */	
    @Column(name="remark")	
    private String remark;	

 }	
