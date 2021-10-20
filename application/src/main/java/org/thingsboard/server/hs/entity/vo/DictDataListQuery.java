package org.thingsboard.server.hs.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 数据字典页面请求
 *
 * @author wwj
 * @since 2021.10.19
 */
@Data
@Accessors(chain = true)
public class DictDataListQuery{
    /**
     * 编码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 数据类型
     */
    private String dictDataType;
}
