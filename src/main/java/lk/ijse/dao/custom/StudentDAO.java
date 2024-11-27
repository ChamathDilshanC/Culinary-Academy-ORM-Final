package lk.ijse.dao.custom;

import lk.ijse.dao.CrudDAO;
import lk.ijse.entity.Student;

import java.util.List;

public interface StudentDAO extends CrudDAO<Student> {
    Integer getCurrentId() throws Exception;
    boolean existsByEmail(String email) throws Exception;
    boolean existsByPhone(String phone) throws Exception;
    List<Student> searchByPhone(String searchText);
}