package org.thingsboard.server.mqtt.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.vm.VM;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.transport.mqtt.MqttTransportHandler;
import org.thingsboard.server.transport.mqtt.MqttTransportServerInitializer;

import java.util.concurrent.ExecutionException;

@Slf4j
@Api(value="设备管理Controller",tags={"设备管理口"})
@RestController
@RequestMapping("/api/mqtt")
public class DictIssueController {

    @ApiOperation("设备配置下发")
    @RequestMapping(value = "/deviceIssue", method = RequestMethod.POST)
    @ResponseBody
    public void deviceIssue(@RequestParam String json,@RequestParam String topic) throws ThingsboardException, ExecutionException, InterruptedException {
        log.info("/api/mqtt/deviceIssue");
        log.info("/deviceIssue设备字典下发主题"+ topic);
        log.info("/deviceIssue设备字典下发"+ json);
        //获取Mqtt实例
        // MqttTransportHandler handler1 = mqttTransportService.getMqttTransportServerInitializer().getHandler();
        MqttTransportHandler handler = MqttTransportServerInitializer.handlerMap.get("handler");
        log.info("调用前：handler：" + handler.toString());
        log.info("调用前handler内存地址：" +VM.current().addressOf(handler));
        handler.dictIssue(topic,json);
    }

    @ApiOperation("设备配置下发")
    @RequestMapping(value = "/deviceIssue1", method = RequestMethod.GET)
    @ResponseBody
    public String  deviceIssue1() throws ThingsboardException, ExecutionException, InterruptedException {
        log.info("/api/mqtt/deviceIssue");
        return "aaa";
    }

    public void testRedisPublish(){

    }

    public void testRedisSubscribe(){

    }
}
