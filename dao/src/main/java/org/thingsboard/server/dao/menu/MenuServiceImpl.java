package org.thingsboard.server.dao.menu;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.common.data.memu.MenuInfo;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.service.Validator;
import org.thingsboard.server.dao.tenantmenu.TenantMenuDao;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.thingsboard.server.dao.service.Validator.validateId;

@Service
@Slf4j
@Transactional
public class MenuServiceImpl extends AbstractEntityService implements MenuService {

    private static final String DEFAULT_TENANT_REGION = "Global";
    public static final String INCORRECT_MENU_ID = "Incorrect menuId ";
    public static final Boolean IS_BUTTON_TRUE = true;
    public static final Boolean IS_BUTTON_FALSE = false;
    public static final int ONE = 1;
    public static final String XTCD = "XTCD"; //系统菜单首字母

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
        menu.setCode(XTCD + String.valueOf(System.currentTimeMillis()));
        //生成租户菜单排序序号
        Integer maxSort = menuDao.getMaxSortByParentId(menu.getParentId());
        menu.setSort(maxSort == null ? ONE:maxSort);
        Menu savedMenut = menuDao.saveMenu(menu);

       /* if(menu.getIsButton()){
            //查询要变更的租户菜单
            List<TenantMenu> addInfos = new ArrayList<>();
            TenantMenu tenantMenuQry = new TenantMenu();
            tenantMenuQry.setSysMenuId(menu.getId());
            List<TenantMenu> allByCdn = tenantMenuDao.findAllByCdn(tenantMenuQry);
            if(CollectionUtils.isNotEmpty(allByCdn)) {
                allByCdn.forEach(i -> {
                    TenantMenu addInfo = menu.toTenantMenuByAddButton(i);

                });
                //添加租户菜单按钮
                tenantMenuDao.saveOrUpdTenantMenu(allByCdn);
            }
        }*/
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
        //按钮名称、lang_key、path、icon修改，变更租户菜单按钮
        /*TenantMenu tenantMenu = new TenantMenu();
        Menu menuById = menuDao.getMenuById(menu.getId());
        if(menuById != null){
            if(menuById.getLangKey() != null && !menuById.getLangKey().equals(menu.getLangKey())){
                tenantMenu.setLangKey(menuById.getLangKey());
            }
            if(menuById.getPath() != null && !menuById.getPath().equals(menu.getPath())){
                tenantMenu.setPath(menuById.getPath());
            }
            if(menuById.getIsButton() && menuById.getMenuIcon() != null && !menuById.getMenuIcon().equals(menu.getMenuIcon())){
                tenantMenu.setTenantMenuIcon(menuById.getMenuIcon());
                tenantMenu.setTenantMenuName(menuById.getName());
                if(menuById.getParentId() != null && menu.getMenuType() != null
                        && !menuById.getParentId().toString().equals(menu.getMenuType())){
                    throw new ThingsboardException("按钮不允许变更父级菜单",ThingsboardErrorCode.FAIL_VIOLATION);
                }
            }
        }
        if(tenantMenu != null ){
            //查询要变更的租户菜单
            TenantMenu tenantMenuQry = new TenantMenu();
            tenantMenuQry.setSysMenuId(menu.getId());
            List<TenantMenu> allByCdn = tenantMenuDao.findAllByCdn(tenantMenuQry);
            if(CollectionUtils.isNotEmpty(allByCdn)){
                allByCdn.forEach(i->{
                    i.setSysMenuId(menu.getId());
                    if(StringUtils.isNotEmpty(menuById.getLangKey())){
                        i.setLangKey(menuById.getLangKey());
                    }
                    if(StringUtils.isNotEmpty(menuById.getPath())){
                        i.setPath(menuById.getPath());
                    }
                    if(StringUtils.isNotEmpty(menuById.getLangKey())){
                        i.setLangKey(menuById.getLangKey());
                    }
                    if(StringUtils.isNotEmpty(menuById.getPath())){
                        i.setPath(menuById.getPath());
                    }
                    if(StringUtils.isNotEmpty(menuById.getLangKey())){
                        i.setTenantMenuIcon(menuById.getMenuIcon());
                    }
                    if(StringUtils.isNotEmpty(menuById.getPath())){
                        i.setTenantMenuName(menuById.getName());
                    }
                });
                tenantMenuDao.saveOrUpdTenantMenu(allByCdn);
            }
        }*/
        return menuDao.saveMenu(menu);
    }

    /**
     * 删除菜单
     * @param id
     */
    public void delMenu(UUID id){
        //删除系统菜单
        menuDao.delMenu(id);
        //删除租户菜单
        tenantMenuDao.delByMenuId(id);
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
    public List<MenuInfo> getTenantMenuListByTenantId(String menuType,UUID tenantId,String name) {
        log.trace("Executing getTenantMenuListByTenantId [{}]", tenantId);
        List<MenuInfo> resultList = new ArrayList<>();

        List<Menu> menuList = menuDao.findMenusByName(menuType,name);
        List<TenantMenu> tenantMenuList = tenantMenuDao.findAllByCdn(new TenantMenu(tenantId,this.IS_BUTTON_FALSE));

        Iterator<TenantMenu> it = tenantMenuList.iterator();
        if(!CollectionUtils.isEmpty(menuList)){
            if(!CollectionUtils.isEmpty(tenantMenuList)){
                menuList.forEach(i->{
                    MenuInfo menuInfo = new MenuInfo(i);
                    tenantMenuList.forEach(j->{
                        if (i.getId().equals(j.getSysMenuId())) {
                            menuInfo.setAssociatedTenant(true);
                            return;
                        }
                    });
                    resultList.add(menuInfo);
                });
            }else {
                menuList.forEach(i->{
                    MenuInfo menuInfo = new MenuInfo(i);
                    resultList.add(menuInfo);
                });
            }
        }
        return resultList;
    }

    /**
     * 条件查询系统菜单列表
     * @param menu
     * @return
     */
    @Override
    public List<Menu> getMenuListByCdn(Menu menu){
        return menuDao.getMenuListByCdn(menu);
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
    public Menu getMenuById(UUID id){
       return menuDao.getMenuById(id);
    }

    /**
     * 查询同级菜单下是否存在
     * @param id
     * @param parentId
     * @param name
     * @return
     */
    @Override
    public Boolean findSameLevelNameRepetition(UUID id, UUID parentId ,String name){
        Menu sameLevelName = menuDao.findSameLevelName(parentId, name);
        if(id == null){
            if(sameLevelName != null){
                //重复
                return true;
            }else {
                //不重复
                return false;
            }
        }else {
            if(sameLevelName != null){
                if(id.toString().equals(sameLevelName.getId().toString())){
                    //不重复
                    return false;
                }else {
                    //重复
                    return true;
                }
            }else {
                //不重复
                return false;
            }
        }

    }

}
