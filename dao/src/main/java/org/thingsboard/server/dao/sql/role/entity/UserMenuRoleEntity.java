package org.thingsboard.server.dao.sql.role.entity;
	

import lombok.Data;	
import org.hibernate.annotations.DynamicInsert;	
import org.hibernate.annotations.DynamicUpdate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Type;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.sql.entity.BaseEntity;

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
@Table(name="USER_MENU_ROLE")	
@DynamicInsert	
@DynamicUpdate	
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class UserMenuRoleEntity  extends BaseEntity implements Serializable {
	
  	private static final long serialVersionUID = 1L;	

//
//    @Id
//    @Column(name = ModelConstants.ID_PROPERTY, columnDefinition = "uuid")
////    @Type(type = "org.hibernate.type.PostgresUUIDType")
//    private UUID id;
    /**	
     * 用户标识	
     */	
    @Column(name="user_id")	
    private String userId;	
    /**	
     * 租户系统角色标识	
     */	
    @Column(name="tenant_sys_role_id")	
    private String tenantSysRoleId;	
    /**	
     * 备注	
     */	
    @Column(name="remark")	
    private String remark;	
//    /**
//     * 创建时间
//     */
//    @Column(name="created_time")
//    private String createdTime;
    /**	
     * 创建人标识	
     */	
    @Column(name="created_user")	
    private String createdUser;	
    /**	
     * 修改时间	
     */	
    @Column(name="updated_time")	
    private String updatedTime;	
    /**	
     * 修改人标识	
     */	
    @Column(name="updated_user")	
    private String updatedUser;



 }	
