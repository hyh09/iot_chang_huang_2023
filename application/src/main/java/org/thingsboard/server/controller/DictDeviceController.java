package org.thingsboard.server.controller;

import com.google.api.client.util.Sets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.ota.ChecksumAlgorithm;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.entity.enums.DictDevicePropertyTypeEnum;
import org.thingsboard.server.dao.hs.entity.po.DictDevice;
import org.thingsboard.server.dao.hs.entity.po.DictDeviceComponent;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hs.service.DictDeviceService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.dao.hsms.entity.vo.DictDevicePropertySwitchNewVO;
import org.thingsboard.server.dao.hsms.entity.vo.DictDeviceSwitchDeviceVO;
import org.thingsboard.server.queue.util.TbCoreComponent;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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
     * 获得设备字典界面资源
     *
     * @return 字典界面资源
     */
    @ApiOperation(value = "设备字典-界面资源")
    @GetMapping("/dict/device/resource")
    public DictDeviceResource listDictDeviceResources() throws ThingsboardException {
        return new DictDeviceResource().setDictDevicePropertyTypeList(CommonUtil.toResourceList(EnumUtils.getEnumList(DictDevicePropertyTypeEnum.class)));
    }


    /**
     * 获得当前可用设备字典编码
     *
     * @return 可用设备字典编码
     */
    @ApiOperation(value = "设备字典-可用编码")
    @GetMapping("/dict/device/availableCode")
    public String getAvailableCode() throws ThingsboardException {
        return this.dictDeviceService.getAvailableCode(getTenantId());
    }

    /**
     * 获得当前默认初始化的分组及分组属性
     *
     * @return 分组及分组属性
     */
    @ApiOperation(value = "设备字典-默认分组及分组属性")
    @GetMapping("/dict/device/group/initData")
    public List<DictDeviceGroupVO> getGroupInitData() throws ThingsboardException {
        return this.dictDeviceService.getDictDeviceGroupInitData();
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
    @ApiOperation(value = "设备字典-列表")
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
        return this.dictDeviceService.listPageDictDevicesByQuery(dictDeviceListQuery, getTenantId(), pageLink);
    }

    /**
     * 新增或修改设备字典
     */
    @ApiOperation(value = "设备字典-新增或修改")
    @PostMapping("/dict/device")
    public DictDeviceVO updateOrSaveDictDevice(@RequestBody @Valid DictDeviceVO dictDeviceVO) throws ThingsboardException {
        CommonUtil.checkCode(dictDeviceVO.getCode(), HSConstants.CODE_PREFIX_DICT_DEVICE);
        CommonUtil.checkDuplicateName(dictDeviceVO, Sets.newHashSet());
        CommonUtil.checkImageUpload(dictDeviceVO);
        return this.dictDeviceService.saveOrUpdateDictDevice(dictDeviceVO, getTenantId());
    }

    /**
     * 获得设备字典详情
     *
     * @param id 设备字典id
     */
    @ApiOperation(value = "设备字典-详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备字典id", paramType = "path", required = true)})
    @GetMapping("/dict/device/{id}")
    public DictDeviceVO getDictDeviceDetail(@PathVariable("id") String id) throws ThingsboardException {
        checkParameter("id", id);
        return this.dictDeviceService.getDictDeviceDetail(id, getTenantId());
    }


    /**
     * 获得打开的设备字典详情,租户用户获取全部
     *
     * @param id 设备字典id
     */
    @ApiOperation(value = "设备开启的字典-详情-租户用户获取全部")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备字典id", paramType = "path", required = true)})
    @GetMapping("/dict/openDevice/{id}")
    public DictDeviceVO getOpenDictDeviceDetail(@PathVariable("id") String id) throws ThingsboardException {
        checkParameter("id", id);
        if (isFactoryUser()) {
            return this.dictDeviceService.getOpenDictDeviceDetail(id, getTenantId());
        }
        return this.dictDeviceService.getDictDeviceDetail(id, getTenantId());
    }

    /**
     * 删除设备字典
     */
    @ApiOperation(value = "设备字典-删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备字典id", paramType = "path", required = true),})
    @DeleteMapping("/dict/device/{id}")
    public void deleteDictDevice(@PathVariable("id") String id) throws ThingsboardException {
        checkParameter("id", id);
        this.dictDeviceService.deleteDictDeviceById(id, getTenantId());
    }

    /**
     * 【不分页】获得设备字典列表
     */
    @ApiOperation(value = "设备字典-列表-不分页")
    @GetMapping("/dict/device/all")
    public List<DictDevice> listAllDictDevice() throws ThingsboardException {
        return this.dictDeviceService.listDictDevices(getTenantId());
    }

    /**
     * 【不分页】获得设备字典绑定的部件
     */
    @ApiOperation(value = "设备字典-部件-不分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dictDeviceId", value = "设备字典id", paramType = "query", required = true),})
    @GetMapping("/dict/device/component")
    public List<DictDeviceComponent> listDictDeviceComponents(@RequestParam("dictDeviceId") String dictDeviceId) throws ThingsboardException {
        checkParameter("dictDeviceId", dictDeviceId);
        return this.dictDeviceService.listDictDeviceTileComponents(getTenantId(), toUUID(dictDeviceId));
    }

    /**
     * 设置默认设备字典
     */
    @ApiOperation(value = "设备字典-设置默认")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备字典id", paramType = "path", required = true),})
    @PostMapping("/dict/device/{id}/default")
    public void updateDictDeviceDefault(@PathVariable("id") String id) throws ThingsboardException {
        checkParameter("id", id);
        this.dictDeviceService.updateDictDeviceDefault(getTenantId(), toUUID(id));
    }

    /**
     * 获得设备字典全部遥测属性-配置下发专用
     */
    @ApiOperation(value = "设备字典-属性-配置下发专用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dictDeviceId", value = "设备字典id", paramType = "query", required = true),})
    @GetMapping("/dict/device/properties")
    public List<DictDeviceTsPropertyResult> listDictDeviceIssueProperties(@RequestParam("dictDeviceId") String dictDeviceId) throws ThingsboardException {
        checkParameter("dictDeviceId", dictDeviceId);
        return this.dictDeviceService.listDictDeviceIssueProperties(getTenantId(), toUUID(dictDeviceId));
    }

    /**
     * 【不分页】获得设备字典全部遥测属性
     */
    @ApiOperation(value = "设备字典-全部属性-不分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dictDeviceId", value = "设备字典id", paramType = "query", required = true),})
    @GetMapping("/dict/device/all/properties")
    public List<DictDeviceTsPropertyVO> listDictDeviceProperties(@RequestParam("dictDeviceId") String dictDeviceId) throws ThingsboardException {
        checkParameter("dictDeviceId", dictDeviceId);
        return this.dictDeviceService.listDictDeviceProperties(getTenantId(), toUUID(dictDeviceId));
    }


    /**
     * 接口描述： 提供的位置：设备关联 > 点击设备字典 关联参数
     * 作者: wwj
     * 时间：2022-01-26
     *
     * @param dictDeviceId
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation(value = "设备字典-图表-列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dictDeviceId", value = "设备字典id", paramType = "query", required = true)})
    @GetMapping("/dict/device/graphs")
    public List<DictDeviceGraphVO> listDictDeviceGraphs(@RequestParam("dictDeviceId") UUID dictDeviceId) throws ThingsboardException {
        checkParameter("dictDeviceId", dictDeviceId);
        return this.dictDeviceService.listDictDeviceGraphs(getTenantId(), dictDeviceId);
    }

    /**
     * 设备字典-图表-详情
     */
    @ApiOperation(value = "设备字典-图表-详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "graphId", value = "设备字典图表id", paramType = "path", required = true)})
    @GetMapping("/dict/device/graph/{graphId}")
    public DictDeviceGraphVO getDictDeviceGraphDetail(@PathVariable("graphId") UUID graphId) throws ThingsboardException {
        checkParameter("graphId", graphId);
        return this.dictDeviceService.getDictDeviceGraphDetail(getTenantId(), graphId);
    }

    /**
     * 设备字典-图表-新增或修改
     */
    @ApiOperation(value = "设备字典-图表-新增或修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dictDeviceId", value = "设备字典id", paramType = "path", required = true)})
    @PostMapping("/dict/device/{dictDeviceId}/graph")
    public DictDeviceGraphVO updateOrSaveDictDeviceGraph(@PathVariable("dictDeviceId") UUID dictDeviceId, @Valid @RequestBody DictDeviceGraphVO dictDeviceGraphVO) throws ThingsboardException {
        checkParameter("dictDeviceId", dictDeviceId);
        var uuid = this.dictDeviceService.updateOrSaveDictDeviceGraph(getTenantId(), dictDeviceId, dictDeviceGraphVO);
        return this.dictDeviceService.getDictDeviceGraphDetail(getTenantId(), uuid);
    }

    /**
     * 设备字典-图表-删除
     */
    @ApiOperation(value = "设备字典-图表-删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "graphId", value = "设备字典图表id", paramType = "path", required = true)})
    @DeleteMapping("/dict/device/graph/{graphId}")
    public void deleteDictDeviceGraph(@PathVariable("graphId") UUID graphId) throws ThingsboardException {
        checkParameter("graphId", graphId);
        this.dictDeviceService.deleteDictDeviceGraph(getTenantId(), graphId);
    }

    /**
     * 设备字典-导入
     */
    @ApiOperation(value = "设备字典-导入")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "checksum", value = "校验和", paramType = "query"),
            @ApiImplicitParam(name = "checksumAlgorithmStr", value = "校验和算法", paramType = "query"),
            @ApiImplicitParam(name = "file", value = "文件", paramType = "form", dataType = "file", required = true),
    })
    @PostMapping(value = "/dict/device/import")
    public void dictDeviceImport(@RequestParam(required = false) String checksum,
                                 @RequestParam(required = false, defaultValue = "MD5") String checksumAlgorithmStr,
                                 @RequestBody MultipartFile file) throws ThingsboardException, IOException {
        if (file == null || file.isEmpty())
            throw new ThingsboardException("文件不能为空！", ThingsboardErrorCode.GENERAL);

        ChecksumAlgorithm checksumAlgorithm = ChecksumAlgorithm.valueOf(checksumAlgorithmStr.toUpperCase());
        this.dictDeviceService.saveDictDevicesFromFile(getTenantId(), getCurrentUser().getId(), checksum, checksumAlgorithm, file);
    }

    /**
     * 数据过滤-设备列表
     *
     * @param pageSize     每页大小
     * @param page         页数
     * @param sortProperty 排序属性
     * @param sortOrder    排序顺序
     * @return 数据过滤列表
     */
    @ApiOperation(value = "数据过滤-设备列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性", paramType = "query", defaultValue = "createdTime"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序", paramType = "query", defaultValue = "desc"),
            @ApiImplicitParam(name = "deviceName", value = "名称", paramType = "query"),
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query"),
    })
    @GetMapping("/dict/device/switch/devices")
    public PageData<DictDeviceSwitchDeviceVO> listDictDeviceSwitchDevices(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false, defaultValue = "createdTime") String sortProperty,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String factoryId
    ) throws ThingsboardException {

        PageLink pageLink = createPageLink(pageSize, page, "", sortProperty, sortOrder);
        validatePageLink(pageLink);
        var query = new FactoryDeviceQuery(factoryId, deviceName);
        if (StringUtils.isBlank(factoryId) && StringUtils.isBlank(deviceName))
            query = FactoryDeviceQuery.newQueryAllEntity();
        return this.dictDeviceService.listDictDeviceSwitchDevicesByQuery(query, getTenantId(), pageLink);
    }

    /**
     * 数据过滤-参数管理列表
     *
     * @param pageSize     每页大小
     * @param page         页数
     * @param sortProperty 排序属性
     * @param sortOrder    排序顺序
     * @return 数据过滤列表
     */
    @ApiOperation(value = "数据过滤-参数管理列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性", paramType = "query", defaultValue = "createdTime"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序", paramType = "query", defaultValue = "desc"),
            @ApiImplicitParam(name = "q", value = "参数描述", paramType = "query"),
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query"),
    })
    @GetMapping("/dict/device/switches")
    public PageData<DictDevicePropertySwitchNewVO> listDictDeviceSwitches(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false, defaultValue = "createdTime") String sortProperty,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String q
    ) throws ThingsboardException {

        PageLink pageLink = createPageLink(pageSize, page, "", sortProperty, sortOrder);
        validatePageLink(pageLink);
        q = StringUtils.isNotBlank(q) ? q.toLowerCase(Locale.ROOT).trim() : q;
        return this.dictDeviceService.listDictDeviceSwitches(getTenantId(), deviceId, q, pageLink);
    }

    /**
     * 数据过滤-属性开关更新或新增
     */
    @ApiOperation(value = "数据过滤-属性开关更新或新增")
    @PostMapping("/dict/device/switches")
    public void updateOrSaveDiceDeviceSwitches(@RequestBody @Valid List<DictDevicePropertySwitchNewVO> propertySwitches) throws ThingsboardException {
        this.dictDeviceService.updateOrSaveDiceDeviceSwitches(getTenantId(), propertySwitches);
    }
}
