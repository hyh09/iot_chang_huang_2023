package org.thingsboard.server.entity.user;

/**
 * @program: thingsboard
 * @description: 用户/角色初始化枚举类
 * @author: HU.YUNHUI
 * @create: 2021-11-01 16:20
 **/
public enum CodeKeyNum {
    key_user("1","YH","0001","%04d"),

    //1-4为系统生成的默认角色
    key_role("2","","005","%03d");



    public  static  CodeKeyNum  getValueByKey(String key)
    {
        for(CodeKeyNum  num:CodeKeyNum.values())
        {
            if(num.getKey().equals(key))
            {
                return num;
            }
        }
        return  null;
    }



    private  String key;

    private  String value;


    private  String  init;


    private String  checkSing;

    CodeKeyNum(String key, String value, String init, String checkSing) {
        this.key = key;
        this.value = value;
        this.init = init;
        this.checkSing = checkSing;
    }

    public String getCheckSing() {
        return checkSing;
    }

    public void setCheckSing(String checkSing) {
        this.checkSing = checkSing;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getInit() {
        return init;
    }

    public void setInit(String init) {
        this.init = init;
    }


}
