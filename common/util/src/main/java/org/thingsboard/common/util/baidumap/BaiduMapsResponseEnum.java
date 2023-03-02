package org.thingsboard.common.util.baidumap;

public enum BaiduMapsResponseEnum {

    SUCCESS(0,"ok","正常"),
    ERROR_1(1,"","服务器内部错误"),
    ERROR_2(2,"Parameter Invalid","请求参数非法"),
    ERROR_3(3,"Verify Failure","权限校验失败"),
    ERROR_4(4,"Quota Failure","配额校验失败"),
    ERROR_5(5,"AK Failure","ak不存在或者非法"),
    ERROR_101(101,"","服务禁用"),
    ERROR_102(102,"","不通过白名单或者安全码不对"),
    ERROR_500(500,"","系统异常");;


    int code;
    String englishMsg;
    String msg;
    BaiduMapsResponseEnum(int code,String englishMsg,String msg){
        this.code = code;
        this.englishMsg = englishMsg;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getEnglishMsg() {
        return englishMsg;
    }

    public String getMsg() {
        return msg;
    }

    static String getMsgByCode(int code){
        for (BaiduMapsResponseEnum responseEnum : BaiduMapsResponseEnum.values()){
            if(responseEnum.code == code){
                return responseEnum.getMsg();
            }
        }
        return null;
    }
}
