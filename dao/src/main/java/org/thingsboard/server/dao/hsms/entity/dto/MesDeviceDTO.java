package org.thingsboard.server.dao.hsms.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mes 设备
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesDeviceDTO {

    private static final long serialVersionUID = 4134987555236813704L;

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private String id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "name")
    private String name;
}
