package lk.ijse.dao.custom;

import lk.ijse.dao.CrudDAO;
import lk.ijse.entity.ProgramRegistration;
import lk.ijse.entity.Registration;

import java.util.List;

public interface ProgramRegistrationDAO extends CrudDAO<ProgramRegistration> {
    List<ProgramRegistration> findByStudentId(Integer studentId) throws Exception;
    List<ProgramRegistration> findByProgramId(String programId) throws Exception;
    List<ProgramRegistration> findByPaymentStatus(Registration.PaymentStatus status) throws Exception;
    boolean existsByStudentAndProgram(Integer studentId, String programId) throws Exception;
    List<ProgramRegistration> findRecentRegistrations() throws Exception;
}