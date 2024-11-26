package lk.ijse.bo.custom;

import lk.ijse.bo.SuperBO;
import lk.ijse.dto.ProgramRegistrationDTO;
import lk.ijse.entity.Registration.PaymentStatus;
import java.util.List;

public interface ProgramRegistrationBO extends SuperBO {
    boolean saveProgramRegistration(ProgramRegistrationDTO dto) throws Exception;
    boolean updateProgramRegistration(ProgramRegistrationDTO dto) throws Exception;
    boolean deleteProgramRegistration(String id) throws Exception;
    ProgramRegistrationDTO searchProgramRegistration(String id) throws Exception;
    List<ProgramRegistrationDTO> getAllProgramRegistrations() throws Exception;
    List<ProgramRegistrationDTO> getProgramRegistrationsByStudent(Integer studentId) throws Exception;
    List<ProgramRegistrationDTO> getProgramRegistrationsByProgram(String programId) throws Exception;
    List<ProgramRegistrationDTO> getProgramRegistrationsByPaymentStatus(PaymentStatus status) throws Exception;
    boolean isStudentRegisteredToProgram(Integer studentId, String programId) throws Exception;
    List<ProgramRegistrationDTO> getRecentProgramRegistrations() throws Exception;
}