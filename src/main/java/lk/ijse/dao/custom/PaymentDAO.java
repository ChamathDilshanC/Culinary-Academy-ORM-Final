package lk.ijse.dao.custom;

import lk.ijse.dao.CrudDAO;
import lk.ijse.entity.Payment;
import java.util.List;

public interface PaymentDAO extends CrudDAO<Payment> {
    String getLastId() throws Exception;
    List<Payment> findByRegistrationId(String registrationId) throws Exception;
    double getTotalPaymentsByRegistrationId(String registrationId) throws Exception;
}