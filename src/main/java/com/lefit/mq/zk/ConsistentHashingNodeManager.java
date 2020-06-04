package com.lefit.mq.zk;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.lefit.mq.common.MqTxContext;
import com.lefit.mq.task.ExecuteMsgTakeService;
import com.lefit.mq.zk.entity.NodeEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @ProjectName: lefit-user-parent
 * @Package: com.lefit.mq.zk
 * @ClassName: ConsistentHashingWithoutVirtualNode
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/5/25 上午10:30
 * @Version: 1.0
 * 不带虚拟节点的一致性Hash算法
 * 重点：1.如何造一个hash环，2.如何在哈希环上映射服务器节点，3.如何找到对应的节点
 * 带虚拟节点的一致性Hash算法
 */
@Component
public class ConsistentHashingNodeManager {

    @Autowired
    private ExecuteMsgTakeService retryMsgTakeService;

    //预期真实存在的服务列表（如其中一台或多台服务不上线 指定延时时间后由其他在线服务消费）
    private Set<String> servers;// = new HashSet<>(Arrays.asList("172.16.12.101#111", "172.16.12.102#222"));

    //当前线上真实存在的服务列表
    //真实结点列表,考虑到服务器上线、下线的场景，即添加、删除的场景会比较频繁
    private Set<String> realNodes = new CopyOnWriteArraySet<>();

    //虚拟节点，key表示虚拟节点的hash值，value表示虚拟节点的名称
    private SortedMap<Integer, NodeEntity> virtualNodes = new TreeMap<>();

    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    private final Lock r = rwl.readLock();

    private final Lock w = rwl.writeLock();

    //虚拟节点的数目，一个真实结点对应5个虚拟节点
    private static final int VIRTUAL_NODES = 5;

    public synchronized void listenNodeChange(List<String> nodeList){
        Set<String> tmpSet = new HashSet<>(realNodes);
        //再添加虚拟节点，遍历LinkedList使用foreach循环效率会比较高
        SortedMap<Integer, NodeEntity> tempNodeMap = new TreeMap<>();
        for (String str : nodeList){
            for(int i=0; i<VIRTUAL_NODES; i++){
                String virtualNodeName = str + "&&VN" + String.valueOf(i);
                int hash = getHash(virtualNodeName);
                System.out.println("虚拟节点[" + virtualNodeName + "]被添加, hash值为" + hash);
                String[] tmp = StringUtils.split(str, "#");
                tempNodeMap.put(hash, new NodeEntity(virtualNodeName, tmp[0], Integer.valueOf(tmp[1])));
            }
        }

        w.lock();
        try {
            realNodes.clear();
            realNodes.addAll(nodeList);
            virtualNodes.clear();
            virtualNodes.putAll(tempNodeMap);
        } finally {
            w.unlock();
        }

        //变化后的节点数小于本地节点数 说明有服务下线 需要延时10分钟交由其他服务消费
        if (tmpSet.size() > nodeList.size()) {
            Set<String> distinctByUniqueList = tmpSet.stream()
                    .filter(item -> !nodeList.stream()
                            .collect(Collectors.toList())
                            .contains(item))
                    .collect(Collectors.toSet());

            distinctByUniqueList.stream().forEach(e -> {
                //将延时消息放到延时任务中
                retryMsgTakeService.offlineToOnlineServiceExu(e);
            });
        }else {
            //变化后节点数多余本地节点数 说明有新服务上线 需要查看本地之前是否有协助消费这个新服务的消息 如果有立即停止
            Set<String> distinctByUniqueList = nodeList.stream()
                    .filter(item -> !tmpSet.stream()
                            .collect(Collectors.toList())
                            .contains(item))
                    .collect(Collectors.toSet());

            distinctByUniqueList.stream().forEach(e -> {
                //停止协助消费其他lable消息
                retryMsgTakeService.stopTimer(e);
            });
        }
    }


    //使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
    private int getHash(String str){
        final int p = 16777619;
        int hash = (int)2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

    public Integer getNodeVal(String key) {
        return getNode(key).getLable();
    }
    public String getNodeIp(String key) {
        return getNode(key).getIp();
    }

    //得到应当路由到的结点
    public NodeEntity getNode(String key){
        //得到该key的hash值
        int hash = getHash(key);
        // 得到大于该Hash值的所有Map
        SortedMap<Integer, NodeEntity> subMap = null;
        r.lock();
        try {
            subMap = virtualNodes.tailMap(hash);
        } finally {
            r.unlock();
        }
        NodeEntity virtualNode;
        if(subMap.isEmpty()){
            //如果没有比该key的hash值大的，则从第一个node开始
            Integer i = virtualNodes.firstKey();
            //返回对应的服务器
            virtualNode = virtualNodes.get(i);
        }else{
            //第一个Key就是顺时针过去离node最近的那个结点
            Integer i = subMap.firstKey();
            //返回对应的服务器
            virtualNode = subMap.get(i);
        }
        //virtualNode虚拟节点名称要截取一下
        if(virtualNode != null){
            return virtualNode;
        }
        return null;
    }

    private Integer getIpLable(String ipService) {
        String[] tmp = ipService.split("#");
        if (tmp.length > 1) {
            return Integer.valueOf(tmp[1]);
        } else {
            return Integer.valueOf(tmp[0].substring(tmp[0].lastIndexOf(".")+1, tmp[0].length()));
        }
    }

    public Set<String> getServers() {
        if (servers == null || servers.isEmpty()) {
            servers = MqTxContext.getBean("services", Set.class);
        }
        return servers;
    }

    public Set<String> getRealNodes() {
        r.lock();
        try {
            return realNodes;
        } finally {
            r.unlock();
        }
    }

}
