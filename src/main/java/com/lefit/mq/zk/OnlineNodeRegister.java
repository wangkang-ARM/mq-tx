package com.lefit.mq.zk;

import com.lefit.mq.task.ExecuteMsgTakeService;
import com.lefit.mq.common.MqTxContext;
import com.lefit.mq.util.NetUtils;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * @ProjectName: lefit-user-parent
 * @Package: com.lefit.mq.register
 * @ClassName: OnlineNodeRegister
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/5/25 上午9:39
 * @Version: 1.0
 */
@Component
public class OnlineNodeRegister implements ApplicationRunner {

    @Autowired
    private ConsistentHashingNodeManager consistentHashingNodeManager;

    @Autowired
    private ExecuteMsgTakeService retryMsgTakeService;

    //服务根节点
    private static String zkRootName = "/mq-tx/";

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        //获取dubbo配置中的zookeeper服务器IP并创建客户端
        ZkClient zkClient = new ZkClient(MqTxContext.getBean("zkServices", String.class),5000);
        //获取dubbo配置中的appname作为根节点
        zkRootName += MqTxContext.getBean("zkRootName", String.class);

        // 注册子节点变更监听（此时path节点并不存在，但可以进行监听注册）
        zkClient.subscribeChildChanges(zkRootName, (parentPath, currentChilds) -> {
            consistentHashingNodeManager.listenNodeChange(currentChilds);
        });

        //校验配置与本地ip是否一致
        String localhostNetIp = NetUtils.getLocalHost();
        if (!consistentHashingNodeManager.getServers().stream().map(e -> e.split("#")[0]).collect(Collectors.toList()).contains(localhostNetIp)) {
            throw new RuntimeException("property not contains localHost ip ");
        }

        //创建临时节点
        String node = consistentHashingNodeManager.getServers().stream().filter(e -> e.contains(localhostNetIp)).findFirst().get();
        zkClient.createPersistent(zkRootName, true);
        zkClient.createEphemeral(zkRootName + "/" +node, "1");

        //延时30s 每个10分钟 执行本地需要补送的消息
        retryMsgTakeService.schedule(node, 30000, 60000*10, false);
    }

}
