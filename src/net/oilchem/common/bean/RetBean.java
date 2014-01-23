package net.oilchem.common.bean;

import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 13-2-26
 * Time: 下午1:38
 * To change this template use File | Settings | File Templates.
 */
@JsonPropertyOrder({"code","msg","currentPage","pageSize","totalPage","type","data"})
public class RetBean<T>  implements Serializable {
    Integer code =1;
    String msg="ok";
    Integer currentPage;
    Integer pageSize;
    Integer totalPage;
    T data;
    String type;

    public RetBean() {
    }

    public RetBean(T data) {
        this.data = data;
    }

    public RetBean(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public RetBean setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public RetBean setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    @SuppressWarnings("deprecation")
    @JsonWriteNullProperties(false)
    public Integer getCurrentPage() {
        return currentPage;
    }

    public RetBean setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    @SuppressWarnings("deprecation")
    @JsonWriteNullProperties(false)
    public Integer getPageSize() {
        return pageSize;
    }

    public RetBean setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    @SuppressWarnings("deprecation")
    @JsonWriteNullProperties(false)
    public Integer getTotalPage() {
        return totalPage;
    }

    public RetBean setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
        return this;
    }

    @SuppressWarnings("deprecation")
    @JsonWriteNullProperties(false)
    public T getData() {
        return data;
    }

    public RetBean setData(T data) {
        this.data = data;
        return this;
    }

    @SuppressWarnings("deprecation")
    @JsonWriteNullProperties(false)
    public String getType() {
        return type;
    }

    public RetBean setType(String type) {
        this.type = type;
        return this;
    }
}
