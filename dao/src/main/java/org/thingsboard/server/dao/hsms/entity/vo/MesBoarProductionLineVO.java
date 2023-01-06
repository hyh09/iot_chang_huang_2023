package org.thingsboard.server.dao.hsms.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.thingsboard.server.dao.hs.entity.po.BasePO;

import java.util.UUID;

/**
 * 产线VO
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesBoarProductionLineVO {

    /**
     * Id
     */
    @ApiModelProperty(value = "id")
    private UUID id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;
}
