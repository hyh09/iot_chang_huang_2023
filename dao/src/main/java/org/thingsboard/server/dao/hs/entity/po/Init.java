package org.thingsboard.server.dao.hs.entity.po;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 初始化数据
 *
 * @author wwj
 * @since 2021.11.18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "初始化数据")
public class Init extends BasePO {

    private static final long serialVersionUID = 4934987555236873700L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "范围")
    private String scope;

    @ApiModelProperty(value = "初始化数据")
    private JsonNode initData;
}
