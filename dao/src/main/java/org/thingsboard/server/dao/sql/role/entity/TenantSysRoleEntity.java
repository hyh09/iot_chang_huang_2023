package org.thingsboard.server.dao.sql.role.entity;	
	
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;	
import org.hibernate.annotations.DynamicUpdate;	
import org.hibernate.annotations.GenericGenerator;	
import org.hibernate.annotations.CreationTimestamp;	
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;	
import org.thingsboard.server.dao.util.sql.entity.TenantBaseEntity;	
	
import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**	
  创建时间: 2021-10-22 18:05:08	
  创建人: HU.YUNHUI	
  对应的数据库表: TENANT_SYS_ROLE	
  描述: 【角色】 对应的实体
*/	
@Data	
@Entity	
@Table(name="TB_TENANT_SYS_ROLE")
@DynamicInsert	
@DynamicUpdate	
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
@ApiModel(value = "角色的实体类")
public class TenantSysRoleEntity  extends TenantBaseEntity {	
	
    /**	
     * 角色编码	
     */
    @ApiModelProperty(value = "角色编码")
    @Column(name="role_code")	
    private String roleCode;	
    /**	
     * 角色名称	
     */
    @ApiModelProperty(value = "角色名称")
    @Column(name="role_name")	
    private String roleName;	
    /**	
     * 角色描述	
     */
    @ApiModelProperty(value = "角色描述")
    @Column(name="role_desc")	
    private String roleDesc;






 }
