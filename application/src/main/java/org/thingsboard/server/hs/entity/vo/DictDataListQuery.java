package org.thingsboard.server.hs.entity.vo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.thingsboard.server.hs.entity.enums.DictDataDataTypeEnum;

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
public class DictDataListQuery extends PageAndSortQuery{
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
    private DictDataDataTypeEnum dictDataType;
}
