/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Valerii Sosliuk
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("org.thingsboard.server.dao.sql")
@EnableJpaRepositories(basePackages = {"org.thingsboard.server.dao.sql"},
        repositoryFactoryBeanClass = org.thingsboard.server.dao.util.sql.jpa.repository.BaseRepositoryFactoryBean.class,
        // 指定Repository的Base类
        // repositoryBaseClass = DefaultRepositoryBaseClass.class,
        // 实体管理工厂引用名称，对应到@Bean注解对应的方法
        entityManagerFactoryRef = "entityManagerFactory",
        // 事务管理工厂引用名称，对应到@Bean注解对应的方法
        transactionManagerRef = "transactionManager",
        // 是否考虑嵌套存储库
        // considerNestedRepositories = false,
        // 开启默认事务
        enableDefaultTransactions = true
)
@EntityScan(value = {"org.thingsboard.server.dao.model.sql","org.thingsboard.server.dao.sql.*.entity"})
@EnableTransactionManagement
public class JpaDaoConfig {

}
