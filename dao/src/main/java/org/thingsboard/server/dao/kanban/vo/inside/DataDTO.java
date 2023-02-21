package org.thingsboard.server.dao.kanban.vo.inside;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @Project Name: thingsboard
 * @File Name: DataDTO
 * @Date: 2022/11/1 9:51
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@ToString
public class DataDTO {

    @ApiModelProperty(value = "部件参数")
    @JsonProperty("Key")
    private String key;

    @ApiModelProperty(value = "合并后的name")
    @JsonIgnore
    private String tableName;


    @ApiModelProperty(value = "部件参数值")
    @JsonProperty("Value")
    private String value;

    @ApiModelProperty(value = "属性开关; true 开， false 关")
    @JsonProperty("flg")
    private Boolean flg;
}
