package org.thingsboard.server.entity.rolemenu;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.dao.util.sql.jpa.ct.VoBeanConverSvc;


@Data
@ToString
@ApiModel(value = "角色管理模块-菜单 出参")
public class OutMenuByUserVo implements VoBeanConverSvc {

    private  String id;

    @JsonProperty("parentId")
    private String pid;

    @JsonProperty("sysMenuName")
    private String name;

    @JsonProperty("flg")
    private  String mark;

    private  String time;

}
