package com.lefit.mq.zk.entity;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: lefit-user-parent
 * @Package: com.lefit.mq.zk.entity
 * @ClassName: Message
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/5/25 下午5:37
 * @Version: 1.0
 */
@Deprecated
public class OnlineMessageEntity implements Delayed {

    private Long id;
    private String body; // 消息内容
    private long excuteTime;// 延迟时长，这个是必须的属性因为要按照这个判断延时时长。

    //1：服务被下线了（需要其他服务协助消费）
    //2：服务上线了（需要查看其他服务是否在线，不在线需要协助消费）
    private Integer type;

    public Long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public long getExcuteTime() {
        return excuteTime;
    }

    public OnlineMessageEntity(Long id, String body, Integer type, long delayTime) {
        this.id = id;
        this.body = body;
        this.type = type;
        this.excuteTime = TimeUnit.NANOSECONDS.convert(delayTime, TimeUnit.MILLISECONDS) + System.nanoTime();
    }

    // 自定义实现比较方法返回 1 0 -1三个参数
    @Override
    public int compareTo(Delayed delayed) {
        OnlineMessageEntity msg = (OnlineMessageEntity) delayed;
        return Long.valueOf(this.id) > Long.valueOf(msg.id) ? 1
                : (Long.valueOf(this.id) < Long.valueOf(msg.id) ? -1 : 0);
    }

    // 延迟任务是否到时就是按照这个方法判断如果返回的是负数则说明到期否则还没到期
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.excuteTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }


}
