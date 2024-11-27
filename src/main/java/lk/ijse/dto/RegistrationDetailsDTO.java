package lk.ijse.dto;

public class RegistrationDetailsDTO {
    private int id;
    private int registrationId;
    private String programId;
    private String programName;
    private double fee;

    public RegistrationDetailsDTO() {
    }

    public RegistrationDetailsDTO(int id, int registrationId, String programId, String programName, double fee) {
        this.id = id;
        this.registrationId = registrationId;
        this.programId = programId;
        this.programName = programName;
        this.fee = fee;
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

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}