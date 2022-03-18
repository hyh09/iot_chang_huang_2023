package org.thingsboard.server.common.data.vo.user.enums;

/**
 * @program: thingsboard
 * @description: 用户等级标识
 * @author: HU.YUNHUI
 * @create: 2022-02-10 15:48
 **/
public enum UserLeveEnums {

    /**
     * 0为默认
     * 1为工厂管理员角色
     * 3为租户管理员角色
     * 4为 用户系统管理员
     */
   DEFAULT_VALUE(0,"默认的数据",true),//普通用户
   FACTORY_ADMIN(1,"工厂管理员",false),
  TENANT_ADMIN(3,"租户管理员",false),
  USER_SYSTEM_ADMIN(4,"用户系统管理员",true),//目前实际交付出去的账号
    ;

    private  int code;

    private  String name;

    private  Boolean enableCan;

    UserLeveEnums(int code, String name,Boolean enableCan) {
        this.code = code;
        this.name = name;
        this.enableCan =enableCan;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Boolean getEnableCan() {
        return enableCan;
    }

    public void setEnableCan(Boolean enableCan) {
        this.enableCan = enableCan;
    }


    public  static  Boolean  getEnableCanByCode(Integer code)
    {
        for(UserLeveEnums enums:UserLeveEnums.values())
        {
            if(code ==enums.getCode())
            {
                return  enums.getEnableCan();
            }
        }
        return  false;
    }


}
