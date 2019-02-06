package es.pryades.imedig.cloud.common.lazy;

import java.util.List;

public interface DataProvider <MODEL> {

    int count(Criteria criteria);
    
    List<MODEL> find(Criteria criteria, int startIndex, int offset, List<OrderByColumn> columns);

}
