package lk.ijse.dao;

import java.util.List;

public interface CrudDAO<T> extends SuperDAO {
    boolean save(T entity) throws Exception;
    boolean update(T entity) throws Exception;
    boolean delete(String id) throws Exception;
    T search(String id) throws Exception;
    List<T> getAll() throws Exception;
}