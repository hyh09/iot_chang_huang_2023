package org.thingsboard.server.dao.sql.role.entity;
	

import lombok.Data;	
import org.hibernate.annotations.DynamicInsert;	
import org.hibernate.annotations.DynamicUpdate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.thingsboard.server.dao.util.sql.entity.TenantBaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**	
  创建时间: 2021-10-21 11:25:01	
  创建人: HU.YUNHUI	
  对应的数据库表: USER_MENU_ROLE 	
*/	
@Data	
@Entity	
@Table(name="TB_USER_MENU_ROLE")
@DynamicInsert	
@DynamicUpdate	
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class UserMenuRoleEntity  extends TenantBaseEntity implements Serializable {
	
  	private static final long serialVersionUID = 1L;	

    /**	
     * 用户标识	
     */	
    @Column(name="user_id")	
    private UUID userId;
    /**	
     * 租户系统角色标识	
     */	
    @Column(name="tenant_sys_role_id")	
    private UUID tenantSysRoleId;
    /**	
     * 备注	
     */	
    @Column(name="remark")	
    private String remark;	




 }	
