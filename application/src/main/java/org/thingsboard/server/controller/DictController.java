package org.thingsboard.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.hs.entity.po.DictData;
import org.thingsboard.server.hs.entity.vo.DictDataQuery;
import org.thingsboard.server.hs.entity.vo.DictDataResource;
import org.thingsboard.server.hs.entity.enums.DictDataType;
import org.thingsboard.server.hs.entity.vo.DictDataListQuery;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.hs.service.DictDataService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;

import javax.validation.Valid;

import static org.thingsboard.server.dao.service.Validator.validateId;
import static org.thingsboard.server.dao.service.Validator.validatePageLink;

/**
 * 字典controller
 *
 * @author wwj
 * @since 2021.10.18
 */
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class DictController extends BaseController {

    @Autowired
    DictDataService dictDataService;

    /**
     * 获得数据字典列表
     *
     * @param pageSize     每页大小
     * @param page         页数
     * @param sortProperty 排序属性
     * @param sortOrder    排序顺序
     * @param code         编码
     * @param name         名称
     * @param dictDataType 数据类型
     * @return 数据字典列表
     */
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping("/dict/data")
    public PageData<DictData> listDictData(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String dictDataType
    )
            throws ThingsboardException {
        try {
            DictDataListQuery dictDataListQuery = DictDataListQuery.builder()
                    .code(code).name(name).dictDataType(dictDataType).build();
            SecurityUser user = getCurrentUser();
            TenantId tenantId = user.getTenantId();
            PageLink pageLink = createPageLink(pageSize, page, "", sortProperty, sortOrder);
            validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
            validatePageLink(pageLink);

            // 查询数据字典列表
            return this.dictDataService.listDictDataByQuery(tenantId, dictDataListQuery, pageLink);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 更新或新增数据字典
     *
     * @param dictDataQuery 数据字典参数
     */
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @PostMapping("/dict/data")
    public void updateOrSaveDictData(@RequestBody @Valid DictDataQuery dictDataQuery) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();
        this.dictDataService.updateOrSaveDictData(dictDataQuery, tenantId);
    }

    /**
     * 获得数据字典详情
     *
     * @param id 数据字典Id
     */
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping("/dict/data/{id}")
    public DictData getDictDataDetail(@PathVariable("id") String id) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();
        return this.dictDataService.getDictDataDetail(id, tenantId);
    }

    /**
     * 删除数据字典
     *
     * @param id 数据字典Id
     */
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @DeleteMapping("/dict/data/{id}")
    public void deleteDictData(@PathVariable("id") String id) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();
        this.dictDataService.deleteDictDataById(id, tenantId);
    }

    /**
     * 获得数据字典界面资源
     *
     * @return 数据字典列表
     */
    @GetMapping("/dict/data/resource")
    public DictDataResource listDictDataResource() throws ThingsboardException {
        try {
            return new DictDataResource().setDictDataTypeMap(DictDataType.BOOLEAN.toLinkMap());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    
}
