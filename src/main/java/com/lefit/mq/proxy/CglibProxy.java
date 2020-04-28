package com.lefit.mq.proxy;

import com.lefit.mq.repository.dao.MqProxyMapper;
import net.sf.cglib.proxy.Enhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ProjectName: teach
 * @Package: proxy
 * @ClassName: CglibProxy
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2019/6/11 下午8:02
 * @Version: 1.0
 */
@Component
public class CglibProxy {

    @Autowired
    private MqProxyMapper mqProxyMapper;

    public Object getProxy(Class cls, Object target) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallback(new MqCglibProxyInterceptor(target, mqProxyMapper));
        return enhancer.create();
    }
}
