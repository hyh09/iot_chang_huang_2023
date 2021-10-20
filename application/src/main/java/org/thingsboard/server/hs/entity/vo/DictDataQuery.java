package org.thingsboard.server.hs.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.thingsboard.server.hs.entity.enums.DictDataType;

/**
 * 数据字典
 */
@Data
@Accessors(chain = true)
public class DictDataQuery {
    /**
     * 数据字典Id,null则为新增
     */
    private String id;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private DictDataType type;

    /**
     * 单位
     */
    private String unit;

    /**
     * 备注
     */
    private String comment;

    /**
     * 图标
     */
    private String icon;

    /**
     * 图片
     */
    private String picture;
}
