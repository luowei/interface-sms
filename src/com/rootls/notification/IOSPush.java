package com.rootls.notification;

import java.io.Serializable;

/**
 * Created by luowei on 2014/8/15.
 */
public class IOSPush implements Serializable{

    Long startTime;
    Long endTime;
    Integer allFlag;

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getAllFlag() {
        return allFlag;
    }

    public void setAllFlag(Integer allFlag) {
        this.allFlag = allFlag;
    }
}
