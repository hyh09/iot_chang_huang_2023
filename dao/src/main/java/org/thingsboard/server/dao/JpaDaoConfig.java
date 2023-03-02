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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableAutoConfiguration
@ComponentScan({"org.thingsboard.server.dao.sql", "org.thingsboard.server.dao.hs"})
//@EnableJpaRepositories({"org.thingsboard.server.dao.sql", "org.thingsboard.server.dao.hs"})
@EntityScan({"org.thingsboard.server.dao.model.sql", "org.thingsboard.server.dao.hs","org.thingsboard.server.dao.sql.*.entity"})
@EnableJpaRepositories(basePackages = {"org.thingsboard.server.dao.sql", "org.thingsboard.server.dao.hs"},
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
@EnableTransactionManagement
public class JpaDaoConfig {

    @Autowired
    private DataSource dataSourceMaster;
    @Autowired
    private JpaProperties jpaProperties;

    @Bean("entityManager")
    @Primary
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactory(builder).getObject().createEntityManager();
    }
    @Bean("entityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(dataSourceMaster)
                .properties(getVendorProperties())
                .packages("org.thingsboard.server.dao.model", "org.thingsboard.server.dao.hs","org.thingsboard.server.dao.sql.*.entity","org.thingsboard.server.dao.hsms.dao")
                .persistenceUnit("masterPersistenceUnit")
                .build();

    }
    private Map<String,String> getVendorProperties() {
        return jpaProperties.getProperties();
    }
    @Bean("transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactory(builder).getObject());
    }

}


