package com.lefit.mq.proxy;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.lefit.mq.repository.dao.MqProxyMapper;
import com.lefit.mq.repository.model.MsgEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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
public class MqReflectProxyInterceptor implements InvocationHandler {

    private Object tager;

    private MqProxyMapper mqProxyMapper;

    public MqReflectProxyInterceptor(Object tager, MqProxyMapper mqProxyMapper) {
        this.tager = tager;
        this.mqProxyMapper = mqProxyMapper;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object res = null;
        if (proxy instanceof Producer && method.getName().equals("send")) {
            insertMsg(args);
        } else {
            res = method.invoke(tager, args);
        }
        return res;
    }

    private void insertMsg(Object[] objects) {
        Message message = (Message) objects[0];
        MsgEntity msgEntity = new MsgEntity();
        msgEntity.setTable(message.getTopic());
        msgEntity.setTag(message.getTag());
        msgEntity.setBody(new String(message.getBody()));
        msgEntity.setMsgId(message.getMsgID());
        msgEntity.setMsgKey(message.getKey());
        msgEntity.setCtime(System.currentTimeMillis());
        mqProxyMapper.insertSelective(msgEntity);
    }
}
