package org.thingsboard.server.dao.hs.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * 分页请求
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Accessors(chain = true)
public class PageQuery {
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
}
