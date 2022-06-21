package org.thingsboard.server.dao.dingding.ben.input;

import lombok.Data;

/**
 * Project Name: thingsboard
 * File Name: ParamVo
 * Package Name: org.thingsboard.server.dao.dingding.ben.input
 * Date: 2022/6/21 14:46
 * author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
public class ParamVo {

    private  String msgtype="text";

    private  ParamTextVo text;


    public ParamVo() {
    }

    public ParamVo(ParamTextVo text) {
        this.text = text;
    }
}
