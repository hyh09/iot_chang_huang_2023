package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.queue.util.TbCoreComponent;

/**
 * 报警规则接口
 *
 * @author wwj
 * @since 2021.10.25
 */
@Api(value = "报警规则接口", tags = {"报警规则接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class HSAlarmRuleController extends BaseController {

}
