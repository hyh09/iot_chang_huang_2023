package org.thingsboard.server.controller;

import io.swagger.annotations.*;
import org.apache.commons.lang3.EnumUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.entity.po.DictData;
import org.thingsboard.server.dao.hs.entity.vo.DictDataQuery;
import org.thingsboard.server.dao.hs.entity.vo.DictDataResource;
import org.thingsboard.server.dao.hs.entity.enums.DictDataDataTypeEnum;
import org.thingsboard.server.dao.hs.entity.vo.DictDataListQuery;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.hs.service.DictDataService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.queue.util.TbCoreComponent;

import javax.validation.Valid;

import java.util.List;

import static org.thingsboard.server.dao.service.Validator.validatePageLink;

/**
 * 数据字典接口
 *
 * @author wwj
 * @since 2021.10.18
 */
@Api(value = "数据字典接口", tags = {"数据字典接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class DictDataController extends BaseController {

    @Autowired
    DictDataService dictDataService;

    /**
     * 获得数据字典界面资源
     *
     * @return 字典界面资源
     */
    @ApiOperation(value = "获得数据字典界面资源")
    @GetMapping("/dict/data/resource")
    public DictDataResource listDictDataResource() throws ThingsboardException {
        return new DictDataResource().setDictDataTypeList(CommonUtil.toResourceList(EnumUtils.getEnumList(DictDataDataTypeEnum.class)));
    }

    /**
     * 获得当前可用数据字典编码
     *
     * @return 数据字典编码
     */
    @ApiOperation(value = "获得当前可用数据字典编码")
    @GetMapping("/dict/data/availableCode")
    public String getAvailableCode() throws ThingsboardException {
        return this.dictDataService.getAvailableCode(getTenantId());
    }

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
    @ApiOperation(value = "获得数据字典列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性", paramType = "query", defaultValue = "createdTime"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序", paramType = "query", defaultValue = "desc"),
            @ApiImplicitParam(name = "code", value = "编码", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "名称", paramType = "query"),
            @ApiImplicitParam(name = "dictDataType", value = "数据类型", dataType = "DictDataType", paramType = "query")})
    @GetMapping("/dict/data")
    public PageData<DictData> listDictData(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false, defaultValue = "createdTime") String sortProperty,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) DictDataDataTypeEnum dictDataType
    )
            throws ThingsboardException {
        DictDataListQuery dictDataListQuery = DictDataListQuery.builder()
                .code(code).name(name).dictDataType(dictDataType).build();
        PageLink pageLink = createPageLink(pageSize, page, "", sortProperty, sortOrder);
        validatePageLink(pageLink);

        // 查询数据字典列表
        return this.dictDataService.listPageDictDataByQuery(getTenantId(), dictDataListQuery, pageLink);
    }

    /**
     * 更新或新增数据字典
     *
     * @param dictDataQuery 数据字典请求参数实体类
     */
    @ApiOperation(value = "更新或新增数据字典")
    @PostMapping(value = "/dict/data")
    public DictDataQuery updateOrSaveDictData(@RequestBody @Valid DictDataQuery dictDataQuery) throws ThingsboardException {
        CommonUtil.checkCode(dictDataQuery.getCode(), HSConstants.CODE_PREFIX_DICT_DATA);
        return this.dictDataService.saveOrUpdateDictData(dictDataQuery, getTenantId());
    }

    /**
     * 获得数据字典详情
     *
     * @param id 数据字典id
     */
    @ApiOperation(value = "获得数据字典详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据字典id", paramType = "path", required = true),})
    @GetMapping("/dict/data/{id}")
    public DictData getDictDataDetail(@PathVariable("id") String id) throws ThingsboardException {
        checkParameter("id", id);
        return this.dictDataService.getDictDataDetail(id, getTenantId());
    }

    /**
     * 删除数据字典
     *
     * @param id 数据字典id
     */
    @ApiOperation(value = "删除数据字典")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据字典id", paramType = "path", required = true),})
    @DeleteMapping("/dict/data/{id}")
    public void deleteDictData(@PathVariable("id") String id) throws ThingsboardException {
        checkParameter("id", id);
        this.dictDataService.deleteDictDataById(id, getTenantId());
    }

    /**
     * 【不分页】获得数据字典列表
     */
    @ApiOperation(value = "获得数据字典列表")
    @GetMapping("/dict/data/all")
    public List<DictData> listDictDataAll() throws ThingsboardException {
        return this.dictDataService.listDictData(getTenantId());
    }
}
