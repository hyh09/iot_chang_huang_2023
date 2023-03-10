package org.thingsboard.server.common.data.vo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: thingsboard
 * @description: 设备字典属性
 * @author: HU.YUNHUI
 * @create: 2021-11-25 13:26
 **/
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "设备字典属性")
public class DeviceDictionaryPropertiesVo {

    @ApiModelProperty("名称")
    private  String name;

    /**
     * 如果标题为空取的是名称来的;
     */
    @ApiModelProperty("标题")
    private String title="";
    @ApiModelProperty("单位")
    private  String unit="";

//    @ApiModelProperty("对应的标题下的属性")
//    private List<String> nameByProperty;




}
