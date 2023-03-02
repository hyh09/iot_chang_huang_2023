package org.thingsboard.server.dao.hs.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 排序请求
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Accessors(chain = true)
public class SortQuery {
    /**
     * 排序属性
     */
    private String sortProperty;
    /**
     * 排序顺序
     */
    private String sortOrder;
}
