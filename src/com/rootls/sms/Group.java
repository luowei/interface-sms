package com.rootls.sms;

import com.rootls.common.bean.InerCache;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-1-24
 * Time: 下午5:25
 * To change this template use File | Settings | File Templates.
 */
@JsonPropertyOrder({"groupId", "name","allowPush" })
public class Group implements Serializable {
    Integer id;
    String groupId;

    String groupName;
    String name;

    Integer push;
    String allowPush;

    public Group() {
    }

    public Group(Integer id, String groupName) {
        this.id = id;
        this.groupName = groupName;
    }

    public Group(Integer id, Integer push) {
        this.id = id;
        this.push = push;
    }

    @JsonIgnore
    public Integer getId() {
        if (groupId != null && StringUtils.isNumeric(groupId)) {
            id = Integer.valueOf(groupId);
        }
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        if(id!=null){
            groupId =String.valueOf(id);
        }
        return groupId==null?"":groupId;
    }

    public String getName() {
        if (id != null) {
            Group group = null;
            try {
                group = InerCache.getGroupMap().get(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            name = group == null ? "" : group.getGroupName();
        }
        return name==null?"":name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getGroupName() {
        return groupName==null?"":groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @JsonIgnore
    public Integer getPush() {
        if (allowPush != null && StringUtils.isNumeric(allowPush)) {
            push = Integer.valueOf(allowPush);
        }
        return push;
    }

    public void setPush(Integer push) {
        this.push = push;
    }

    public String getAllowPush() {
        if(push!=null){
            allowPush =String.valueOf(push);
        }
        return allowPush==null?"":allowPush;
    }

    public void setAllowPush(String allowPush) {
        this.allowPush = allowPush;
    }
}
