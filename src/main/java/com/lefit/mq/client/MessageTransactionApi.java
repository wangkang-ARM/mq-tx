package com.lefit.mq.client;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;

/**
 * @ProjectName: lefit-user-parent
 * @Package: com.lefit.mq.client
 * @ClassName: MqTxApi
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/5/29 下午3:32
 * @Version: 1.0
 */
public interface MessageTransactionApi {

    /**
     * 代理处理MQ消息 先落库后异步发送 内部实现补偿
     * @param message
     * @return
     */
    public SendResult proxyProcessorMqMessage(Message message);

}
