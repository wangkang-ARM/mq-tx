package com.lefit.mq.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @ProjectName: mq-proxy
 * @Package: com.lefit.mq.config
 * @ClassName: AppConfiguration
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/4/26 上午11:30
 * @Version: 1.0
 */
@Component
@PropertySource(value={"classpath:mq-proxy-dev.properties"})
public class PropertiesConfiguration {

    @Value("${mq.datasource.user.url}")
    private String url;
    @Value("${mq.datasource.user.username}")
    private String username;
    @Value("${mq.datasource.user.password}")
    private String password;
    @Value("${mq.datasource.user.validationQuery}")
    private String validationQuery;
    @Value("${mq.datasource.user.driverClassName}")
    private String driverClassName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
}
