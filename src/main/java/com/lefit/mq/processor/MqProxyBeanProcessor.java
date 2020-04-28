package com.lefit.mq.processor;

import com.aliyun.openservices.ons.api.Producer;
import com.lefit.mq.proxy.CglibProxy;
import com.lefit.mq.proxy.MqReflectProxyInterceptor;
import com.lefit.mq.repository.dao.MqProxyMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @ProjectName: lefit-marketing
 * @Package: com.lefit.mq.processor
 * @ClassName: AliOnsProxyBeanProcessor
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/4/26 上午11:40
 * @Version: 1.0
 */
@Component
public class MqProxyBeanProcessor implements BeanPostProcessor {

    @Autowired
    CglibProxy cglibProxy;

    @Autowired
    private MqProxyMapper mqProxyMapper;

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Nullable
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Producer) {
            //Producer image = (Producer) cglibProxy.getProxy(Producer.class, bean);
            Object obj = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Producer.class}, getMqReflectProxyInterceptor(bean, mqProxyMapper));
            return obj;
        } else {
            for (Field field : bean.getClass().getDeclaredFields() ) {
                if (field.getType() == Producer.class){
                    field.setAccessible(true);
                    try {
                        Object obj = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Producer.class}, getMqReflectProxyInterceptor(bean, mqProxyMapper));
                        field.set(bean, obj);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return bean;
    }

    @Lookup
    public MqReflectProxyInterceptor getMqReflectProxyInterceptor(Object tager, MqProxyMapper mqProxyMapper) {
        return null;
    }

}
