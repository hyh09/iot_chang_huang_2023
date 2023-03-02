package org.thingsboard.server.dao.sqlserver.mes.domain.production.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MesOrderListVo", description = "订单列表返参")
public class MesOrderListVo {

    @ApiModelProperty("订单号")
    private String sOrderNo;
    /*  @ApiModelProperty("所属工厂")
      private String sOrderNo;
      @ApiModelProperty("紧急程度")
      private String sOrderNo;
      @ApiModelProperty("跟单员")
      private String sOrderNo;
      @ApiModelProperty("计划完工日期")
      private String sOrderNo;*/
    @ApiModelProperty("订单类型")
    private String sOrderTypeName;
    @ApiModelProperty("创建人")
    private String sCreator;
    @ApiModelProperty("创建时间")
    private String tCreateTime;

}
