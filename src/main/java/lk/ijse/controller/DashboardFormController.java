package lk.ijse.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import lk.ijse.bo.BOFactory;
import lk.ijse.bo.custom.UserBO;
import lk.ijse.dto.UserDTO;
import lk.ijse.util.AlertUtil;
import lk.ijse.util.NotificationUtil;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardFormController implements Initializable {

    @FXML public Label labelUserId;
    public Button btnDashboard;
    public Button btnStudents;
    public Button btnRegistrations;
    public Button btnSettings;
    @FXML private BorderPane contentArea;
    @FXML private Label timeLabel;
    @FXML private Label dateLabel;
    @FXML private Label userName;
    @FXML private Label userRole;

    // Add buttons for restricted features
    @FXML private Button btnPrograms;
    @FXML private Button btnReports;
    @FXML private Button btnUserManagement;

    private UserDTO currentUser;
    private final UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.USER);

    private final Map<String, Button> restrictedButtons = new HashMap<>();
    private final Map<String, FontAwesomeIconView> lockIcons = new HashMap<>();

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

    private void setupRoleBasedAccess() {
        restrictedButtons.put("programs", btnPrograms);
        restrictedButtons.put("reports", btnReports);
        restrictedButtons.put("userManagement", btnUserManagement);

        boolean isAdmin = userRole.getText().equals("ADMIN");

        restrictedButtons.forEach((key, button) -> {
            HBox buttonContent = (HBox) button.getGraphic();
            buttonContent.getChildren().removeIf(node ->
                    node instanceof FontAwesomeIconView &&
                            ((FontAwesomeIconView) node).getGlyphName().equals("LOCK")
            );

            if (!isAdmin) {
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                buttonContent.getChildren().add(spacer);

                FontAwesomeIconView lockIcon = new FontAwesomeIconView(FontAwesomeIcon.LOCK);
                lockIcon.getStyleClass().add("lock-icon");
                lockIcon.setStyle("-fx-fill: #ef4444;");
                buttonContent.getChildren().add(lockIcon);
                lockIcons.put(key, lockIcon);
                button.setDisable(true);
            } else {
                button.setDisable(false);
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
                        setupRoleBasedAccess(); // Initialize role-based access
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
        if (!userRole.getText().equals("ADMIN")) {
            NotificationUtil.showWarning("Access denied. Admin privileges required.");
            return;
        }
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
        if (!userRole.getText().equals("ADMIN")) {
            NotificationUtil.showWarning("Access denied. Admin privileges required.");
            return;
        }
        NotificationUtil.showWarning("Reports module coming soon!");
    }

    @FXML
    public void loadUserManagement() {
        if (!userRole.getText().equals("ADMIN")) {
            NotificationUtil.showWarning("Access denied. Admin privileges required.");
            return;
        }
        loadFXML("UserManagement.fxml");
        NotificationUtil.showSuccess("User Management loaded successfully");
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
                if (userRole != null) {
                    userRole.setText(role);
                    setupRoleBasedAccess(); // Update access when role changes
                }
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
