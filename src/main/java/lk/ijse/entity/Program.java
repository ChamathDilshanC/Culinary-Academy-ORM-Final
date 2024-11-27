package lk.ijse.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "program")
public class Program {
    @Id
    @Column(name = "program_id", length = 10)
    private String programId;

    @Column(name = "program_name", nullable = false, length = 100)
    private String programName;

    @Column(name = "duration_months", nullable = false)
    private int durationMonths;

    @Column(name = "fee", nullable = false, precision = 10, scale = 2)
    private double fee;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "program", fetch = FetchType.EAGER)
    private List<RegistrationDetails> registrationDetails = new ArrayList<>();

    public Program() {
    }

    public Program(String programId, String programName, int durationMonths, double fee, String description) {
        this.programId = programId;
        this.programName = programName;
        this.durationMonths = durationMonths;
        this.fee = fee;
        this.description = description;
    }

    // Getters and Setters
    public String getProgramId() { return programId; }
    public void setProgramId(String programId) { this.programId = programId; }

    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }

    public int getDurationMonths() { return durationMonths; }
    public void setDurationMonths(int durationMonths) { this.durationMonths = durationMonths; }

    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<RegistrationDetails> getRegistrationDetails() { return registrationDetails; }
    public void setRegistrationDetails(List<RegistrationDetails> registrationDetails) {
        this.registrationDetails = registrationDetails;
    }
}