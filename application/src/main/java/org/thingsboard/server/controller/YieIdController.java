package org.thingsboard.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.sqlserver.server.OrderAnalysisServer;
import org.thingsboard.server.dao.sqlserver.server.ProcessAnalysisServer;
import org.thingsboard.server.dao.sqlserver.server.YieIdServer;
import org.thingsboard.server.dao.sqlserver.server.vo.QueryYieIdVo;
import org.thingsboard.server.dao.sqlserver.server.vo.order.OrderAnalysisVo;
import org.thingsboard.server.dao.sqlserver.server.vo.order.OrderCarNoVo;
import org.thingsboard.server.dao.sqlserver.server.vo.process.ProcessAnalysisVo;
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
    @Autowired
    private ProcessAnalysisServer processAnalysisServer;
    @Autowired
    private OrderAnalysisServer orderAnalysisServer;


    @GetMapping("/teamQuery")
    public PageData<QueryYieIdVo> teamQuery(@RequestParam int pageSize, @RequestParam int page,
                                            @RequestParam(required = false) String sortProperty,
                                            @RequestParam(required = false) String sortOrder,
                                            QueryYieIdVo queryYieIdVo) throws ThingsboardException {
        try {
            PageLink pageLink = createPageLink(pageSize, page, null, null, null);
            return yieIdServer.query(queryYieIdVo, DaoUtil.toPageable(pageLink));
        } catch (Exception e) {
            log.info("打印异常日志:{}", e);
            return null;
        }
    }

    /**
     * 2023-02-15 测试提出： 不支持客户姓名查询
     *
     * @param pageSize
     * @param page
     * @param sortProperty
     * @param sortOrder
     * @param queryYieIdVo
     * @return
     * @throws ThingsboardException
     */
    @GetMapping("/processQuery")
    public PageData<ProcessAnalysisVo> processQuery(@RequestParam int pageSize, @RequestParam int page,
                                                    @RequestParam(required = false) String sortProperty,
                                                    @RequestParam(required = false) String sortOrder,
                                                    ProcessAnalysisVo queryYieIdVo) throws ThingsboardException {
        try {
            PageLink pageLink = createPageLink(pageSize, page, null, null, null);
            return processAnalysisServer.query(queryYieIdVo, DaoUtil.toPageable(pageLink));
        } catch (Exception e) {
            log.info("打印异常日志:{}", e);
            return null;
        }
    }


    /**
     * 能耗分析 > 订单 列表接口
     *
     * @param pageSize
     * @param page
     * @param sortProperty
     * @param sortOrder
     * @param vo
     * @return
     * @throws ThingsboardException
     */
    @GetMapping("/orderQuery")
    public PageData<OrderAnalysisVo> orderQuery(@RequestParam int pageSize, @RequestParam int page,
                                                @RequestParam(required = false) String sortProperty,
                                                @RequestParam(required = false) String sortOrder,
                                                OrderAnalysisVo vo) throws ThingsboardException {
        try {
            PageLink pageLink = createPageLink(pageSize, page, null, null, null);
            return orderAnalysisServer.queryPage(vo, DaoUtil.toPageable(pageLink));
        } catch (Exception e) {
            log.info("打印异常日志:{}", e);
            return null;
        }
    }


    /**
     * 能耗分析 > 订单 列表接口  中的详情接口
     *
     * @param pageSize
     * @param page
     * @param sortProperty
     * @param sortOrder
     * @param vo
     * @return
     * @throws ThingsboardException
     */
    @GetMapping("/queryCartPage")
    public PageData<OrderCarNoVo> queryCartPage(@RequestParam int pageSize, @RequestParam int page,
                                                @RequestParam(required = false) String sortProperty,
                                                @RequestParam(required = false) String sortOrder,
                                                OrderCarNoVo vo) throws ThingsboardException {
        try {
            PageLink pageLink = createPageLink(pageSize, page, null, null, null);
            return orderAnalysisServer.queryCarNoPage(vo, DaoUtil.toPageable(pageLink));
        } catch (Exception e) {
            log.info("打印异常日志:{}", e);
            return null;
        }
    }

}
