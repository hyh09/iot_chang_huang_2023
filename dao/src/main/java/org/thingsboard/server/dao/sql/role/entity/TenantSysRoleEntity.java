package org.thingsboard.server.dao.sql.role.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.anno.JpaOperatorsType;
import org.thingsboard.server.dao.util.sql.JpaQueryHelper;
import org.thingsboard.server.dao.util.sql.entity.TenantBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.UUID;

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

    /**
     * 被创建者的类型:
     */
    @Column(name = ModelConstants.USER_USER_TYPE)
    @JpaOperatorsType(JpaQueryHelper.Operators.eq)
    private  String type;

    /**
     * 工厂id
     */
    @Column(name = ModelConstants.USER_USER_FACTORY_ID)
    private UUID factoryId;


    /**
     *  0是默认值  正常逻辑
     *  1 是只允许修改密码 （不可以编辑修改， 删除)
     *
     *  默认是null ;---和0是一个意思
     *    如果普通用户就带上null
     */
    @Column(name = ModelConstants.USER_OPERATION_TYPE,columnDefinition=" integer DEFAULT 0;")//;
    private  Integer operationType;



    /**
     * 1是系统生成的  系统生成的不准删除
     * 0不是系统生成
     */
    @Column(name = "system_tab")
    @JpaOperatorsType(JpaQueryHelper.Operators.eq)
    private String  systemTab="0";

  //    @JpaOperatorsType(JpaQueryHelper.Operators.in)
    /**
     * 和用户中的userLevel 一样
     */
    @Column(name = ModelConstants.USER_USER_LEVEL,columnDefinition=" integer DEFAULT 0;")
    private  Integer userLevel;


    @Transient
    @JpaOperatorsType(value = JpaQueryHelper.Operators.in,columnName = "userLevel")
    private List<Integer> userLevelList;







}
