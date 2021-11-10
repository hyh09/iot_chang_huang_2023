package org.thingsboard.server.entity.tenantmenu.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@ApiModel(description = "修改或保存",value = "SaveTenantMenuDto")
public class SaveTenantMenuDto {
    @ApiModelProperty(value = "租户id",required = true)
    private UUID tenantId;
    @ApiModelProperty("PC菜单")
    List<AddTenantMenuDto> pcList;
    @ApiModelProperty("APP菜单")
    List<AddTenantMenuDto> appList;

    public List<TenantMenu> toTenantMenuList(List<AddTenantMenuDto> dtoList){
        List<TenantMenu> menuList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(dtoList)){
            dtoList.forEach(i->{
                menuList.add(i.toTenantMenu());
            });
        }
        return menuList;
    }

    /**
     * 租户菜单新增/修改时把递归参数转为平级排列
     */
    //递归最终结果
    List<TenantMenu> paramTenanetMenuList = new ArrayList<>();
    public List<TenantMenu> toTenantMenuListBySave(List<AddTenantMenuDto> pcList,List<AddTenantMenuDto> appList,UUID loginUserId,UUID parentId,UUID tenantId){
        if(CollectionUtils.isNotEmpty(pcList)){
            this.common(pcList,"PC",loginUserId,parentId,tenantId);
        }
        if(CollectionUtils.isNotEmpty(appList)){
            this.common(appList,"APP",loginUserId,parentId,tenantId);
        }
        return paramTenanetMenuList;
    }
    private void common(List<AddTenantMenuDto> list,String menuType,UUID loginUserId,UUID parentId,UUID tenantId){
        if(!CollectionUtils.isEmpty(list)){
            for (int i=0 ;i < list.size();i++){
                AddTenantMenuDto addTenantMenuDto = list.get(i);
                TenantMenu tenantMenu = addTenantMenuDto.toTenantMenu(loginUserId,tenantId);
                tenantMenu.setSort(i+1);
                tenantMenu.setMenuType(menuType);
                tenantMenu.setParentId(parentId);
                paramTenanetMenuList.add(tenantMenu);
                if(CollectionUtils.isEmpty(addTenantMenuDto.getChildren()) || addTenantMenuDto.getChildren().size() == 0){
                    continue;
                }else {
                    tenantMenu.setHasChildren(true);
                    if(menuType.equals("APP")){
                        this.toTenantMenuListBySave(null,addTenantMenuDto.getChildren(),loginUserId,tenantMenu.getId(),tenantId);
                    }else {
                        this.toTenantMenuListBySave(addTenantMenuDto.getChildren(),null, loginUserId,tenantMenu.getId(),tenantId);
                    }
                }
            }
        }
    }


}
