package org.thingsboard.server.dao.sql.role.service.rolemenu;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@ApiModel(value = "角色管理模块-菜单 出参")
public class OutMenuByUserVo  {

    private String id;

    private Long  time1;


    private String pid;

    private String name;

    private String code;

    private  String mark;

    private  String time;


   private String button;



   private String langkey;

   private String path;




}
