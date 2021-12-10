package org.thingsboard.server.common.data.vo.enums;

/**
 * @program: thingsboard
 * @description: 用于分类统计
 * @author: HU.YUNHUI
 * @create: 2021-12-08 16:18
 **/
public enum KeyTitleEnums {
    key_water("耗水量"), //产能
    key_gas("耗气量"),//能耗
    key_cable("耗电量"),//能耗

    ;

    /**
     * 分组的名称
     */
    private  String gName;


    KeyTitleEnums(String gName) {
        this.gName = gName;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }
}
