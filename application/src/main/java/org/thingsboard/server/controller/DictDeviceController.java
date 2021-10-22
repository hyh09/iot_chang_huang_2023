package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.hs.entity.po.DictDevice;
import org.thingsboard.server.hs.entity.vo.DictDataListQuery;
import org.thingsboard.server.hs.entity.vo.DictDeviceListQuery;
import org.thingsboard.server.hs.entity.vo.DictDeviceVO;
import org.thingsboard.server.hs.service.DictDeviceService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;

import javax.validation.Valid;

import static org.thingsboard.server.dao.service.Validator.validateId;
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

    /**
     * 获得当前可用设备字典编码
     *
     * @return 可用设备字典编码
     */
    @ApiOperation(value = "获得当前可用设备字典编码")
    @GetMapping("/dict/device/availableCode")
    public String getAvailableCode() throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();
        return this.dictDeviceService.getAvailableCode(tenantId);
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
            @ApiImplicitParam(name = "page", value = "页数"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小"),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序"),
            @ApiImplicitParam(name = "code", value = "编码"),
            @ApiImplicitParam(name = "name", value = "名称"),
            @ApiImplicitParam(name = "supplier", value = "供应商")})
    @GetMapping("/dict/device")
    public PageData<DictDevice> listDictDevice(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String supplier
    ) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();
        DictDeviceListQuery dictDataListQuery = DictDeviceListQuery.builder()
                .code(code).name(name).supplier(supplier).build();
        PageLink pageLink = createPageLink(pageSize, page, "", sortProperty, sortOrder);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return this.dictDeviceService.listDictDeviceByQuery(dictDataListQuery, tenantId, pageLink);
    }

    /**
     * 新增或修改设备字典
     */
    @ApiOperation(value = "新增或修改设备字典")
    @PostMapping("/dict/device")
    public DictDeviceVO updateOrSaveDictDevice(@RequestBody @Valid DictDeviceVO dictDeviceVO) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();

        if (!StringUtils.isBlank(dictDeviceVO.getCode())) {
            if (!dictDeviceVO.getCode().startsWith("SBZD")) {
                throw new ThingsboardException("设备字典编码不符合规则", ThingsboardErrorCode.GENERAL);
            }
            try {
                int intV = Integer.parseInt(dictDeviceVO.getCode().split("SBZD")[1]);
                if (intV < 1 || intV > 9999) {
                    throw new ThingsboardException("设备字典编码不符合规则", ThingsboardErrorCode.GENERAL);
                }
            } catch (Exception ignore) {
                throw new ThingsboardException("设备字典编码不符合规则", ThingsboardErrorCode.GENERAL);
            }
        }

        // TODO 校验部件的编码
        this.dictDeviceService.updateOrSaveDictDevice(dictDeviceVO, tenantId);
        return dictDeviceVO;
    }

    /**
     * 获得设备字典详情
     *
     * @param id 设备字典id
     */
    @ApiOperation(value = "获得设备字典详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备字典id"),})
    @GetMapping("/dict/device/{id}")
    public DictDeviceVO getDictDeviceDetail(@PathVariable("id") String id) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();
        return this.dictDeviceService.getDictDeviceDetail(id, tenantId);
    }

    /**
     * 删除设备字典
     */
    @ApiOperation(value = "删除设备字典")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备字典id"),})
    @DeleteMapping("/dict/device/{id}")
    public void deleteDictDevice(@PathVariable("id") String id) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();

        this.dictDeviceService.deleteDictDevice(id, tenantId);
    }

}
