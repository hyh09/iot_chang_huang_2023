package org.thingsboard.server.log.fiter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Project Name: all-in-one-multi-end-code
 * File Name: LogBackFilter
 * Package Name: org.thingsboard.server.log.filter
 * Date: 2022/6/25 23:28
 * author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Slf4j
public class LogBackFilter extends Filter<ILoggingEvent> {

private List<String> list=new ArrayList<>();
    {
//        list.add("org.thingsboard.server.queue.common.DefaultTbQueueRequestTemplate");
//        list.add("org.thingsboard.server.queue.kafka");//TbKafkaConsumerTemplate
        list.add("org.thingsboard.server.queue.common");//DefaultTbQueueRequestTemplate
        list.add("org.hibernate.engine.internal.TwoPhaseLoad"); //TwoPhaseLoad没看懂这个;

        /**
         * spring
         */
//        list.add("org.springframework.kafka.listener");//KafkaMessageListenerContainer
        list.add("org.springframework.data.auditing");  //AuditingHandler
         list.add("org.springframework.data.convert.CustomConversions");//sprig 的自定义转换器
        list.add("org.springframework");

        list.add("org.eclipse");

        /**
         * hiberanete
         */
        list.add("org.hibernate.hql.internal"); //  HqlSqlWalker
        list.add("org.hibernate.hql.internal.ast.util");// JoinProcessor
        list.add("org.hibernate.engine.jdbc.internal");
        list.add("org.hibernate.loader.plan.build.internal.spaces");
        list.add("org.hibernate.validator");
        list.add("org.hibernate.engine.transaction.internal.TransactionImpl");//事务l
        list.add("org.hibernate.internal.SessionFactoryImpl"); //
        list.add("org.hibernate.engine.query.spi.QueryPlanCache");// HQL query plan
        list.add("org.hibernate.resource.transaction.backend.jdbc.internal");
        list.add("org.hibernate.event");//AbstractFlushingEventListener
        list.add("org.hibernate.engine");
        list.add("springfox");//BeanModelProperty
        list.add("org.hibernate.persister");
        list.add("org.hibernate.loader");//EntityReferenceInitializerImpl  ResultSetProcessorImpl
        list.add("org.hibernate.type.descriptor.sql"); //BasicBinder  绑定参数的打印
       list.add("org.hibernate.SQL");//打印sql
        list.add("org.hibernate.query");//查询语句
        list.add("delight.nashornsandbox");//打印sql
        /**
         * 连接池
         */
        list.add("com.zaxxer.hikari.pool.HikariPool");
        list.add("com.datastax.oss.driver");
        list.add("org.hibernate.metamodel.internal");
        list.add("org.hibernate.resource.jdbc");
        list.add("org.hibernate.internal.SessionImpl");

        /**
         * org.postgresql
         */
        list.add("org.postgresql");

        /**
         * apache
         */
        list.add("org.apache");
        list.add("io.netty");
        list.add("javax.management");


        /**
         * 业务的
         */
        list.add("org.thingsboard.server.service.state.DefaultDeviceStateService");
        list.add("org.thingsboard.server.queue.kafka");  //TbKafkaConsumerTemplate
        list.add("org.thingsboard.server.service.subscription"); //DefaultSubscriptionManagerService
        list.add("org.thingsboard.server.dao.util");

        list.add("org.thingsboard.server.service.queue");//打印许多遥测数据
        list.add("org.thingsboard.server.actors.device");
        list.add("org.thingsboard.server.actors.TbActorMailbox");
        list.add("org.thingsboard.server.actors.ruleChain.RuleNodeActor");

        list.add("org.thingsboard.server.service.transport");//DefaultTbCoreToTransportService
        list.add("org.thingsboard.server.dao.nosql");//CassandraAbstractDao
        list.add("org.thingsboard.server.queue.common");




    }

    /**
     * DENY拒绝
     * @param event
     * @return
     */
    @Override
    public FilterReply decide(ILoggingEvent event) {
        // 指定打印日志类
        if (isExist(event.getLoggerName())) {
            log.info("[拒绝]当前的类名:"+event.getLoggerName());
            return FilterReply.DENY;  /** 拒绝*/
        } else {
            log.info("当前的类名:"+event.getLoggerName());
            return FilterReply.ACCEPT;
        }
    }


    private boolean  isExist(String name)
    {
       for(String str:list){
           if(name.contains(str))
           {

               return  true;
           }
       }
       log.info("当前日志name:{}",name);
       return  false;
    }




}
