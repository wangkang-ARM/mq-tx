package com.lefit.mq.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;

/**
 * @ProjectName: lefit-user-parent
 * @Package: com.lefit.mq.annotation
 * @ClassName: AbstractBeanProcessor
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/6/4 下午2:52
 * @Version: 1.0
 */
public abstract class AbstractBeanProcessor implements BeanPostProcessor{

    @Nullable
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Nullable
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
