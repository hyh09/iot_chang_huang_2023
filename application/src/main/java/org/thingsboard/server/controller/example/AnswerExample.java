package org.thingsboard.server.controller.example;

/**
 * @program: thingsboard
 * @description: 响应示例
 * @author: HU.YUNHUI
 * @create: 2021-11-19 14:37
 **/
public interface AnswerExample {

    /**
     * 能耗的历史数据
     */
    public  static String  queryEnergyHistory_messg="{\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"time1\": 2728802,\n" +
            "            \"水\": 89987,\n" +
            "            \"气\": 76987,\n" +
            "            \"电\": 83487,\n" +
            "            \"ts\": 1637281403407\n" +
            "        },\n" +
            "        {\n" +
            "            \"time1\": 2728817,\n" +
            "            \"水\": 89990,\n" +
            "            \"气\": 76990,\n" +
            "            \"电\": 83490,\n" +
            "            \"ts\": 1637290799086\n" +
            "        }\n" +
            "    ],\n" +
            "    \"totalPages\": 1,\n" +
            "    \"totalElements\": 2,\n" +
            "    \"hasNext\": false\n" +
            "}";
}
