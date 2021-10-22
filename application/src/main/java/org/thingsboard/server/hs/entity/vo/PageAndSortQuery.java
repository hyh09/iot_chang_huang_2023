package org.thingsboard.server.hs.entity.vo;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

/**
 * 分页及排序请求
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Accessors(chain = true)
public class PageAndSortQuery {
    /**
     * 页数
     */
    @NotNull
    private int page;

    /**
     * 每页大小
     */
    @NotNull
    private int pageSize;

    /**
     * 排序属性
     */
    private String sortProperty;
    /**
     * 排序顺序
     */
    private String sortOrder;
}
