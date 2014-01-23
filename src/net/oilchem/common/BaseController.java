package net.oilchem.common;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 13-2-22
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
public class BaseController {

    Integer getTotalPage(Integer pageSize,Integer totalCount){
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
