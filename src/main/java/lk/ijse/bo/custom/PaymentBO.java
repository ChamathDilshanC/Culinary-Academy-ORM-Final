package lk.ijse.bo.custom;

import lk.ijse.bo.SuperBO;
import lk.ijse.dto.PaymentDTO;
import lk.ijse.entity.Payment.PaymentStatus;
import java.util.List;

public interface PaymentBO extends SuperBO {
    boolean savePayment(PaymentDTO dto) throws Exception;
    boolean updatePayment(PaymentDTO dto) throws Exception;
    boolean deletePayment(String id) throws Exception;
    PaymentDTO searchPayment(String id) throws Exception;
    List<PaymentDTO> getAllPayments() throws Exception;
    List<PaymentDTO> getPaymentsByRegistration(String registrationId) throws Exception;
    List<PaymentDTO> getPaymentsByStatus(PaymentStatus status) throws Exception;
    PaymentDTO getLatestPayment(String registrationId) throws Exception;
    boolean hasPayments(String registrationId) throws Exception;
}