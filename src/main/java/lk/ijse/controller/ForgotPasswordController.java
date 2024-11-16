package lk.ijse.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.util.AlertUtil;
import lk.ijse.util.NotificationUtil;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupUI();
    }

    private void setupUI() {
        step1Container.setVisible(true);
        step2Container.setVisible(false);
        step2Container.setManaged(false);
    }

    @FXML
    private void handleNextStep() {
        if (validateUsername()) {
            step1Indicator.getStyleClass().add("active");
            step2Indicator.getStyleClass().add("active");

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
            NotificationUtil.showSuccess("Username verified successfully");
        }
    }

    private boolean validateUsername() {
        String username = txtUsername.getText().trim();
        if (username.isEmpty()) {
            NotificationUtil.showWarning("Please enter your username");
            return false;
        }
        // TODO: Add actual username validation
        return true;
    }

    @FXML
    private void handleResetPassword() {
        if (validatePasswords()) {
            NotificationUtil.showSuccess("Password reset successful!");
            closeWindow();
        }
    }

    private boolean validatePasswords() {
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            NotificationUtil.showWarning("Please fill in all password fields");
            return false;
        }

        if (newPassword.length() < 6) {
            NotificationUtil.showError("Password must be at least 6 characters");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            NotificationUtil.showError("Passwords do not match");
            return false;
        }

        return true;
    }

    @FXML
    private void handleClose() {
        if (step2Container.isVisible() && !txtNewPassword.getText().isEmpty()) {
            if (AlertUtil.showConfirmation("Cancel Reset", "Are you sure you want to cancel the password reset?")) {
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
}