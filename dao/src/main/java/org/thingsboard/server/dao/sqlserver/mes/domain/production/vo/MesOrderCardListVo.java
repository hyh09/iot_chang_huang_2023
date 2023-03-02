package org.thingsboard.server.dao.sqlserver.mes.domain.production.vo;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 生产卡列表
 *
 * @author 范王勇
 */
@Data
@ApiModel(value = "MesOrderCardListVo", description = "生产卡列表")
public class MesOrderCardListVo {

    @JsonProperty("sCardNo")
    @ApiModelProperty("卡号")
    private String sCardNo;

    @JsonProperty("sOrderNo")
    @ApiModelProperty("订单号")
    private String sOrderNo;

    @JsonProperty("sMaterialName")
    @ApiModelProperty("品名")
    private String sMaterialName;

    @JsonProperty("sColorName")
    @ApiModelProperty("颜色")
    private String sColorName;

    @JsonProperty("tCreateTime")
    @ApiModelProperty("创建时间")
    private Date tCreateTime;

}
