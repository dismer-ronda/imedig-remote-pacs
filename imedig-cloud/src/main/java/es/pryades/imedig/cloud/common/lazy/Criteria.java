package es.pryades.imedig.cloud.common.lazy;

public interface Criteria {

    public int getLastCount();

    public void setLastCount(int lastCount);

    public boolean isDirty();

    public void setDirty(boolean dirty);

    public String getFilter();

    public void setFilter(String filter);
}
