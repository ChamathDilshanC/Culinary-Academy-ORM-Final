package lk.ijse.bo.custom;

import lk.ijse.bo.SuperBO;
import lk.ijse.dto.RegistrationDTO;
import lk.ijse.entity.Registration.PaymentStatus;
import java.util.List;

public interface RegistrationBO extends SuperBO {
    boolean saveRegistration(RegistrationDTO dto) throws Exception;
    boolean updateRegistration(RegistrationDTO dto) throws Exception;
    boolean deleteRegistration(String id) throws Exception;
    RegistrationDTO searchRegistration(String id) throws Exception;
    List<RegistrationDTO> getAllRegistrations() throws Exception;
    List<RegistrationDTO> getRegistrationsByStudent(String studentId) throws Exception;
    List<RegistrationDTO> getRegistrationsByProgram(String programId) throws Exception;
    List<RegistrationDTO> getRegistrationsByPaymentStatus(PaymentStatus status) throws Exception;
    boolean isStudentRegisteredToProgram(String studentId, String programId) throws Exception;
    List<RegistrationDTO> getRecentRegistrations() throws Exception;
}