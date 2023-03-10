package org.thingsboard.server.dao.sql.role.service.annotatonsvc;

public enum  MunuTypeEnum {

    APP("systemType","APP"),

    PC("systemType","PC"),
    ;





    private String key;

    private String value;

    MunuTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
