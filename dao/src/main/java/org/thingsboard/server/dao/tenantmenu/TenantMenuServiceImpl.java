package org.thingsboard.server.dao.tenantmenu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.dao.entity.AbstractEntityService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TenantMenuServiceImpl extends AbstractEntityService implements TenantMenuService {

    private static final String DEFAULT_TENANT_REGION = "Global";
    public static final String INCORRECT_MENU_ID = "Incorrect menuId ";
    public static final int ONE = 1;
    public static final String ZHCD = "ZHCD"; //系统菜单首字母
    public static final String PC = "PC"; //PC菜单
    public static final String APP = "APP"; //APP菜单

    private final TenantMenuDao tenantMenuDao;

    public TenantMenuServiceImpl(TenantMenuDao tenantMenuDao){
        this.tenantMenuDao = tenantMenuDao;
    }

    /**
     * 新增/修改租户菜单
     * @param tenantMenuList
     */
    @Override
    public void saveOrUpdTenantMenu(List<TenantMenu> tenantMenuList, UUID tenantId){
        //删除所有租户菜单
//        if(tenantId != null){
//            tenantMenuDao.deletedByTenant(tenantId);
//        }
        //保存租户菜单
        tenantMenuDao.saveOrUpdTenantMenu(tenantMenuList);
    }

    /**
     * 保存系统菜单
     * @param tenantMenuList
     * @return
     */
    @Override
    public List<TenantMenu> saveTenantMenuList(List<TenantMenu> tenantMenuList) {
        log.trace("Executing saveTenantMenuList [{}]", tenantMenuList);
        TenantId tenantId = new TenantId(tenantMenuList.get(0).getTenantId());
        tenantMenuList.forEach(tenantMenu->{
            tenantMenu.setRegion(DEFAULT_TENANT_REGION);
            //生成租户菜单编码
            tenantMenu.setTenantMenuCode(ZHCD + String.valueOf(System.currentTimeMillis()));
            //生成租户菜单排序序号
            Integer maxSort = tenantMenuDao.getMaxSortByParentId(tenantMenu.getParentId());
            tenantMenu.setSort(maxSort == null ? 0:maxSort);
            tenantMenuDao.save(tenantMenu);
        });
        //查询调整后的菜单列表
        List<TenantMenu> tenantMenus = tenantMenuDao.find(tenantId);
        return tenantMenus;
    }

    /**
     * 修改后刷新值
     * @param tenantMenu
     * @return
     */
    @Override
    public List<TenantMenu> updTenantMenu(TenantMenu tenantMenu){
        log.trace("Executing updTenantMenu [{}]", tenantMenu);
        TenantMenu newTenantMenu = tenantMenuDao.findById(new TenantId(tenantMenu.getTenantId()), tenantMenu.getId());
        newTenantMenu.updTenantMenu(tenantMenu);
        tenantMenuDao.save(newTenantMenu);
        //查询调整后的菜单列表
        List<TenantMenu> tenantMenus = tenantMenuDao.find( new TenantId(tenantMenu.getTenantId()));
        return tenantMenus;
    }

    /**
     * 调整排序
     * @param id
     * @param frontId
     * @return
     */
    @Override
    public List<TenantMenu> updTenantMenuSort(String id,String frontId){
        log.trace("Executing updTenantMenuSort [{}]",id, frontId);
        TenantMenu byId = tenantMenuDao.findById(null, UUID.fromString(id));
        TenantMenu byFrondId = tenantMenuDao.findById(null, UUID.fromString(frontId));
        if(byFrondId != null ){
            List<TenantMenu> rearList = tenantMenuDao.findRearList(byFrondId.getSort(), byFrondId.getParentId());
            if(!CollectionUtils.isEmpty(rearList)){
                for (int i = 0; i<rearList.size() ;i++){
                    if( i == ONE){
                        byId.setSort(byFrondId.getSort() + ONE);
                        tenantMenuDao.save(byId);
                        if(rearList.get(ONE).getSort() - byFrondId.getSort() > ONE){
                            break;
                        }else {
                           continue;
                        }
                    }else {
                        rearList.get(i).setSort(rearList.get(i).getSort() + ONE);
                        tenantMenuDao.save(rearList.get(i));
                        if(rearList.get(i).getSort() - rearList.get(i-ONE).getSort() > ONE){
                            break;
                        }
                    }
                }
            }
        }
        //查询调整后的菜单列表
        List<TenantMenu> tenantMenus = tenantMenuDao.find( new TenantId(byId.getTenantId()));
        return tenantMenus;
    }

    /**
     * 删除后刷新值
     * @param id
     * @return
     */
    @Override
    public List<TenantMenu> delTenantMenu(String id,String tenantId){
        log.trace("Executing delTenantMenu [{}]",id, tenantId);
        tenantMenuDao.removeById(new TenantId(UUID.fromString(tenantId)), UUID.fromString(id));
        //查询调整后的菜单列表
        List<TenantMenu> tenantMenus = tenantMenuDao.find(new TenantId(UUID.fromString(tenantId)));
        return tenantMenus;
    }

    /**
     * 修改系统菜单
     * @param tenantMenu
     * @return
     */
    @Override
    public TenantMenu updateTenantMenu(TenantMenu tenantMenu) {
        log.trace("Executing updateTenantMenu [{}]", tenantMenu);
        tenantMenu.setRegion(DEFAULT_TENANT_REGION);
        TenantMenu savedTenantMenut = tenantMenuDao.save(null, tenantMenu);
        return savedTenantMenut;
    }

    /**
     * 查询租户菜单
     * @param tenantMenu
     * @return
     */
    @Override
    public List<TenantMenu> getTenantMenuList(TenantMenu tenantMenu){
        log.trace("Executing getTenantMenuList [{}]", tenantMenu);
        return tenantMenuDao.getTenantMenuList(tenantMenu);
    }

    @Override
    public TenantMenu findById(UUID tenantMenuId){
        log.trace("Executing findById [{}]", tenantMenuId);
        return tenantMenuDao.findById(null,tenantMenuId);
    }

    @Override
    public List<TenantMenu> findByIdIn(List<UUID> ids) {
        log.trace(" --tenantMenuDao.findByIdIn 的入参{}",ids);
        return tenantMenuDao.findByIdIn(ids);
    }
    /**
     * 获取租户下的菜单
     * @param menuType
     * @param tenantId
     * @return
     */
    @Override
    public List<TenantMenu> getTenantMenuListByTenantId(String menuType, UUID tenantId) {
        return tenantMenuDao.getTenantMenuListByTenantId(menuType,tenantId);
    }

    @Override
    public List<TenantMenu> getTenantMenuListByIds(String menuType, UUID tenantId, List<UUID> ids) {
        return tenantMenuDao.getTenantMenuListByIds(menuType,tenantId,ids);
    }


}
