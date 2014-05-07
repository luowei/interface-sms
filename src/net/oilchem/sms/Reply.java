package net.oilchem.sms;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;

/**
 * Created by luowei on 2014/5/6.
 */
public class Reply implements Serializable,Comparable<Reply>{
    String id;
    String msgId;
    String username;
    String reply;
    String replyTime;

    public Reply() {
    }

    public Reply(String id, String msgId, String username, String reply) {
        this.id = id;
        this.msgId = msgId;
        this.username = username;
        this.reply = reply;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    @Override
    public int compareTo(Reply re) {
        if(re !=null && this.getReplyTime()!=null && re.getReplyTime()!=null){
            return this.getReplyTime().compareTo(re.getReplyTime());
        }
        return 0;
    }


}
