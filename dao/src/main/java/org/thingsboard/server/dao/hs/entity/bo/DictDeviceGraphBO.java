package org.thingsboard.server.dao.hs.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

/**
 * 设备字典图表 BO
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class DictDeviceGraphBO implements Graph {
    /**
     * 图表Id
     */
    private UUID id;

    /**
     * 图表名称
     */
    private String name;

    /**
     * 设备字典Id
     */
    private UUID dictDeviceId;

    /**
     * 是否显示图表
     */
    private Boolean enable;

    /**
     * 属性列表
     */
    private List<DictDeviceGraphItemBO> items;
}
