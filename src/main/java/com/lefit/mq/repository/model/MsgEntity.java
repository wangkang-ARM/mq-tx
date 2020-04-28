package com.lefit.mq.repository.model;

import java.util.Date;

/**
 * @ProjectName: lefit-marketing
 * @Package: com.lefit.mq.repository.model
 * @ClassName: MsgEntity
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/4/27 下午5:54
 * @Version: 1.0
 */
public class MsgEntity {

    private Long id;
    private String topic;
    private String tag;
    private String body;
    private String msgId;
    private String msgKey;
    private Long ctime;
    private Date mtime;

    private String table;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public Date getMtime() {
        return mtime;
    }

    public void setMtime(Date mtime) {
        this.mtime = mtime;
    }
}
