package org.thingsboard.server.entity.rolemenu;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString
@ApiModel(value = "角色管理模块-菜单 出参")
public class OutMenuByUserVo {

    private  UUID id;

    private String parentId;

    private String sysMenuName;

    private  String mark;

}
