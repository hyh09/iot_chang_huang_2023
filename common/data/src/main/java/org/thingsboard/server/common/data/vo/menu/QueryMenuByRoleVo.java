package org.thingsboard.server.common.data.vo.menu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @program: thingsboard
 * @description: 通过角色查询返回菜单实体类
 * @author: HU.YUNHUI
 * @create: 2021-11-04 13:46
 **/
@Data
@ToString
@ApiModel(value = "通过角色查询返回菜单实体类")
public class QueryMenuByRoleVo {

    @ApiModelProperty("是否绑定标识")
    private boolean checked;

    @ApiModelProperty("创建时间")
    private long  createdTime;

    @ApiModelProperty("URL")
    private String url;


    public Boolean isButton=false;



    private  String id;

    private  String parentId;

//    private String tenantMenuCode;

    private String name;

//    private String sysMenuName;
//
//    private String sysMenuCode;

    private String menuType;

    private String path;

    private String langKey;






}
