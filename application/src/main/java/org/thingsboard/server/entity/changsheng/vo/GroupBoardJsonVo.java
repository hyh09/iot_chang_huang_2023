package org.thingsboard.server.entity.changsheng.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.thingsboard.server.common.data.vo.bodrd.DashboardV3Vo;
import org.thingsboard.server.common.data.vo.bodrd.TodaySectionHistoryVo;
import org.thingsboard.server.common.data.vo.tskv.ConsumptionTodayVo;
import org.thingsboard.server.common.data.vo.tskv.TrendChart02Vo;
import org.thingsboard.server.dao.hs.entity.vo.DeviceOnlineStatusResult;
import org.thingsboard.server.dao.hs.entity.vo.OrderCustomCapacityResult;
import org.thingsboard.server.dao.sql.role.entity.BoardV3DeviceDitEntity;
import org.thingsboard.server.entity.productioncalender.vo.ProductionMonitorListVo;
import org.thingsboard.server.entity.statisticoee.vo.StatisticOeeVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户定制化JSON格式数据  集团看板
 * (这里未遵循驼峰命名法则，因客户特殊要求)
 */
@Data
@ApiModel("GroupBoardJsonVo")
public class GroupBoardJsonVo {

    @ApiModelProperty("总产量")
    private TotalProduction TotalProduction;
    @ApiModelProperty("设备综合")
    private EquipmentOverview EquipmentOverview;
    @ApiModelProperty("设备OEE")
    private EquipmentOEE EquipmentOEE;
    @ApiModelProperty("生产监控")
    private ProductionMonitor ProductionMonitor;
    @ApiModelProperty("订单监控")
    private OrderMonitor OrderMonitor;
    @ApiModelProperty("设备类型列表")
    private List<EquipmentTypes> EquipmentTypes;


    /**
     * 设备综合实体
     */
    @Data
    public class EquipmentOverview {
        @ApiModelProperty("在线设备")
        private Integer EquipmentOnline;
        @ApiModelProperty("离线设备")
        private Integer EquipmentOffline;

        public EquipmentOverview() {
        }

        public EquipmentOverview(DeviceOnlineStatusResult result) {
            if (result != null) {
                this.EquipmentOnline = result.getOnLineDeviceCount();
                this.EquipmentOffline = result.getOffLineDeviceCount();
            }
        }
    }

    /**
     * 设备OEE实体
     */
    @Data
    public class EquipmentOEE {

        @ApiModelProperty("设备OEE")
        private List<KeyValue> EquipmentOEE;

        public EquipmentOEE() {
        }

        public EquipmentOEE(List<StatisticOeeVo> statisticOeeListByRealTime) {
            EquipmentOEE = new ArrayList<KeyValue>();
            if (CollectionUtils.isNotEmpty(statisticOeeListByRealTime)) {
                statisticOeeListByRealTime.forEach(i -> EquipmentOEE.add(new GroupBoardJsonVo().new KeyValue(i)));
            } else {
                EquipmentOEE.add(new KeyValue());
            }
        }
    }

    /**
     * 生产监控实体
     */
    @Data
    public class ProductionMonitor {
        @ApiModelProperty("生产监控")
        private List<ProductionMonitorSon> ProductionMonitor;

        public ProductionMonitor(List<ProductionMonitorListVo> productionMonitorenantList) {
            ProductionMonitor = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(productionMonitorenantList)) {
                productionMonitorenantList.forEach(i -> ProductionMonitor.add(new GroupBoardJsonVo().new ProductionMonitorSon(i)));
            }
        }

