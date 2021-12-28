package org.thingsboard.server.dao.systemversion;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.systemversion.SystemVersion;

import java.util.List;
import java.util.UUID;

public interface SystemVersionDao{

    /**
     * 保存后刷新值
     * @param systemVersion
     * @return
     */
    SystemVersion saveSystemVersion(SystemVersion systemVersion)throws ThingsboardException;

    /**
     * 修改后刷新值
     * @param systemVersion
     * @return
     */
    SystemVersion updSystemVersion(SystemVersion systemVersion)throws ThingsboardException;

    /**
     * 删除
     * @param id
     * @param id
     * @return
     */
    void delSystemVersion(UUID id)throws ThingsboardException ;

    /**
     * 分页查询
     * @param pageLink
     * @return
     */
    PageData<SystemVersion> findSystemVersionPage(SystemVersion systemVersion ,PageLink pageLink);

    /**
     * 查询历史版本列表
     * @param systemVersion
     * @return
     */
    List<SystemVersion> findSystemVersionList(SystemVersion systemVersion);

    /**
     * 查询详情
     * @param id
     * @return
     */
    SystemVersion findById(UUID id);

    /**
     * 查询系统最新版本
     * @param tenantId
     * @return
     */
    SystemVersion getSystemVersionMax(TenantId tenantId) throws ThingsboardException;
}
