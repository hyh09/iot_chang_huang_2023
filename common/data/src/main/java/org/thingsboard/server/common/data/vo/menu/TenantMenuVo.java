package org.thingsboard.server.common.data.vo.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: TenantMenuVo
 * @author: HU.YUNHUI
 * @create: 2021-11-05 09:16
 **/
@Data
@ToString
public class TenantMenuVo {

    private UUID id;

    private UUID tenantId;
    private UUID sysMenuId;
    private String sysMenuCode;
    private String sysMenuName;
    private String tenantMenuName;
    private String tenantMenuCode;
    private Integer level;
    private Integer sort;
    private String url;
    private UUID parentId;
    private String tenantMenuIcon;
    private String tenantMenuImages;
    private String menuType;
    //"是按钮（true/false）")
    public Boolean isButton;
    private UUID createdUser;
    private long createdTime;
    private long updatedTime;
    private UUID updatedUser;
    private String region;
    //多语言Key
    private String langKey;

    //额外加的逻辑字段配合前端
    private  String name;

    private  Boolean checked=false;


}
