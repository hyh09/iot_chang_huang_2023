package org.thingsboard.server.entity.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("DeviceIssueDto")
public class DeviceIssueDto {

    @ApiModelProperty("协议类型")
    private String type;

    @ApiModelProperty("驱动版本号")
    private String driverVersion;

    @ApiModelProperty("设备信息")
    private List<DeviceFromIssue> deviceList;

    @ApiModelProperty("驱动配置信息")
    private List<DriveConig> driverConfigList;

    @Data
    @ApiModel("DeviceIssueDto.DriveConig")
    public static class DriveConig{
        @ApiModelProperty("点名称")
        private String pointName;

        @ApiModelProperty("数据类型")
        private String dataType;

        @ApiModelProperty("寄存器类型")
        private String registerType;

        @ApiModelProperty("寄存器地址")
        private String registerAddress;

        @ApiModelProperty("长度")
        private String length;

        @ApiModelProperty("运算符")
        private String operator;

        @ApiModelProperty("运算值")
        private String operationValue;

        @ApiModelProperty("读写方向")
        private String readWrite;

        @ApiModelProperty("反转字序")
        private String reverse;

        @ApiModelProperty("小端在前")
        private String littleEndian;

        @ApiModelProperty("设备字典属性参数描述")
        private String description;
        @ApiModelProperty("设备字典属性参数类型")
        private String category;


        public Map savePointMap(){
            //点详细信息
            Map<String,String> pointMap = new HashMap<>();
            pointMap.put("pointName",this.pointName);
            pointMap.put("dataType",this.dataType);
            pointMap.put("registerType",this.registerType);
            pointMap.put("registerAddress",this.registerAddress);
            pointMap.put("length",this.length);
            pointMap.put("operator",this.operator);
            pointMap.put("operationValue",this.operationValue);
            pointMap.put("readWrite",this.readWrite);
            pointMap.put("reverse",this.reverse);
            pointMap.put("littleEndian",this.littleEndian);
            return pointMap;
        }

    }


    @Data
    @ApiModel("DeviceIssueDto.DeviceFromIssue")
    public static class DeviceFromIssue{
        @ApiModelProperty("设备名称")
        private String deviceName;
        @ApiModelProperty("网关标识")
        private String gatewayId;
    }

}
