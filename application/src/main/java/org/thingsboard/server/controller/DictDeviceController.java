package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.hs.entity.po.DictDevice;
import org.thingsboard.server.hs.entity.vo.DictDeviceVO;
import org.thingsboard.server.hs.service.DictDeviceService;
import org.thingsboard.server.queue.util.TbCoreComponent;

import javax.validation.Valid;


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
     * 获得设备字典列表
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
        return null;
    }

    /**
     * 新增或修改设备字典
     */
    @ApiOperation(value = "新增或修改设备字典")
    @PostMapping("/dict/device")
    public void updateOrSaveDictDevice(@RequestBody @Valid DictDeviceVO dictDeviceVO) throws ThingsboardException {
        this.dictDeviceService.updateOrSaveDictDevice(dictDeviceVO);
    }

    /**
     * 获得设备字典详情
     */
    @ApiOperation(value = "获得设备字典详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备字典id"),})
    @GetMapping("/dict/device/{id}")
    public DictDeviceVO getDictDeviceDetail(@PathVariable("id") String id) throws ThingsboardException {
        return null;
    }

    /**
     * 删除设备字典
     */
    @ApiOperation(value = "删除设备字典")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备字典id"),})
    @DeleteMapping("/dict/device/{id}")
    public void deleteDictDevice(@PathVariable("id") String id) throws ThingsboardException {

    }

}
