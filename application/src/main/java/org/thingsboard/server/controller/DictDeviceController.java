package org.thingsboard.server.controller;

import com.google.api.client.util.Sets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.transport.service.DefaultTransportService;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.entity.po.DictDevice;
import org.thingsboard.server.dao.hs.entity.po.DictDeviceComponent;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupVO;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceListQuery;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceVO;
import org.thingsboard.server.dao.hs.service.DictDeviceService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.queue.util.TbCoreComponent;

import javax.validation.Valid;

import java.util.List;

import static org.thingsboard.server.dao.service.Validator.validatePageLink;


/**
 * 设备字典接口
 *
 * @author wwj
 * @since 2021.10.21
 */
@Api(value = "设备字典接口", tags = {"设备字典接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class DictDeviceController extends BaseController {

    @Autowired
    DictDeviceService dictDeviceService;

    @Autowired
    DefaultTransportService defaultTransportService;


    /**
     * 获得当前可用设备字典编码
     *
     * @return 可用设备字典编码
     */
    @ApiOperation(value = "获得当前可用设备字典编码")
    @GetMapping("/dict/device/availableCode")
    public String getAvailableCode() throws ThingsboardException {
        return this.dictDeviceService.getAvailableCode(getTenantId());
    }

    /**
     * 获得当前默认初始化的分组及分组属性
     *
     * @return 分组及分组属性
     */
    @ApiOperation(value = "获得当前默认初始化的分组及分组属性")
    @GetMapping("/dict/device/group/initData")
    public List<DictDeviceGroupVO> getGroupInitData() throws ThingsboardException {
        return this.dictDeviceService.getGroupInitData();
    }

    /**
     * 获得设备字典列表
     *
     * @param pageSize     每页大小
     * @param page         页数
     * @param sortProperty 排序属性
     * @param sortOrder    排序顺序
     * @param code         编码
     * @param name         名称
     * @param supplier     供应商
     * @return 设备字典列表
     */
    @ApiOperation(value = "获得设备字典列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性", paramType = "query", defaultValue = "createdTime"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序", paramType = "query", defaultValue = "desc"),
            @ApiImplicitParam(name = "code", value = "编码", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "名称", paramType = "query"),
            @ApiImplicitParam(name = "supplier", value = "供应商", paramType = "query")})
    @GetMapping("/dict/device")
    public PageData<DictDevice> listDictDevice(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false, defaultValue = "createdTime") String sortProperty,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String supplier
    ) throws ThingsboardException {
        DictDeviceListQuery dictDeviceListQuery = DictDeviceListQuery.builder()
                .code(code).name(name).supplier(supplier).build();

        PageLink pageLink = createPageLink(pageSize, page, "", sortProperty, sortOrder);
        validatePageLink(pageLink);
        return this.dictDeviceService.listDictDeviceByQuery(dictDeviceListQuery, getTenantId(), pageLink);
    }

    /**
     * 新增或修改设备字典
     */
    @ApiOperation(value = "新增或修改设备字典")
    @PostMapping("/dict/device")
    public DictDeviceVO updateOrSaveDictDevice(@RequestBody @Valid DictDeviceVO dictDeviceVO) throws ThingsboardException {
        CommonUtil.checkCode(dictDeviceVO.getCode(), HSConstants.CODE_PREFIX_DICT_DEVICE);
//        CommonUtil.recursionCheckComponentCode(dictDeviceVO.getComponentList(), new HashSet<>());
//        CommonUtil.checkDictDeviceGroupVOListHeadIsUnlike(dictDeviceVO.getGroupList(), this.dictDeviceService.getGroupInitData());
        CommonUtil.checkDuplicateName(dictDeviceVO, Sets.newHashSet());
        return this.dictDeviceService.updateOrSaveDictDevice(dictDeviceVO, getTenantId());
    }

    /**
     * 获得设备字典详情
     *
     * @param id 设备字典id
     */
    @ApiOperation(value = "获得设备字典详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备字典id", paramType = "path", required = true)})
    @GetMapping("/dict/device/{id}")
    public DictDeviceVO getDictDeviceDetail(@PathVariable("id") String id) throws ThingsboardException {
        checkParameter("id", id);
        return this.dictDeviceService.getDictDeviceDetail(id, getTenantId());
    }

    /**
     * 删除设备字典
     */
    @ApiOperation(value = "删除设备字典")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备字典id", paramType = "path", required = true),})
    @DeleteMapping("/dict/device/{id}")
    public void deleteDictDevice(@PathVariable("id") String id) throws ThingsboardException {
        checkParameter("id", id);
        this.dictDeviceService.deleteDictDevice(id, getTenantId());
    }

    /**
     * 【不分页】获得设备字典列表
     */
    @ApiOperation(value = "【不分页】获得设备字典列表")
    @GetMapping("/dict/device/all")
    public List<DictDevice> listAllDictDevice() throws ThingsboardException {
        return this.dictDeviceService.listAllDictDevice(getTenantId());
    }

    /**
     * 【不分页】获得设备字典绑定的部件
     */
    @ApiOperation(value = "【不分页】获得设备字典绑定的部件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dictDeviceId", value = "设备字典id", paramType = "query", required = true),})
    @GetMapping("/dict/device/component")
    public List<DictDeviceComponent> listDictDeviceComponents(@RequestParam("dictDeviceId") String dictDeviceId) throws ThingsboardException {
        checkParameter("dictDeviceId", dictDeviceId);
        return this.dictDeviceService.listDictDeviceComponents(getTenantId(), toUUID(dictDeviceId));
    }

    /**
     * 设置默认设备字典
     */
    @ApiOperation(value = "设置默认设备字典")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备字典id", paramType = "path", required = true),})
    @PostMapping("/dict/device/{id}/default")
    public void updateDictDeviceDefault(@PathVariable("id") String id) throws ThingsboardException {
        checkParameter("id", id);
        this.dictDeviceService.updateDictDeviceDefault(getTenantId(), toUUID(id));
    }
}
