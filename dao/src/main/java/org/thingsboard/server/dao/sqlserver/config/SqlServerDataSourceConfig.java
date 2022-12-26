package org.thingsboard.server.dao.sqlserver.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

/**
 * @Project Name: thingsboard
 * @File Name: SqlServerDataSourceConfig
 * @Date: 2022/12/26 10:14
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryMaster",//配置连接工厂 entityManagerFactory
        transactionManagerRef = "transactionManagerMaster", //配置事物管理器  transactionManager
        basePackages = {"org.thingsboard.server.dao.sqlserver.jpa.dao"} //配置主dao(repository)所在目录
)public class SqlServerDataSourceConfig {
    @Autowired
    private Environment env;
    @Autowired
    private JpaProperties jpaProperties;
    @Autowired
    private HibernateProperties hibernateProperties;


    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driverClassName"));
        return dataSource;
    }

    @Primary
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


    @Bean(name = "sqlserverDataSource")
    @ConfigurationProperties("spring.sqlserver")
    public DataSource masterDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(env.getProperty("spring.sqlserver.url"));
        dataSource.setUsername(env.getProperty("spring.sqlserver.username"));
        dataSource.setPassword(env.getProperty("spring.sqlserver.password"));
        dataSource.setDriverClassName(env.getProperty("spring.sqlserver.driverClassName"));
        return dataSource;
    }


    @Bean(name ="sqlServerTemplate")
    public JdbcTemplate sqlServerTemplate(@Qualifier("sqlserverDataSource") DataSource dataSource2) {
        return new JdbcTemplate(dataSource2);
    }

//
//    @Bean("entityManagerSqlServer")
//    public EntityManager entityManagerSqlServer(EntityManagerFactoryBuilder builder) {
//        return Objects.requireNonNull(localContainerEntityManagerFactoryBean(builder).getObject()).createEntityManager();
//    }
//
//    @Bean("entityManagerFactorySqlServer")
//    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean(EntityManagerFactoryBuilder builder) {
//        return builder.dataSource(masterDataSource())
//                .properties(getVendorProperties())
//                //设置实体类所在目录
//                .packages("org.thingsboard.server.dao.sqlserver.jpa.entity")
//                //持久化单元名称，当存在多个EntityManagerFactory时，需要制定此名称
//                .persistenceUnit("entityManagerFactorySqlServer")
//                .build();
//    }
//
//    private Map<String, Object> getVendorProperties() {
//        return hibernateProperties.determineHibernateProperties(
//                jpaProperties.getProperties(),
//                new HibernateSettings()
//        );
//    }










}
