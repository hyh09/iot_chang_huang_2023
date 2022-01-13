package org.thingsboard.server.common.data.vo.enums;

/**
 * @program: thingsboard
 * @description: 用于分类统计
 * @author: HU.YUNHUI
 * @create: 2021-12-08 16:18
 **/
public enum KeyTitleEnums {
    key_water("耗水量","1"), //水
    key_gas("耗气量","2"),//气
    key_cable("耗电量","3"),//电


    key_capacity("总产量","0"),//总产能

    ;

    /**
     * 分组的名称
     */
    private  String gName;

    private  String code;

    KeyTitleEnums(String gName, String code) {
        this.gName = gName;
        this.code = code;
    }


    public static  String getNameByCode(String  code)
    {


        for(KeyTitleEnums enums:KeyTitleEnums.values())
        {
            if(code.equals(enums.getCode()))
            {
                return enums.getgName();
            }
        }
        return  null;
    }



    public static  KeyTitleEnums getEnumsByCode(String  code)
    {


        for(KeyTitleEnums enums:KeyTitleEnums.values())
        {
            if(code.equals(enums.getCode()))
            {
                return enums;
            }
        }
        return  null;
    }




    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
