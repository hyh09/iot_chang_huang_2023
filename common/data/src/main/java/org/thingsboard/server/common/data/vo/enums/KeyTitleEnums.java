package org.thingsboard.server.common.data.vo.enums;

/**
 * @program: thingsboard
 * @description: 用于分类统计
 * @author: HU.YUNHUI
 * @create: 2021-12-08 16:18
 **/
public enum KeyTitleEnums {
    key_water("水","耗水量","1","1"), //水
    key_gas("气","耗气量","2","3"),//气
    key_cable("电","耗电量","3","2"),//电
   key_capacity("产量","总产量","0",""),//总产能



    ;

    /**
     * 简称
     */
    private  String abbreviationName;

    /**
     * 分组的名称
     */
    private  String gName;

    private  String code;

    /**
     * Pc端的入参
     */
    private  String pcCode;

    KeyTitleEnums(String  abbreviationName,String gName, String code,String pcCode) {
        this.abbreviationName =abbreviationName;
        this.gName = gName;
        this.code = code;
        this.pcCode=pcCode;
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






    /********************************
     * 为了兼容Pc端
     * pc端的入参 前端传的 不按KeyTitleEnums code来
     * @param pcCode
     * @return
     */
    public static  KeyTitleEnums getEnumsByPCCode(String  pcCode)
    {


        for(KeyTitleEnums enums:KeyTitleEnums.values())
        {
            if(pcCode.equals(enums.getPcCode()))
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


    public String getPcCode() {
        return pcCode;
    }

    public void setPcCode(String pcCode) {
        this.pcCode = pcCode;
    }

    public String getAbbreviationName() {
        return abbreviationName;
    }

    public void setAbbreviationName(String abbreviationName) {
        this.abbreviationName = abbreviationName;
    }
}
