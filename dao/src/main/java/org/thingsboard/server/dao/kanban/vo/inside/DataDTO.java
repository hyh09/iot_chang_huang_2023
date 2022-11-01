package org.thingsboard.server.dao.kanban.vo.inside;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("Key")
    private String key;
    @JsonProperty("Value")
    private String value;
}
