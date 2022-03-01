package org.thingsboard.server.dao.sql.role.enmus;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2022-01-20 18:34
 **/

public enum  RoleVisibleEnum {

    administrator("administrator","1"),
    guest("guest","1"),



    ;

    public static  String getValueBYName(String  name)
    {


        for(RoleVisibleEnum enums:RoleVisibleEnum.values())
        {
            if(name.equals(enums.getgName()))
            {
                return enums.getCode();
            }
        }
        return  "0";
    }

    /**
     * 分组的名称
     */
    private  String gName;

    private  String code;

    RoleVisibleEnum(String gName, String code) {
        this.gName = gName;
        this.code = code;
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
