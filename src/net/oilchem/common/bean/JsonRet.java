package net.oilchem.common.bean;

import org.codehaus.jackson.annotate.JsonPropertyOrder;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-1-24
 * Time: 下午4:37
 * To change this template use File | Settings | File Templates.
 */
@JsonPropertyOrder({"stat","error","data"})
public class JsonRet<T> implements Serializable {

    String stat="1";
    String error="";
    T data;

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
