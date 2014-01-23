package net.oilchem.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static net.oilchem.common.bean.Config.PAGE_SIZE;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 13-1-4
 * Time: 下午2:56
 * To change this template use File | Settings | File Templates.
 */
public class Page<T> implements Serializable {

    Integer pageNumber;
    Integer pageSize = PAGE_SIZE;
    Integer totalCount;
    String order = "price_date desc";
    List<T> content = new ArrayList<T>();

    public Page() {
    }

    public Page(Integer pageNumber, Integer pageSize, Integer totalCount,
                List<T> content) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.content = content;
    }

    public Page(Integer pageNumber, Integer pageSize,
                Integer totalCount, String order) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.order = order;
    }

    public Page(Integer pageNumber, Integer pageSize, Integer totalCount,
                String order, List<T> content) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.order = order;
        this.content = content;
    }

    public Integer getPageNumber() {
        if(pageNumber==null || pageNumber.equals(0)){
            return 1;
        }
        return pageNumber;
    }

    public Page setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public Integer getPageSize() {
        if(pageSize==null || pageSize.equals(0)){
            return PAGE_SIZE;
        }
        return pageSize;
    }

//    public Page setPageSize(Integer pageSize) {
//        this.pageSize = pageSize;
//        return this;
//    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public Page setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public String getOrder() {
        return order;
    }

    public Page setOrder(String order) {
        this.order = order;
        return this;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Integer getTotalPage(){
        if(pageSize!=null && !pageSize.equals(0)){
            if(totalCount%pageSize==0){
                return totalCount / pageSize;
            }else {
                return totalCount / pageSize+1;
            }
        }
        return 0;
    }

}
