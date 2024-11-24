package lk.ijse.dao.custom;

import lk.ijse.dao.CrudDAO;
import lk.ijse.entity.Payment;
import java.util.List;

public interface PaymentDAO extends CrudDAO<Payment> {
    List<Payment> findByRegistrationId(String registrationId) throws Exception;
    List<Payment> findByStatus(Payment.PaymentStatus status) throws Exception;
    Payment findLatestPaymentByRegistration(String registrationId) throws Exception;
    boolean existsByRegistrationId(String registrationId) throws Exception;
}