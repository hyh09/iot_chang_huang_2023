package org.thingsboard.server.common.data.vo.bodrd;

import lombok.Data;

/**
 * @program: thingsboard
 * @description: 仪表盘的返回对象
 * @author: HU.YUNHUI
 * @create: 2022-03-08 09:09
 **/
@Data
public class DashboardV3Vo {

    /**
     * 单位
     */
    private  String unit;

    private  String name;
    /**
     * 标准值
     * 直接取字典的额定值就可以
     */
    private String standardValue;
    /**
     * 实际值
     */
    private  String actualValue;

    /**
     * 自定义key
     */
    private String key;
}
