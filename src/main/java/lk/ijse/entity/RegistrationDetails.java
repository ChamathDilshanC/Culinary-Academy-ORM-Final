package lk.ijse.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registration_details")
public class RegistrationDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "registration_id", nullable = false)
    private Registration registration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Registration getRegistration() { return registration; }
    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public Program getProgram() { return program; }
    public void setProgram(Program program) { this.program = program; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}