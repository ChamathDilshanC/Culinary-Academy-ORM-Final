package lk.ijse.dto;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.time.LocalDateTime;

public class ProgramRegistrationDTO extends RecursiveTreeObject<ProgramRegistrationDTO> {
    private Integer registrationId;
    private Integer studentId;
    private String programId;
    private LocalDateTime registrationDate;
    private String status;
    private Double paymentAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional display fields
    private String studentName;
    private String programName;

    public ProgramRegistrationDTO() {}

    public ProgramRegistrationDTO(Integer registrationId, Integer studentId, String programId,
                                  LocalDateTime registrationDate, String status, Double paymentAmount,
                                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.programId = programId;
        this.registrationDate = registrationDate;
        this.status = status;
        this.paymentAmount = paymentAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Integer getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Integer registrationId) {
        this.registrationId = registrationId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
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
}