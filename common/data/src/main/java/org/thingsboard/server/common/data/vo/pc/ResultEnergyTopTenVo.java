package org.thingsboard.server.common.data.vo.pc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @program: thingsboard
 * @description: pc端能耗top-10
 * @author: HU.YUNHUI
 * @create: 2022-01-19 17:37
 **/
@Data
@ToString
@ApiModel(value = "pc端能耗top-10出参")
public class ResultEnergyTopTenVo {

    /**
     * 设备的名称
     */
    @ApiModelProperty("设备的名称 ")
    private String deviceName;
    /**
     * 设备的id
     */
    @ApiModelProperty("设备的id ")
    private UUID deviceId;

    @ApiModelProperty("值 ")
    private  String value="0";


    /**大到小*/
    public static List<ResultEnergyTopTenVo> compareToMaxToMin(List<ResultEnergyTopTenVo> list){
        return list.stream().sorted((s1, s2) -> new BigDecimal(s2.getValue()).compareTo(new BigDecimal(s1.getValue()))).collect(Collectors.toList());
    }
}
