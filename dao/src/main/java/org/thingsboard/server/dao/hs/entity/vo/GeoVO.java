package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Geo VO
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Geo VO", description = "Geo VO")
public class GeoVO {

    @ApiModelProperty("城市名")
    private String cityName;

    @ApiModelProperty("国家名")
    private String countryName;

    @ApiModelProperty("经度")
    private String longitude;

    @ApiModelProperty("纬度")
    private String latitude;

    @ApiModelProperty("语言环境")
    private String language;

    @ApiModelProperty("邮政编码")
    private String postcode;

    @ApiModelProperty("显示名称")
    private String displayName;
}
