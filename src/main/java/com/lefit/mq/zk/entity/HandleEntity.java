package com.lefit.mq.zk.entity;

import java.util.Set;

/**
 * @ProjectName: lefit-user-parent
 * @Package: com.lefit.mq.zk.entity
 * @ClassName: HandleEntity
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/6/1 下午5:12
 * @Version: 1.0
 */
public class HandleEntity {

    private Set<String> services;

    private Set<String> topics;

    public Set<String> getServices() {
        return services;
    }

    public void setServices(Set<String> services) {
        this.services = services;
    }

    public Set<String> getTopics() {
        return topics;
    }

    public void setTopics(Set<String> topics) {
        this.topics = topics;
    }
}
