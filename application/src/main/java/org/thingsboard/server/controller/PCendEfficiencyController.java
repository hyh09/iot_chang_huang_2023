package org.thingsboard.server.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.effciency.EfficiencyEntityInfo;
import org.thingsboard.server.common.data.effciency.data.EfficiencyHistoryDataVo;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageDataAndTotalValue;
import org.thingsboard.server.common.data.page.PageDataWithNextPage;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.CustomException;
import org.thingsboard.server.common.data.vo.QueryTsKvHisttoryVo;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.device.RunningStateVo;
import org.thingsboard.server.common.data.vo.device.input.InputRunningSateVo;
import org.thingsboard.server.common.data.vo.device.out.OutRunningStateVo;
import org.thingsboard.server.common.data.vo.enums.ActivityException;
import org.thingsboard.server.common.data.vo.parameter.PcTodayEnergyRaningVo;
import org.thingsboard.server.common.data.vo.parameter.PcTodayProportionChartOutput;
import org.thingsboard.server.common.data.vo.pc.ResultEnergyTopTenVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.CapacityHistoryVo;
import org.thingsboard.server.controller.example.AnswerExample;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.dao.util.StringUtilToll;
import org.thingsboard.server.excel.po.AppDeviceCapPo;
import org.thingsboard.server.excel.po.CapacityHistoryPo;
import org.thingsboard.server.excel.po.EfficiencyEntityInfoPo;
import org.thingsboard.server.excel.po.EfficiencyHistoryDataPo;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @program: thingsboard
 * @description: Pc?????????????????????
 * @author: HU.YUNHUI
 * @create: 2021-11-16 09:49
 **/
@Api(value = "Pc?????????????????????", tags = {"Pc?????????????????????"})
@Slf4j
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/pc/efficiency")
public class PCendEfficiencyController extends BaseController implements AnswerExample {


