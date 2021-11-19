package org.thingsboard.server.controller.example;

/**
 * @program: thingsboard
 * @description: 响应示例
 * @author: HU.YUNHUI
 * @create: 2021-11-19 14:37
 **/
public interface AnswerExample {

    public  static String  queryEnergyHistory_messg="{\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"time1\": 2726400,\n" +
            "            \"水\": 0,\n" +
            "            \"气\": 0,\n" +
            "            \"电\": 0,\n" +
            "            \"ts\": 1635840000000\n" +
            "        }\n" +
            "    ],\n" +
            "    \"totalPages\": 1,\n" +
            "    \"totalElements\": 1,\n" +
            "    \"hasNext\": false\n" +
            "}";
}
