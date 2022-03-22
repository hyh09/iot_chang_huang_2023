package org.thingsboard.common.util.baidumap;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.thingsboard.common.util.JacksonUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * 百度地图SDK
 */
@Slf4j
public class BaiduMaps {
    //百度地图秘钥
    private static final String AK ="T7gsdb6Kw14sYLWrY3TOSTNmXZdmXI3z";
    private static Map<String,String> map = new HashMap<>();
    //经度
    private static final String LONGITUDE = "longitude";
    //纬度
    private static final String LATITUDE = "latitude";

    /**
     * 调用百度地图API.根据地址获取坐标
     * 地理编码服务
     * 用户可通过该功能，将结构化地址（省/市/区/街道/门牌号）解析为对应的位置坐标。地址结构越完整，地址内容越准确，解析的坐标精度越高。
     * 地理编码服务当前未推出国际化服务，解析地址仅限国内；
     * @param address
     * @return
     */
    public static Map<String,String> getCoordinate(String address) {
        log.info("地址："+address);
        map = BaiduMapsResponse.success();
        map.put(LONGITUDE,"");
        map.put(LATITUDE,"");
        if(!StringUtils.isEmpty(address)){
            //地理编码服务API
            String url = "https://api.map.baidu.com/geocoding/v3/?address="+address+"&output=json&ak="+AK;
            String jsonStr = loadJSON(url);
            log.info("百度地图API返回信息："+jsonStr);

            if(!StringUtils.isEmpty(jsonStr)){
                JsonNode jsonNode = JacksonUtil.toJsonNode(jsonStr);
                String status = jsonNode.get("status").toString();
                if("0".equals(status)){
                    JsonNode jsonNodeLocation = jsonNode.get("result").get("location");
                    if(jsonNodeLocation != null){
                        //经度值
                        JsonNode jsonNodeLng = jsonNodeLocation.get("lng");
                        //纬度值
                        JsonNode jsonNodeLat = jsonNodeLocation.get("lat");
                        if(jsonNodeLng != null){
                            log.info("经度值："+JacksonUtil.toString(jsonNodeLng));
                            map.put(LONGITUDE,JacksonUtil.toString(jsonNodeLng));
                        }
                        if(jsonNodeLat != null){
                            log.info("纬度值："+JacksonUtil.toString(jsonNodeLat));
                            map.put(LATITUDE,JacksonUtil.toString(jsonNodeLat));
                        }
                    }
                }else {
                    return BaiduMapsResponse.error(Integer.parseInt(status));
                }
            }else {
                return BaiduMapsResponse.error("地址为空！");
            }
        }
        return map;
    }

    /**
     * 地点检索-行政区划查询
     * 检索某一行政区划内（目前最细到城市级别）的地点信息。
     * @param query 必填 检索关键字。行政区划区域检索不支持多关键字检索。
     * 如果需要按POI分类进行检索，请将分类通过query参数进行设置，如query=美食
     * @param address 检索分类偏好，与q组合进行检索，多个分类以","分隔
     * （POI分类），如果需要严格按分类检索，请通过query参数设置
     * @return
     */
    public static Map<String,String> getBoroughList(String query,String address) {
        log.info("搜索条件："+address);
        map = BaiduMapsResponse.success();
        map.put(LONGITUDE,"");
        map.put(LATITUDE,"");
        if(!StringUtils.isEmpty(address)){
            String url ="https://api.map.baidu.com/place/v2/search?query="+query+"&tag=省,省级城市,地级市,区县,商圈,乡镇,村庄,其他&region="+address+"&output=json&ak="+AK;
            String jsonStr = loadJSON(url);
            log.info("百度地图API返回信息："+jsonStr);

            if(!StringUtils.isEmpty(jsonStr)){
                JsonNode jsonNode = JacksonUtil.toJsonNode(jsonStr);
                String status = jsonNode.get("status").toString();
                if("0".equals(status)){
                    JsonNode jsonNodeLocation = jsonNode.get("result").get("location");
                    if(jsonNodeLocation != null){
                        //经度值
                        JsonNode jsonNodeLng = jsonNodeLocation.get("lng");
                        //纬度值
                        JsonNode jsonNodeLat = jsonNodeLocation.get("lat");
                        if(jsonNodeLng != null){
                            log.info("经度值："+JacksonUtil.toString(jsonNodeLng));
                            map.put(LONGITUDE,JacksonUtil.toString(jsonNodeLng));
                        }
                        if(jsonNodeLat != null){
                            log.info("纬度值："+JacksonUtil.toString(jsonNodeLat));
                            map.put(LATITUDE,JacksonUtil.toString(jsonNodeLat));
                        }
                    }
                }else {
                    return BaiduMapsResponse.error(Integer.parseInt(status));
                }
            }else {
                return BaiduMapsResponse.error("地址为空！");
            }
        }
        return map;
    }

    public static String loadJSON(String url){
        StringBuilder json = new StringBuilder();
        try {
            URL newUrl = new URL(url);
            URLConnection connection = newUrl.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            String inputLine = null;
            while ((inputLine = in.readLine()) != null){
                json.append(inputLine);
            }
            in.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }

}
