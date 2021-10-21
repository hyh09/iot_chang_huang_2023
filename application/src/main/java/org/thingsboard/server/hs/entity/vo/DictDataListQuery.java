package org.thingsboard.server.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.thingsboard.server.hs.entity.enums.DictDataTypeEnum;

/**
 * 数据字典页面请求
 *
 * @author wwj
 * @since 2021.10.19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Builder
@ApiModel("dwdwd")
public class DictDataListQuery extends PageAndSortQuery{
    /**
     * 编码
     */
    @ApiModelProperty("dwdwd")
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 数据类型
     */
    private DictDataTypeEnum dictDataType;
}
