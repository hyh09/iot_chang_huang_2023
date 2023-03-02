package org.thingsboard.server.entity.role;

import com.fasterxml.jackson.annotation.JsonFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.common.data.page.PageLink;

import javax.persistence.Column;

@Data
@ToString
@ApiModel("角色查询实体类")
public class PageRoleVo   {

    private int pageSize;

    private  int page;

    private  String textSearch;

    private  String  sortProperty;

    private  String sortOrder;



    /**
     * 角色编码
     */
    @ApiModelProperty(value = "角色编码")
    private String roleCode;
    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    /**
     * 角色描述
     */
    @ApiModelProperty(value = "角色描述")
    private String roleDesc;



}
