package net.oilchem.common.bean;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 13-2-22
 * Time: 上午9:01
 * To change this template use File | Settings | File Templates.
 */
public class IArgDto implements Serializable {

    Integer currentPage;
    Integer pageSize;
    Integer totalElements;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public IArgDto setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public IArgDto setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Integer getTotalElements() {
        return totalElements;
    }

    public IArgDto setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
        return this;
    }
}
