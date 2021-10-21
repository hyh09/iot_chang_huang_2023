package org.thingsboard.server.hs.entity.po;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * 数据字典
 *
 * @author wwj
 * @since 2021.10.18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DictData extends BasePO {

    private static final long serialVersionUID = 4934987555236873700L;
    /**
     * 数据字典Id
     */
    private String id;
    /**
     * 租户Id
     */
    private String tenantId;

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
    private String type;

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
