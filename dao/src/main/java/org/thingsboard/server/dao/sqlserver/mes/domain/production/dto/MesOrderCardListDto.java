package org.thingsboard.server.dao.sqlserver.mes.domain.production.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 生产卡列表
 * @author 范王勇
 */
@Data
@ApiModel(value = "MesOrderCardListDto", description = "生产卡列表")
public class MesOrderCardListDto {

    @ApiModelProperty("开始日期")
    private String dateBegin;

    @ApiModelProperty("结束日期")
    private String dateEnd;

    @JsonProperty("sOrderNo")
    @ApiModelProperty("订单号")
    private String sOrderNo;

    @JsonProperty("sCardNo")
    @ApiModelProperty("卡号")
    private String sCardNo;

    @JsonProperty("sMaterialName")
    @ApiModelProperty("品名")
    private String sMaterialName;

}
