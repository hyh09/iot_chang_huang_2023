package org.thingsboard.server.entity.tenantmenu.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@ApiModel(description = "修改或保存",value = "SaveTenantMenuDto")
public class SaveTenantMenuDto {
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
    public List<TenantMenu> toTenantMenuListBySave(UUID loginUserId,UUID parentId){
        this.common(this.pcList,loginUserId,parentId);
        this.common(this.appList,loginUserId,parentId);
        return paramTenanetMenuList;
    }
    private void common(List<AddTenantMenuDto> list,UUID loginUserId,UUID parentId){
        if(!CollectionUtils.isEmpty(list)){
            for (int i=0 ;i < list.size();i++){
                AddTenantMenuDto addTenantMenuDto = list.get(i);
                TenantMenu tenantMenu = addTenantMenuDto.toTenantMenu(loginUserId);
                tenantMenu.setSort(i+1);
                tenantMenu.setMenuType("PC");
                tenantMenu.setParentId(parentId);
                paramTenanetMenuList.add(tenantMenu);
                if(CollectionUtils.isEmpty(addTenantMenuDto.getChildren()) || addTenantMenuDto.getChildren().size() == 0){
                    continue;
                }else {
                    this.toTenantMenuListBySave(loginUserId,tenantMenu.getId().getId());
                }
            }
        }
    }


}
