package net.oilchem.sms;

import net.oilchem.common.bean.InerCache;
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
@JsonPropertyOrder({"groupId", "ts", "content","tittle" })
public class Group implements Serializable {
    Integer id;
    String groupId;

    String name;

    Integer push;
    String allowPush;

    public Group() {
    }

    public Group(Integer id, String name) {
        this.id = id;
        this.name = name;
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
        return groupId;
    }

    public String getName() {
        if (groupId != null) {
            Group group = InerCache.getUserMap().get(groupId);
            name = group == null ? "" : group.getName();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return allowPush;
    }

    public void setAllowPush(String allowPush) {
        this.allowPush = allowPush;
    }
}
