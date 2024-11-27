package lk.ijse.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDTO {
    private int id;
    private int studentId;
    private String studentName;
    private LocalDateTime registrationDate;
    private String paymentStatus;
    private List<RegistrationDetailsDTO> registrationDetails;
    private List<PaymentDTO> payments;

    public RegistrationDTO() {
        this.registrationDetails = new ArrayList<>();
        this.payments = new ArrayList<>();
    }

    public RegistrationDTO(int id, int studentId, String studentName, LocalDateTime registrationDate, String paymentStatus) {
        this();
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.registrationDate = registrationDate;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public List<RegistrationDetailsDTO> getRegistrationDetails() {
        return registrationDetails;
    }
    public void setRegistrationDetails(List<RegistrationDetailsDTO> details) {
        this.registrationDetails = details;
    }

    public List<PaymentDTO> getPayments() { return payments; }
    public void setPayments(List<PaymentDTO> payments) {
        this.payments = payments;
    }

    // Convenience Methods
    public double getTotalAmount() {
        return registrationDetails.stream()
                .mapToDouble(RegistrationDetailsDTO::getFee)
                .sum();
    }

    public double getPaidAmount() {
        return payments.stream()
                .mapToDouble(PaymentDTO::getAmount)
                .sum();
    }

    public double getRemainingAmount() {
        return getTotalAmount() - getPaidAmount();
    }
}