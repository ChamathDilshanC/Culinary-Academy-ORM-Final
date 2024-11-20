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
import lk.ijse.bo.BOFactory;
import lk.ijse.bo.custom.UserBO;
import lk.ijse.dto.UserDTO;
import lk.ijse.util.AlertUtil;
import lk.ijse.util.NotificationUtil;

public class DashboardFormController implements Initializable {

    @FXML private Label labelUserId;
    @FXML private BorderPane contentArea;
    @FXML private Label timeLabel;
    @FXML private Label dateLabel;
    @FXML private Label userName;
    @FXML private Label userRole;

    private UserDTO currentUser;
    private final UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.USER);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            try {
                initializeClock();
                loadDefaultView();
                setupWindowProperties();
            } catch (Exception e) {
                NotificationUtil.showError("Error initializing dashboard: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void setupWindowProperties() {
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) contentArea.getScene().getWindow();
                if (stage != null) {
                    stage.setMinWidth(1366);
                    stage.setMinHeight(768);
                    stage.setTitle("Culinary Academy - Management System");
                }
            } catch (Exception e) {
                NotificationUtil.showError("Error setting up window properties: " + e.getMessage());
            }
        });
    }

    private void initializeClock() {
        try {
            Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
                LocalDateTime currentTime = LocalDateTime.now();
                timeLabel.setText(currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                dateLabel.setText(currentTime.format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")));
            }), new KeyFrame(Duration.seconds(1)));

            clock.setCycleCount(Animation.INDEFINITE);
            clock.play();
        } catch (Exception e) {
            NotificationUtil.showError("Error initializing clock: " + e.getMessage());
        }
    }

    private void loadDefaultView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard_content.fxml"));
            Parent root = loader.load();
            contentArea.setCenter(root);
        } catch (IOException e) {
            NotificationUtil.showError("Failed to load default view: " + e.getMessage());
        }
    }

    public void initializeWithUser(UserDTO user) {
        try {
            this.currentUser = user;
            Platform.runLater(() -> {
                try {
                    Integer userId = userBO.getUserIdByUsername(user.getUsername());
                    if (labelUserId != null) {
                        labelUserId.setText(userId.toString());
                    }
                    if (userName != null) {
                        userName.setText(user.getUsername());
                    }
                    if (userRole != null) {
                        userRole.setText(user.getRole().name());
                    }
                } catch (Exception e) {
                    NotificationUtil.showError("Failed to initialize user data: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            NotificationUtil.showError("Error initializing user data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        if (AlertUtil.showConfirmation("Logout", "Are you sure you want to logout?")) {
            try {
                clearUserSession();
                loadLoginForm();
                NotificationUtil.showSuccess("Successfully logged out");
            } catch (IOException e) {
                NotificationUtil.showError("Failed to logout: " + e.getMessage());
            }
        }
    }

    private void loadLoginForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login_form.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) contentArea.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private void clearUserSession() {
        currentUser = null;
    }

    @FXML
    public void loadDashboard() {
        loadFXML("dashboard_content.fxml");
        NotificationUtil.showSuccess("Dashboard loaded successfully");
    }

    @FXML
    public void loadStudents() {
        loadFXML("StudentForm.fxml");
        NotificationUtil.showSuccess("Student Management loaded successfully");
    }

    @FXML
    public void loadCulinaryPrograms() {
        loadFXML("ProgramsForm.fxml");
        NotificationUtil.showSuccess("Culinary Programs loaded successfully");
    }

    @FXML
    public void loadRegistrations() {
        loadFXML("Registrations.fxml");
        NotificationUtil.showSuccess("Registrations loaded successfully");
    }

    @FXML
    public void loadReports() {
        NotificationUtil.showWarning("Reports module coming soon!");
    }

    @FXML
    public void loadUserManagement() {
            loadFXML("UserManagement.fxml");
            NotificationUtil.showSuccess("User Management loaded successfully");
    }

    public void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxmlFile));
            Parent root = loader.load();
            contentArea.setCenter(root);
        } catch (IOException e) {
            NotificationUtil.showError("Failed to load " + fxmlFile + ": " + e.getMessage());
        }
    }

    @FXML
    public void loadSettings(ActionEvent actionEvent) {
        NotificationUtil.showWarning("Settings module coming soon!");
    }

    @FXML
    private void handleMinimize() {
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setIconified(true);
        } catch (Exception e) {
            NotificationUtil.showError("Error minimizing window: " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        if (AlertUtil.showConfirmation("Exit", "Are you sure you want to exit?")) {
            Platform.exit();
        }
    }

    public void setUserInfo(String username, String role) {
        Platform.runLater(() -> {
            try {
                if (userName != null) userName.setText(username);
                if (userRole != null) userRole.setText(role);
            } catch (Exception e) {
                NotificationUtil.showError("Error setting user info: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleMaximize() {
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            if (stage != null) {
                stage.setMaximized(!stage.isMaximized());
            }
        } catch (Exception e) {
            NotificationUtil.showError("Error maximizing window: " + e.getMessage());
        }
    }

    // Method to refresh the dashboard content
    public void refreshDashboard() {
        loadDefaultView();
    }

    public UserDTO getCurrentUser() {
        return currentUser;
    }

    public void handleExit(ActionEvent actionEvent) {
        if (AlertUtil.showConfirmation("Exit", "Are you sure you want to exit?")) {
            Platform.exit();
        }
    }


}