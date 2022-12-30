package org.thingsboard.server.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.excel.dto.ExportDto;
import org.thingsboard.server.utils.ExcelUtil;

import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
@RequestMapping("/excel")
public class ExcelController {

    @Autowired
    private ExcelUtil fileService;

    /**
     * 导出excel
     * @param dto
     * @param response
     */
    @RequestMapping("/export")
    @ApiOperation("导出excel")
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
