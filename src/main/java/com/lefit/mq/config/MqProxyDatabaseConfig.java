package com.lefit.mq.config;


import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.lefit.mq.repository.dao", sqlSessionTemplateRef = "mqProxySqlSessionTemplate")
public class MqProxyDatabaseConfig {

    @Resource
    private PropertiesConfiguration propertiesConfiguration;

    @Bean(name = "mqProxyDataSource")
    public DataSource testDataSource() {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(propertiesConfiguration.getDriverClassName());
        ds.setUsername(propertiesConfiguration.getUsername());
        ds.setUrl(propertiesConfiguration.getUrl());
        ds.setPassword(propertiesConfiguration.getPassword());
        ds.setMaxActive(30);
        ds.setInitialSize(5);
        ds.setMaxWait(10);
        ds.setMinIdle(30);
        ds.setValidationQuery(propertiesConfiguration.getValidationQuery());

        return ds;
    }

    @Bean(name = "mqProxySqlSessionFactory")
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("mqProxyDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        PathMatchingResourcePatternResolver rs = new PathMatchingResourcePatternResolver();
        bean.setConfigLocation(rs.getResource("classpath:/mybatis-config.xml") );
        bean.setMapperLocations(rs.getResources("classpath:/mqProxyMapper/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "mqProxyTransactionManager")
    public DataSourceTransactionManager testTransactionManager(@Qualifier("mqProxyDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "mqProxySqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("mqProxySqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
