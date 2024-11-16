package lk.ijse.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import lk.ijse.dto.UserDTO;
import lk.ijse.util.AlertUtil;
import lk.ijse.util.NotificationUtil;

public class DashboardFormController implements Initializable {

    @FXML private BorderPane contentArea;
    @FXML private Label timeLabel;
    @FXML private Label dateLabel;
    @FXML private Label userName;
    @FXML private Label userRole;

    private String currentUser;
    private String userRoleText;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeClock();
        initializeUserInfo();
        loadDefaultView();
        setupWindowProperties();
    }

    private void setupWindowProperties() {
        Platform.runLater(() -> {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            if (stage != null) {
                stage.setMinWidth(1366);
                stage.setMinHeight(768);
                stage.setTitle("Culinary Academy - Management System");
            }
        });
    }

    private void initializeClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime currentTime = LocalDateTime.now();
            timeLabel.setText(currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            dateLabel.setText(currentTime.format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")));
        }), new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void initializeUserInfo() {
        if (currentUser != null) {
            userName.setText(currentUser);
            userRole.setText(userRoleText);
        }
    }

    private void loadDefaultView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard_content.fxml"));
            Parent root = loader.load();
            contentArea.setCenter(root);
        } catch (IOException e) {
            NotificationUtil.showError("Failed to load dashboard content");
        }
    }

    @FXML
    private void handleLogout() {
        if (AlertUtil.showConfirmation("Logout", "Are you sure you want to logout?")) {
            clearUserSession();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login_form.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) contentArea.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
                NotificationUtil.showSuccess("Successfully logged out");
            } catch (IOException e) {
                NotificationUtil.showError("Failed to load login form");
            }
        }
    }

    @FXML
    private void handleExit() {
        if (AlertUtil.showConfirmation("Exit", "Are you sure you want to exit?")) {
            Platform.exit();
        }
    }

    private void clearUserSession() {
        currentUser = null;
        userRoleText = null;
    }

    public void setUserInfo(String username, String role) {
        this.currentUser = username;
        this.userRoleText = role;
        userName.setText(username);
        userRole.setText(role);
    }

    @FXML
    public void loadDashboard() {
        loadFXML("dashboard_content.fxml");
        NotificationUtil.showSuccess("Loaded dashboard");
    }

    @FXML
    public void loadStudents() {
        loadFXML("StudentForm.fxml");
        NotificationUtil.showSuccess("Loaded Students");
    }

    @FXML
    public void loadCulinaryPrograms() {
        loadFXML("ProgramsForm.fxml");
        NotificationUtil.showSuccess("Loaded Culinary Programs");
    }

    @FXML
    public void loadRegistrations() {
        loadFXML("Registrations.fxml");
        NotificationUtil.showSuccess("Loaded Registrations");
    }

    @FXML
    public void loadReports() {
        NotificationUtil.showWarning("Reports module coming soon!");
        //loadFXML("Reports.fxml"); // Uncomment when Reports module is ready to use.
    }

    @FXML
    public void loadUserManagement() {
        loadFXML("UserManagement.fxml");
    }

    public void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxmlFile));
            Parent root = loader.load();
            contentArea.setCenter(root);
        } catch (IOException e) {
            NotificationUtil.showError("Failed to load " + fxmlFile);
        }
    }

    public void loadSettings(ActionEvent actionEvent) {
        NotificationUtil.showWarning("Settings module coming soon!");
    }

    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) contentArea.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleClose() {
        if (AlertUtil.showConfirmation("Exit", "Are you sure you want to exit?")) {
            Platform.exit();
        }
    }

    public void initializeWithUser(UserDTO user) {
        setUserInfo(user.getUsername(), user.getRole().name());
    }

}