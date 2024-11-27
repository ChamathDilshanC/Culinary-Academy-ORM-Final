package lk.ijse.dao.custom;

import lk.ijse.dao.CrudDAO;
import lk.ijse.entity.RegistrationDetails;
import java.util.List;

public interface RegistrationDetailsDAO extends CrudDAO<RegistrationDetails> {
    List<RegistrationDetails> findByRegistrationId(String registrationId) throws Exception;
    List<RegistrationDetails> findByProgramId(String programId) throws Exception;
}