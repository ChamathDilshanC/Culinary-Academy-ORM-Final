package lk.ijse.bo.custom.impl;

import lk.ijse.bo.custom.RegistrationBO;
import lk.ijse.dao.DAOFactory;
import lk.ijse.dao.custom.*;
import lk.ijse.dto.*;
import lk.ijse.entity.*;
import lk.ijse.config.FactoryConfiguration;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.stream.Collectors;

public class RegistrationBOImpl implements RegistrationBO {
    private final RegistrationDAO registrationDAO = (RegistrationDAO) DAOFactory.getInstance()
            .getDAO(DAOFactory.DAOTypes.REGISTRATION);
    private final StudentDAO studentDAO = (StudentDAO) DAOFactory.getInstance()
            .getDAO(DAOFactory.DAOTypes.STUDENT);
    private final ProgramDAO programDAO = (ProgramDAO) DAOFactory.getInstance()
            .getDAO(DAOFactory.DAOTypes.PROGRAM);

    @Override
    public boolean saveRegistration(RegistrationDTO dto) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        try {
            // Get student
            Student student = session.get(Student.class, dto.getStudentId());
            if (student == null) {
                throw new RuntimeException("Student not found: " + dto.getStudentId());
            }

            // Create registration
            Registration registration = new Registration();
            registration.setStudent(student);
            registration.setRegistrationDate(dto.getRegistrationDate());
            registration.setPaymentStatus(Registration.PaymentStatus.valueOf(dto.getPaymentStatus()));

            // Add details
            for (RegistrationDetailsDTO detailDTO : dto.getRegistrationDetails()) {
                Program program = session.get(Program.class, detailDTO.getProgramId());
                if (program == null) {
                    throw new RuntimeException("Program not found: " + detailDTO.getProgramId());
                }

                RegistrationDetails details = new RegistrationDetails();
                details.setRegistration(registration);
                details.setProgram(program);
                registration.getRegistrationDetails().add(details);
            }

            // Add payments
            if (!dto.getPayments().isEmpty()) {
                for (PaymentDTO paymentDTO : dto.getPayments()) {
                    Payment payment = new Payment();
                    payment.setRegistration(registration);
                    payment.setAmount(paymentDTO.getAmount());
                    payment.setPaymentDate(paymentDTO.getPaymentDate());
                    payment.setPaymentMethod(Payment.PaymentMethod.valueOf(paymentDTO.getPaymentMethod()));
                    payment.setStatus(Registration.PaymentStatus.valueOf(paymentDTO.getStatus()));
                    registration.getPayments().add(payment);
                }
            }

            session.save(registration);
            transaction.commit();
            return true;

        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Error saving registration: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public boolean updateRegistration(RegistrationDTO dto) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        try {
            Registration registration = session.get(Registration.class, dto.getId());
            if (registration == null) {
                throw new RuntimeException("Registration not found: " + dto.getId());
            }

            registration.setPaymentStatus(Registration.PaymentStatus.valueOf(dto.getPaymentStatus()));
            registration.setRegistrationDate(dto.getRegistrationDate());

            session.update(registration);
            transaction.commit();
            return true;

        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Error updating registration: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public boolean deleteRegistration(String id) throws Exception {
        return registrationDAO.delete(id);
    }

    @Override
    public RegistrationDTO searchRegistration(String id) throws Exception {
        Registration registration = registrationDAO.search(id);
        return registration != null ? convertToDTO(registration) : null;
    }

    @Override
    public List<RegistrationDTO> getAllRegistrations() throws Exception {
        return registrationDAO.getAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String getLastRegistrationId() throws Exception {
        return registrationDAO.getLastId();
    }

    @Override
    public List<RegistrationDTO> findByStudent(String studentId) throws Exception {
        return registrationDAO.findByStudent(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private RegistrationDTO convertToDTO(Registration registration) {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setId(registration.getId());
        dto.setStudentId(registration.getStudent().getStudentId());
        dto.setStudentName(registration.getStudent().getFirstName() + " " + registration.getStudent().getLastName());
        dto.setRegistrationDate(registration.getRegistrationDate());
        dto.setPaymentStatus(registration.getPaymentStatus().name());

        List<RegistrationDetailsDTO> detailsDTOs = registration.getRegistrationDetails().stream()
                .map(detail -> new RegistrationDetailsDTO(
                        detail.getId(),
                        registration.getId(),
                        detail.getProgram().getProgramId(),
                        detail.getProgram().getProgramName(),
                        detail.getProgram().getFee()
                ))
                .collect(Collectors.toList());
        dto.setRegistrationDetails(detailsDTOs);

        List<PaymentDTO> paymentDTOs = registration.getPayments().stream()
                .map(payment -> new PaymentDTO(
                        payment.getId(),
                        registration.getId(),
                        payment.getTotalAmount(),
                        payment.getAmount(),
                        payment.getBalance(),
                        payment.getPaymentDate(),
                        payment.getPaymentMethod().name(),
                        payment.getStatus().name(),
                        payment.getCreatedAt(),
                        payment.getUpdatedAt()
                ))
                .collect(Collectors.toList());
        dto.setPayments(paymentDTOs);

        return dto;
    }
    @Override
    public boolean isStudentRegisteredForProgram(String studentId, String programId) throws Exception {
        return registrationDAO.isStudentRegisteredForProgram(
                Integer.parseInt(studentId),
                programId
        );
    }
}