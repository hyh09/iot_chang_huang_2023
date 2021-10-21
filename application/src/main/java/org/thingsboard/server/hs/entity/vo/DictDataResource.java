package org.thingsboard.server.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 数据字典资源
 *
 * @author wwj
 * @since 2021.10.18
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "数据字典资源")
public class DictDataResource {
    @ApiModelProperty(value = "数据类型Map")
    private Map<String, String> DictDataTypeMap;
}
