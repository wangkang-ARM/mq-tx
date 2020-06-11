package com.lefit.mq.task;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendResult;
import com.lefit.mq.processor.AfterTransactionOpt;
import com.lefit.mq.repository.dao.MqBackUpMapper;
import com.lefit.mq.repository.dao.MqProxyMapper;
import com.lefit.mq.repository.model.MsgEntity;
import com.lefit.mq.util.NetUtils;
import com.lefit.mq.common.MqTxContext;
import com.lefit.mq.zk.ConsistentHashingNodeManager;
import com.lefit.mq.zk.entity.NodeEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @ProjectName: lefit-user-parent
 * @Package: com.lefit.mq.task
 * @ClassName: RetryMsgTakeService
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/5/25 下午6:47
 * @Version: 1.0
 */
@Component
public class ExecuteMsgTakeService {

    //public static final ExecutorService executorService = new ThreadPoolExecutor(4, 4, 60, TimeUnit.SECONDS, new ArrayBlockingQueue(100));

    private Map<String, Timer> timerMap = new HashMap<>();

    @Autowired
    private ConsistentHashingNodeManager consistentHashingNodeManager;

    @Autowired
    private MqProxyMapper mqProxyMapper;

    @Autowired
    private MqBackUpMapper mqBackUpMapper;

    @Autowired
    private AfterTransactionOpt afterTransactionOpt;

    /**
     * 执行自己或其他服务下线需要补偿的任务
     * @param node 上线或下线节点服务
     * @param delay 延时多久执行
     * @param period 每隔多久执行维持心跳（自身节点设为非0因为随时存在失败的节点入库，非自身节点设为0因为该节点不在线不会存在新数据落库不需要心跳检查）
     * @param checkOnline 是否检查节点在线（防止节点下线后 节点重新上线 丢弃该任务）
     */
    public void schedule(String node, long delay, long period, boolean checkOnline) {
        Integer lable = Integer.valueOf(StringUtils.split(node, "#")[1]);
        Timer timer =  new Timer();
        if(timerMap.containsKey(node)) {
            return;
        }
        timerMap.put(node, timer);
        if (period > 0) {
            timer.schedule(new TimerTaskExc(node, lable, checkOnline), delay, period);
        } else {
            timer.schedule(new TimerTaskExc(node, lable, checkOnline), delay);
        }
    }

    class TimerTaskExc extends TimerTask {
        private String node;
        private Integer lable;
        private Boolean checkOnline;

        public TimerTaskExc(String node, Integer lable, Boolean checkOnline) {
            this.node = node;
            this.lable = lable;
            this.checkOnline = checkOnline;
        }

        @Override
        public void run() {
            if (checkOnline && consistentHashingNodeManager.getRealNodes().contains(node)) {
                return;
            }

            Set<String> topicList = MqTxContext.getBean("topics", Set.class);
            topicList.stream().forEach(topic -> {
                List<MsgEntity> msgList = null;
                do {
                    msgList = mqProxyMapper.queryFailMsg(topic, lable);
                    for (MsgEntity entity : msgList) {
                        Message message = new Message();
                        BeanUtils.copyProperties(entity, message);
                        message.setTopic(topic);
                        message.setBody(entity.getBody().getBytes());
                        SendResult result = MqTxContext.getBean(Producer.class).send(message);
                        if(result != null && StringUtils.isNotBlank(result.getMessageId())) {
                            mqProxyMapper.updateMessageId(message.getTopic(), entity.getId(), result.getMessageId());
                        } else {
                            mqProxyMapper.updateRetryNum(message.getTopic(), entity.getId());
                        }
                    }
                } while (!msgList.isEmpty());
            });
        }
    }

    public void stopTimer(String offlineService) {
        if (timerMap.containsKey(offlineService) && !offlineService.contains(NetUtils.getLocalHost())) {
            timerMap.get(offlineService).cancel();
            timerMap.remove(offlineService);
        }
    }

    /**
     * 下线服务的任务补偿 延时15分钟后仍不在线交由其他 交由hash环中最近一个在线节点的服务执行
     * @param offlineService
     */
    public void offlineToOnlineServiceExu(String offlineService) {
        NodeEntity nearOnlineNode = consistentHashingNodeManager.getNode(offlineService);
        if (NetUtils.getLocalHost().equals(nearOnlineNode.getIp())) {
            schedule(offlineService, 60000*15, 0, true);
        }
    }

    public SendResult insertMsg(Object[] objects, Object target) {
        Message message = (Message) objects[0];
        MsgEntity msgEntity = new MsgEntity();
        msgEntity.setTable(message.getTopic());
        msgEntity.setTag(message.getTag());
        msgEntity.setBody(new String(message.getBody()));
        msgEntity.setMessageKey(message.getKey());
        msgEntity.setCtime(System.currentTimeMillis());
        msgEntity.setLable(consistentHashingNodeManager.getNodeVal(msgEntity.getCtime().toString()));
        mqProxyMapper.insertSelective(msgEntity);

        afterTransactionOpt.execute(() -> {
            SendResult result = ((Producer)target).send(message);
            if(result != null && StringUtils.isNotBlank(result.getMessageId())) {
                mqProxyMapper.updateMessageId(message.getTopic(), msgEntity.getId(), result.getMessageId());
            }
        });

        SendResult result = new SendResult();
        result.setTopic(message.getTopic());
        result.setMessageId(msgEntity.getId().toString());
        return result;
    }

    public SendResult insertMsg(Message message) {
        MsgEntity msgEntity = new MsgEntity();
        msgEntity.setTable(message.getTopic());
        msgEntity.setTag(message.getTag());
        msgEntity.setBody(new String(message.getBody()));
        msgEntity.setMessageKey(message.getKey());
        msgEntity.setCtime(System.currentTimeMillis());
        msgEntity.setLable(consistentHashingNodeManager.getNodeVal(msgEntity.getCtime().toString()));
        mqProxyMapper.insertSelective(msgEntity);

        afterTransactionOpt.execute(() -> {
            SendResult result = MqTxContext.getBean(Producer.class).send(message);
            if(result != null && StringUtils.isNotBlank(result.getMessageId())) {
                mqProxyMapper.updateMessageId(message.getTopic(), msgEntity.getId(), result.getMessageId());
            }
        });

        SendResult result = new SendResult();
        result.setTopic(message.getTopic());
        result.setMessageId(msgEntity.getId().toString());
        return result;
    }

    public void historyBackUp() {
        Set<String> topicList = MqTxContext.getBean("topics", Set.class);
        topicList.stream().forEach(topic -> {
            try {
                List<MsgEntity> list = mqProxyMapper.queryBackUp(topic);
                while (!list.isEmpty()) {
                    doBackUp(topic, list);
                    list = mqProxyMapper.queryBackUp(topic);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Transactional
    public void doBackUp(String topic, List<MsgEntity> list) {
        for (MsgEntity e : list) {
            List<MsgEntity> tmpList = Arrays.asList(e);
            mqBackUpMapper.batchInsert(topic, tmpList);
            mqProxyMapper.deleteBackupAlready(topic, tmpList.stream().map(MsgEntity::getId).collect(Collectors.toList()));
        }
    }
}
