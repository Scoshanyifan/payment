package com.kunbu.pay.payment.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 简单形式的多数据源配置，jpa和quartz
 *
 * @author kunbu
 * @date 2020/12/4 11:32
 *
 **/
@Configuration
public class MysqlJpaConfig {

    private static final String DATASOURCE_NAME_JPA = "jpaDataSource";

    /**
     * 数据源配置的前缀，必须与application.properties中配置的对应数据源的前缀一致
     */
    private static final String BIZ_DATASOURCE_PREFIX = "spring.datasource.druid.jpa";


    @Bean(name = DATASOURCE_NAME_JPA)
    @ConfigurationProperties(prefix = BIZ_DATASOURCE_PREFIX)
    public DruidDataSource druidDataSource() {
        return new DruidDataSource();
    }


}
