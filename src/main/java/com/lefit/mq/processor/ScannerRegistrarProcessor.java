package com.lefit.mq.processor;

import com.lefit.mq.asm.AsmMapperScan;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.io.IOException;
import java.util.*;

/**
 * @ProjectName: lefit-marketing
 * @Package: com.lefit.mq.processor
 * @ClassName: LefitMqProxyRegistrar
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/4/26 下午2:53
 * @Version: 1.0
 */
public class ScannerRegistrarProcessor implements ImportBeanDefinitionRegistrar{

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
        Set<BeanDefinition> beanDefinitionSet = provider.findCandidateComponents("com.lefit.mq");
        beanDefinitionSet.stream().forEach(e -> registry.registerBeanDefinition(e.getBeanClassName(), e));
    }

}
