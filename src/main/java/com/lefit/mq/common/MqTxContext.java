package com.lefit.mq.common;

import com.alibaba.fastjson.JSONObject;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.lefit.mq.util.URL;
import com.lefit.mq.zk.entity.HandleEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: lefit-user-parent
 * @Package: com.lefit.mq.util
 * @ClassName: Util
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/5/28 下午2:42
 * @Version: 1.0
 */
@Component
public class MqTxContext implements ApplicationContextAware, EnvironmentAware {

    private static Map<Object, Object> nativeContext = new HashMap();

    private ApplicationContext applicationContextAware;

    private Environment environment;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContextAware = applicationContext;

        try {
            Object dubboRegistryConfig = applicationContext.getBean("com.alibaba.dubbo.config.RegistryConfig");
            Field addressField = dubboRegistryConfig.getClass().getDeclaredField("address");
            addressField.setAccessible(true);
            String zkStrUrl = (String) addressField.get(dubboRegistryConfig);
            //zk集群 ip
            nativeContext.put("zkServices", URL.valueOf(zkStrUrl).getAddress());

            Object dubboApplicationConfig = applicationContext.getBean(Class.forName("com.alibaba.dubbo.config.ApplicationConfig"));
            Field nameField = dubboApplicationConfig.getClass().getDeclaredField("name");
            nameField.setAccessible(true);
            //dubbo名称 同时作为zk节点根目录
            String name = (String) nameField.get(dubboApplicationConfig);
            nativeContext.put("zkRootName", name);

            //优先Apollo获取配置 不存在再去本地获取配置 都不存在抛出异常
            String tmp = "";
            if (StringUtils.isNotBlank(environment.getProperty("apollo.bootstrap.enabled"))) {
                Config config = ConfigService.getConfig("tech_server.mq-tx");
                tmp =config.getProperty(name, "");
            }

            if (StringUtils.isBlank(tmp)) {
                tmp = environment.getProperty(name);
            }

            if (StringUtils.isBlank(tmp)) {
                throw new RuntimeException("Apollo与本地property均无配置 服务名:{"+name+"}");
            }
            HandleEntity entity = JSONObject.parseObject(tmp, HandleEntity.class);
            nativeContext.put("services", entity.getServices());
            nativeContext.put("topics", entity.getTopics());
        } catch (Exception e) {
            throw new RuntimeException("读取bubbo或Apollo配置失败", e);
        }
    }

    public static  <T> T getBean(Class<T> requiredType) {
        Object jndiObject = nativeContext.get(requiredType.getName());
        if (requiredType != null && !requiredType.isInstance(jndiObject)) {
            throw new RuntimeException("元类型与目标类型不匹配");
        }
        return (T) jndiObject;
    }

    public static  <T> T getBean(Object var1, Class<T> requiredType) {
        Object jndiObject = nativeContext.get(var1);
        if (requiredType != null && !requiredType.isInstance(jndiObject)) {
            throw new RuntimeException("元类型与目标类型不匹配");
        }
        return (T) jndiObject;
    }

    public static void putBean(String key, Object val) {
        nativeContext.put(key, val);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
