package lk.ijse.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lk.ijse.bo.BOFactory;
import lk.ijse.bo.custom.UserBO;
import lk.ijse.dto.UserDTO;
import lk.ijse.entity.User.Role;
import lk.ijse.util.PasswordEncoder;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class UserFormController implements Initializable {
    @FXML private VBox formContainer;
    @FXML private TextField txtUserId;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private CheckBox chkShowPassword;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<Role> cmbRole;
    @FXML private Label lblLastLogin;
    @FXML private TextField txtSearch;
    @FXML private TableView<UserDTO> tblUser;
    @FXML private TableColumn<UserDTO, String> colUserId;
    @FXML private TableColumn<UserDTO, String> colUsername;
    @FXML private TableColumn<UserDTO, String> colEmail;
    @FXML private TableColumn<UserDTO, String> colRole;
    @FXML private TableColumn<UserDTO, String> colLastLogin;
    @FXML private TableColumn<UserDTO, Void> colActions;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnClear;
    @FXML private Button btnShowHideForm;
    @FXML private Label totalUsersLabel;

    private final UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.USER);
    private final ObservableList<UserDTO> userList = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private boolean isFormVisible = true;

    // Validation patterns
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            setupUI();
            setupValidation();
            setupTable();
            loadUsers();
            updateTotalUsers();
            try {
                generateNewId();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setupUI() {
        // Initialize ComboBox
        cmbRole.setItems(FXCollections.observableArrayList(Role.values()));

        // Setup password visibility toggle
        setupPasswordToggle();

        // Initial button states
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        txtUserId.setEditable(false);

        // Setup search functionality
        setupSearch();
    }

    private void setupPasswordToggle() {
        chkShowPassword.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                txtPasswordVisible.setText(txtPassword.getText());
                txtPasswordVisible.setVisible(true);
                txtPasswordVisible.setManaged(true);
                txtPassword.setVisible(false);
                txtPassword.setManaged(false);
            } else {
                txtPassword.setText(txtPasswordVisible.getText());
                txtPassword.setVisible(true);
                txtPassword.setManaged(true);
                txtPasswordVisible.setVisible(false);
                txtPasswordVisible.setManaged(false);
            }
        });

        // Sync password fields
        txtPassword.textProperty().addListener((obs, old, newVal) -> {
            if (!chkShowPassword.isSelected()) {
                txtPasswordVisible.setText(newVal);
            }
        });

        txtPasswordVisible.textProperty().addListener((obs, old, newVal) -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setText(newVal);
            }
        });
    }

    private void setupValidation() {
        // Username validation
        setupFieldValidation(txtUsername, USERNAME_PATTERN,
                "Username must be 4-20 characters and can contain letters, numbers, and underscores");

        // Email validation
        setupFieldValidation(txtEmail, EMAIL_PATTERN,
                "Please enter a valid email address");

        // Password validation
        setupFieldValidation(txtPassword, PASSWORD_PATTERN,
                "Password must have 8+ chars, including uppercase, lowercase, numbers, and special chars");
        setupFieldValidation(txtPasswordVisible, PASSWORD_PATTERN,
                "Password must have 8+ chars, including uppercase, lowercase, numbers, and special chars");
    }

    private void setupFieldValidation(TextField field, Pattern pattern, String errorMessage) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                boolean isValid = pattern.matcher(newVal).matches();
                field.setStyle(isValid ? "" : "-fx-border-color: red;");

                if (!isValid) {
                    field.setTooltip(new Tooltip(errorMessage));
                } else {
                    field.setTooltip(null);
                }
            } else {
                field.setStyle("");
                field.setTooltip(null);
            }
        });
    }

    private void setupTable() {
        // Setup columns
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colLastLogin.setCellValueFactory(cellData -> {
            LocalDateTime lastLogin = cellData.getValue().getLastLogin();
            return new SimpleStringProperty(lastLogin != null ? lastLogin.format(formatter) : "Never");
        });

        // Setup action column
        setupActionColumn();

        // Row selection handler
        tblUser.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
                btnSave.setDisable(true);
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
            }
        });
    }

    private void setupActionColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox actionBox = new HBox(5, editButton, deleteButton);

            {
                editButton.getStyleClass().add("edit-button");
                deleteButton.getStyleClass().add("delete-button");

                editButton.setOnAction(event -> {
                    UserDTO user = getTableView().getItems().get(getIndex());
                    populateFields(user);
                    btnSave.setDisable(true);
                    btnUpdate.setDisable(false);
                    btnDelete.setDisable(false);
                });

                deleteButton.setOnAction(event -> {
                    UserDTO user = getTableView().getItems().get(getIndex());
                    handleDelete(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionBox);
            }
        });
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                tblUser.setItems(userList);
            } else {
                ObservableList<UserDTO> filteredList = FXCollections.observableArrayList();
                userList.forEach(user -> {
                    if (matchesSearch(user, newVal.toLowerCase())) {
                        filteredList.add(user);
                    }
                });
                tblUser.setItems(filteredList);
            }
            updateTotalUsers();
        });
    }

    private boolean matchesSearch(UserDTO user, String searchTerm) {
        return user.getUserId().toString().contains(searchTerm) ||
                user.getUsername().toLowerCase().contains(searchTerm) ||
                user.getEmail().toLowerCase().contains(searchTerm) ||
                user.getRole().toString().toLowerCase().contains(searchTerm);
    }

    private void loadUsers() {
        try {
            userList.clear();
            userList.addAll(userBO.getAllUsers());
            tblUser.setItems(userList);
            updateTotalUsers();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load users", e.getMessage());
        }
    }

    private void updateTotalUsers() {
        totalUsersLabel.setText("Total Users: " + tblUser.getItems().size());
    }

    private void populateFields(UserDTO user) {
        txtUserId.setText(user.getUserId().toString());
        txtUsername.setText(user.getUsername());
        txtEmail.setText(user.getEmail());
        cmbRole.setValue(user.getRole());
        txtPassword.clear();
        txtPasswordVisible.clear();

        LocalDateTime lastLogin = user.getLastLogin();
        lblLastLogin.setText("Last Login: " + (lastLogin != null ? lastLogin.format(formatter) : "Never"));
    }

    @FXML
    private void btnShowHideFormOnAction() {
        isFormVisible = !isFormVisible;
        formContainer.setVisible(isFormVisible);
        formContainer.setManaged(isFormVisible);
        btnShowHideForm.setText(isFormVisible ? "Hide Form" : "Show Form");
    }

    @FXML
    private void btnSaveOnAction() {
        if (!validateInputs()) return;

        try {
            if (userBO.existsByUsername(txtUsername.getText())) {
                showAlert(Alert.AlertType.ERROR, "Error", "Username already exists");
                return;
            }

            if (userBO.existsByEmail(txtEmail.getText())) {
                showAlert(Alert.AlertType.ERROR, "Error", "Email already exists");
                return;
            }

            UserDTO dto = new UserDTO();
            dto.setUsername(txtUsername.getText());
            dto.setPassword(PasswordEncoder.encode(txtPassword.getText()));
            dto.setEmail(txtEmail.getText());
            dto.setRole(cmbRole.getValue());
            dto.setCreatedAt(LocalDateTime.now());
            dto.setUpdatedAt(LocalDateTime.now());

            if (userBO.saveUser(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User saved successfully!");
                clearFields();
                loadUsers();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save user", e.getMessage());
        }
    }

    @FXML
    private void btnUpdateOnAction() {
        if (!validateInputs(true)) return;

        try {
            String userId = txtUserId.getText();
            UserDTO existingUser = userBO.findByUsername(txtUsername.getText());
            if (existingUser != null && !existingUser.getUserId().toString().equals(userId)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Username already exists");
                return;
            }

            existingUser = userBO.findByEmail(txtEmail.getText());
            if (existingUser != null && !existingUser.getUserId().toString().equals(userId)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Email already exists");
                return;
            }

            UserDTO dto = userBO.searchUser(userId);
            dto.setUsername(txtUsername.getText());
            if (!txtPassword.getText().isEmpty()) {
                dto.setPassword(PasswordEncoder.encode(txtPassword.getText()));
            }
            dto.setEmail(txtEmail.getText());
            dto.setRole(cmbRole.getValue());
            dto.setUpdatedAt(LocalDateTime.now());

            if (userBO.updateUser(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully!");
                clearFields();
                loadUsers();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update user", e.getMessage());
        }
    }

    @FXML
    private void btnDeleteOnAction() {
        handleDelete(tblUser.getSelectionModel().getSelectedItem());
    }

    private void handleDelete(UserDTO user) {
        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a user to delete");
            return;
        }

        Optional<ButtonType> result = showConfirmation(
                "Delete User",
                "Are you sure you want to delete this user?",
                "This action cannot be undone."
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (userBO.deleteUser(user.getUserId().toString())) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully!");
                    clearFields();
                    loadUsers();
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user", e.getMessage());
            }
        }
    }

    @FXML
    private void btnClearOnAction() {
        clearFields();
    }

    private void clearFields() {
        txtUserId.clear();
        txtUsername.clear();
        txtPassword.clear();
        txtPasswordVisible.clear();
        txtEmail.clear();
        cmbRole.setValue(null);
        lblLastLogin.setText("Last Login: Never");

        btnSave.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);

        tblUser.getSelectionModel().clearSelection();

        txtUsername.setStyle("");
        txtEmail.setStyle("");
        txtPassword.setStyle("");
        txtPasswordVisible.setStyle("");
    }

    private boolean validateInputs() {
        return validateInputs(false);
    }

    private boolean validateInputs(boolean isUpdate) {
        StringBuilder errors = new StringBuilder();

        if (txtUsername.getText().isEmpty()) {
            errors.append("Username is required\n");
        } else if (!USERNAME_PATTERN.matcher(txtUsername.getText()).matches()) {
            errors.append("Invalid username format\n");
        }

        if (txtEmail.getText().isEmpty()) {
            errors.append("Email is required\n");
        } else if (!EMAIL_PATTERN.matcher(txtEmail.getText()).matches()) {
            errors.append("Invalid email format\n");
        }

        if (!isUpdate && txtPassword.getText().isEmpty()) {
            errors.append("Password is required\n");
        } else if (!txtPassword.getText().isEmpty() && !PASSWORD_PATTERN.matcher(txtPassword.getText()).matches()) {
            errors.append("Invalid password format\n");
        }

        if (cmbRole.getValue() == null) {
            errors.append("Role must be selected\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", errors.toString());
            return false;
        }

        return true;
    }

    @FXML
    private void validateField() {
        boolean isValid = true;
        StringBuilder message = new StringBuilder();

        // Validate username
        if (!txtUsername.getText().isEmpty() && !USERNAME_PATTERN.matcher(txtUsername.getText()).matches()) {
            isValid = false;
            message.append("Invalid username format\n");
            txtUsername.setStyle("-fx-border-color: red;");
        } else {
            txtUsername.setStyle("");
        }

        // Validate email
        if (!txtEmail.getText().isEmpty() && !EMAIL_PATTERN.matcher(txtEmail.getText()).matches()) {
            isValid = false;
            message.append("Invalid email format\n");
            txtEmail.setStyle("-fx-border-color: red;");
        } else {
            txtEmail.setStyle("");
        }

        // Validate password
        String passwordText = chkShowPassword.isSelected() ? txtPasswordVisible.getText() : txtPassword.getText();
        if (!passwordText.isEmpty() && !PASSWORD_PATTERN.matcher(passwordText).matches()) {
            isValid = false;
            message.append("Invalid password format\n");
            if (chkShowPassword.isSelected()) {
                txtPasswordVisible.setStyle("-fx-border-color: red;");
            } else {
                txtPassword.setStyle("-fx-border-color: red;");
            }
        } else {
            txtPassword.setStyle("");
            txtPasswordVisible.setStyle("");
        }

        // Update save button state
        btnSave.setDisable(!isValid);

        // Show validation message if there are errors
        if (!isValid) {
            showValidationMessage(message.toString());
        }
    }

    private void showValidationMessage(String message) {
        Tooltip tooltip = new Tooltip(message);
        tooltip.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white;");
        tooltip.setAutoHide(true);

        if (!txtUsername.getText().isEmpty() && !USERNAME_PATTERN.matcher(txtUsername.getText()).matches()) {
            txtUsername.setTooltip(tooltip);
        }

        if (!txtEmail.getText().isEmpty() && !EMAIL_PATTERN.matcher(txtEmail.getText()).matches()) {
            txtEmail.setTooltip(tooltip);
        }

        String passwordText = chkShowPassword.isSelected() ? txtPasswordVisible.getText() : txtPassword.getText();
        if (!passwordText.isEmpty() && !PASSWORD_PATTERN.matcher(passwordText).matches()) {
            if (chkShowPassword.isSelected()) {
                txtPasswordVisible.setTooltip(tooltip);
            } else {
                txtPassword.setTooltip(tooltip);
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        styleAlert(alert);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        styleAlert(alert);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        styleAlert(alert);
        return alert.showAndWait();
    }

    private void styleAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/UserManagement.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
    }

    // Utility methods for user operations
    public void updateLastLoginForUser(Integer userId) {
        try {
            UserDTO user = userBO.searchUser(userId.toString());
            if (user != null) {
                user.setLastLogin(LocalDateTime.now());
                userBO.updateUser(user);
                loadUsers(); // Refresh the table to show updated last login time
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Role getUserRole(String username) {
        try {
            UserDTO user = userBO.findByUsername(username);
            return user != null ? user.getRole() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean authenticateUser(String username, String password) {
        try {
            UserDTO user = userBO.findByUsername(username);
            if (user != null) {
                boolean isValid = PasswordEncoder.verify(password, user.getPassword());
                if (isValid) {
                    updateLastLoginForUser(user.getUserId());
                }
                return isValid;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Additional utility methods
    public void filterByRole(Role role) {
        try {
            if (role == null) {
                loadUsers();
            } else {
                userList.clear();
                userList.addAll(userBO.findByRole(role));
                tblUser.setItems(userList);
                updateTotalUsers();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to filter users by role", e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = txtSearch.getText().toLowerCase();
        ObservableList<UserDTO> filteredList = FXCollections.observableArrayList();

        if (searchText.isEmpty()) {
            tblUser.setItems(userList);
        } else {
            userList.forEach(user -> {
                if (matchesSearch(user, searchText)) {
                    filteredList.add(user);
                }
            });
            tblUser.setItems(filteredList);
        }
        updateTotalUsers();
    }

    private  void generateNewId() throws Exception {
        Integer currentId = userBO.getCurrentId();
        currentId = currentId != 0 ? currentId + 1 : 1;
        txtUserId.setText(currentId.toString());

    }
}