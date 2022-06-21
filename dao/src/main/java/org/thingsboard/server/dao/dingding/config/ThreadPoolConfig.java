package org.thingsboard.server.dao.dingding.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Project Name: thingsboard
 * File Name: ThreadPoolConfig
 * Package Name: org.thingsboard.server.dao.dingding.config
 * Date: 2022/6/21 16:05
 * author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Configuration
public class ThreadPoolConfig {


        @Bean("threadPoolTaskExecutor_1")
        public ThreadPoolTaskExecutor threadPoolTaskExecutor_1() {
            ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
            //配置线程池
            threadPoolTaskExecutor.setCorePoolSize(10);
            threadPoolTaskExecutor.setMaxPoolSize(30);
            threadPoolTaskExecutor.setQueueCapacity(100);
            threadPoolTaskExecutor.setKeepAliveSeconds(60);
            threadPoolTaskExecutor.setThreadNamePrefix("threadPoolTaskExecutor1");
            threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
            threadPoolTaskExecutor.setAwaitTerminationSeconds(60);
            //初始化线程池
            threadPoolTaskExecutor.initialize();
            return threadPoolTaskExecutor;
        }



}
