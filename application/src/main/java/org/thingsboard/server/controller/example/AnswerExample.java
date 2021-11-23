package org.thingsboard.server.controller.example;

/**
 * @program: thingsboard
 * @description: 响应示例
 * @author: HU.YUNHUI
 * @create: 2021-11-19 14:37
 **/
public interface AnswerExample {

   //能耗历史的表头
    public  static  String queryEnergyHistoryHeader="[\"设备名称\",\"总耗水量 (T)\",\"总耗电量 (KWH)\",\"总耗气量 (T)\",\"createTime\"]";

    /**
     * 能耗的历史数据
     */
    public  static String  queryEnergyHistory_messg="{\"data\":[{\"总耗电量 (KWH)\":77000,\"设备名称\":\"电脑设备05\",\"总耗气量 (T)\":70500,\"createTime\":1594167803353,\"总耗水量 (T)\":83500}],\"totalPages\":1000,\"totalElements\":1000,\"hasNext\":true}";


    String queryEntityByKeysHeader="[\"设备名称\",\"水 (T)\",\"电 (KWH)\",\"气 (T)\",\"单位能耗能耗水 (T)\",\"单位能耗能耗电 (KWH)\",\"单位能耗能耗气 (T)\"]";

}
