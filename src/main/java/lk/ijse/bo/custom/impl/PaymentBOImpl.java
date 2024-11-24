package lk.ijse.bo.custom.impl;

import lk.ijse.bo.custom.PaymentBO;
import lk.ijse.dao.DAOFactory;
import lk.ijse.dao.custom.PaymentDAO;
import lk.ijse.dao.custom.RegistrationDAO;
import lk.ijse.dto.PaymentDTO;
import lk.ijse.entity.Payment;
import lk.ijse.entity.Registration;

import java.util.ArrayList;
import java.util.List;

public class PaymentBOImpl implements PaymentBO {
    private final PaymentDAO paymentDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.PAYMENT);
    private final RegistrationDAO registrationDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.REGISTRATION);

    @Override
    public boolean savePayment(PaymentDTO dto) throws Exception {
        try {
            Registration registration = registrationDAO.search(String.valueOf(dto.getRegistrationId()));
            if (registration == null) {
                throw new Exception("Registration not found");
            }

            Payment payment = new Payment();
            payment.setRegistration(registration);
            payment.setAmount(dto.getAmount());
            payment.setPaymentDate(dto.getPaymentDate());
            payment.setPaymentMethod(dto.getPaymentMethod());
            payment.setStatus(dto.getStatus());

            return paymentDAO.save(payment);
        } catch (Exception e) {
            throw new Exception("Error saving payment: " + e.getMessage());
        }
    }

    @Override
    public boolean updatePayment(PaymentDTO dto) throws Exception {
        try {
            Registration registration = registrationDAO.search(String.valueOf(dto.getRegistrationId()));
            if (registration == null) {
                throw new Exception("Registration not found");
            }

            Payment payment = new Payment();
            payment.setPaymentId(Integer.valueOf(String.valueOf(dto.getPaymentId())));
            payment.setRegistration(registration);
            payment.setAmount(dto.getAmount());
            payment.setPaymentDate(dto.getPaymentDate());
            payment.setPaymentMethod(dto.getPaymentMethod());
            payment.setStatus(dto.getStatus());
            payment.setCreatedAt(dto.getCreatedAt());
            payment.setUpdatedAt(dto.getUpdatedAt());

            return paymentDAO.update(payment);
        } catch (Exception e) {
            throw new Exception("Error updating payment: " + e.getMessage());
        }
    }

    @Override
    public boolean deletePayment(String id) throws Exception {
        try {
            return paymentDAO.delete(id);
        } catch (Exception e) {
            throw new Exception("Error deleting payment: " + e.getMessage());
        }
    }

    @Override
    public PaymentDTO searchPayment(String id) throws Exception {
        try {
            Payment payment = paymentDAO.search(id);
            return payment != null ? convertToDTO(payment) : null;
        } catch (Exception e) {
            throw new Exception("Error searching payment: " + e.getMessage());
        }
    }

    @Override
    public List<PaymentDTO> getAllPayments() throws Exception {
        try {
            List<Payment> payments = paymentDAO.getAll();
            List<PaymentDTO> dtoList = new ArrayList<>();
            for (Payment payment : payments) {
                dtoList.add(convertToDTO(payment));
            }
            return dtoList;
        } catch (Exception e) {
            throw new Exception("Error getting all payments: " + e.getMessage());
        }
    }

    @Override
    public List<PaymentDTO> getPaymentsByRegistration(String registrationId) throws Exception {
        try {
            List<Payment> payments = paymentDAO.findByRegistrationId(registrationId);
            List<PaymentDTO> dtoList = new ArrayList<>();
            for (Payment payment : payments) {
                dtoList.add(convertToDTO(payment));
            }
            return dtoList;
        } catch (Exception e) {
            throw new Exception("Error getting payments by registration: " + e.getMessage());
        }
    }

    @Override
    public List<PaymentDTO> getPaymentsByStatus(Payment.PaymentStatus status) throws Exception {
        try {
            List<Payment> payments = paymentDAO.findByStatus(status);
            List<PaymentDTO> dtoList = new ArrayList<>();
            for (Payment payment : payments) {
                dtoList.add(convertToDTO(payment));
            }
            return dtoList;
        } catch (Exception e) {
            throw new Exception("Error getting payments by status: " + e.getMessage());
        }
    }

    @Override
    public PaymentDTO getLatestPayment(String registrationId) throws Exception {
        try {
            Payment payment = paymentDAO.findLatestPaymentByRegistration(registrationId);
            return payment != null ? convertToDTO(payment) : null;
        } catch (Exception e) {
            throw new Exception("Error getting latest payment: " + e.getMessage());
        }
    }

    @Override
    public boolean hasPayments(String registrationId) throws Exception {
        try {
            return paymentDAO.existsByRegistrationId(registrationId);
        } catch (Exception e) {
            throw new Exception("Error checking payment existence: " + e.getMessage());
        }
    }

    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(Integer.valueOf(String.valueOf(payment.getPaymentId())));
        dto.setRegistrationId(Integer.valueOf(String.valueOf(payment.getRegistration().getRegistrationId())));
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }
}