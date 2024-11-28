package lk.ijse.bo.custom;

import lk.ijse.bo.SuperBO;
import lk.ijse.dto.RegistrationDTO;
import java.util.List;

public interface RegistrationBO extends SuperBO {
    boolean saveRegistration(RegistrationDTO dto) throws Exception;
    boolean updateRegistration(RegistrationDTO dto) throws Exception;
    boolean deleteRegistration(String id) throws Exception;
    RegistrationDTO searchRegistration(String id) throws Exception;
    List<RegistrationDTO> getAllRegistrations() throws Exception;
    String getLastRegistrationId() throws Exception;
    List<RegistrationDTO> findByStudent(String studentId) throws Exception;
    boolean isStudentRegisteredForProgram(String studentId, String programId) throws Exception;
}