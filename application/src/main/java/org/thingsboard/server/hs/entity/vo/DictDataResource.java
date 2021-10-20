package org.thingsboard.server.hs.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 数据字典资源
 *
 * @author wwj
 * @since 2021.10.18
 */
@Data
@Accessors(chain = true)
public class DictDataResource {
    /**
     * 数据类型
     */
    private Map<String, String> DictDataTypeMap;
}
