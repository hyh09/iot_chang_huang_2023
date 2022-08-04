package org.thingsboard.server.dao.token;

public enum TokenPreEnums {
    APP_TOKEN_01("USER_TOKEN:IOTSTD:APP:"),
    PC_TOKEN_00("USER_TOKEN:IOTSTD:PC:")
    ;

    private  String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    TokenPreEnums(String key) {
        this.key = key;
    }
}
