package org.thingsboard.server.common.data.vo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @program: thingsboard
 * @description: 属性组-分组下的属性
 * @author: HU.YUNHUI
 * @create: 2021-11-11 19:06
 **/
@Data
@ToString
//@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "提供app端--【属性组-分组下的属性】 数据库返回")
public class DictDeviceDataVo {

    //组name
    private  String  groupName;

    //分组下面的name
    @ApiModelProperty("属性的name ; #图表的时候为空")
    private  String  name;

    @ApiModelProperty("标题; 如果是图表就是图表的名称")
    private String  title;

    @ApiModelProperty("单位;(图表的单位是取第一个不为空的属性的单位)")
    private  String unit="";

    @ApiModelProperty("图表的id;先返回起")
    private String  chartId;

    @ApiModelProperty("当前的标题(或者图表下)下的属性值")
    private List<String> attributeNames;


    private Boolean  enable;




    public DictDeviceDataVo(String groupName, String name, String title, String unit) {
        this.groupName = groupName;
        this.name = name;
        this.title = title;
        this.unit = unit;
    }



}
