package com.lefit.mq.service;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.lefit.mq.client.MessageTransactionApi;
import com.lefit.mq.task.ExecuteMsgTakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ProjectName: lefit-user-parent
 * @Package: com.lefit.mq.processor
 * @ClassName: MessageTransactionApiImpl
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/5/29 下午3:44
 * @Version: 1.0
 */
@Component
public class MessageTransactionApiImpl implements MessageTransactionApi{

    @Autowired
    private ExecuteMsgTakeService retryMsgTakeService;

    @Override
    public SendResult proxyProcessorMqMessage(Message message) {
        return retryMsgTakeService.insertMsg(message);
    }


}
