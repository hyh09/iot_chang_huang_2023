package org.thingsboard.server.dao.menu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.common.data.memu.MenuInfo;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.service.Validator;
import org.thingsboard.server.dao.tenantmenu.TenantMenuDao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.thingsboard.server.dao.service.Validator.validateId;

@Service
@Slf4j
public class MenuServiceImpl extends AbstractEntityService implements MenuService {

    private static final String DEFAULT_TENANT_REGION = "Global";
    public static final String INCORRECT_MENU_ID = "Incorrect menuId ";
    public static final int ONE = 1;

    private final MenuDao menuDao;
    private final TenantMenuDao tenantMenuDao;

    public MenuServiceImpl(MenuDao menuDao,TenantMenuDao tenantMenuDao){
        this.menuDao = menuDao;
        this.tenantMenuDao = tenantMenuDao;
    }


    /**
     * 保存系统菜单
     * @param menu
     * @return
     */
    @Override
    public Menu saveMenu(Menu menu) throws ThingsboardException {
        log.trace("Executing saveMenu [{}]", menu);
        menu.setRegion(DEFAULT_TENANT_REGION);
        //生成租户菜单编码
        menu.setCode(String.valueOf(System.currentTimeMillis()));
        //生成租户菜单排序序号
        Integer maxSort = menuDao.getMaxSortByParentId(menu.getParentId());
        menu.setSort(maxSort == null ? ONE:maxSort);
        Menu savedMenut = menuDao.saveMenu(null, menu);
        return savedMenut;
    }

    /**
     * 修改系统菜单
     * @param menu
     * @return
     */
    @Override
    public Menu updateMenu(Menu menu)  throws ThingsboardException{
        log.trace("Executing updateMenu [{}]", menu);
        return menuDao.saveMenu(null, menu);
    }

    public void delMenu(UUID id){
        menuDao.delMenu(id);
    }

    /**
     * 调整排序
     * @param id
     * @param frontId
     * @return
     */
    @Override
    public Menu updMenuSort(String id,String frontId){
        log.trace("Executing updMenuSort [{}]",id, frontId);

        Menu menuVo = new Menu();
        Menu byId = menuDao.findById(null, UUID.fromString(id));
        Menu byFrondId = menuDao.findById(null, UUID.fromString(frontId));
        if(byFrondId != null ){
            List<Menu> rearList = menuDao.findRearList(byFrondId.getSort(), byFrondId.getParentId());
            if(!CollectionUtils.isEmpty(rearList)){
                for (int i = 0; i<rearList.size() ;i++){
                    if( i == ONE){
                        byId.setSort(byFrondId.getSort() + ONE);
                        menuVo = menuDao.save(null, byId);
                        if(rearList.get(ONE).getSort() - byFrondId.getSort() > ONE){
                            break;
                        }else {
                            continue;
                        }
                    }else {
                        rearList.get(i).setSort(rearList.get(i).getSort() + ONE);
                        menuVo = menuDao.save(null,rearList.get(i));
                        if(rearList.get(i).getSort() - rearList.get(i-ONE).getSort() > ONE){
                            break;
                        }
                    }
                }
            }
        }
        return menuVo;
    }


    @Override
    public PageData<Menu> findMenus(PageLink pageLink) {
        log.trace("Executing findTenantInfos pageLink [{}]", pageLink);
        Validator.validatePageLink(pageLink);
        return menuDao.findMenusByRegion(new MenuId(EntityId.NULL_UUID), DEFAULT_TENANT_REGION, pageLink);
    }

    /**
     *查询系统菜单列表（标记被当前租户绑定过的）
     */
    @Override
    public List<MenuInfo> getTenantMenuListByTenantId(String menuType,String tenantId,String name) {
        log.trace("Executing getTenantMenuListByTenantId [{}]", tenantId);
        List<MenuInfo> resultList = new ArrayList<>();
        List<Menu> menuList = menuDao.findMenusByName(menuType,name);
        List<TenantMenu> tenantMenuList = tenantMenuDao.find(new TenantId(UUID.fromString(tenantId)));
        Iterator<TenantMenu> it = tenantMenuList.iterator();
        if(!CollectionUtils.isEmpty(menuList)){
            if(!CollectionUtils.isEmpty(tenantMenuList)){
                for (Menu menu :menuList ){
                    MenuInfo menuInfo = new MenuInfo(menu);
                    while(it.hasNext()) {
                        TenantMenu tenantMenu = it.next();
                        if (menu.getId().getId().equals(tenantMenu.getSysMenuId())) {
                            menuInfo.setAssociatedTenant(true);
                            it.remove();
                            break;
                        }
                    }
                    resultList.add(menuInfo);
                }
            }
        }
        return resultList;
    }

    /**
     * 条件查询系统菜单列表
     * @param menuType
     * @param name
     * @return
     */
    @Override
    public List<Menu> getMenuListByCdn(String menuType,String name){
        return menuDao.findMenusByName(menuType,name);
    }

    @Override
    public Menu findMenuById(MenuId menuId) {
        log.trace("Executing findMenuById [{}]", menuId);
        validateId(menuId, INCORRECT_MENU_ID + menuId);
        return menuDao.findById(null, menuId.getId());
    }

    /**
     * 查询一级菜单
     * @param menuType
     * @return
     */
    public List<Menu> getOneLevel(String menuType){
        return menuDao.getOneLevel(menuType);
    }

    /**
     * 查询系统菜单列表分页
     * @param menu
     * @param pageLink
     * @return
     */
    @Override
    public PageData<Menu> getMenuPage(Menu menu, PageLink pageLink) throws ThingsboardException {
        return menuDao.getMenuPage(menu,pageLink);
    }

    @Override
    public Menu getTenantById(UUID id){
       return menuDao.getTenantById(id);
    }

}
