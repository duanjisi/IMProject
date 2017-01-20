package im.boss66.com.db.helper;

import java.util.List;

public interface IDatabase<T> {
    public void save(List<T> list);

    public void save(T entity);

    public T query(int id);

    public List<T> query();

    public void delete(int id);

    public void delete(String str);

    public void update(T entity);
}
