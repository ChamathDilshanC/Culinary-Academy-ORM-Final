package lk.ijse.dao.custom;

import lk.ijse.dao.CrudDAO;
import lk.ijse.entity.Program;

public interface ProgramDAO extends CrudDAO<Program> {
    String getLastId() throws Exception;
    boolean existsById(String id) throws Exception;
}