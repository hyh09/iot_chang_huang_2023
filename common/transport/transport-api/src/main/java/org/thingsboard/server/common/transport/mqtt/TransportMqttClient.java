package org.thingsboard.server.common.transport.mqtt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.Builder;
import lombok.Data;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.TbMsgMetaData;
import org.thingsboard.server.common.msg.session.SessionMsgType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
public class TransportMqttClient {

    private final Gson gson = new Gson();
    private static final Logger LOG = LoggerFactory.getLogger(TransportMqttClient.class);
    private final String mqttHost;
    private MqttClient mqttClient;
    private boolean initialized;

    public TransportMqttClient(String mqttHost) {
        this.mqttHost = mqttHost;
        this.mqttClient = null;
        this.initialized = false;
    }

    public boolean isConnected() {
        return this.mqttClient.isConnected();
    }

    /**
     * 连接Mqtt服务端
     *
     * @return 成功true，失败false
     */
    private boolean connectToServer() {
        try {
            if (null == this.mqttClient) {
                MemoryPersistence persist = new MemoryPersistence();
                this.mqttClient = new MqttClient(mqttHost, "yunyun", persist);
            }
            this.mqttClient.connect(getConnectOptions());
        } catch (MqttException ex) {
            LOG.error("connect to mqtt server failed", ex);
            this.mqttClient = null;
            return false;
        }

        return true;
    }

    private MqttConnectOptions getConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        // MQTT 连接选项
        MqttConnectOptions connOpts = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(20);
        //LOG.info("MQTT  getConnectOptions  max inflight :{}", options.getMaxInflight());
        //options.setMaxInflight(1000);
        //已经连接上后断线，系统自动重连
        options.setAutomaticReconnect(true);
        return options;
    }

    public void initialize() {
        if (connectToServer()) {
            initialized = true;
            LOG.info("Mqtt client init successful");

//            try {
//                // 订阅初始化位置 ACK
//                mqttClient.subscribe(
//                        String.format(IExpressMqttTopicDefine.INIT_POSITION_ACK, vehicleName),
//                        0,
//                        new InitPositionCallback(adapter, this));
//            } catch (MqttException ex) {
//                LOG.error(ex.getMessage());
//            }

        } else {
            LOG.info("Mqtt client init failed");
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void terminate() {
        try {
            if (mqttClient.isConnected()) {
                mqttClient.disconnect();
                mqttClient.close();
            }
        } catch (MqttException ex) {
            LOG.error("Adapter terminate mqttclient error", ex);
            return;
        }
        initialized = false;
        LOG.info("Adapter terminate successful");
    }

    public void publish(BaseMessageModel model) throws MqttException {
        if (mqttClient == null || !mqttClient.isConnected()) {
            return;
        }
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        MqttMessage message = new MqttMessage(gson.toJson(model).getBytes());
        //MqttMessage message = new MqttMessage(JSON.toJSONString(model).getBytes());
        //LOG.info("topic----------{},{}",model.getTopic(),message);
        this.mqttClient.publish(model.getTopic(), message);
        //TODO
        //LOG.info("publish message :{}",message);
    }


    public void publish(TenantId tenantId, DeviceId deviceId, CustomerId customerId, TbMsgMetaData metaData, JsonObject json, SessionMsgType sessionMsgType, String topic) {
        MqttMessage msg = new MqttMessage();
        //封装数据
        JSONObject obj = new JSONObject();
        obj.put("ts",LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        obj.put("tenantId",tenantId.toString());
        obj.put("deviceId",deviceId.toString());
        obj.put("customerId",customerId.toString());
        obj.put("msg",gson.toJson(json));
        obj.put("tbMsgMetaData",gson.toJson(metaData));
        obj.put("sessionMsgType",sessionMsgType);
        obj.put("topic",topic);
        msg.setPayload(obj.toJSONString().getBytes());
        try {
            // 发送给 云云服务得mqtt
            mqttClient.publish(topic,msg);
            LOG.info("向云云服务推送设备消息：{}",msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }
}
