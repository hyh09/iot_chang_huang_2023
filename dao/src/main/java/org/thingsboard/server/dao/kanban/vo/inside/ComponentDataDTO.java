package org.thingsboard.server.dao.kanban.vo.inside;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Project Name: thingsboard
 * @File Name: ComponentDataDTO
 * @Date: 2022/11/1 9:51
 * @author: wb04
 * 业务中文描述: /零部件数据
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@ToString
public class ComponentDataDTO {

    /**
     * 零件名
     */
    @JsonProperty("Name")
    private String name;
    /**
     * 零件数据
     */
    @JsonProperty("Data")
    private List<DataDTO> data;

}
