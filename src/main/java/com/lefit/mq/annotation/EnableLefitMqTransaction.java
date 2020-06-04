package com.lefit.mq.annotation;

import com.lefit.mq.processor.ScannerRegistrarProcessor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @ProjectName: lefit-marketing
 * @Package: com.lefit.mq.annotation
 * @ClassName: EnableLefitMqProxy
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/4/26 下午2:55
 * @Version: 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ScannerRegistrarProcessor.class)
public @interface EnableLefitMqTransaction {
}
