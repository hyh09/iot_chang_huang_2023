package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * 分页及排序请求
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Accessors(chain = true)
@ApiModel("分页及排序请求参数")
public class PageAndSortQuery {
    /**
     * 页数
     */
    @NotNull
    @ApiModelProperty(value = "页数")
    private int page;

    /**
     * 每页大小
     */
    @NotNull
    @ApiModelProperty(value = "每页大小")
    private int pageSize;

    /**
     * 排序属性
     */
    @ApiModelProperty(value = "排序属性")
    private String sortProperty;
    /**
     * 排序顺序
     */
    @ApiModelProperty(value = "排序顺序")
    private String sortOrder;
}
