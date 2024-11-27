package lk.ijse.dao.custom;

import lk.ijse.dao.CrudDAO;
import lk.ijse.entity.Registration;
import java.util.List;

public interface RegistrationDAO extends CrudDAO<Registration> {
    String getLastId() throws Exception;
    List<Registration> findByStudent(String studentId) throws Exception;
}