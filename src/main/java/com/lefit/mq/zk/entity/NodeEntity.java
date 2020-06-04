package com.lefit.mq.zk.entity;

/**
 * @ProjectName: lefit-user-parent
 * @Package: com.lefit.mq.zk.entity
 * @ClassName: NodeEntity
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/5/25 下午8:11
 * @Version: 1.0
 */
public class NodeEntity {

    private String virtualNodes;
    private String ip;
    private Integer lable;

    public NodeEntity(String virtualNodes, String ip, Integer lable) {
        this.virtualNodes = virtualNodes;
        this.ip = ip;
        this.lable = lable;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getLable() {
        return lable;
    }

    public void setLable(Integer lable) {
        this.lable = lable;
    }

    public String getVirtualNodes() {
        return virtualNodes;
    }

    public void setVirtualNodes(String virtualNodes) {
        this.virtualNodes = virtualNodes;
    }
}
