package lk.ijse.bo.custom.impl;

import lk.ijse.bo.custom.PaymentBO;
import lk.ijse.dao.DAOFactory;
import lk.ijse.dao.custom.PaymentDAO;
import lk.ijse.dao.custom.RegistrationDAO;
import lk.ijse.dto.PaymentDTO;
import lk.ijse.entity.Payment;
import lk.ijse.entity.Registration;
import lk.ijse.config.FactoryConfiguration;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.stream.Collectors;

public class PaymentBOImpl implements PaymentBO {
    private final PaymentDAO paymentDAO = (PaymentDAO) DAOFactory.getInstance()
            .getDAO(DAOFactory.DAOTypes.PAYMENT);
    private final RegistrationDAO registrationDAO = (RegistrationDAO) DAOFactory.getInstance()
            .getDAO(DAOFactory.DAOTypes.REGISTRATION);

    @Override
    public boolean savePayment(PaymentDTO dto) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        try {
            Registration registration = session.get(Registration.class, dto.getRegistrationId());
            if (registration == null) {
                throw new RuntimeException("Registration not found: " + dto.getRegistrationId());
            }

            Payment payment = new Payment();
            payment.setRegistration(registration);
            payment.setAmount(dto.getAmount());
            payment.setTotalAmount(dto.getTotalAmount());
            payment.setBalance(dto.getBalance());
            payment.setPaymentDate(dto.getPaymentDate());
            payment.setPaymentMethod(Payment.PaymentMethod.valueOf(dto.getPaymentMethod()));
            payment.setStatus(Registration.PaymentStatus.valueOf(dto.getStatus()));

            session.save(payment);
            // Update registration payment status
            updateRegistrationPaymentStatus(registration, session);

            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Error saving payment: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public boolean updatePayment(PaymentDTO dto) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        try {
            Payment payment = session.get(Payment.class, dto.getId());
            if (payment == null) {
                throw new RuntimeException("Payment not found: " + dto.getId());
            }

            payment.setAmount(dto.getAmount());
            payment.setPaymentMethod(Payment.PaymentMethod.valueOf(dto.getPaymentMethod()));
            payment.setStatus(Registration.PaymentStatus.valueOf(dto.getStatus()));

            session.update(payment);

            // Update registration payment status
            updateRegistrationPaymentStatus(payment.getRegistration(), session);

            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Error updating payment: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public boolean deletePayment(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        try {
            Payment payment = session.get(Payment.class, Integer.parseInt(id));
            if (payment != null) {
                Registration registration = payment.getRegistration();
                session.delete(payment);

                // Update registration payment status
                updateRegistrationPaymentStatus(registration, session);

                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Error deleting payment: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public PaymentDTO searchPayment(String id) throws Exception {
        Payment payment = paymentDAO.search(id);
        return payment != null ? convertToPaymentDTO(payment) : null;
    }

    @Override
    public List<PaymentDTO> getAllPayments() throws Exception {
        return paymentDAO.getAll().stream()
                .map(this::convertToPaymentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String getLastPaymentId() throws Exception {
        return paymentDAO.getLastId();
    }

    @Override
    public List<PaymentDTO> getPaymentsByRegistration(String registrationId) throws Exception {
        return paymentDAO.findByRegistrationId(registrationId).stream()
                .map(this::convertToPaymentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public double getTotalPaymentsByRegistration(String registrationId) throws Exception {
        return paymentDAO.getTotalPaymentsByRegistrationId(registrationId);
    }

    private PaymentDTO convertToPaymentDTO(Payment payment) {
        return new PaymentDTO(
                payment.getId(),
                payment.getRegistration().getId(),
                payment.getTotalAmount(),
                payment.getAmount(),
                payment.getBalance(),
                payment.getPaymentDate(),
                payment.getPaymentMethod().name(),
                payment.getStatus().name(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    private void updateRegistrationPaymentStatus(Registration registration, Session session) {
        double totalAmount = registration.getRegistrationDetails().stream()
                .mapToDouble(detail -> detail.getProgram().getFee())
                .sum();

        double paidAmount = registration.getPayments().stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        if (paidAmount >= totalAmount) {
            registration.setPaymentStatus(Registration.PaymentStatus.COMPLETED);
        } else if (paidAmount > 0) {
            registration.setPaymentStatus(Registration.PaymentStatus.PARTIAL);
        } else {
            registration.setPaymentStatus(Registration.PaymentStatus.PENDING);
        }

        session.update(registration);
    }
}
