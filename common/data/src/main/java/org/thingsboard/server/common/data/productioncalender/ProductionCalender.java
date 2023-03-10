package org.thingsboard.server.common.data.productioncalender;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.StringUtils;

import java.util.UUID;

@Data
public class ProductionCalender {
    @ApiModelProperty("唯一标识")
    public UUID id;
    @ApiModelProperty("设备标识")
    public UUID deviceId;
    @ApiModelProperty("设备名称")
    public String deviceName;
    @ApiModelProperty("工厂标识")
    public UUID factoryId;
    @ApiModelProperty("工厂名称")
    public String factoryName;
    @ApiModelProperty("开始时间")
    public Long startTime;
    @ApiModelProperty("结束时间")
    public Long endTime;
    @ApiModelProperty(name = "租户")
    public UUID tenantId;
    @ApiModelProperty("创建人标识")
    public UUID createdUser;
    @ApiModelProperty("创建时间")
    public Long createdTime;
    @ApiModelProperty("修改时间")
    public Long updatedTime;
    @ApiModelProperty("修改人")
    public UUID updatedUser;

    /*****************************************************非数据库字段****************************************************************/

    @ApiModelProperty(value = "查询类型（1.集团看板 2.工厂看板，3.车间看板）")
    private Integer qryType;

    @ApiModelProperty("完成量/计划量")
    private String achieveOrPlan;

    @ApiModelProperty("年产能达成率")
    private String yearAchieve;

    @ApiModelProperty("生产状态(true正常  false异常)")
    private Boolean productionState;

    @ApiModelProperty("车间id")
    private UUID workshopId;

    @ApiModelProperty("排序字段")
    String sortProperty;

    @ApiModelProperty("排序值（DESC/ASC）")
    String sortOrder;


    /*********************************************************************************************************************/

    public ProductionCalender(){}

    /**
     * 生产日历分页查询入参
     * @param deviceName
     * @param factoryName
     * @param tenantId
     * @param sortProperty
     * @param sortOrder
     */
    public ProductionCalender(UUID factoryId,String deviceName,String factoryName,UUID tenantId,String sortProperty,String sortOrder) {
        this.factoryId = factoryId;
        this.deviceName = deviceName;
        this.factoryName = factoryName;
        this.tenantId = tenantId;
        this.sortProperty = sortProperty;
        this.sortOrder = sortOrder;
    }

    public ProductionCalender(UUID deviceId){
        this.deviceId = deviceId;
    }

    public ProductionCalender(UUID deviceId,String sortProperty,String sortOrder){
        this.deviceId = deviceId;
        this.sortProperty = sortProperty;
        this.sortOrder = sortOrder;
    }

    /**
     * 看板生产监控数据反参
     * @param deviceName
     * @param achieveOrPlan
     * @param yearAchieve
     * @param productionState
     */
    public ProductionCalender(String deviceName,String achieveOrPlan, String yearAchieve, Boolean productionState) {
        this.deviceName = deviceName;
        this.achieveOrPlan = achieveOrPlan;
        if (StringUtils.isNotEmpty(yearAchieve)){
            this.yearAchieve = (Integer.parseInt(yearAchieve) * 100) + "";
        }
        this.productionState = productionState;
    }

    /**
     * 看板生产监控数据入参
     * @param startTime
     * @param endTime
     * @param tenantId
     */
    public ProductionCalender(Long startTime,Long endTime,UUID factoryId ,UUID workshopId,UUID tenantId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.factoryId = factoryId;
        this.workshopId = workshopId;
        this.tenantId = tenantId;
    }

}
