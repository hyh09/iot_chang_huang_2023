package org.thingsboard.server.common.data.enums;

/**
 * Project Name: thingsboard
 * File Name: AdminSettingsKeyEmuns
 * Package Name: org.thingsboard.server.common.data.enums
 * Date: 2022/6/11 15:06
 * author: wb04
 * 业务中文描述: 系统配置枚举类
 * Copyright (c) 2022,All Rights Reserved.
 */
public enum  AdminSettingsKeyEmuns {

    appStoreCrud("应用商城租户用户同步开关"), //配置的信息:  {"switch":false}

     dingding_webhook("钉钉群机器人url"),
        ;


    private String keyNotes;


    AdminSettingsKeyEmuns(String keyNotes) {
        this.keyNotes = keyNotes;
    }

    public String getKeyNotes() {
        return keyNotes;
    }

    public void setKeyNotes(String keyNotes) {
        this.keyNotes = keyNotes;
    }
}
