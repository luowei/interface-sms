package net.oilchem.sms;

import net.oilchem.common.bean.InerCache;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import java.io.Serializable;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-1-23
 * Time: 下午5:09
 * To change this template use File | Settings | File Templates.
 */
@JsonPropertyOrder({"msgId","groupId", "ts","title", "content","replies" })
public class Sms implements Serializable {

    Integer id;
    String smsId;
    String msgId;

    String title;

    Date time;
    String ts;

    String content;

    Integer gId;
    String groupId;
    String groupIds;
    String groupName;

    String replies="";


    public Sms() {
    }

    public Sms(Integer id, Date time, String content, Integer gId) {
        this.id = id;
        this.time = time;
        this.content = content;
        this.gId = gId;
    }

    @JsonIgnore
    public Integer getId() {
        if (smsId != null && isNumeric(smsId)) {
            id = Integer.valueOf(smsId);
        }
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    public String getSmsId() {
        if (id != null) {
            smsId = String.valueOf(id);
        }
        return smsId==null?"":smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }

    public String getTitle() {
        if(isNotBlank(content) && content.length() >= 10){
           title = content.substring(0,9);
        }
        return title ==null?"": title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonIgnore
    public Date getTime() {
        if(isNotBlank(ts) && isNumeric(ts)){
            time = new Date(Long.valueOf(ts));
        }
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTs() {
        if (time != null) {
            ts = String.valueOf(time.getTime());
        }
        return ts==null?"":ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonIgnore
    public Integer getgId() {
        return gId;
    }

    public void setgId(Integer gId) {
        this.gId = gId;
    }

    public String getGroupId() {
        if(gId!=null && !gId.equals(0)){
            groupId = String.valueOf(gId);
        }
        return groupId==null?"":groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @JsonIgnore
    public String getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(String groupIds) {
        this.groupIds = groupIds;
    }

    //    @SuppressWarnings("deprecation")
    //    @JsonWriteNullProperties(false)
//    @JsonIgnore
//    @JsonProperty("groupName")
    public String getGroupName() {
        if (gId != null) {
            Group group = InerCache.getGroupMap().get(gId);
            groupName = group == null ? "" : group.getGroupName();
        }
        return groupName==null?"":groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getReplies() {
        return replies;
    }

    public void setReplies(String replies) {
        this.replies = replies;
    }
}
