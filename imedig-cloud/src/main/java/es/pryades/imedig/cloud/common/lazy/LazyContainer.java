package es.pryades.imedig.cloud.common.lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.SimpleStringFilter;

public class LazyContainer extends BeanContainer {

    private Criteria criteria;
    private DataProvider<?> provider;

    private List<OrderByColumn> orderByColumns = new ArrayList<>();

    private int minFilterLength;

    public LazyContainer(Class<?> type, DataProvider<?> provider, Criteria criteria) {
        super(type);
        this.criteria = criteria;
        this.provider = provider;
        minFilterLength = 3;
    }

    @Override
    public int size() {
        filterStringToSearchCriteria();
        if (criteria.getLastCount() == 0 || criteria.isDirty()) {
            getCount();
        } else if (isFiltered() && criteria.getFilter() != null) {
            getCount();
        }
        return criteria.getLastCount();
    }

    private void getCount() {
        int count = provider.count(criteria);
        criteria.setDirty(false);
        criteria.setLastCount(count);
    }

    @Override
    public BeanItem getItem(Object itemId) {
        return new BeanItem(itemId);
    }

    @Override
    public List<?> getItemIds(int startIndex, int numberOfIds) {
        filterStringToSearchCriteria();
        return findItems(startIndex, numberOfIds);
    }
    
    @Override
    protected List<?> getVisibleItemIds() {
        if (isFiltered()) {
            return getFilteredItemIds();
        } else {
            return getAllItemIds();
        }
    }

    private List<?> findItems(int startIndex, int numberOfIds) {
        return provider.find(criteria, startIndex, numberOfIds, orderByColumns);
    }

    private void filterStringToSearchCriteria() {
        if (isFiltered()) {
            Set<Filter> filters = getFilters();
            for (Filter filter : filters) {
                if (filter instanceof SimpleStringFilter) {
                    SimpleStringFilter stringFilter = (SimpleStringFilter) filter;
                    String filterString = stringFilter.getFilterString();
                    if (filterString.length() > minFilterLength) {
                        criteria.setFilter(filterString);
                    } else {
                        criteria.setFilter(null);
                    }
                }
            }
        }else if (criteria.getFilter() != null){
        	criteria.setFilter(null);
        	criteria.setDirty(true);
        }
    }

    @Override
    public void sort(Object[] propertyIds, boolean[] ascending) {
        orderByColumns.clear();
        for (int i = 0; i < propertyIds.length; i++) {
            Object propertyId = propertyIds[i];
            OrderByColumn.Type type = ascending[i] ? OrderByColumn.Type.ASC : OrderByColumn.Type.DESC;
            String name = propertyId.toString();
            orderByColumns.add(new OrderByColumn(name, type));
        }
    }

    @Override
    public boolean containsId(Object itemId) {
        return true;
    }

    public int getMinFilterLength() {
        return minFilterLength;
    }

    public void setMinFilterLength(int minFilterLength) {
        this.minFilterLength = minFilterLength;
    }
}
