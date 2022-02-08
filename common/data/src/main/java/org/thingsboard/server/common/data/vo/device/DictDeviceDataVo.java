package org.thingsboard.server.common.data.vo.device;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: thingsboard
 * @description: 属性组-分组下的属性
 * @author: HU.YUNHUI
 * @create: 2021-11-11 19:06
 **/
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "提供app端--【属性组-分组下的属性】 数据库返回")
public class DictDeviceDataVo {

    //组name
    private  String  groupName;

    //分组下面的name
    private  String  name;

    private String  title;

    private  String unit;

}
