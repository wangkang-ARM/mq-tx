package com.lefit.mq.processor;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.lefit.mq.common.MqTxContext;
import com.lefit.mq.task.ExecuteMsgTakeService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @ProjectName: teach
 * @Package: proxy
 * @ClassName: JavaProxy
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2019/6/10 下午5:50
 * @Version: 1.0
 */
@Component
@Scope("prototype")
public class MqReflectProxyInterceptor implements InvocationHandler, ApplicationContextAware{

    private ApplicationContext applicationContext;

    private Object target;

    @Autowired
    public MqReflectProxyInterceptor(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object res = null;
        if (proxy instanceof Producer
                && method.getName().equals("send")
                && MqTxContext.getBean("topics", Set.class).contains(((Message)args[0]).getTopic())) {
            return getRetryMsgTakeService().insertMsg(args, target);
        } else {
            res = method.invoke(target, args);
        }
        return res;
    }

    public ExecuteMsgTakeService getRetryMsgTakeService() {
        return applicationContext.getBean(ExecuteMsgTakeService.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
