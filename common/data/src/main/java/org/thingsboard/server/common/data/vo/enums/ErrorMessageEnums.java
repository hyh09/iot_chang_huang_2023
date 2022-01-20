package org.thingsboard.server.common.data.vo.enums;

import org.thingsboard.server.common.data.StringUtils;

/**
 * @program: thingsboard
 * @description: 系统异常提示
 * @author: HU.YUNHUI
 * @create: 2021-11-30 13:55
 **/

public enum  ErrorMessageEnums {
    SING_ON_USER_FAILED("6"," 身份验证失败 ！","Authentication failed"),
    SING_ON_USER_TOKEN_EXPIRED("7"," 令牌已过期 ！","Token has expired"),
    SING_ON_USER_LOCKED("8"," 由于安全策略，用户帐户被锁定 ！","User account is locked due to security policy"),
    SING_ON_USER_INVALID("9"," 无效的用户名或密码  ！","Invalid username or password"),
    SING_ON_AUTHENTICATION("10"," 用户帐户未激活 ！","User account is not active"),
    PARAMETER_NOT_NULL("11","入参错误！"," Input parameter error "),
    USER_NOT_EXIST("12","用户不存在！"," user does not exist ")
    ;


    /**
     *
     * @param key
     * @param lang  目前就中文和英文
     * @return
     */
    public  static   String  getLanguage(String key,String lang)
    {
        for(ErrorMessageEnums enums:ErrorMessageEnums.values())
        {
            if(enums.getKey().equals(key))
            {
                 if(StringUtils.isEmpty(lang)) {
                     return enums.getCNLanguage();

                 }else if(lang.equals("zh_CN")) {

                     return enums.getCNLanguage();
                 }else {
                     return enums.getEnLanguage();

                 }
            }
        }

        return  null;


    }



    public  static   String  getLanguage(String key)
    {
        for(ErrorMessageEnums enums:ErrorMessageEnums.values())
        {
            if(enums.getKey().equals(key))
            {
                return  enums.getCNLanguage();
            }
        }

        return  key;


    }


    ErrorMessageEnums(String key, String CNLanguage, String enLanguage) {
        this.key = key;
        this.CNLanguage = CNLanguage;
        EnLanguage = enLanguage;
    }

    private  String key;
    private  String CNLanguage;
    private  String EnLanguage;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCNLanguage() {
        return CNLanguage;
    }

    public void setCNLanguage(String CNLanguage) {
        this.CNLanguage = CNLanguage;
    }

    public String getEnLanguage() {
        return EnLanguage;
    }

    public void setEnLanguage(String enLanguage) {
        EnLanguage = enLanguage;
    }
}
