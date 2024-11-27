package lk.ijse.dto;

import java.time.LocalDateTime;

public class PaymentDTO {
    private int id;
    private int registrationId;
    private double amount;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String status;

    public PaymentDTO() {
    }

    public PaymentDTO(int id, int registrationId, double amount, LocalDateTime paymentDate, String paymentMethod, String status) {
        this.id = id;
        this.registrationId = registrationId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}