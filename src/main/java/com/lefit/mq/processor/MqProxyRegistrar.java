package com.lefit.mq.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Set;

/**
 * @ProjectName: lefit-marketing
 * @Package: com.lefit.mq.processor
 * @ClassName: LefitMqProxyRegistrar
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/4/26 下午2:53
 * @Version: 1.0
 */
public class MqProxyRegistrar implements ImportBeanDefinitionRegistrar{
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
        Set<BeanDefinition> beanDefinitionSet = provider.findCandidateComponents("com.lefit.mq");
        beanDefinitionSet.stream().forEach(e -> {
            registry.registerBeanDefinition(e.getBeanClassName(), e);
        });
    }

}
