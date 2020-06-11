package com.lefit.mq.processor;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.lefit.mq.repository.dao.MqProxyMapper;
import com.lefit.mq.repository.model.MsgEntity;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;

/**
 * @ProjectName: teach
 * @Package: proxy
 * @ClassName: CglibProxy
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2019/6/11 下午8:02
 * @Version: 1.0
 */
public class MqCglibProxyInterceptor implements MethodInterceptor {

    private Object object;

    private MqProxyMapper mqProxyMapper;

    MqCglibProxyInterceptor(Object object, MqProxyMapper mqProxyMapper) {
        this.object = object;
        this.mqProxyMapper = mqProxyMapper;
    }

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Object res = null;
        if (o instanceof Producer && method.getName().equals("send")) {
            insertMsg(objects);
        } else {
            res = method.invoke(object, objects);
        }
        return res;
    }

    private void insertMsg(Object[] objects) {
        Message message = (Message) objects[0];
        MsgEntity msgEntity = new MsgEntity();
        msgEntity.setTable(message.getTopic());
        msgEntity.setTag(message.getTag());
        msgEntity.setBody(new String(message.getBody()));
        msgEntity.setMessageId(message.getMsgID());
        msgEntity.setMessageKey(message.getKey());
        msgEntity.setCtime(System.currentTimeMillis());
        mqProxyMapper.insertSelective(msgEntity);
    }
}
