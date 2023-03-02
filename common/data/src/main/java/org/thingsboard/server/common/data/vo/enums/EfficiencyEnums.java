package org.thingsboard.server.common.data.vo.enums;

/**
 * @program: thingsboard
 * @description: 效能分析的枚举-组名
 * @author: HU.YUNHUI
 * @create: 2021-11-11 12:43
 **/

public enum  EfficiencyEnums {
    CAPACITY_001("产量"), //产能
    ENERGY_002("能耗"),//能耗


    ;

    /**
     * 分组的名称
     */
    private  String gName;


    EfficiencyEnums(String gName) {
        this.gName = gName;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }


}
