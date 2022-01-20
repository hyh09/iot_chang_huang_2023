package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.vo.enums.ErrorMessageEnums;
import org.thingsboard.server.common.data.vo.parameter.PcTodayEnergyRaningVo;
import org.thingsboard.server.dao.sql.device.DeviceRepository;
import org.thingsboard.server.dao.sql.factoryUrl.entity.FactoryURLAppTableEntity;
import org.thingsboard.server.dao.sql.factoryUrl.service.FactoryURLAppTableService;
import org.thingsboard.server.dao.sql.role.dao.EffciencyAnalysisRepository;
import org.thingsboard.server.dao.sql.role.service.UserRoleMenuSvc;
import org.thingsboard.server.dao.tool.UserLanguageSvc;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.model.token.JwtTokenFactory;

import java.time.LocalDate;

/**
 * @program: thingsboard
 * @description: 测试接口不提交
 * @author: HU.YUNHUI
 * @create: 2021-11-02 12:09
 **/
@Api(value = "测试接口不提交", tags = {"测试接口不提交"})
@Slf4j
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/test/")
public class TestController01   extends BaseController {

  @Autowired  private JwtTokenFactory jwtTokenFactory;
  @Autowired  private UserLanguageSvc  userLanguageSvc;
  @Autowired  private UserRoleMenuSvc  userRoleMenuSvc;
  @Autowired  private UserService userService;
  @Autowired  private DeviceRepository deviceRepository;

  @GetMapping("/getLanguageByUserLang")
  public  String  Test001() throws ThingsboardException {
    try {
      SecurityUser authUser = getCurrentUser();
      UserId userId = new UserId((authUser.getUuidId()));
     return userLanguageSvc.getLanguageByUserLang(ErrorMessageEnums.SING_ON_AUTHENTICATION, authUser.getTenantId(), userId);
    }catch (Exception e)
    {
       e.printStackTrace();
      return  null;
    }
  }



  @Autowired  private EffciencyAnalysisRepository effciencyAnalysisRepository;


  @PostMapping("/queryTodayTest")
  public  Object  queryTodayE(@RequestBody PcTodayEnergyRaningVo vo) throws ThingsboardException {
    try {
      LocalDate today = LocalDate.now();
      SecurityUser authUser = getCurrentUser();
      vo.setTenantId(getTenantId().getId());
      vo.setDate(today);
        return effciencyAnalysisRepository.queryTodayEffceency(vo);
    }catch (Exception e)
    {
      e.printStackTrace();
      return  e;
    }
  }



  @Autowired
  FactoryURLAppTableService factoryURLAppTableService;



  @PostMapping("/appurl/save")
  public  Object  save(@RequestBody FactoryURLAppTableEntity  factoryURLAppTableEntity) throws ThingsboardException {
        SecurityUser  securityUser = getCurrentUser();
        if(!securityUser.getAuthority().equals(Authority.SYS_ADMIN))
        {
          return "请求错误!";
        }
       return factoryURLAppTableService.save(factoryURLAppTableEntity);
  }

  @GetMapping("/appurl/findByUrl")
  public  Object  findByUrl(@RequestParam("appUrl") String appUrl) throws ThingsboardException {
    SecurityUser  securityUser = getCurrentUser();
    if(!securityUser.getAuthority().equals(Authority.SYS_ADMIN))
    {
      return "请求错误!";
    }
    return factoryURLAppTableService.queryAllByAppUrl(appUrl);
  }





}
