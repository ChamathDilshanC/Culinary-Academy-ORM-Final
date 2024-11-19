package lk.ijse.controller;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lk.ijse.bo.BOFactory;
import lk.ijse.bo.custom.StudentBO;
import lk.ijse.dto.StudentDTO;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

public class StudentFormController {

    @FXML private VBox formContainer;
    @FXML private TextField idField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TextField searchField;
    @FXML private Button saveBtn;
    @FXML private Button clearBtn;
    @FXML private Button hideFormBtn;
    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;
    @FXML private TableView<StudentDTO> studentTable;
    @FXML private TableColumn<StudentDTO, String> colId;
    @FXML private TableColumn<StudentDTO, String> colFullName;
    @FXML private TableColumn<StudentDTO, String> colEmail;
    @FXML private TableColumn<StudentDTO, String> colPhone;
    @FXML private TableColumn<StudentDTO, String> colAddress;
    @FXML private TableColumn<StudentDTO, Void> colActions;
    @FXML private Label totalStudentsLabel;

    // Validation UI elements
    @FXML private Label firstNameValidation;
    @FXML private Label lastNameValidation;
    @FXML private Label emailValidation;
    @FXML private Label phoneValidation;
    @FXML private Label addressValidation;
    @FXML private HBox firstNameValidationContainer;
    @FXML private HBox lastNameValidationContainer;
    @FXML private HBox emailValidationContainer;
    @FXML private HBox phoneValidationContainer;
    @FXML private HBox addressValidationContainer;

