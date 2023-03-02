package org.thingsboard.server.dao.dingding.ben.input;

import lombok.Data;

/**
 * Project Name: thingsboard
 * File Name: ParamTextVo
 * Package Name: org.thingsboard.server.dao.dingding.ben.input
 * Date: 2022/6/21 14:47
 * author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
public class ParamTextVo {

    private  String content;


    public ParamTextVo(String content) {
        StringBuffer  stringBuffer = new StringBuffer();
        stringBuffer.append("提示:[").append(content).append("]所有设备离线了，请及时处理!");
        this.content =stringBuffer.toString();
    }

    public ParamTextVo() {
    }
}
