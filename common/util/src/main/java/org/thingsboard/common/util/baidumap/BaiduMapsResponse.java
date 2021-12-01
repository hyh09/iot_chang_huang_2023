package org.thingsboard.common.util.baidumap;

import java.util.HashMap;
import java.util.Map;

public class BaiduMapsResponse {

    static Map<String,String> map = new HashMap<>();;

    public static Map<String,String> success(){
        map.put("status",BaiduMapsResponseEnum.SUCCESS.getCode()+"");
        map.put("msg",BaiduMapsResponseEnum.SUCCESS.getMsg());
        return map;
    }

    public static Map<String,String> error(int code){
        map.put("status",code+"");
        map.put("msg",BaiduMapsResponseEnum.getMsgByCode(code));
        return map;
    }

    public static Map<String,String> error(String msg){
        map.put("status",BaiduMapsResponseEnum.ERROR_500.getCode()+"");
        map.put("msg",msg);
        return map;
    }

}
