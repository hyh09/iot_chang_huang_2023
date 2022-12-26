package org.thingsboard.server.dao.sqlserver.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

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
public class SqlServerDataSourceConfig {
    @Autowired
    private Environment env;



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



}
