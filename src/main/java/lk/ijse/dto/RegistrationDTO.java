package lk.ijse.dto;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lk.ijse.entity.Registration.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RegistrationDTO extends RecursiveTreeObject<RegistrationDTO> {
    private Integer registrationId;  // Changed to Integer
    private String studentId;
    private String programId;
    private LocalDateTime registrationDate;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional display fields
    private String studentName;
    private String programName;
    private Integer programDuration;
    private Double programFee;

    public RegistrationDTO() {}

    // Getters and Setters
    public Integer getRegistrationId() {  // Changed to Integer
        return registrationId;
    }

    public void setRegistrationId(Integer registrationId) {  // Changed to Integer
        this.registrationId = registrationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public Integer getProgramDuration() {
        return programDuration;
    }

    public void setProgramDuration(Integer programDuration) {
        this.programDuration = programDuration;
    }

    public Double getProgramFee() {
        return programFee;
    }

    public void setProgramFee(Double programFee) {
        this.programFee = programFee;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setProgramFee(BigDecimal bigDecimal) {
    }
}