    private final StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);
    private final ObservableList<StudentDTO> studentList = FXCollections.observableArrayList();
    private FilteredList<StudentDTO> filteredStudents;
    private boolean isFormVisible = true;
    private boolean isEditMode = false;
    private Integer currentEditingId = null;

    // Validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]{2,30}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^[A-Za-z0-9\\s,./-]{5,100}$");

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            setupValidationContainers();
            setupTable();
            loadStudents();
            setupSearch();
            setupInitialButtonStates();
            setupValidationListeners();
            generateNextId();
            playInitialAnimation();
        });
    }

    private void setupValidationContainers() {
        firstNameValidationContainer.setManaged(false);
        firstNameValidationContainer.setVisible(false);
        lastNameValidationContainer.setManaged(false);
        lastNameValidationContainer.setVisible(false);
        emailValidationContainer.setManaged(false);
        emailValidationContainer.setVisible(false);
        phoneValidationContainer.setManaged(false);
        phoneValidationContainer.setVisible(false);
        addressValidationContainer.setManaged(false);
        addressValidationContainer.setVisible(false);
    }

    private void setupInitialButtonStates() {
        updateBtn.setVisible(false);
        updateBtn.setManaged(false);
        deleteBtn.setVisible(false);
        deleteBtn.setManaged(false);
        idField.setEditable(false);
    }

    private void setupValidationListeners() {
        firstNameField.textProperty().addListener((obs, old, newVal) -> validateField());
        lastNameField.textProperty().addListener((obs, old, newVal) -> validateField());
        emailField.textProperty().addListener((obs, old, newVal) -> validateField());
        phoneField.textProperty().addListener((obs, old, newVal) -> validateField());
        addressField.textProperty().addListener((obs, old, newVal) -> validateField());
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        colFullName.setCellValueFactory(cellData -> {
            StudentDTO student = cellData.getValue();
            String fullName = student.getFirstName() + " " + student.getLastName();
            return new SimpleStringProperty(fullName);
        });

        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        setupActionColumn();

        // Set up table selection listener
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                handleEdit(newSelection);
            }
        });

        filteredStudents = new FilteredList<>(studentList, p -> true);
        studentTable.setItems(filteredStudents);
    }

    private void setupActionColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox container = new HBox(8, editButton, deleteButton);

            {
                editButton.getStyleClass().add("edit-button");
                deleteButton.getStyleClass().add("delete-button");
                container.getStyleClass().add("action-container");

                editButton.setOnAction(event -> {
                    StudentDTO student = getTableView().getItems().get(getIndex());
                    handleEdit(student);
                });

                deleteButton.setOnAction(event -> {
                    StudentDTO student = getTableView().getItems().get(getIndex());
                    handleDelete(student);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredStudents.setPredicate(student -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return student.getFirstName().toLowerCase().contains(lowerCaseFilter) ||
                        student.getLastName().toLowerCase().contains(lowerCaseFilter) ||
                        student.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                        student.getPhoneNumber().contains(lowerCaseFilter) ||
                        student.getAddress().toLowerCase().contains(lowerCaseFilter);
            });
            updateTotalStudentsLabel();
        });
    }

    @FXML
    private void validateField() {
        validateName(firstNameField, firstNameValidation, firstNameValidationContainer, "First name");
        validateName(lastNameField, lastNameValidation, lastNameValidationContainer, "Last name");
        validateEmail();
        validatePhone();
        validateAddress();

        updateSaveButtonState();
    }

    private void validateName(TextField field, Label validationLabel, HBox container, String fieldName) {
        if (!field.getText().isEmpty()) {
            boolean isValid = NAME_PATTERN.matcher(field.getText()).matches();
            updateFieldValidation(field, validationLabel, container,
                    isValid, fieldName + " must contain only letters and be 2-30 characters long");
        } else {
            resetFieldValidation(field, validationLabel, container);
        }
    }

    private void validateEmail() {
        if (!emailField.getText().isEmpty()) {
            boolean isValid = EMAIL_PATTERN.matcher(emailField.getText()).matches();
            updateFieldValidation(emailField, emailValidation, emailValidationContainer,
                    isValid, "Please enter a valid email address");
        } else {
            resetFieldValidation(emailField, emailValidation, emailValidationContainer);
        }
    }

    private void validatePhone() {
        if (!phoneField.getText().isEmpty()) {
            boolean isValid = PHONE_PATTERN.matcher(phoneField.getText()).matches();
            updateFieldValidation(phoneField, phoneValidation, phoneValidationContainer,
                    isValid, "Phone number must be 10 digits");
        } else {
            resetFieldValidation(phoneField, phoneValidation, phoneValidationContainer);
        }
    }

    private void validateAddress() {
        if (!addressField.getText().isEmpty()) {
            boolean isValid = ADDRESS_PATTERN.matcher(addressField.getText()).matches();
            updateFieldValidation(addressField, addressValidation, addressValidationContainer,
                    isValid, "Address must be 5-100 characters and contain only letters, numbers, and basic punctuation");
        } else {
            resetFieldValidation(addressField, addressValidation, addressValidationContainer);
        }
    }

    private void updateFieldValidation(TextField field, Label validationLabel,
                                       HBox validationContainer, boolean isValid, String errorMessage) {
        field.getStyleClass().removeAll("field-error", "field-success");
        field.getStyleClass().add(isValid ? "field-success" : "field-error");

        validationContainer.setManaged(!isValid);
        validationContainer.setVisible(!isValid);
        validationLabel.setText(isValid ? "" : errorMessage);
    }

    private void resetFieldValidation(TextField field, Label validationLabel, HBox validationContainer) {
        field.getStyleClass().removeAll("field-error", "field-success");
        validationContainer.setManaged(false);
        validationContainer.setVisible(false);
        validationLabel.setText("");
    }

    private void updateSaveButtonState() {
        boolean isValid = !firstNameField.getText().isEmpty() &&
                !lastNameField.getText().isEmpty() &&
                !emailField.getText().isEmpty() &&
                !phoneField.getText().isEmpty() &&
                !addressField.getText().isEmpty();

        if (isEditMode) {
            updateBtn.setDisable(!isValid);
        } else {
            saveBtn.setDisable(!isValid);
        }
    }

    private void switchToUpdateMode() {
        isEditMode = true;
        saveBtn.setVisible(false);
        saveBtn.setManaged(false);
        updateBtn.setVisible(true);
        updateBtn.setManaged(true);
        deleteBtn.setVisible(true);
        deleteBtn.setManaged(true);
    }

    private void switchToSaveMode() {
        isEditMode = false;
        saveBtn.setVisible(true);
        saveBtn.setManaged(true);
        updateBtn.setVisible(false);
        updateBtn.setManaged(false);
        deleteBtn.setVisible(false);
        deleteBtn.setManaged(false);
    }

    @FXML
    private void handleToggleForm() {
        isFormVisible = !isFormVisible;

        TranslateTransition translate = new TranslateTransition(Duration.millis(300), formContainer);
        FadeTransition fade = new FadeTransition(Duration.millis(300), formContainer);

        if (!isFormVisible) {
            translate.setFromY(0);
            translate.setToY(-20);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(e -> {
                formContainer.setVisible(false);
                formContainer.setManaged(false);
                VBox.setVgrow(studentTable, Priority.ALWAYS);
                studentTable.setPrefHeight(Region.USE_COMPUTED_SIZE);
            });
            hideFormBtn.setText("Show Form");
        } else {
            formContainer.setVisible(true);
            formContainer.setManaged(true);
            translate.setFromY(-20);
            translate.setToY(0);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            hideFormBtn.setText("Hide Form");
            studentTable.setPrefHeight(Region.USE_COMPUTED_SIZE);
        }

        translate.play();
        fade.play();
    }

    @FXML
    private void handleSave() {
        if (!validateAllFields()) return;

        try {
            StudentDTO student = new StudentDTO(
                    Integer.parseInt(idField.getText()),
                    1, // Default userId
                    firstNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText().toLowerCase(),
                    phoneField.getText(),
                    addressField.getText(),
                    LocalDateTime.now()
            );
            if (studentBO.existsByEmail(student.getEmail())) {
                showError("Error", "Student with this email already exists");
                return;
            }
            if (studentBO.existsByPhone(student.getPhoneNumber())) {
                showError("Error", "Student with this phone number already exists");
                return;
            }

            if (studentBO.saveStudent(student)) {
                showSuccess("Student saved successfully!");
                handleClear();
                loadStudents();
                generateNextId();
            }
        } catch (Exception e) {
            showError("Error", "Failed to save student: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (!validateAllFields()) return;

        try {
            StudentDTO student = new StudentDTO(
                    currentEditingId,
                    1, // Default userId
                    firstNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText().toLowerCase(),
                    phoneField.getText(),
                    addressField.getText(),
                    LocalDateTime.now()
            );

            if (studentBO.updateStudent(student)) {
                showSuccess("Student updated successfully!");
                handleClear();
                loadStudents();
            }
        } catch (Exception e) {
            showError("Error", "Failed to update student: " + e.getMessage());
        }
    }

    private void handleEdit(StudentDTO student) {
        idField.setText(student.getStudentId().toString());
        firstNameField.setText(student.getFirstName());
        lastNameField.setText(student.getLastName());
        emailField.setText(student.getEmail());
        phoneField.setText(student.getPhoneNumber());
        addressField.setText(student.getAddress());

        currentEditingId = student.getStudentId();
        switchToUpdateMode();

        if (!formContainer.isVisible()) {
            handleToggleForm();
        }
    }

    @FXML
    private void handleDelete() throws Exception {
        if (currentEditingId == null) {
            showError("Error", "Please select a student to delete");
            return;
        }

        handleDelete(studentBO.searchStudent(currentEditingId.toString()));
    }

    private void handleDelete(StudentDTO student) {
        Optional<ButtonType> result = showConfirmation(
                "Delete Student",
                "Are you sure you want to delete this student?",
                "This action cannot be undone."
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (studentBO.deleteStudent(student.getStudentId().toString())) {
                    showSuccess("Student deleted successfully!");
                    handleClear();
                    loadStudents();
                }
            } catch (Exception e) {
                showError("Error", "Failed to delete student: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        currentEditingId = null;
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();

        switchToSaveMode();
        resetAllValidations();
        generateNextId();
        studentTable.getSelectionModel().clearSelection();
    }

    private boolean validateAllFields() {
        boolean isValid = true;

        if (firstNameField.getText().isEmpty() || !NAME_PATTERN.matcher(firstNameField.getText()).matches()) {
            updateFieldValidation(firstNameField, firstNameValidation, firstNameValidationContainer,
                    false, "First name is required and must contain only letters");
            isValid = false;
        }

        if (lastNameField.getText().isEmpty() || !NAME_PATTERN.matcher(lastNameField.getText()).matches()) {
            updateFieldValidation(lastNameField, lastNameValidation, lastNameValidationContainer,
                    false, "Last name is required and must contain only letters");
            isValid = false;
        }

        if (emailField.getText().isEmpty() || !EMAIL_PATTERN.matcher(emailField.getText()).matches()) {
            updateFieldValidation(emailField, emailValidation, emailValidationContainer,
                    false, "Valid email is required");
            isValid = false;
        }

        if (phoneField.getText().isEmpty() || !PHONE_PATTERN.matcher(phoneField.getText()).matches()) {
            updateFieldValidation(phoneField, phoneValidation, phoneValidationContainer,
                    false, "Valid 10-digit phone number is required");
            isValid = false;
        }

        if (addressField.getText().isEmpty() || !ADDRESS_PATTERN.matcher(addressField.getText()).matches()) {
            updateFieldValidation(addressField, addressValidation, addressValidationContainer,
                    false, "Valid address is required (5-100 characters)");
            isValid = false;
        }

        if (!isValid) {
            showError("Validation Error", "Please correct the highlighted fields");
        }

        return isValid;
    }

    private void resetAllValidations() {
        resetFieldValidation(firstNameField, firstNameValidation, firstNameValidationContainer);
        resetFieldValidation(lastNameField, lastNameValidation, lastNameValidationContainer);
        resetFieldValidation(emailField, emailValidation, emailValidationContainer);
        resetFieldValidation(phoneField, phoneValidation, phoneValidationContainer);
        resetFieldValidation(addressField, addressValidation, addressValidationContainer);
    }

    private void loadStudents() {
        try {
            studentList.clear();
            studentList.addAll(studentBO.getAllStudents());
            filteredStudents = new FilteredList<>(studentList, p -> true);
            studentTable.setItems(filteredStudents);
            updateTotalStudentsLabel();
        } catch (Exception e) {
            showError("Error", "Failed to load students: " + e.getMessage());
        }
    }

    private void generateNextId() {
        try {
            Integer nextId = studentBO.getNextStudentId();
            idField.setText(nextId.toString());
        } catch (Exception e) {
            showError("Error", "Failed to generate ID: " + e.getMessage());
        }
    }

    private void updateTotalStudentsLabel() {
        totalStudentsLabel.setText("Total Students: " + studentTable.getItems().size());
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert);
        alert.show();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert);
        alert.show();
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
        dialogPane.getStylesheets().add(getClass().getResource("/styles/StudentManagement.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
    }

    private void playInitialAnimation() {
        formContainer.setOpacity(0);
        formContainer.setTranslateY(-20);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), formContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), formContainer);
        slideIn.setFromY(-20);
        slideIn.setToY(0);

        fadeIn.play();
        slideIn.play();
    }

    public void handleSearch(KeyEvent keyEvent) {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredStudents.setPredicate(student -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return student.getFirstName().toLowerCase().contains(lowerCaseFilter) ||
                        student.getLastName().toLowerCase().contains(lowerCaseFilter) ||
                        student.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                        student.getPhoneNumber().contains(lowerCaseFilter) ||
                        student.getAddress().toLowerCase().contains(lowerCaseFilter);
            });
            updateTotalStudentsLabel();
        });
    }

}