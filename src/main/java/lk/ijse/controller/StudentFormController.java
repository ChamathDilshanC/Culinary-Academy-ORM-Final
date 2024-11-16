package lk.ijse.controller;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lk.ijse.bo.BOFactory;
import lk.ijse.bo.custom.StudentBO;
import lk.ijse.dto.StudentDTO;
import lk.ijse.util.ValidationUtil;
import lk.ijse.util.ValidationUtil.ValidationResult;

import java.time.LocalDateTime;
import java.util.Optional;

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
    @FXML private TableView<StudentDTO> studentTable;
    @FXML private TableColumn<StudentDTO, Integer> colId;
    @FXML private TableColumn<StudentDTO, String> colFullName;
    @FXML private TableColumn<StudentDTO, String> colEmail;
    @FXML private TableColumn<StudentDTO, String> colPhone;
    @FXML private TableColumn<StudentDTO, String> colAddress;
    @FXML private TableColumn<StudentDTO, Void> colActions;
    @FXML private Label totalStudentsLabel;

    private final StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);
    private final ObservableList<StudentDTO> studentList = FXCollections.observableArrayList();
    private FilteredList<StudentDTO> filteredStudents;
    private boolean isEditMode = false;
    private Integer currentEditingId = null;

    @FXML
    public void initialize() {
        setupTable();
        loadStudents();
        generateAndSetNextId();
        setupSearchFilter();
        Platform.runLater(this::playInitialAnimation);
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        colFullName.setCellValueFactory(cellData -> {
            StudentDTO student = cellData.getValue();
            String fullName = student.getFirstName() + " " + student.getLastName();
            return javafx.beans.binding.Bindings.createStringBinding(() -> fullName);
        });

        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        setupActionColumn();

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

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredStudents.setPredicate(student -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return student.getFirstName().toLowerCase().contains(lowerCaseFilter)
                        || student.getLastName().toLowerCase().contains(lowerCaseFilter)
                        || student.getEmail().toLowerCase().contains(lowerCaseFilter)
                        || student.getPhoneNumber().toLowerCase().contains(lowerCaseFilter)
                        || student.getAddress().toLowerCase().contains(lowerCaseFilter);
            });
            updateTotalStudentsLabel();
        });
    }

    @FXML
    public void validateField(KeyEvent event) {
        TextField field = (TextField) event.getSource();
        ValidationUtil.ValidationField validationType = null;

        if (field == firstNameField || field == lastNameField) {
            validationType = ValidationUtil.ValidationField.NAME;
        } else if (field == emailField) {
            validationType = ValidationUtil.ValidationField.EMAIL;
        } else if (field == phoneField) {
            validationType = ValidationUtil.ValidationField.PHONE;
        } else if (field == addressField) {
            validationType = ValidationUtil.ValidationField.ADDRESS;
        }

        if (validationType != null) {
            ValidationResult result = ValidationUtil.validate(field.getText(), validationType);
            updateFieldValidation(field, result.isValid());
        }
    }

    private void updateFieldValidation(TextField field, boolean isValid) {
        field.getStyleClass().removeAll("field-error", "field-success");
        field.getStyleClass().add(isValid ? "field-success" : "field-error");
    }

    @FXML
    public void handleSave() {
        if (!validateAllFields()) {
            showError("Validation Error", "Please fill all fields correctly.");
            return;
        }

        try {
            StudentDTO student = new StudentDTO(
                    isEditMode ? currentEditingId : Integer.parseInt(idField.getText()),
                    1, // Default userId - modify as needed
                    firstNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    addressField.getText(),
                    LocalDateTime.now()
            );

            boolean success;
            if (isEditMode) {
                success = studentBO.updateStudent(student);
            } else {
                success = studentBO.saveStudent(student);
            }

            if (success) {
                showSuccess(isEditMode ? "Student Updated" : "Student Saved");
                handleClear();
                loadStudents();
                if (!isEditMode) {
                    generateAndSetNextId();
                }
            }
        } catch (Exception e) {
            showError("Error Saving Student", e.getMessage());
        }
    }

    @FXML
    public void handleClear() {
        isEditMode = false;
        currentEditingId = null;
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
        saveBtn.setText("Save");
        generateAndSetNextId();

        // Clear validation styles
        clearValidationStyles(firstNameField);
        clearValidationStyles(lastNameField);
        clearValidationStyles(emailField);
        clearValidationStyles(phoneField);
        clearValidationStyles(addressField);
    }

    private void clearValidationStyles(TextField field) {
        field.getStyleClass().removeAll("field-error", "field-success");
    }

    @FXML
    public void handleToggleForm() {
        TranslateTransition translate = new TranslateTransition(Duration.millis(300), formContainer);
        FadeTransition fade = new FadeTransition(Duration.millis(300), formContainer);

        boolean isVisible = formContainer.isVisible();
        if (isVisible) {
            translate.setFromY(0);
            translate.setToY(-20);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(e -> formContainer.setVisible(false));
            hideFormBtn.setText("Show Form");
        } else {
            formContainer.setVisible(true);
            translate.setFromY(-20);
            translate.setToY(0);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            hideFormBtn.setText("Hide Form");
        }

        translate.play();
        fade.play();
    }

    private boolean validateAllFields() {
        boolean firstNameValid = ValidationUtil.validate(firstNameField.getText(), ValidationUtil.ValidationField.NAME).isValid();
        boolean lastNameValid = ValidationUtil.validate(lastNameField.getText(), ValidationUtil.ValidationField.NAME).isValid();
        boolean emailValid = ValidationUtil.validate(emailField.getText(), ValidationUtil.ValidationField.EMAIL).isValid();
        boolean phoneValid = ValidationUtil.validate(phoneField.getText(), ValidationUtil.ValidationField.PHONE).isValid();
        boolean addressValid = ValidationUtil.validate(addressField.getText(), ValidationUtil.ValidationField.ADDRESS).isValid();

        updateFieldValidation(firstNameField, firstNameValid);
        updateFieldValidation(lastNameField, lastNameValid);
        updateFieldValidation(emailField, emailValid);
        updateFieldValidation(phoneField, phoneValid);
        updateFieldValidation(addressField, addressValid);

        return firstNameValid && lastNameValid && emailValid && phoneValid && addressValid;
    }

    private void handleEdit(StudentDTO student) {
        isEditMode = true;
        currentEditingId = student.getStudentId();
        idField.setText(student.getStudentId().toString());
        firstNameField.setText(student.getFirstName());
        lastNameField.setText(student.getLastName());
        emailField.setText(student.getEmail());
        phoneField.setText(student.getPhoneNumber());
        addressField.setText(student.getAddress());
        saveBtn.setText("Update");

        if (!formContainer.isVisible()) {
            handleToggleForm();
        }
    }

    private void handleDelete(StudentDTO student) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Student");
        alert.setContentText("Are you sure you want to delete this student?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (studentBO.deleteStudent(student.getStudentId().toString())) {
                    showSuccess("Student Deleted");
                    loadStudents();
                }
            } catch (Exception e) {
                showError("Error Deleting Student", e.getMessage());
            }
        }
    }

    private void loadStudents() {
        try {
            studentList.clear();
            studentList.addAll(studentBO.getAllStudents());
            updateTotalStudentsLabel();
        } catch (Exception e) {
            showError("Error Loading Students", e.getMessage());
        }
    }

    private void generateAndSetNextId() {
        try {
            Integer nextId = studentBO.getNextStudentId();
            idField.setText(nextId.toString());
        } catch (Exception e) {
            showError("Error Generating ID", e.getMessage());
        }
    }

    private void updateTotalStudentsLabel() {
        int total = filteredStudents.size();
        totalStudentsLabel.setText(String.format("Total Students: %d", total));
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

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    public void handleSearch(KeyEvent event) {
        String searchText = searchField.getText();
        filteredStudents.setPredicate(student -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = searchText.toLowerCase();

            return student.getFirstName().toLowerCase().contains(lowerCaseFilter)
                    || student.getLastName().toLowerCase().contains(lowerCaseFilter)
                    || student.getEmail().toLowerCase().contains(lowerCaseFilter)
                    || student.getPhoneNumber().toLowerCase().contains(lowerCaseFilter)
                    || student.getAddress().toLowerCase().contains(lowerCaseFilter);
        });
        updateTotalStudentsLabel();
    }
}
