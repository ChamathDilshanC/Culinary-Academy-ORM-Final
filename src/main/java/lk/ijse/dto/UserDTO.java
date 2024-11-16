package lk.ijse.dto;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lk.ijse.entity.User.Role;
import java.time.LocalDateTime;

public class UserDTO extends RecursiveTreeObject<UserDTO> {
    private Integer userId;
    private String username;
    private String password;
    private String email;
    private Role role;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserDTO() {}

    public UserDTO(Integer userId, String username, String password, String email,
                   Role role, LocalDateTime lastLogin, LocalDateTime createdAt,
                   LocalDateTime updatedAt) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
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
}