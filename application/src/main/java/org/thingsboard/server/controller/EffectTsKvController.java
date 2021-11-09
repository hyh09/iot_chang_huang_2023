package org.thingsboard.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.dao.sql.role.dao.EffectTsKvRepository;
import org.thingsboard.server.queue.util.TbCoreComponent;

/**
 * @program: thingsboard
 * @description: 效能分析接口
 * @author: HU.YUNHUI
 * @create: 2021-11-09 09:42
 **/
@Slf4j
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class EffectTsKvController  extends BaseController{

    @Autowired  private EffectTsKvRepository repository;

    @RequestMapping("/app/production/queryCap")
    public  Object queryProductionCapacity(QueryTsKvVo vo){
      return   repository.queryEntity(vo);
    }
}
