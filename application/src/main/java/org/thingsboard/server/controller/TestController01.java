//package org.thingsboard.server.controller;
//
//import io.swagger.annotations.Api;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.thingsboard.server.common.data.exception.ThingsboardException;
//import org.thingsboard.server.common.data.id.UserId;
//import org.thingsboard.server.common.data.vo.enums.ErrorMessageEnums;
//import org.thingsboard.server.common.data.vo.parameter.PcTodayEnergyRaningVo;
//import org.thingsboard.server.dao.kafka.service.KafkaProducerService;
//import org.thingsboard.server.dao.sql.device.DeviceRepository;
//import org.thingsboard.server.dao.sql.role.dao.EffciencyAnalysisRepository;
//import org.thingsboard.server.dao.sql.role.service.UserRoleMenuSvc;
//import org.thingsboard.server.dao.tool.UserLanguageSvc;
//import org.thingsboard.server.dao.user.UserService;
//import org.thingsboard.server.queue.util.TbCoreComponent;
//import org.thingsboard.server.service.security.model.SecurityUser;
//import org.thingsboard.server.service.security.model.token.JwtTokenFactory;
//
//import java.time.LocalDate;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeoutException;
//
///**
// * @program: thingsboard
// * @description: 测试接口不提交
// * @author: HU.YUNHUI
// * @create: 2021-11-02 12:09
// **/
//@Api(value = "测试接口不提交", tags = {"测试接口不提交"})
//@Slf4j
//@RequiredArgsConstructor
//@RestController
//@TbCoreComponent
//@RequestMapping("/api/test/")
//public class TestController01   extends BaseController {
//
//  @Autowired  private KafkaProducerService kafkaProducerService;
//
//
//
//  @PostMapping(value="/post")
//  public void sendMessage(@RequestParam("msg") String msg) {
//
//      try {
//        kafkaProducerService.sendMessageSync("",msg);
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//
//
//  }
//
//
//
//
//
//
//
//
//
//
//
//
//
//}
