package lk.ijse.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.dto.UserDTO;
import lk.ijse.util.AlertUtil;
import lk.ijse.util.NotificationUtil;
import lk.ijse.bo.custom.UserBO;
import lk.ijse.bo.custom.impl.UserBOImpl;
import lk.ijse.util.ValidationUtil;
import lk.ijse.util.PasswordEncoder;

import java.net.URL;
import java.util.ResourceBundle;

public class ForgotPasswordController implements Initializable {

    @FXML private VBox step1Container;
    @FXML private VBox step2Container;
    @FXML private Circle step1Indicator;
    @FXML private Circle step2Indicator;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;

    private final UserBO userBO = new UserBOImpl();
    private UserDTO currentUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupUI();
        setupValidation();
    }

    private void setupUI() {
        step1Container.setVisible(true);
        step2Container.setVisible(false);
        step2Container.setManaged(false);
    }

    private void setupValidation() {
        // Real-time validation for password matching
        txtConfirmPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                validatePasswordMatch(newVal);
            }
        });

        txtNewPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!txtConfirmPassword.getText().isEmpty()) {
                validatePasswordMatch(txtConfirmPassword.getText());
            }
        });
    }

    private void validatePasswordMatch(String confirmPassword) {
        if (!confirmPassword.equals(txtNewPassword.getText())) {
            txtConfirmPassword.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 10;");
        } else {
            txtConfirmPassword.setStyle("-fx-border-color: #27ae60; -fx-border-width: 2; -fx-border-radius: 10;");
        }
    }

    @FXML
    private void handleNextStep() {
        try {
            if (validateUsername()) {
                currentUser = userBO.findByUsername(txtUsername.getText().trim());
                if (currentUser != null) {
                    moveToNextStep();
                } else {
                    NotificationUtil.showError("Username not found");
                    txtUsername.requestFocus();
                }
            }
        } catch (Exception e) {
            NotificationUtil.showError("Error verifying username: " + e.getMessage());
        }
    }

    private boolean validateUsername() {
        String username = txtUsername.getText().trim();
        if (username.isEmpty()) {
            NotificationUtil.showWarning("Please enter your username");
            txtUsername.requestFocus();
            return false;
        }
        return true;
    }

    private void moveToNextStep() {
        // Animate transition
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), step1Container);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            step1Container.setVisible(false);
            step1Container.setManaged(false);
            step2Container.setVisible(true);
            step2Container.setManaged(true);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), step2Container);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();

        // Update indicators
        step1Indicator.getStyleClass().add("active");
        step2Indicator.getStyleClass().add("active");

        NotificationUtil.showSuccess("Username verified successfully");
    }

    @FXML
    private void handleResetPassword() {
        try {
            if (validatePasswords()) {
                String hashedPassword = PasswordEncoder.encode(txtNewPassword.getText());
                if (userBO.updatePassword(currentUser.getUserId(), hashedPassword)) {
                    NotificationUtil.showSuccess("Password reset successful!");
                    closeWindow();
                } else {
                    NotificationUtil.showError("Failed to update password");
                }
            }
        } catch (Exception e) {
            NotificationUtil.showError("Error resetting password: " + e.getMessage());
        }
    }

    private boolean validatePasswords() {
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            NotificationUtil.showWarning("Please fill in all password fields");
            return false;
        }

        if (!validatePasswordStrength(newPassword)) {
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            NotificationUtil.showError("Passwords do not match");
            txtConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validatePasswordStrength(String password) {
        if (password.length() < 8) {
            NotificationUtil.showWarning("Password must be at least 8 characters long");
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            NotificationUtil.showWarning("Password must contain at least one uppercase letter");
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            NotificationUtil.showWarning("Password must contain at least one lowercase letter");
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            NotificationUtil.showWarning("Password must contain at least one number");
            return false;
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            NotificationUtil.showWarning("Password must contain at least one special character");
            return false;
        }

        return true;
    }

    @FXML
    private void handleClose() {
        if (step2Container.isVisible() && !txtNewPassword.getText().isEmpty()) {
            if (AlertUtil.showConfirmation("Cancel Reset",
                    "Are you sure you want to cancel the password reset?")) {
                closeWindow();
            }
        } else {
            closeWindow();
        }
    }

    private void closeWindow() {
        FadeTransition fade = new FadeTransition(Duration.millis(200),
                txtUsername.getScene().getRoot());
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.close();
        });
        fade.play();
    }

    public void initData(UserDTO user) {
        if (user != null) {
            txtUsername.setText(user.getUsername());
            currentUser = user;
        }
    }
}