    @ApiOperation(value = "???PC????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "????????????"),
            @ApiImplicitParam(name = "endTime", value = "????????????"),
            @ApiImplicitParam(name = "deviceId", value = "??????id"),
            @ApiImplicitParam(name = "productionLineId", value = "??????id  UUID??????"),
            @ApiImplicitParam(name = "workshopId", value = "??????id UUID??????"),
            @ApiImplicitParam(name = "factoryId", value = "??????id  UUID??????"),
    })
    @RequestMapping(value = "/queryCapacity", params = {"pageSize", "page"}, method = RequestMethod.GET)

    @ResponseBody
    public PageDataAndTotalValue<AppDeviceCapVo> queryCapacity(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) UUID deviceId,
            @RequestParam(required = false) UUID productionLineId,
            @RequestParam(required = false) UUID workshopId,
            @RequestParam(required = false) UUID factoryId
    ) throws ThingsboardException {
        try {
            QueryTsKvVo queryTsKvVo = new QueryTsKvVo(startTime, endTime, deviceId, productionLineId, workshopId, factoryId);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            queryTsKvVo.setTenantId(getTenantId().getId());
            return efficiencyStatisticsSvc.queryPCCapAppNewMethod(queryTsKvVo, getTenantId(), pageLink);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ActivityException.FAILURE_ERROR.getCode(), e.getMessage());
        }
    }


    @ApiOperation(value = "???PC????????????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "????????????"),
            @ApiImplicitParam(name = "endTime", value = "????????????"),
            @ApiImplicitParam(name = "deviceId", value = "??????id"),
            @ApiImplicitParam(name = "productionLineId", value = "??????id  UUID??????"),
            @ApiImplicitParam(name = "workshopId", value = "??????id UUID??????"),
            @ApiImplicitParam(name = "factoryId", value = "??????id  UUID??????"),
    })
    @RequestMapping(value = "/queryCapacityOnSecondLeve", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageDataAndTotalValue<AppDeviceCapVo> queryCapacityOnSecondLeve(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) UUID deviceId,
            @RequestParam(required = false) UUID productionLineId,
            @RequestParam(required = false) UUID workshopId,
            @RequestParam(required = false) UUID factoryId
    ) throws ThingsboardException {
        try {
            QueryTsKvVo queryTsKvVo = new QueryTsKvVo(startTime, endTime, deviceId, productionLineId, workshopId, factoryId);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            queryTsKvVo.setTenantId(getTenantId().getId());
            return efficiencyStatisticsSvc.queryCapacityOnSecondLeve(queryTsKvVo, getTenantId(), pageLink);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ActivityException.FAILURE_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * ?????????????????????
     */
    @RequestMapping(value = "excelCapacity", method = RequestMethod.GET)
    public void excelCapacity(@RequestParam int pageSize,
                              @RequestParam int page,
                              @RequestParam(required = false) String textSearch,
                              @RequestParam(required = false) String sortProperty,
                              @RequestParam(required = false) String sortOrder,
                              @RequestParam(required = false) Long startTime,
                              @RequestParam(required = false) Long endTime,
                              @RequestParam(required = false) UUID deviceId,
                              @RequestParam(required = false) UUID productionLineId,
                              @RequestParam(required = false) UUID workshopId,
                              @RequestParam(required = false) UUID factoryId, HttpServletResponse response) throws IOException, ThingsboardException {
        PageDataAndTotalValue<AppDeviceCapVo> pageDataAndTotalValue = queryCapacity(pageSize, page, textSearch, sortProperty, sortOrder, startTime, endTime, deviceId, productionLineId, workshopId, factoryId);
        List<AppDeviceCapPo> list = pageDataAndTotalValue.getData().stream().map(vo -> AppDeviceCapPo.builder().rename(vo.getRename()).value(vo.getValue()).build()).collect(Collectors.toList());
        easyExcel(response, "????????????", "", list, AppDeviceCapPo.class);
    }


    @ApiOperation("????????????????????????????????????????????????")
    @ApiResponses({
            @ApiResponse(code = 200, message = queryEntityByKeysHeader),
    })
    @RequestMapping(value = "/queryEntityByKeysHeader", method = RequestMethod.GET)
    public Object queryEntityByKeysHeader() throws ThingsboardException {
        try {
            return efficiencyStatisticsSvc.queryEntityByKeysHeader();
        } catch (Exception e) {
            log.error("???????????????-?????????????????????????????????-???????????? ???????????????:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }
    }


    @ApiOperation("???????????? ???????????????; ???????????????????????? ###?????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "????????????"),
            @ApiImplicitParam(name = "endTime", value = "????????????"),
            @ApiImplicitParam(name = "deviceId", value = "??????id")
    })
    @RequestMapping(value = "/queryEntityByKeys", method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(code = 200, message = queryEnergyHistory_messg),
    })
    @ResponseBody
    public Object queryEntityByKeys(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) UUID deviceId,
            @RequestParam(required = false) UUID productionLineId,
            @RequestParam(required = false) UUID workshopId,
            @RequestParam(required = false) UUID factoryId
    ) throws ThingsboardException {
        try {
            if (startTime == null || endTime == null) {
                startTime = (CommonUtils.getZero());
                endTime = (CommonUtils.getNowTime());
            }

            QueryTsKvVo queryTsKvVo = new QueryTsKvVo();
            queryTsKvVo.setDeviceId(deviceId);
            queryTsKvVo.setProductionLineId(productionLineId);
            queryTsKvVo.setWorkshopId(workshopId);
            queryTsKvVo.setFactoryId(factoryId);
            queryTsKvVo.setStartTime(startTime);
            queryTsKvVo.setEndTime(endTime);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            queryTsKvVo.setTenantId(getTenantId().getId());
            PageDataAndTotalValue<Map> obj = efficiencyStatisticsSvc.queryEntityByKeysNewMethod(queryTsKvVo, getTenantId(), pageLink);
            return obj;

        } catch (Exception e) {
            log.error("??????????????? ???????????????; ???????????????????????????????????????:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }
    }


    /**
     * ??????5???????????????????????????
     *
     * @param pageSize
     * @param page
     * @param textSearch
     * @param sortProperty
     * @param sortOrder
     * @param startTime
     * @param endTime
     * @param deviceId
     * @param productionLineId
     * @param workshopId
     * @param factoryId
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("???????????? ???????????????; ???????????????????????? ###?????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "????????????"),
            @ApiImplicitParam(name = "endTime", value = "????????????"),
            @ApiImplicitParam(name = "deviceId", value = "??????id")
    })
    @RequestMapping(value = "/queryEntityByKeysNew", method = RequestMethod.GET)
    @ResponseBody
    public PageDataAndTotalValue<EfficiencyEntityInfo> queryEntityByKeysNew(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) UUID deviceId,
            @RequestParam(required = false) UUID productionLineId,
            @RequestParam(required = false) UUID workshopId,
            @RequestParam(required = false) UUID factoryId
    ) throws ThingsboardException {
        try {
            if (startTime == null || endTime == null) {
                startTime = (CommonUtils.getZero());
                endTime = (CommonUtils.getNowTime());
            }

            QueryTsKvVo queryTsKvVo = new QueryTsKvVo();
            queryTsKvVo.setDeviceId(deviceId);
            queryTsKvVo.setProductionLineId(productionLineId);
            queryTsKvVo.setWorkshopId(workshopId);
            queryTsKvVo.setFactoryId(factoryId);
            queryTsKvVo.setStartTime(startTime);
            queryTsKvVo.setEndTime(endTime);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            queryTsKvVo.setTenantId(getTenantId().getId());
            PageDataAndTotalValue<EfficiencyEntityInfo> obj = efficiencyStatisticsSvc.queryEntityByKeysNew(queryTsKvVo, getTenantId(), pageLink);
            return obj;

        } catch (Exception e) {
            log.error("??????????????? ???????????????; ???????????????????????????????????????:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }
    }


    @ApiOperation("???????????????????????? ?????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "????????????"),
            @ApiImplicitParam(name = "endTime", value = "????????????"),
            @ApiImplicitParam(name = "deviceId", value = "??????id")
    })
    @RequestMapping(value = "/queryEntityByKeysNewOnSecondLeve", method = RequestMethod.GET)
    @ResponseBody
    public PageDataAndTotalValue<EfficiencyEntityInfo> queryEntityByKeysNewOnSecondLeve(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) UUID deviceId,
            @RequestParam(required = false) UUID productionLineId,
            @RequestParam(required = false) UUID workshopId,
            @RequestParam(required = false) UUID factoryId
    ) throws ThingsboardException {
        try {
            if (startTime == null || endTime == null) {
                startTime = (CommonUtils.getZero());
                endTime = (CommonUtils.getNowTime());
            }

            QueryTsKvVo queryTsKvVo = new QueryTsKvVo();
            queryTsKvVo.setDeviceId(deviceId);
            queryTsKvVo.setProductionLineId(productionLineId);
            queryTsKvVo.setWorkshopId(workshopId);
            queryTsKvVo.setFactoryId(factoryId);
            queryTsKvVo.setStartTime(startTime);
            queryTsKvVo.setEndTime(endTime);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            queryTsKvVo.setTenantId(getTenantId().getId());
            PageDataAndTotalValue<EfficiencyEntityInfo> obj = efficiencyStatisticsSvc.queryEntityByKeysNewOnSecondLeve(queryTsKvVo, getTenantId(), pageLink);
            return obj;

        } catch (Exception e) {
            log.error("??????????????? ???????????????; ???????????????????????????????????????:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }
    }

    @RequestMapping(value = "/excelEntityByKeysNew", method = RequestMethod.GET)
    @ResponseBody
    public void excelEntityByKeysNew(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) UUID deviceId,
            @RequestParam(required = false) UUID productionLineId,
            @RequestParam(required = false) UUID workshopId,
            @RequestParam(required = false) UUID factoryId,
            HttpServletResponse response
    ) throws ThingsboardException, IOException {
        PageDataAndTotalValue<EfficiencyEntityInfo> pageDataAndTotalValue = this.queryEntityByKeysNew(pageSize, page, textSearch, sortProperty, sortOrder, startTime, endTime, deviceId, productionLineId, workshopId, factoryId);
        List<EfficiencyEntityInfo> list = pageDataAndTotalValue.getData();
        List<EfficiencyEntityInfoPo> poList = list.stream().map(vo ->
                EfficiencyEntityInfoPo.builder()
                        .rename(vo.getRename())
                        .waterConsumption(vo.getWaterConsumption()).unitWaterConsumption(vo.getUnitWaterConsumption())
                        .electricConsumption(vo.getElectricConsumption()).unitElectricConsumption(vo.getUnitElectricConsumption())
                        .gasConsumption(vo.getGasConsumption()).unitGasConsumption(vo.getUnitGasConsumption())
                        .capacityConsumption(vo.getCapacityConsumption())
                        .build()).collect(Collectors.toList());
        easyExcel(response, "????????????", "", poList, EfficiencyEntityInfoPo.class);
    }

    @ApiOperation("????????????-?????????????????????????????????-????????????")
    @ApiResponses({
            @ApiResponse(code = 200, message = queryEnergyHistoryHeader),
    })
    @ResponseBody
    @RequestMapping(value = "/queryEnergyHistoryHeader", method = RequestMethod.GET)
    public List<String> queryEnergyHistoryHeader() throws ThingsboardException {
        try {
            return efficiencyStatisticsSvc.queryEnergyHistoryHeader();
        } catch (Exception e) {
            log.error("???????????????-?????????????????????????????????-???????????? ???????????????:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }

    }


    @ApiOperation("????????????-????????????????????????????????? ---?????????????????????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "????????????"),
            @ApiImplicitParam(name = "endTime", value = "????????????"),
            @ApiImplicitParam(name = "deviceId", value = "??????id")
    })
    @RequestMapping(value = "/queryEnergyHistory", method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(code = 200, message = queryEnergyHistory_messg),
    })
    @ResponseBody
    public Object queryEnergyHistory(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) UUID deviceId
    ) throws ThingsboardException {
        try {
            if (startTime == null || endTime == null) {
                startTime = (CommonUtils.getZero());
                endTime = (CommonUtils.getNowTime());
            }

            QueryTsKvHisttoryVo queryTsKvVo = new QueryTsKvHisttoryVo();
            queryTsKvVo.setDeviceId(deviceId);
            queryTsKvVo.setStartTime(startTime);
            queryTsKvVo.setEndTime(endTime);
            queryTsKvVo.setSortOrder(sortOrder);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);

            return efficiencyStatisticsSvc.queryEnergyHistory(queryTsKvVo, getTenantId(), pageLink);
        } catch (Exception e) {
            log.error("???????????????-????????????????????????????????? ---????????????????????????????????????????????? ???????????????:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }
    }


    @ApiOperation("????????????-????????????????????????????????? ---?????????????????????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "????????????"),
            @ApiImplicitParam(name = "endTime", value = "????????????"),
            @ApiImplicitParam(name = "deviceId", value = "??????id")
    })
    @RequestMapping(value = "/queryEnergyHistoryNew", method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(code = 200, message = queryEnergyHistory_messg),
    })
    @ResponseBody
    public PageDataWithNextPage<EfficiencyHistoryDataVo> queryEnergyHistoryNew(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) UUID deviceId
    ) throws ThingsboardException {
        try {
            if (startTime == null || endTime == null) {
                startTime = (CommonUtils.getZero());
                endTime = (CommonUtils.getNowTime());
            }

            QueryTsKvHisttoryVo queryTsKvVo = new QueryTsKvHisttoryVo();
            queryTsKvVo.setDeviceId(deviceId);
            queryTsKvVo.setStartTime(startTime);
            queryTsKvVo.setEndTime(endTime);
            queryTsKvVo.setSortOrder(sortOrder);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);

            return efficiencyStatisticsSvc.queryEnergyHistoryNew(queryTsKvVo, getTenantId(), pageLink);
        } catch (Exception e) {
            log.error("???????????????-????????????????????????????????? ---????????????????????????????????????????????? ???????????????:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }
    }


    @ApiOperation("???????????????????????????")
    @RequestMapping(value = "/excelEnergyHistoryNew", method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(code = 200, message = queryEnergyHistory_messg),
    })
    @ResponseBody
    public void excelEnergyHistoryNew(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) UUID deviceId,
            HttpServletResponse response
    ) throws ThingsboardException, IOException {
        PageDataWithNextPage<EfficiencyHistoryDataVo> pageDataWithNextPage = this.queryEnergyHistoryNew(pageSize, page, textSearch, sortProperty, sortOrder, startTime, endTime, deviceId);
        List<EfficiencyHistoryDataPo> poList = getEfficiencyHistoryPo(pageDataWithNextPage);
        easyExcel(response, "????????????", "", poList, EfficiencyHistoryDataPo.class);
    }


    @ApiOperation("????????????-????????????????????????????????? ---?????????????????????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "????????????"),
            @ApiImplicitParam(name = "endTime", value = "????????????"),
            @ApiImplicitParam(name = "deviceId", value = "??????id")
    })
    @RequestMapping(value = "/queryCapacityHistory", method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(code = 200, message = queryEnergyHistory_messg),
    })
    @ResponseBody
    public PageDataWithNextPage<CapacityHistoryVo> queryCapacityHistory(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) UUID deviceId
    ) throws ThingsboardException {
        try {
            if (startTime == null || endTime == null) {
                startTime = (CommonUtils.getZero());
                endTime = (CommonUtils.getNowTime());
            }

            QueryTsKvHisttoryVo queryTsKvVo = new QueryTsKvHisttoryVo();
            queryTsKvVo.setDeviceId(deviceId);
            queryTsKvVo.setStartTime(startTime);
            queryTsKvVo.setEndTime(endTime);
            queryTsKvVo.setSortOrder(sortOrder);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);

            return efficiencyStatisticsSvc.queryCapacityHistory(queryTsKvVo, getTenantId(), pageLink);
        } catch (Exception e) {
            log.error("???????????????-????????????????????????????????? ---????????????????????????????????????????????? ???????????????:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }
    }

    @RequestMapping(value = "excelCapacityHistory", method = RequestMethod.GET)
    public void excelCapacityHistory(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) UUID deviceId,
            HttpServletResponse response
    ) throws ThingsboardException, IOException {
        PageDataWithNextPage<CapacityHistoryVo> pageData = queryCapacityHistory(pageSize, page, textSearch, sortProperty, sortOrder, startTime, endTime, deviceId);
        List<CapacityHistoryPo> list = getCapacityHistoryPo(pageData);
        easyExcel(response, "????????????", "", list, CapacityHistoryPo.class);
    }


    /**
     * PC???
     *
     * @param deviceId
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("??????????????????????????????name????????????--pc????????????")
    @RequestMapping(value = "/queryDictName", method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(code = 200, message = pc_queryDictName),
    })
    @ResponseBody
    public List<RunningStateVo> queryDictName(@RequestParam("deviceId") UUID deviceId) throws ThingsboardException {
        try {
            SecurityUser securityUser = getCurrentUser();

            return efficiencyStatisticsSvc.queryDictDevice(deviceId, getTenantId(), isFactoryUser());
        } catch (Exception e) {
            e.printStackTrace();
            log.info("====>:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }
    }


    @ApiOperation(value = "???PC???????????????????????????????????????")
    @ApiResponses({
            @ApiResponse(code = 200, message = pc_queryTheRunningStatusByDevice),
    })
    @RequestMapping(value = "/queryTheRunningStatusByDevice", method = RequestMethod.POST)
    @ResponseBody
    public List<OutRunningStateVo> queryTheRunningStatusByDevice(@RequestBody InputRunningSateVo queryTsKvVo) throws ThingsboardException {
        try {
            return efficiencyStatisticsSvc.queryPcTheRunningStatusByDevice(queryTsKvVo, getTenantId(), isFactoryUser());
        } catch (Exception e) {
            log.error("???PC???????????????????????????????????????????????????:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }
    }


    @ApiOperation(value = "?????????????????????????????????")
    @PostMapping("/queryTodayEffceency")
    public List<ResultEnergyTopTenVo> queryTodayEffceency(@RequestBody PcTodayEnergyRaningVo vo) throws ThingsboardException {
        try {
            LocalDate today = LocalDate.now();
            SecurityUser authUser = getCurrentUser();
            vo.setTenantId(getTenantId().getId());
            vo.setDate(today);
            return efficiencyStatisticsSvc.queryPcResultEnergyTopTenVo(vo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);

        }
    }


    @ApiOperation(value = "????????????????????????")
    @PostMapping("/queryTodayProportionChartOutput")
    public Object queryTodayProportionChartOutput(@RequestBody PcTodayProportionChartOutput vo) {
        return null;
    }


    /**
     * ????????????????????????0
     *
     * @param pageData
     * @return
     */
    private List<CapacityHistoryPo> getCapacityHistoryPo(PageDataWithNextPage<CapacityHistoryVo> pageData) {

        List<CapacityHistoryPo> capacityHistoryPoList = new ArrayList<>();
        List<CapacityHistoryVo> voList = pageData.getData();
        if (CollectionUtils.isEmpty(voList)) {
            return capacityHistoryPoList;
        }
        CapacityHistoryVo lastData = pageData.getNextData();
        for (int i = 0; i < voList.size(); i++) {
            if ((i + 1) < voList.size()) {
                capacityHistoryPoList.add(getCapacityHistoryPoByCurrentVoAndNex(voList.get(i), voList.get(i + 1)));
            } else {
                capacityHistoryPoList.add(getCapacityHistoryPoByCurrentVoAndNex(voList.get(i), lastData));
            }


        }
        return capacityHistoryPoList;

    }

    private CapacityHistoryPo getCapacityHistoryPoByCurrentVoAndNex(CapacityHistoryVo currentVo, CapacityHistoryVo nexVo) {
        CapacityHistoryPo po = new CapacityHistoryPo();
        po.setCreatedTime(CommonUtils.stampToDate(currentVo.getCreatedTime()));
        po.setDeviceName(currentVo.getDeviceName());
        if (nexVo != null) {
            po.setValue(StringUtilToll.sub(currentVo.getValue(), nexVo.getValue()));
            return po;
        }
        po.setValue("0");
        return po;
    }


    /**
     * ????????????????????????0
     *
     * @param pageData
     * @return
     */
    private List<EfficiencyHistoryDataPo> getEfficiencyHistoryPo(PageDataWithNextPage<EfficiencyHistoryDataVo> pageData) {

        List<EfficiencyHistoryDataPo> poList = new ArrayList<>();
        List<EfficiencyHistoryDataVo> voList = pageData.getData();
        if (CollectionUtils.isEmpty(voList)) {
            return poList;
        }
        EfficiencyHistoryDataVo lastData1 = pageData.getNextData();

        for (int i = 0; i < voList.size(); i++) {
            if ((i + 1) < voList.size()) {
                poList.add(geEfficiencyHistoryPoByCurrentVoAndNex(voList.get(i), voList.get(i + 1)));
            } else {
                poList.add(geEfficiencyHistoryPoByCurrentVoAndNex(voList.get(i), lastData1));
            }


        }
        return poList;

    }

    private EfficiencyHistoryDataPo geEfficiencyHistoryPoByCurrentVoAndNex(EfficiencyHistoryDataVo currentVo, EfficiencyHistoryDataVo nexVo) {
        EfficiencyHistoryDataPo po = new EfficiencyHistoryDataPo();
        po.setCreatedTime(CommonUtils.stampToDateByLong(currentVo.getCreatedTime()));
        po.setDeviceName(currentVo.getDeviceName());
        if (nexVo != null) {
            po.setWater(StringUtilToll.sub(currentVo.getWater(), nexVo.getWater()));
            po.setGas(StringUtilToll.sub(currentVo.getGas(), nexVo.getGas()));
            po.setElectric(StringUtilToll.sub(currentVo.getElectric(), nexVo.getElectric()));
            return po;
        }
        po.setWater("0");
        po.setGas("0");
        po.setElectric("0");
        return po;
    }


}
