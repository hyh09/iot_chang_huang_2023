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
        entityManagerFactoryRef = "entityManagerFactorySqlServer",//配置连接工厂 entityManagerFactory
        transactionManagerRef = "transactionManagerSqlServer", //配置事物管理器  transactionManager
        basePackages = {"org.thingsboard.server.dao.sqlserver.jpa.dao"} //配置主dao(repository)所在目录
)public class SqlServerDataSourceConfig {
    @Autowired
    private Environment env;
    @Autowired
    private JpaProperties jpaProperties;



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
    public DataSource sqlserverDataSource() {
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





    @Bean("entityManagerFactorySqlServer")
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(sqlserverDataSource())
                .properties(getVendorProperties())
                .packages("org.thingsboard.server.dao.sqlserver.jpa.entity")
                .build();
    }


    @Bean("entityManagerSqlServer")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return localContainerEntityManagerFactoryBean(builder).getObject().createEntityManager();
    }

    @Bean("transactionManagerSqlServer")
    public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(localContainerEntityManagerFactoryBean(builder).getObject());
    }

    private Map getVendorProperties() {
        return jpaProperties.getProperties();
    }












}
