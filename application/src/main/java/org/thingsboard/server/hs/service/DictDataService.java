package org.thingsboard.server.hs.service;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.hs.entity.po.DictData;
import org.thingsboard.server.hs.entity.vo.DictDataListQuery;
import org.thingsboard.server.hs.entity.vo.DictDataQuery;

/**
 * 数据字典接口
 *
 * @author wwj
 * @since 2021.10.18
 */
public interface DictDataService {
    /**
     * 查询数据字典列表
     *
     * @param tenantId          租户Id
     * @param dictDataListQuery 查询条件
     * @param pageLink          分页条件
     * @return 数据字典列表
     */
    PageData<DictData> listDictDataByQuery(TenantId tenantId, DictDataListQuery dictDataListQuery, PageLink pageLink);

    /**
     * 更新或保存数据字典
     *
     * @param tenantId 租户Id
     * @param dictDataQuery 数据字典参数
     */
    void updateOrSaveDictData(DictDataQuery dictDataQuery, TenantId tenantId) throws ThingsboardException;

    /**
     * 获得数据字典详情
     *
     * @param tenantId 租户Id
     * @param id 数据字典Id
     */
    DictData getDictDataDetail(String id, TenantId tenantId) throws ThingsboardException;

    /**
     * 删除数据字典
     *
     * @param tenantId 租户Id
     * @param id 数据字典Id
     */
    void deleteDictDataById(String id, TenantId tenantId) throws ThingsboardException;
}