        public ProductionMonitor() {
        }

    }

    @Data
    public class ProductionMonitorSon {
        @ApiModelProperty("工厂名称")
        private String Name;
        @ApiModelProperty("完成量/计划量")
        private String CountCompletedAndPlanned;
        @ApiModelProperty("产品达成率")
        private String Percentage;
        @ApiModelProperty("状态（正常-0/异常-1）")
        private String State;

        public ProductionMonitorSon() {
        }

        public ProductionMonitorSon(ProductionMonitorListVo vo) {
            if (vo != null) {
                this.Name = vo.getFactoryName();
                this.CountCompletedAndPlanned = vo.getAchieveOrPlan();
                this.Percentage = vo.getYearAchieve();
                if (vo.getProductionState() != null) {
                    this.State = vo.getProductionState() ? "1" : "0";
                }
            }
        }
    }

    /**
     * 订单监控实体
     */
    @Data
    public class OrderMonitor {
        @ApiModelProperty("订单监控")
        private List<OrderMonitorSon> OrderMonitor;

        public OrderMonitor() {
        }

        public OrderMonitor(List<OrderCustomCapacityResult> orders) {
            OrderMonitor = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(orders)) {
                orders.forEach(i -> OrderMonitor.add(new GroupBoardJsonVo().new OrderMonitorSon(i)));
            }
        }
    }

    @Data
    public class OrderMonitorSon {
        @ApiModelProperty("订单号")
        private String Id;
        @ApiModelProperty("工厂名")
        private String FactoryName;
        @ApiModelProperty("完成数/总数量 ")
        private String CompletedDividedByTotal;
        @ApiModelProperty("完成率")
        private String CompletedPercentage;
        @ApiModelProperty("是否超时（0-正常，1-超时）")
        private Integer IsOvertime;

        public OrderMonitorSon() {
        }

        public OrderMonitorSon(OrderCustomCapacityResult vo) {
            if (vo != null) {
                this.Id = vo.getOrderNo();
                this.FactoryName = vo.getFactoryName();
                this.CompletedDividedByTotal = vo.getCompletedCapacities() + "/" + vo.getTotal();
                this.CompletedPercentage = vo.getCompleteness() + "";
                if (vo.getIsOvertime() != null) {
                    this.IsOvertime = vo.getIsOvertime() ? 1 : 0;
                }
            }
        }
    }

    /**
     * 设备类型列表实体
     */
    @Data
    public class EquipmentTypes {
        @ApiModelProperty("设备类型名")
        private String EquipmentTypeName;

        /**
         * 仪表盘水电气
         **/
        @ApiModelProperty("单位能耗排行_水，从大到小")
        private DashBoardData DashBoardData_Water;
        @ApiModelProperty("仪表盘数据_电")
        private DashBoardData DashBoardData_Electricity;
        @ApiModelProperty("仪表盘数据_气")
        private DashBoardData DashBoardData_Gas;


        /**
         * 能耗排行
         **/
        @ApiModelProperty("单位能耗排行_水，从大到小")
        private List<Equipments> EquipmentConsumptionRank_Water;
        @ApiModelProperty("单位能耗排行_电，从大到小")
        private List<Equipments> EquipmentConsumptionRank_Electricity;
        @ApiModelProperty("单位能耗排行_气，从大到小")
        private List<Equipments> EquipmentConsumptionRank_Gas;

        /**
         * 能耗趋势
         **/
        @ApiModelProperty("单位能耗水趋势")
        private List<KeyValue> EnergyConsumptionTrend_Water;
        @ApiModelProperty("单位能耗电趋势")
        private List<KeyValue> EnergyConsumptionTrend_Electricity;
        @ApiModelProperty("单位能耗气趋势")
        private List<KeyValue> EnergyConsumptionTrend_Gas;

        public EquipmentTypes() {
        }

        public EquipmentTypes(BoardV3DeviceDitEntity entity) {
            if (entity != null) {
                this.EquipmentTypeName = entity.getName();
            }
        }

        /**
         * 仪表盘水电气
         *
         * @param queryDashboardValue
         */
        public void setDashBoardData(List<DashboardV3Vo> queryDashboardValue) {
            final String WATER = "水";
            final String ELECTRICITY = "电";
            final String GAS = "气";
            if (CollectionUtils.isNotEmpty(queryDashboardValue)) {
                queryDashboardValue.forEach(i -> {
                    switch (i.getName()) {
                        case WATER:
                            DashBoardData_Water = new DashBoardData(i.getStandardValue(), i.getActualValue(), i.getKey());
                            break;
                        case ELECTRICITY:
                            DashBoardData_Electricity = new DashBoardData(i.getStandardValue(), i.getActualValue(), i.getKey());
                            break;
                        case GAS:
                            DashBoardData_Gas = new DashBoardData(i.getStandardValue(), i.getActualValue(), i.getKey());
                            break;
                    }
                });
            }
        }

        /**
         * 能耗排行
         *
         * @param vo
         */
        public void setEnergyConsumptionTrend(ConsumptionTodayVo vo) {
            EquipmentConsumptionRank_Water = new ArrayList<>();
            EquipmentConsumptionRank_Electricity = new ArrayList<>();
            EquipmentConsumptionRank_Gas = new ArrayList<>();
            if (vo != null) {
                if (CollectionUtils.isNotEmpty(vo.getWaterList())) {
                    vo.getWaterList().forEach(i -> {
                        EquipmentConsumptionRank_Water.add(new Equipments(i.getDeviceName(), i.getValue(), i.getFactoryName()));
                    });
                }
                if (CollectionUtils.isNotEmpty(vo.getElectricList())) {
                    vo.getElectricList().forEach(i -> {
                        EquipmentConsumptionRank_Electricity.add(new Equipments(i.getDeviceName(), i.getValue(), i.getFactoryName()));
                    });
                }
                if (CollectionUtils.isNotEmpty(vo.getGasList())) {
                    vo.getGasList().forEach(i -> {
                        EquipmentConsumptionRank_Gas.add(new Equipments(i.getDeviceName(), i.getValue(), i.getFactoryName()));
                    });
                }
            }
        }

        /**
         * 能耗趋势-水
         *
         * @param vo
         */
        public void setTrendChart02VoWater(TrendChart02Vo vo) {
            EnergyConsumptionTrend_Water = new ArrayList<>();
            if (vo != null && CollectionUtils.isNotEmpty(vo.getSolidLine())) {
                vo.getSolidLine().forEach(i -> {
                    EnergyConsumptionTrend_Water.add(new KeyValue(i.getTime() + "", i.getValue()));
                });
            }
        }

        /**
         * 能耗趋势-电
         *
         * @param vo
         */
        public void setTrendChart02VoElectricity(TrendChart02Vo vo) {
            EnergyConsumptionTrend_Electricity = new ArrayList<>();
            if (vo != null && CollectionUtils.isNotEmpty(vo.getSolidLine())) {
                vo.getSolidLine().forEach(i -> {
                    EnergyConsumptionTrend_Water.add(new KeyValue(i.getTime() + "", i.getValue()));
                });
            }
        }

        /**
         * 能耗趋势-气
         *
         * @param vo
         */
        public void setTrendChart02VoGas(TrendChart02Vo vo) {
            EnergyConsumptionTrend_Gas = new ArrayList<>();
            if (vo != null && CollectionUtils.isNotEmpty(vo.getSolidLine())) {
                vo.getSolidLine().forEach(i -> {
                    EnergyConsumptionTrend_Water.add(new KeyValue(i.getTime() + "", i.getValue()));
                });
            }
        }
    }

    /**
     * 对应仪表盘数据实体
     */
    @Data
    public class DashBoardData {
        @ApiModelProperty("仪表盘最大值，值为标准单位能耗*随机数（1.5-2）")
        private String MaxValue;
        @ApiModelProperty("当前值")
        private String CurrentValue;
        /**
         * 自定义key
         */
        private String key;

        public DashBoardData() {
        }

        public DashBoardData(String maxValue, String currentValue, String key) {
            MaxValue = maxValue;
            CurrentValue = currentValue;
            key = key;
        }
    }

    /**
     * 对应能耗排行数据实体
     */
    @Data
    public class Equipments {
        @ApiModelProperty("设备名")
        private String EquipmentName;
        @ApiModelProperty("单位能耗")
        private String UnitEnergyConsumption;
        @ApiModelProperty("工厂名")
        private String FactoryName;

        public Equipments(String equipmentName, String unitEnergyConsumption, String factoryName) {
            EquipmentName = equipmentName;
            UnitEnergyConsumption = unitEnergyConsumption;
            FactoryName = factoryName;
        }
    }

    /**
     * 通用实体
     */
    @Data
    public class KeyValue {
        @ApiModelProperty("key")
        private String Key;
        @ApiModelProperty("value")
        private String Value;

        public KeyValue() {
        }

        public KeyValue(String key, String value) {
            Key = key;
            Value = value;
        }

        /**
         * OEE复赋值
         *
         * @param vo
         */
        public KeyValue(StatisticOeeVo vo) {
            if (vo != null) {
                this.Key = vo.getTimeHours() + "";
                this.Value = vo.getOeeValue() + "";
            }
        }
    }

    /**
     * 总产量
     */
    @Data
    public class TotalProduction {
        @ApiModelProperty("今日总产量")
        private String ProductionToday;
        @ApiModelProperty("总产量）")
        private String ProductuinTotal;
        @ApiModelProperty("历史总产量")
        private String HistoryProductionTotal;

        public TotalProduction() {
        }

        public TotalProduction(TodaySectionHistoryVo todaySectionHistoryVo) {
            if (todaySectionHistoryVo != null) {
                this.ProductionToday = todaySectionHistoryVo.getTodayValue();
                this.ProductuinTotal = todaySectionHistoryVo.getSectionValue();
                this.HistoryProductionTotal = todaySectionHistoryVo.getHistoryValue();
            }
        }
    }


}
