package org.thingsboard.server.mqtt.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.exception.ThingsboardException;

import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/api/mqtt")
public class Test {
    @ApiOperation("设备配置下发")
    @RequestMapping(value = "/deviceIssue2", method = RequestMethod.GET)
    @ResponseBody
    public String  deviceIssue1() throws ThingsboardException, ExecutionException, InterruptedException {
        log.info("/api/mqtt/deviceIssue");
        return "aaa";
    }

}
