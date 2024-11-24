package lk.ijse.dao.custom;

import lk.ijse.dao.CrudDAO;
import lk.ijse.entity.Registration;
import lk.ijse.entity.Registration.PaymentStatus;
import java.util.List;

public interface RegistrationDAO extends CrudDAO<Registration> {
    List<Registration> findByStudentId(String studentId) throws Exception;
    List<Registration> findByProgramId(String programId) throws Exception;
    List<Registration> findByPaymentStatus(PaymentStatus status) throws Exception;
    boolean existsByStudentAndProgram(String studentId, String programId) throws Exception;
    List<Registration> findRecentRegistrations() throws Exception;
}