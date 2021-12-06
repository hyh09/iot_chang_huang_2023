package org.thingsboard.server.controller;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.Uuid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.entity.system.SystemVo;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Api(value="系统版本",tags={"系统管理接口"})
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/system")
public class SystemController {

    @ApiOperation("获取系统版本")
    @RequestMapping(value = "/getSystemVersion", method = RequestMethod.GET)
    @ResponseBody
    public SystemVo getSystemVersion(){
        // TODO: 2021/12/6 系统版本获取
        return new SystemVo("1.0.0", Uuids.unixTimestamp(Uuids.timeBased()));
    }
}
