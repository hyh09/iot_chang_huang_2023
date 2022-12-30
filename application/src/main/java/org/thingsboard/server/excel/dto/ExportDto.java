package org.thingsboard.server.excel.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "ExportDto",description = "excel导出入参")
public class ExportDto {
    //数据
    @ApiModelProperty("数据")
    private List<List<String>> dataList;
    //标题
    @ApiModelProperty("标题")
    private String title;
    //导出地址
    @ApiModelProperty("导出地址")
    private String path;

}
