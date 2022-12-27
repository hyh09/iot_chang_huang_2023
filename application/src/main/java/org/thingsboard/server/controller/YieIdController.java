package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.sqlserver.server.YieIdServer;
import org.thingsboard.server.dao.sqlserver.server.vo.QueryYieIdEntryVo;
import org.thingsboard.server.dao.sqlserver.server.vo.QueryYieIdVo;
import org.thingsboard.server.entity.productioncalender.dto.ProductionCalenderPageQry;
import org.thingsboard.server.queue.util.TbCoreComponent;

/**
 * @Project Name: thingsboard
 * @File Name: YieIdController
 * @Date: 2022/12/26 13:43
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/yieId")
public class YieIdController extends BaseController {

    @Autowired
    private YieIdServer yieIdServer;


    @RequestMapping("/query")
    public Object query(@RequestParam int pageSize, @RequestParam int page,
                        @RequestParam(required = false) String sortProperty,
                        @RequestParam(required = false)  String sortOrder,
                        QueryYieIdVo queryYieIdVo) throws ThingsboardException {
        try {
            PageLink pageLink = createPageLink(pageSize, page, null, null, null);
            return yieIdServer.query(queryYieIdVo, DaoUtil.toPageable(pageLink));
        }catch (Exception e){
            log.info("打印异常日志:{}",e);
            return null;
        }
    }

}
