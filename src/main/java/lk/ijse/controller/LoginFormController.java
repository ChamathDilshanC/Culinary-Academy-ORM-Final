package lk.ijse.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import de.jensd.fx.glyphs.fontawesome.*;
import lk.ijse.bo.BOFactory;
import lk.ijse.bo.custom.UserBO;
import lk.ijse.dto.UserDTO;
import lk.ijse.entity.User.Role;
import lk.ijse.util.AlertUtil;
import lk.ijse.util.NotificationUtil;
import lk.ijse.util.PasswordEncoder;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class LoginFormController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private Pane backgroundPane;
    @FXML private Circle bgCircle1, bgCircle2, bgCircle3;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private FontAwesomeIconView iconEye;
    @FXML private CheckBox chkRemember;
    @FXML private Button btnLogin;
    @FXML private StackPane loadingPane;

    private final UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.USER);
    private boolean passwordVisible = false;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupUI();
        initializeAnimations();
        setupEventHandlers();
    }

    private void setupUI() {
        txtPasswordVisible.setManaged(false);
        txtPasswordVisible.setVisible(false);
        iconEye.setIcon(FontAwesomeIcon.EYE);
        loadingPane.setVisible(false);
        loadingPane.setManaged(false);

        // Add key event handlers
        txtUsername.setOnKeyPressed(this::handleKeyPress);
        txtPassword.setOnKeyPressed(this::handleKeyPress);
        txtPasswordVisible.setOnKeyPressed(this::handleKeyPress);

        // Make login form draggable
        rootPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        rootPane.setOnMouseDragged(event -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    private void initializeAnimations() {
        setupBackgroundAnimations();
    }

    private void setupBackgroundAnimations() {
        animateBackgroundShape(bgCircle1, -20, 20, 3);
        animateBackgroundShape(bgCircle2, 20, -20, 4);
        animateBackgroundShape(bgCircle3, -15, 15, 5);
    }

    private void animateBackgroundShape(Circle circle, double startY, double endY, double duration) {
        TranslateTransition translate = new TranslateTransition(Duration.seconds(duration), circle);
        translate.setByY(endY);
        translate.setCycleCount(TranslateTransition.INDEFINITE);
        translate.setAutoReverse(true);

        ScaleTransition scale = new ScaleTransition(Duration.seconds(duration), circle);
        scale.setByX(0.2);
        scale.setByY(0.2);
        scale.setCycleCount(TranslateTransition.INDEFINITE);
        scale.setAutoReverse(true);

        ParallelTransition parallel = new ParallelTransition(translate, scale);
        parallel.play();
    }

    private void setupEventHandlers() {
        // Password field sync
        txtPassword.textProperty().addListener((obs, old, newValue) -> {
            if (!passwordVisible) {
                txtPasswordVisible.setText(newValue);
            }
        });

        txtPasswordVisible.textProperty().addListener((obs, old, newValue) -> {
            if (passwordVisible) {
                txtPassword.setText(newValue);
            }
        });

        // Enable/disable login button
        btnLogin.disableProperty().bind(
                txtUsername.textProperty().isEmpty()
                        .or(txtPassword.textProperty().isEmpty()
                                .and(txtPasswordVisible.textProperty().isEmpty()))
        );
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String username = txtUsername.getText();
            String password = passwordVisible ? txtPasswordVisible.getText() : txtPassword.getText();

            if (!username.isEmpty() && !password.isEmpty()) {
                handleLogin();
            } else {
                NotificationUtil.showWarning("Please enter both username and password");
            }
        }
    }

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = passwordVisible ? txtPasswordVisible.getText() : txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            NotificationUtil.showWarning("Please enter both username and password");
            return;
        }

        showLoading(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> {
            try {
                UserDTO user = userBO.findByUsername(username);
                if (user != null && PasswordEncoder.verify(password, user.getPassword())) {
                    // Update last login time
                    user.setLastLogin(LocalDateTime.now());
                    userBO.updateUser(user);
                    onLoginSuccess(user);
                } else {
                    onLoginFailed();
                }
            } catch (Exception ex) {
                NotificationUtil.showError("Login failed: " + ex.getMessage());
                onLoginFailed();
            } finally {
                showLoading(false);
            }
        });
        pause.play();
    }

    private void onLoginSuccess(UserDTO user) {
        String roleText = user.getRole() == Role.ADMIN ? "Administrator" : "Staff";
        NotificationUtil.showSuccess("Welcome back, " + user.getUsername() + "!");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard_form.fxml"));
            Scene scene = new Scene(loader.load());

            DashboardFormController dashboardController = loader.getController();
            dashboardController.setUserInfo(user.getUsername(), roleText);
            dashboardController.initializeWithUser(user);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (IOException e) {
            NotificationUtil.showError("Failed to load dashboard: " + e.getMessage());
        }
    }

    private void onLoginFailed() {
        NotificationUtil.showError("Invalid username or password");
        playShakeAnimation();
        txtPassword.clear();
        txtPasswordVisible.clear();
    }

    private void playShakeAnimation() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(rootPane.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(100), new KeyValue(rootPane.translateXProperty(), -10)),
                new KeyFrame(Duration.millis(200), new KeyValue(rootPane.translateXProperty(), 10)),
                new KeyFrame(Duration.millis(300), new KeyValue(rootPane.translateXProperty(), -10)),
                new KeyFrame(Duration.millis(400), new KeyValue(rootPane.translateXProperty(), 10)),
                new KeyFrame(Duration.millis(500), new KeyValue(rootPane.translateXProperty(), 0))
        );
        timeline.play();
    }

    @FXML
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            txtPassword.setManaged(false);
            txtPassword.setVisible(false);
            txtPasswordVisible.setManaged(true);
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setText(txtPassword.getText());
            iconEye.setIcon(FontAwesomeIcon.EYE_SLASH);
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length());
        } else {
            txtPassword.setManaged(true);
            txtPassword.setVisible(true);
            txtPasswordVisible.setManaged(false);
            txtPasswordVisible.setVisible(false);
            txtPassword.setText(txtPasswordVisible.getText());
            iconEye.setIcon(FontAwesomeIcon.EYE);
            txtPassword.requestFocus();
            txtPassword.positionCaret(txtPassword.getText().length());
        }
    }

    @FXML
    private void handleForgotPassword() {
        String username = txtUsername.getText();
        if (username.isEmpty()) {
            NotificationUtil.showWarning("Please enter your username first");
            return;
        }

        try {
            UserDTO user = userBO.findByUsername(username);
            if (user == null) {
                NotificationUtil.showError("No account found with this username");
                return;
            }

            NotificationUtil.showInfo("Opening password reset window");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/forgot_password_form.fxml"));
            Parent root = loader.load();

            // Pass the user to ForgotPasswordFormController
            ForgotPasswordController controller = loader.getController();
            controller.initData(user);

            Scene scene = new Scene(root);
            scene.setFill(null);

            Stage popupStage = new Stage(StageStyle.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(btnLogin.getScene().getWindow());
            popupStage.centerOnScreen();
            popupStage.show();

        } catch (Exception e) {
            NotificationUtil.showError("Error verifying username: " + e.getMessage());
        }
    }

    private void showLoading(boolean show) {
        loadingPane.setVisible(show);
        loadingPane.setManaged(show);
        btnLogin.setVisible(!show);
        btnLogin.setManaged(!show);
    }

    @FXML
    private void minimizeStage() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void maximizeStage() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        if (stage != null) {
            if (stage.isMaximized()) {
                stage.setMaximized(false);
            } else {
                stage.setMaximized(true);
            }
        }
    }

    @FXML
    private void closeStage() {
        if (AlertUtil.showConfirmation("Exit", "Are you sure you want to exit?")) {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
        }
    }
}