package com.lefit.mq.processor;

import com.aliyun.openservices.ons.api.Producer;
import com.lefit.mq.annotation.AbstractBeanProcessor;
import com.lefit.mq.common.MqTxContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
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
public class MqProxyBeanProcessor extends AbstractBeanProcessor {

    @Nullable
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Producer) {
            MqTxContext.putBean(Producer.class.getName(), bean);
            Object obj = Proxy.newProxyInstance(Producer.class.getClassLoader(), new Class[]{Producer.class}, getMqReflectProxyInterceptor(bean));
            return obj;
        } else {
            for (Field field : bean.getClass().getDeclaredFields() ) {
                if (field.getType() == Producer.class){
                    field.setAccessible(true);
                    try {
                        Producer producer = (Producer) field.get(bean);
                        MqTxContext.putBean(Producer.class.getName(), producer);
                        Object obj = Proxy.newProxyInstance(Producer.class.getClassLoader(), new Class[]{Producer.class}, getMqReflectProxyInterceptor(producer));
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
    public MqReflectProxyInterceptor getMqReflectProxyInterceptor(Object target) {
        return null;
    }

}
