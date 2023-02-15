package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.excel.dto.ExportDto;
import org.thingsboard.server.utils.ExcelUtil;
import javax.servlet.http.HttpServletResponse;
@Api(value="通用导出excelController",tags={"通用导出excel接口"})
@RestController
@Slf4j
@RequestMapping("/api/excel")
public class ExcelController {

    @Autowired
    private ExcelUtil fileService;

    /**
     * 导出excel
     * @param dto
     * @param response
     */
    @ApiOperation("导出excel")
    @RequestMapping(value = "/export", method = RequestMethod.POST)
    @ApiImplicitParam(name = "ExportDto", value = "入参实体", dataType = "ExportDto", paramType = "dto")
    @ResponseBody
    public void downloadExcel(@RequestBody ExportDto dto, HttpServletResponse response) {
        try {
            if (dto.getDataList() == null || dto.getDataList().size() == 0) {
                throw new RuntimeException("数据list不能为空");
            }
            fileService.downloadExcel(dto, response);
        } catch (Exception e) {
            log.error("导出excel异常", e);
        }
    }

}
