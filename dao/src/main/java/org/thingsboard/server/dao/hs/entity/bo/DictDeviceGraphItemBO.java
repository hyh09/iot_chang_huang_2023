package org.thingsboard.server.dao.hs.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.thingsboard.server.dao.hs.entity.enums.DictDevicePropertyTypeEnum;

import java.util.UUID;

/**
 * 设备字典图表分项 BO
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class DictDeviceGraphItemBO {

    /**
     * 属性名称
     */
    private String name;

    /**
     * 属性标题
     */
    private String title;

    /**
     * 属性Id
     */
    private UUID propertyId;

    /**
     * 属性类型
     */
    private DictDevicePropertyTypeEnum propertyTypeEnum;
}
