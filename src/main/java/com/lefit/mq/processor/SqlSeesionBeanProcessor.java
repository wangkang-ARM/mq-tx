package com.lefit.mq.processor;

import com.lefit.mq.annotation.AbstractBeanProcessor;
import com.lefit.mq.common.MqTxContext;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

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
public class SqlSeesionBeanProcessor extends AbstractBeanProcessor {

    @Nullable
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            if (MqTxContext.getBean(SqlSessionFactory.class.getName(), List.class).contains(beanName)) {
                if (!SqlSessionFactory.class.isInstance(bean)) {
                    throw new IllegalArgumentException("Target class [" + SqlSessionFactory.class.getName() +
                            "] not assignable to Editable class [" + bean.getClass().getName() + "]");
                }

                SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) bean;
                Configuration configuration = sqlSessionFactory.getConfiguration();

                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                Resource[] mapperLocations = resolver.getResources("classpath:/mqTxMapper/*.xml");

                for (Resource mapperLocation : mapperLocations) {
                    if (mapperLocation == null) {
                        continue;
                    }

                    try {
                        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(mapperLocation.getInputStream(),
                                configuration, mapperLocation.toString(), configuration.getSqlFragments());
                        xmlMapperBuilder.parse();
                    } catch (Exception e) {
                        throw new NestedIOException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
                    } finally {
                        ErrorContext.instance().reset();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bean;
    }

}
