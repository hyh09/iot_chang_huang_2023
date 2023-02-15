package org.thingsboard.server.dao.hsms.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 设备VO
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesBoarDeviceVO {

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

    /**
     * mes Id
     */
    @ApiModelProperty(value = "mes Id")
    private UUID mesId;

    /**
     * mes 名称
     */
    @ApiModelProperty(value = "mes 名称")
    private String mesName;
}
