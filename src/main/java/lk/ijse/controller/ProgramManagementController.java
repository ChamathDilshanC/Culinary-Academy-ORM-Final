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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lk.ijse.bo.BOFactory;
import lk.ijse.bo.custom.ProgramBO;
import lk.ijse.dto.ProgramDTO;

import java.util.Optional;
import java.util.regex.Pattern;

public class ProgramManagementController {

    @FXML private VBox formContainer;
    @FXML private TextField programIdField;
    @FXML private TextField programNameField;
    @FXML private TextField durationMonthsField;
    @FXML private TextField feeField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField searchField;
    @FXML private Button saveBtn;
    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;
    @FXML private Button clearBtn;
    @FXML private Button hideFormBtn;
    @FXML private TableView<ProgramDTO> programTable;
    @FXML private TableColumn<ProgramDTO, String> colId;
    @FXML private TableColumn<ProgramDTO, String> colName;
    @FXML private TableColumn<ProgramDTO, Integer> colDuration;
    @FXML private TableColumn<ProgramDTO, Double> colFee;
    @FXML private TableColumn<ProgramDTO, String> colDescription;
    @FXML private TableColumn<ProgramDTO, Void> colActions;
    @FXML private Label totalProgramsLabel;

    // Validation UI elements
    @FXML private Label nameValidation;
    @FXML private Label durationValidation;
    @FXML private Label feeValidation;
    @FXML private HBox nameValidationContainer;
    @FXML private HBox durationValidationContainer;
    @FXML private HBox feeValidationContainer;

    private final ProgramBO programBO = (ProgramBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PROGRAM);
    private final ObservableList<ProgramDTO> programList = FXCollections.observableArrayList();
    private FilteredList<ProgramDTO> filteredPrograms;
    private boolean isFormVisible = true;
    private boolean isEditMode = false;
    private String currentEditingId = null;

    // Validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s&-]{2,100}$");
    private static final Pattern DURATION_PATTERN = Pattern.compile("^[1-9][0-9]?$|^100$");
    private static final Pattern FEE_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$");

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            setupValidationContainers();
            setupTable();
            loadPrograms();
            setupSearch();
            setupInitialButtonStates();
            setupValidationListeners();
            generateNextId();
            playInitialAnimation();
        });
    }

    private void setupValidationContainers() {
        nameValidationContainer.setManaged(false);
        nameValidationContainer.setVisible(false);
        durationValidationContainer.setManaged(false);
        durationValidationContainer.setVisible(false);
        feeValidationContainer.setManaged(false);
        feeValidationContainer.setVisible(false);
    }

    private void setupInitialButtonStates() {
        updateBtn.setVisible(false);
        updateBtn.setManaged(false);
        deleteBtn.setVisible(false);
        deleteBtn.setManaged(false);
        programIdField.setEditable(false);
    }

    private void setupValidationListeners() {
        programNameField.textProperty().addListener((obs, old, newVal) -> validateField());
        durationMonthsField.textProperty().addListener((obs, old, newVal) -> validateField());
        feeField.textProperty().addListener((obs, old, newVal) -> validateField());
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("programId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("programName"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("durationMonths"));
        colFee.setCellValueFactory(new PropertyValueFactory<>("fee"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        setupActionColumn();

        programTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                handleEdit(newSelection);
            }
        });

        filteredPrograms = new FilteredList<>(programList, p -> true);
        programTable.setItems(filteredPrograms);
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
                    ProgramDTO program = getTableView().getItems().get(getIndex());
                    handleEdit(program);
                });

                deleteButton.setOnAction(event -> {
                    ProgramDTO program = getTableView().getItems().get(getIndex());
                    handleDelete(program);
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
            filteredPrograms.setPredicate(program -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return program.getProgramName().toLowerCase().contains(lowerCaseFilter) ||
                        program.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                        program.getProgramId().toLowerCase().contains(lowerCaseFilter);
            });
            updateTotalProgramsLabel();
        });
    }

    @FXML
    private void validateField() {
        validateName();
        validateDuration();
        validateFee();
        updateSaveButtonState();
    }

    private void validateName() {
        if (!programNameField.getText().isEmpty()) {
            boolean isValid = NAME_PATTERN.matcher(programNameField.getText()).matches();
            updateFieldValidation(programNameField, nameValidation, nameValidationContainer,
                    isValid, "Program name must contain only letters, spaces, & and -");
        } else {
            resetFieldValidation(programNameField, nameValidation, nameValidationContainer);
        }
    }

    private void validateDuration() {
        if (!durationMonthsField.getText().isEmpty()) {
            boolean isValid = DURATION_PATTERN.matcher(durationMonthsField.getText()).matches();
            updateFieldValidation(durationMonthsField, durationValidation, durationValidationContainer,
                    isValid, "Duration must be a number between 1-100");
        } else {
            resetFieldValidation(durationMonthsField, durationValidation, durationValidationContainer);
        }
    }

    private void validateFee() {
        if (!feeField.getText().isEmpty()) {
            boolean isValid = FEE_PATTERN.matcher(feeField.getText()).matches();
            updateFieldValidation(feeField, feeValidation, feeValidationContainer,
                    isValid, "Fee must be a valid number with up to 2 decimal places");
        } else {
            resetFieldValidation(feeField, feeValidation, feeValidationContainer);
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
        boolean isValid = !programNameField.getText().isEmpty() &&
                !durationMonthsField.getText().isEmpty() &&
                !feeField.getText().isEmpty();

        if (isEditMode) {
            updateBtn.setDisable(!isValid);
        } else {
            saveBtn.setDisable(!isValid);
        }
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
                VBox.setVgrow(programTable, Priority.ALWAYS);
                programTable.setPrefHeight(Region.USE_COMPUTED_SIZE);
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
            programTable.setPrefHeight(Region.USE_COMPUTED_SIZE);
        }

        translate.play();
        fade.play();
    }

    @FXML
    private void handleSave() {
        if (!validateAllFields()) return;

        try {
            ProgramDTO program = new ProgramDTO(
                    programIdField.getText(),
                    programNameField.getText(),
                    Integer.parseInt(durationMonthsField.getText()),
                    Double.parseDouble(feeField.getText()),
                    descriptionArea.getText()
            );

            if (programBO.saveProgram(program)) {
                showSuccess("Program saved successfully!");
                handleClear();
                loadPrograms();
                generateNextId();
            }
        } catch (Exception e) {
            showError("Error", "Failed to save program: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (!validateAllFields()) return;

        try {
            ProgramDTO program = new ProgramDTO(
                    currentEditingId,
                    programNameField.getText(),
                    Integer.parseInt(durationMonthsField.getText()),
                    Double.parseDouble(feeField.getText()),
                    descriptionArea.getText()
            );

            if (programBO.updateProgram(program)) {
                showSuccess("Program updated successfully!");
                handleClear();
                loadPrograms();
            }
        } catch (Exception e) {
            showError("Error", "Failed to update program: " + e.getMessage());
        }
    }

    private void handleEdit(ProgramDTO program) {
        programIdField.setText(program.getProgramId());
        programNameField.setText(program.getProgramName());
        durationMonthsField.setText(String.valueOf(program.getDurationMonths()));
        feeField.setText(String.valueOf(program.getFee()));
        descriptionArea.setText(program.getDescription());

        currentEditingId = program.getProgramId();
        switchToUpdateMode();

        if (!formContainer.isVisible()) {
            handleToggleForm();
        }
    }

    @FXML
    private void handleDelete() {
        if (currentEditingId == null) {
            showError("Error", "Please select a program to delete");
            return;
        }

        try {
            handleDelete(programBO.searchProgram(currentEditingId));
        } catch (Exception e) {
            showError("Error", "Failed to delete program: " + e.getMessage());
        }
    }

    private void handleDelete(ProgramDTO program) {
        Optional<ButtonType> result = showConfirmation(
                "Delete Program",
                "Are you sure you want to delete this program?",
                "This action cannot be undone."
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (programBO.deleteProgram(program.getProgramId())) {
                    showSuccess("Program deleted successfully!");
                    handleClear();
                    loadPrograms();
                }
            } catch (Exception e) {
                showError("Error", "Failed to delete program: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        currentEditingId = null;
        programNameField.clear();
        durationMonthsField.clear();
        feeField.clear();
        descriptionArea.clear();

        switchToSaveMode();
        resetAllValidations();
        generateNextId();
        programTable.getSelectionModel().clearSelection();
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

    private boolean validateAllFields() {
        boolean isValid = true;

        if (programNameField.getText().isEmpty() || !NAME_PATTERN.matcher(programNameField.getText()).matches()) {
            updateFieldValidation(programNameField, nameValidation, nameValidationContainer,
                    false, "Valid program name is required");
            isValid = false;
        }

        if (durationMonthsField.getText().isEmpty() || !DURATION_PATTERN.matcher(durationMonthsField.getText()).matches()) {
            updateFieldValidation(durationMonthsField, durationValidation, durationValidationContainer,
                    false, "Valid duration (1-100) is required");
            isValid = false;
        }

        // Continuing from the validateAllFields method...

        if (feeField.getText().isEmpty() || !FEE_PATTERN.matcher(feeField.getText()).matches()) {
            updateFieldValidation(feeField, feeValidation, feeValidationContainer,
                    false, "Valid fee amount is required");
            isValid = false;
        }

        if (!isValid) {
            showError("Validation Error", "Please correct the highlighted fields");
        }

        return isValid;
    }

    private void resetAllValidations() {
        resetFieldValidation(programNameField, nameValidation, nameValidationContainer);
        resetFieldValidation(durationMonthsField, durationValidation, durationValidationContainer);
        resetFieldValidation(feeField, feeValidation, feeValidationContainer);
    }

    private void loadPrograms() {
        try {
            programList.clear();
            programList.addAll(programBO.getAllPrograms());
            filteredPrograms = new FilteredList<>(programList, p -> true);
            programTable.setItems(filteredPrograms);
            updateTotalProgramsLabel();
        } catch (Exception e) {
            showError("Error", "Failed to load programs: " + e.getMessage());
        }
    }

    private void generateNextId() {
        try {
            String nextId = programBO.getNextProgramId();
            programIdField.setText(nextId);
        } catch (Exception e) {
            showError("Error", "Failed to generate ID: " + e.getMessage());
        }
    }

    private void updateTotalProgramsLabel() {
        totalProgramsLabel.setText("Total Programs: " + programTable.getItems().size());
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
        dialogPane.getStylesheets().add(getClass().getResource("/styles/ProgramManagement.css").toExternalForm());
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

    @FXML
    void handleSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredPrograms.setPredicate(program -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return program.getProgramName().toLowerCase().contains(lowerCaseFilter) ||
                        program.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                        program.getProgramId().toLowerCase().contains(lowerCaseFilter) ||
                        String.valueOf(program.getFee()).contains(lowerCaseFilter) ||
                        String.valueOf(program.getDurationMonths()).contains(lowerCaseFilter);
            });
            updateTotalProgramsLabel();
        });
    }

    @FXML
    void handleNewProgram() {
        handleClear();
        if (!formContainer.isVisible()) {
            handleToggleForm();
        }
    }
}