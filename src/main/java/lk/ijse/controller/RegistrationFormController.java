package lk.ijse.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import lk.ijse.bo.BOFactory;
import lk.ijse.bo.custom.ProgramBO;
import lk.ijse.bo.custom.RegistrationBO;
import lk.ijse.bo.custom.StudentBO;
import lk.ijse.dto.*;
import lk.ijse.entity.Registration;
import lk.ijse.entity.Registration.PaymentStatus;
import lk.ijse.util.AlertUtil;
import lk.ijse.util.NotificationUtil;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class RegistrationFormController implements Initializable {
    @FXML private StackPane rootPane;
    @FXML private StackPane loadingPane;
    @FXML private Label lblRegistrationId;

    // Student Section
    @FXML private ComboBox<String> cmbStudentId;
    @FXML private TextField txtStudentSearch;
    @FXML private TextField txtStudentName;
    @FXML private TextField txtContact;
    @FXML private TextField txtEmail;

    // Program Section
    @FXML private ComboBox<String> cmbProgram;
    @FXML private TableView<ProgramDTO> tblSelectedPrograms;
    @FXML private TableColumn<ProgramDTO, String> colProgramId;
    @FXML private TableColumn<ProgramDTO, String> colProgramName;
    @FXML private TableColumn<ProgramDTO, String> colDuration;
    @FXML private TableColumn<ProgramDTO, String> colFee;
    @FXML private TableColumn<ProgramDTO, Void> colRemove;

    // Payment Section
    @FXML private ComboBox<String> cmbPaymentMethod;
    @FXML private ComboBox<String> cmbPaymentStatus;
    @FXML private TextField txtAmount;
    @FXML private Label lblTotalAmount;

    // Recent Registrations Table
    @FXML private TableView<RegistrationDTO> tblRegistrations;
    @FXML private TableColumn<RegistrationDTO, String> colRegId;
    @FXML private TableColumn<RegistrationDTO, String> colStudent;
    @FXML private TableColumn<RegistrationDTO, String> colProgram;
    @FXML private TableColumn<RegistrationDTO, String> colDate;
    @FXML private TableColumn<RegistrationDTO, String> colAmount;
    @FXML private TableColumn<RegistrationDTO, String> colStatus;
    @FXML private TextField txtSearch;

    private final StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);
    private final ProgramBO programBO = (ProgramBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PROGRAM);
    private final RegistrationBO registrationBO = (RegistrationBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.REGISTRATION);

    private final ObservableList<ProgramDTO> selectedPrograms = FXCollections.observableArrayList();
    private final ObservableList<RegistrationDTO> registrations = FXCollections.observableArrayList();
    private double totalAmount = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showLoading(false);
        initializeTables();
        setupEventHandlers();
        loadInitialData();
    }

    private void initializeTables() {
        // Setup Selected Programs Table
        setupSelectedProgramsTable();

        // Setup Recent Registrations Table
        setupRegistrationsTable();

        // Setup Payment Methods
        setupPaymentMethods();
    }

    private void setupSelectedProgramsTable() {
        colProgramId.setCellValueFactory(new PropertyValueFactory<>("programId"));
        colProgramName.setCellValueFactory(new PropertyValueFactory<>("programName"));
        colDuration.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDurationMonths() + " Months"));
        colFee.setCellValueFactory(data ->
                new SimpleStringProperty(formatCurrency(data.getValue().getFee())));

        setupRemoveButton();
        tblSelectedPrograms.setItems(selectedPrograms);
    }

    private void setupRegistrationsTable() {
        colRegId.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("REG%03d", data.getValue().getId())));
        colStudent.setCellValueFactory(data -> {
            try {
                StudentDTO student = studentBO.searchStudent(String.valueOf(data.getValue().getStudentId()));
                return new SimpleStringProperty(student.getFirstName() + " " + student.getLastName());
            } catch (Exception e) {
                return new SimpleStringProperty("N/A");
            }
        });
        colProgram.setCellValueFactory(data -> {
            List<String> programNames = data.getValue().getRegistrationDetails().stream()
                    .map(RegistrationDetailsDTO::getProgramName)
                    .toList();
            return new SimpleStringProperty(String.join(", ", programNames));
        });
        colDate.setCellValueFactory(data ->
                new SimpleStringProperty(formatDateTime(data.getValue().getRegistrationDate())));
        colAmount.setCellValueFactory(data ->
                new SimpleStringProperty(formatCurrency(calculateTotalAmount(data.getValue()))));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        setupSearchFunctionality();
    }

    private void setupPaymentMethods() {
        cmbPaymentMethod.setItems(FXCollections.observableArrayList(
                "CASH", "CREDIT_CARD", "DEBIT_CARD", "BANK_TRANSFER", "ONLINE_PAYMENT"
        ));
        cmbPaymentStatus.setItems(FXCollections.observableArrayList(
                Arrays.toString(PaymentStatus.values())
        ));
        cmbPaymentStatus.setValue(PaymentStatus.PENDING.name());
    }

    private void setupEventHandlers() {
        // Student Selection Event
        cmbStudentId.setOnAction(e -> loadStudentDetails());

        // Student Search Event
        txtStudentSearch.textProperty().addListener((obs, old, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                searchStudents(newValue);
            }
        });

        // Amount Validation
        txtAmount.textProperty().addListener((obs, old, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                txtAmount.setText(old);
            }
            validateAndUpdateAmount(newValue);
        });

        // Clear Form Button
        cmbPaymentMethod.setOnAction(e -> updatePaymentStatus());
    }

    @FXML
    private void handleAddProgram() {
        if (cmbProgram.getValue() == null) {
            NotificationUtil.showWarning("Please select a program");
            return;
        }

        try {
            ProgramDTO program = programBO.searchProgram(cmbProgram.getValue());
            if (program == null) {
                NotificationUtil.showError("Program not found");
                return;
            }

            if (selectedPrograms.stream()
                    .noneMatch(p -> p.getProgramId().equals(program.getProgramId()))) {
                selectedPrograms.add(program);
                updateTotalAmount();
                cmbProgram.setValue(null);
            } else {
                NotificationUtil.showWarning("Program already added");
            }
        } catch (Exception e) {
            NotificationUtil.showError("Error adding program: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        if (!validateRegistration()) {
            return;
        }

        try {
            showLoading(true);

            // Create Registration
            StudentDTO studentDTO = studentBO.searchStudent(cmbStudentId.getValue());
            if (studentDTO == null) {
                NotificationUtil.showError("Student not found");
                return;
            }

            RegistrationDTO registrationDTO = new RegistrationDTO();
            registrationDTO.setStudentId(studentDTO.getStudentId());
            registrationDTO.setRegistrationDate(LocalDateTime.now());
            registrationDTO.setPaymentStatus(calculatePaymentStatus());

            // Add Registration Details
            for (ProgramDTO program : selectedPrograms) {
                RegistrationDetailsDTO detailDTO = new RegistrationDetailsDTO();
                detailDTO.setProgramId(program.getProgramId());
                detailDTO.setProgramName(program.getProgramName());
                detailDTO.setFee(program.getFee());
                registrationDTO.getRegistrationDetails().add(detailDTO);
            }

            // Add Payment if exists
            if (!txtAmount.getText().isEmpty() && cmbPaymentMethod.getValue() != null) {
                PaymentDTO paymentDTO = new PaymentDTO();
                paymentDTO.setAmount(Double.parseDouble(txtAmount.getText()));
                paymentDTO.setPaymentDate(LocalDateTime.now());
                paymentDTO.setPaymentMethod(cmbPaymentMethod.getValue());
                paymentDTO.setStatus(registrationDTO.getPaymentStatus());
                registrationDTO.getPayments().add(paymentDTO);
            }

            // Save Registration
            if (registrationBO.saveRegistration(registrationDTO)) {
                NotificationUtil.showSuccess("Registration completed successfully");
                clearForm();
                loadRegistrations();
                generateNewRegistrationId();
            }

        } catch (Exception e) {
            NotificationUtil.showError("Error processing registration: " + e.getMessage());
        } finally {
            showLoading(false);
        }
    }

    private String calculatePaymentStatus() {
        double paidAmount = txtAmount.getText().isEmpty() ? 0.0 :
                Double.parseDouble(txtAmount.getText());
        if (paidAmount >= totalAmount) {
            return PaymentStatus.COMPLETED.name();
        } else if (paidAmount > 0) {
            return PaymentStatus.PARTIAL.name();
        }
        return PaymentStatus.PENDING.name();
    }

    private void loadStudentDetails() {
        try {
            String studentId = cmbStudentId.getValue();
            if (studentId != null) {
                StudentDTO student = studentBO.searchStudent(studentId);
                if (student != null) {
                    txtStudentName.setText(student.getFirstName() + " " + student.getLastName());
                    txtContact.setText(student.getPhoneNumber());
                    txtEmail.setText(student.getEmail());
                }
            }
        } catch (Exception e) {
            NotificationUtil.showError("Error loading student details: " + e.getMessage());
        }
    }

    private void searchStudents(String searchText) {
        try {
            List<StudentDTO> students = studentBO.searchStudentsByPhone(searchText);
            cmbStudentId.setItems(FXCollections.observableArrayList(
                    students.stream()
                            .map(s -> String.format("%03d", s.getStudentId()))
                            .toList()
            ));
            if (!students.isEmpty()) {
                cmbStudentId.show();
            }
        } catch (Exception e) {
            NotificationUtil.showError("Error searching students: " + e.getMessage());
        }
    }

    private boolean validateRegistration() {
        if (cmbStudentId.getValue() == null) {
            NotificationUtil.showWarning("Please select a student");
            return false;
        }

        if (selectedPrograms.isEmpty()) {
            NotificationUtil.showWarning("Please select at least one program");
            return false;
        }

        if (!txtAmount.getText().isEmpty()) {
            if (cmbPaymentMethod.getValue() == null) {
                NotificationUtil.showWarning("Please select a payment method");
                return false;
            }

            try {
                double amount = Double.parseDouble(txtAmount.getText());
                if (amount <= 0 || amount > totalAmount) {
                    NotificationUtil.showWarning("Invalid payment amount");
                    return false;
                }
            } catch (NumberFormatException e) {
                NotificationUtil.showWarning("Invalid payment amount format");
                return false;
            }
        }

        return true;
    }

    private void updatePaymentStatus() {
        if (!txtAmount.getText().isEmpty()) {
            cmbPaymentStatus.setValue(calculatePaymentStatus());
        }
    }

    private void generateNewRegistrationId() {
        try {
            String lastId = registrationBO.getLastRegistrationId();
            int nextId = (lastId == null) ? 1 : Integer.parseInt(lastId) + 1;
            lblRegistrationId.setText(String.format("REG%03d", nextId));
        } catch (Exception e) {
            NotificationUtil.showError("Error generating registration ID: " + e.getMessage());
            lblRegistrationId.setText("REG???");
        }
    }

    private void loadRegistrations() {
        try {
            registrations.clear();
            registrations.addAll(registrationBO.getAllRegistrations());
        } catch (Exception e) {
            NotificationUtil.showError("Error loading registrations: " + e.getMessage());
        }
    }

    private void clearForm() {
        cmbStudentId.setValue(null);
        txtStudentName.clear();
        txtContact.clear();
        txtEmail.clear();
        selectedPrograms.clear();
        cmbPaymentMethod.setValue(null);
        cmbPaymentStatus.setValue(PaymentStatus.PENDING.name());
        txtAmount.clear();
        updateTotalAmount();
    }

    private void showLoading(boolean show) {
        loadingPane.setVisible(show);
    }

    private void updateTotalAmount() {
        totalAmount = selectedPrograms.stream()
                .mapToDouble(ProgramDTO::getFee)
                .sum();
        lblTotalAmount.setText(formatCurrency(totalAmount));
    }

    private double calculateTotalAmount(RegistrationDTO registration) {
        return registration.getRegistrationDetails().stream()
                .mapToDouble(RegistrationDetailsDTO::getFee)
                .sum();
    }

    private String formatCurrency(double amount) {
        return String.format("Rs. %.2f", amount);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private void setupSearchFunctionality() {
        FilteredList<RegistrationDTO> filteredData = new FilteredList<>(registrations, p -> true);
        txtSearch.textProperty().addListener((obs, old, newValue) -> {
            filteredData.setPredicate(reg -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                try {
                    StudentDTO student = studentBO.searchStudent(String.valueOf(reg.getStudentId()));
                    String searchableText = (student.getFirstName() + " " + student.getLastName() +
                            " " + reg.getId() + " " + reg.getPaymentStatus()).toLowerCase();
                    return searchableText.contains(newValue.toLowerCase());
                } catch (Exception e) {
                    return false;
                }
            });
        });
        tblRegistrations.setItems(filteredData);
    }

    private void setupRemoveButton() {
        colRemove.setCellFactory(param -> new TableCell<>() {
            private final Button btnRemove = new Button();
            {
                FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                icon.setStyleClass("delete-icon");
                btnRemove.setGraphic(icon);
                btnRemove.getStyleClass().add("btn-delete");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnRemove);
                    btnRemove.setOnAction(event -> {
                        ProgramDTO program = getTableView().getItems().get(getIndex());
                        if (AlertUtil.showConfirmation("Remove Program",
                                "Are you sure you want to remove " + program.getProgramName() + "?")) {
                            selectedPrograms.remove(program);
                            updateTotalAmount();
                        }
                    });
                }
            }
        });
    }

    private void loadInitialData() {
        try {
            // Load Students
            loadStudents();

            // Load Programs
            loadPrograms();

            // Load Registrations
            loadRegistrations();

            // Generate new registration ID
            generateNewRegistrationId();

        } catch (Exception e) {
            NotificationUtil.showError("Error loading initial data: " + e.getMessage());
        }
    }

    private void loadStudents() throws Exception {
        List<StudentDTO> students = studentBO.getAllStudents();
        cmbStudentId.setItems(FXCollections.observableArrayList(
                students.stream()
                        .map(s -> String.format("%03d", s.getStudentId()))
                        .toList()
        ));
    }

    private void loadPrograms() throws Exception {
        List<ProgramDTO> programs = programBO.getAllPrograms();
        cmbProgram.setItems(FXCollections.observableArrayList(
                programs.stream()
                        .map(ProgramDTO::getProgramId)
                        .toList()
        ));
    }

    private void validateAndUpdateAmount(String amount) {
        if (!amount.isEmpty()) {
            try {
                double value = Double.parseDouble(amount);
                if (value > totalAmount) {
                    NotificationUtil.showWarning("Amount cannot exceed total fee: " +
                            formatCurrency(totalAmount));
                    txtAmount.setText("");
                } else {
                    updatePaymentStatus();
                }
            } catch (NumberFormatException e) {
                NotificationUtil.showWarning("Please enter a valid amount");
                txtAmount.setText("");
            }
        } else {
            cmbPaymentStatus.setValue(PaymentStatus.PENDING.name());
        }
    }

    @FXML
    private void handleProgramSelection() {
        String programId = cmbProgram.getValue();
        if (programId != null) {
            if (selectedPrograms.stream()
                    .anyMatch(p -> p.getProgramId().equals(programId))) {
                NotificationUtil.showWarning("Program already added");
                cmbProgram.setValue(null);
            }
        }
    }

    @FXML
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (event.getSource() == cmbStudentId) {
                cmbProgram.requestFocus();
            } else if (event.getSource() == cmbProgram) {
                handleAddProgram();
            } else if (event.getSource() == txtAmount && !txtAmount.getText().isEmpty()) {
                handleRegister();
            }
        }
    }

    public void handleClear(ActionEvent actionEvent) {
        clearForm();
    }
}