package org.thingsboard.server.hs.entity.vo;

import lombok.*;
import lombok.experimental.Accessors;
import org.thingsboard.server.hs.entity.enums.DictDataTypeEnum;

/**
 * 设备字典页面请求
 *
 * @author wwj
 * @since 2021.10.22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DictDeviceListQuery extends PageAndSortQuery{
    /**
     * 编码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 供应商
     */
    private String supplier;
}
