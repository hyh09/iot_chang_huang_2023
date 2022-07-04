package org.thingsboard.server.common.data.energyboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EnergyBoard {

    @ApiModelProperty("设备数量")
    private Integer deviceTotal;

    @ApiModelProperty("在线数量")
    private Integer onLineTotal;

    @ApiModelProperty("离线数量")
    private Integer offLineTotal;

    @ApiModelProperty("开机数量")
    private Integer startUpTotal;

    @ApiModelProperty("开机率")
    private BigDecimal startUpPercentages;

    @ApiModelProperty("日电能耗")
    private BigDecimal todayElectricity;

    @ApiModelProperty("昨日电能耗")
    private BigDecimal yesterdayElectricity;

    @ApiModelProperty("日电能耗比")
    private BigDecimal dayElectricityPercentages;

    @ApiModelProperty("日水能耗")
    private BigDecimal todaywater;

    @ApiModelProperty("昨日水电能耗")
    private BigDecimal yesterdaywater;

    @ApiModelProperty("日水能耗比")
    private BigDecimal daywWterPercentages;

    @ApiModelProperty("日燃气能耗")
    private BigDecimal todayFuelGas;

    @ApiModelProperty("昨日燃气能耗")
    private BigDecimal yesterdayFuelGas;

    @ApiModelProperty("日燃气耗比")
    private BigDecimal dayFuelGasPercentages;

    public EnergyBoard(){}

    public void setEnergy(BigDecimal todayElectricity,
                       BigDecimal yesterdayElectricity,
                       BigDecimal dayElectricityPercentages,
                       BigDecimal todaywater,
                       BigDecimal yesterdaywater,
                       BigDecimal daywWterPercentages,
                       BigDecimal todayFuelGas,
                       BigDecimal yesterdayFuelGas,
                       BigDecimal dayFuelGasPercentages) {
        this.todayElectricity = todayElectricity;
        this.yesterdayElectricity = yesterdayElectricity;
        this.dayElectricityPercentages = dayElectricityPercentages;
        this.todaywater = todaywater;
        this.yesterdaywater = yesterdaywater;
        this.daywWterPercentages = daywWterPercentages;
        this.todayFuelGas = todayFuelGas;
        this.yesterdayFuelGas = yesterdayFuelGas;
        this.dayFuelGasPercentages = dayFuelGasPercentages;
    }

    public void setSwitch(Integer startUpTotal, BigDecimal startUpPercentages) {
        this.startUpTotal = startUpTotal;
        this.startUpPercentages = startUpPercentages;
    }
}
