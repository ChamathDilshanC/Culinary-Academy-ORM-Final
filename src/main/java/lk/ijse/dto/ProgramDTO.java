package lk.ijse.dto;

public class ProgramDTO {
    private String programId;
    private String programName;
    private int durationMonths;
    private double fee;
    private String description;

    public ProgramDTO() {
    }

    public ProgramDTO(String programId, String programName, int durationMonths, double fee, String description) {
        this.programId = programId;
        this.programName = programName;
        this.durationMonths = durationMonths;
        this.fee = fee;
        this.description = description;
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

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}