package org.thingsboard.server.common.transport.mqtt;

import com.google.gson.annotations.Expose;

/**
 * Json 基础实体类
 */
public class BaseMessageModel {

    @Expose
    private int messageID;              // 消息id

    @Expose(serialize = false, deserialize = true)
    private int code;                   // 返回码

    private String topic;               // mqtt topic

    private TYPE type;

    public String getTopic() {
        return topic;
    }

    public int getMessageID() {
        return messageID;
    }

    public int getCode() {
        return code;
    }

    public BaseMessageModel(String topic, TYPE type, int messageID) {
        this.topic = topic;
        this.type = type;
        this.messageID = messageID;
    }

    public BaseMessageModel() {
        messageID = 0;
        topic = "";
        type = TYPE.UNKNOWN;
    }

    public enum TYPE {
        POST_TELEMETRY_REQUEST,
        POST_ATTRIBUTES_REQUEST,
        UNKNOWN
    }

    public enum CODE {
        UNKNOWN,
        OK,
        JSON_ERROR
    }

}
