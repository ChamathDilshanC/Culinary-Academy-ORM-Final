package lk.ijse.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import lk.ijse.bo.BOFactory;
import lk.ijse.bo.custom.*;
import lk.ijse.dto.*;
import lk.ijse.entity.Payment.PaymentMethod;
import lk.ijse.entity.Registration.PaymentStatus;
import lk.ijse.util.NotificationUtil;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ProgramRegistrationFormController implements Initializable {

    @FXML private ComboBox<StudentDTO> cmbStudentId;
    @FXML private TextField txtStudentSearch;
    @FXML private Label lblRegistrationId;
    @FXML private TextField txtStudentName;
    @FXML private TextField txtContact;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<ProgramDTO> cmbProgram;
    @FXML private Label lblTotalAmount;
    @FXML private ComboBox<PaymentMethod> cmbPaymentMethod;
    @FXML private ComboBox<PaymentStatus> cmbPaymentStatus;
    @FXML private TextField txtAmount;
    @FXML private TableView<ProgramDTO> tblSelectedPrograms;
    @FXML private TableView<RegistrationDTO> tblRegistrations;
    @FXML private TextField txtSearch;
    @FXML private StackPane loadingPane;

    // Table Columns
    @FXML private TableColumn<ProgramDTO, String> colProgramId;
    @FXML private TableColumn<ProgramDTO, String> colProgramName;
    @FXML private TableColumn<ProgramDTO, Integer> colDuration;
    @FXML private TableColumn<ProgramDTO, Double> colFee;
    @FXML private TableColumn<ProgramDTO, Void> colRemove;

    @FXML private TableColumn<RegistrationDTO, String> colRegId;
    @FXML private TableColumn<RegistrationDTO, String> colStudent;
    @FXML private TableColumn<RegistrationDTO, String> colProgram;
    @FXML private TableColumn<RegistrationDTO, LocalDateTime> colDate;
    @FXML private TableColumn<RegistrationDTO, Double> colAmount;
    @FXML private TableColumn<RegistrationDTO, PaymentStatus> colStatus;

    private final RegistrationBO registrationBO;
    private final PaymentBO paymentBO;
    private final StudentBO studentBO;
    private final ProgramBO programBO;
    private final ProgramRegistrationBO programRegistrationBO;

    private final ObservableList<ProgramDTO> selectedPrograms = FXCollections.observableArrayList();
    private final ObservableList<RegistrationDTO> registrationList = FXCollections.observableArrayList();
    private final FilteredList<RegistrationDTO> filteredRegistrations;
    private StudentDTO currentStudent;
    private Integer nextRegistrationId = 1;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ProgramRegistrationFormController() {
        this.registrationBO = (RegistrationBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.REGISTRATION);
        this.paymentBO = (PaymentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PAYMENT);
        this.studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);
        this.programBO = (ProgramBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PROGRAM);
        this.programRegistrationBO = (ProgramRegistrationBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PROGRAM_REGISTRATION);
        this.filteredRegistrations = new FilteredList<>(registrationList);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        loadInitialData();
    }

    private void setupUI() {
        setupComboBoxes();
        setupTables();
        setupEventHandlers();
        setupSearch();
        generateNextRegistrationId();
        hideLoading();
    }

    private void setupComboBoxes() {
        // Student ComboBox
        cmbStudentId.setConverter(new StringConverter<StudentDTO>() {
            @Override
            public String toString(StudentDTO student) {
                return student == null ? "" :
                        String.format("%s - %s %s", student.getStudentId(),
                                student.getFirstName(), student.getLastName());
            }

            @Override
            public StudentDTO fromString(String string) {
                return null;
            }
        });

        // Program ComboBox
        cmbProgram.setConverter(new StringConverter<ProgramDTO>() {
            @Override
            public String toString(ProgramDTO program) {
                return program == null ? "" :
                        String.format("%s - %s (Rs. %.2f)", program.getProgramId(),
                                program.getProgramName(), program.getFee());
            }

            @Override
            public ProgramDTO fromString(String string) {
                return null;
            }
        });

        // Payment Method and Status
        cmbPaymentMethod.setItems(FXCollections.observableArrayList(PaymentMethod.values()));
        cmbPaymentStatus.setItems(FXCollections.observableArrayList(PaymentStatus.values()));
    }

    private void setupTables() {
        // Selected Programs Table
        colProgramId.setCellValueFactory(new PropertyValueFactory<>("programId"));
        colProgramName.setCellValueFactory(new PropertyValueFactory<>("programName"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("durationMonths"));
        colFee.setCellValueFactory(new PropertyValueFactory<>("fee"));

        // Format Fee Column
        colFee.setCellFactory(column -> new TableCell<ProgramDTO, Double>() {
            @Override
            protected void updateItem(Double fee, boolean empty) {
                super.updateItem(fee, empty);
                if (empty || fee == null) {
                    setText(null);
                } else {
                    setText(String.format("Rs. %.2f", fee));
                }
            }
        });

        // Registrations Table
        colRegId.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%03d", data.getValue().getRegistrationId())));
        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colProgram.setCellValueFactory(new PropertyValueFactory<>("programName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("programFee"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        // Format Amount Column
        colAmount.setCellFactory(column -> new TableCell<RegistrationDTO, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("Rs. %.2f", amount));
                }
            }
        });

        setupRemoveButton();
        formatDateColumn();
        formatStatusColumn();

        tblSelectedPrograms.setItems(selectedPrograms);
        tblRegistrations.setItems(filteredRegistrations);
    }

    private void setupRemoveButton() {
        colRemove.setCellFactory(param -> new TableCell<>() {
            private final Button removeBtn = new Button();

            {
                FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                icon.setStyle("-fx-fill: white;");
                removeBtn.setGraphic(icon);
                removeBtn.getStyleClass().add("btn-danger");
                removeBtn.setOnAction(event -> {
                    ProgramDTO program = getTableView().getItems().get(getIndex());
                    handleRemoveProgram(program);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeBtn);
            }
        });
    }

    private void formatDateColumn() {
        colDate.setCellFactory(column -> new TableCell<RegistrationDTO, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(dateFormatter.format(date));
                }
            }
        });
    }

    private void formatStatusColumn() {
        colStatus.setCellFactory(column -> new TableCell<RegistrationDTO, PaymentStatus>() {
            @Override
            protected void updateItem(PaymentStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    getStyleClass().removeAll("status-pending", "status-completed",
                            "status-cancelled");
                } else {
                    setText(status.toString());
                    getStyleClass().removeAll("status-pending", "status-completed",
                            "status-cancelled");
                    getStyleClass().add("status-" + status.toString().toLowerCase());
                }
            }
        });
    }

    private void setupEventHandlers() {
        // Student Selection
        cmbStudentId.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                currentStudent = newVal;
                fillStudentDetails(newVal);
            }
        });

        // Phone Search
        txtStudentSearch.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.isEmpty() && newVal.matches("\\d{10}")) {
                searchStudentByPhone(newVal);
            }
        });

        // Amount Validation
        txtAmount.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                txtAmount.setText(old);
            }
        });

        // Total Amount Update
        selectedPrograms.addListener((javafx.collections.ListChangeListener<ProgramDTO>) c ->
                updateTotalAmount());
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((obs, old, newVal) -> {
            filteredRegistrations.setPredicate(registration -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }
                String searchTerm = newVal.toLowerCase();
                return registration.getRegistrationId().toString().contains(searchTerm) ||
                        registration.getStudentName().toLowerCase().contains(searchTerm) ||
                        registration.getProgramName().toLowerCase().contains(searchTerm);
            });
        });
    }

    private void loadInitialData() {
        showLoading(true);
        try {
            // Load Students
            cmbStudentId.setItems(FXCollections.observableArrayList(
                    studentBO.getAllStudents()));

            // Load Programs
            cmbProgram.setItems(FXCollections.observableArrayList(
                    programBO.getAllPrograms()));

            // Load Registrations
            loadRegistrations();
        } catch (Exception e) {
            NotificationUtil.showError("Error loading initial data: " + e.getMessage());
        } finally {
            hideLoading();
        }
    }

    @FXML
    private void handleAddProgram() {
        ProgramDTO selectedProgram = cmbProgram.getValue();
        if (selectedProgram == null) {
            NotificationUtil.showWarning("Please select a program");
            return;
        }

        if (selectedPrograms.stream()
                .anyMatch(p -> p.getProgramId().equals(selectedProgram.getProgramId()))) {
            NotificationUtil.showWarning("Program already added");
            return;
        }

        selectedPrograms.add(selectedProgram);
        updateTotalAmount();
        cmbProgram.getSelectionModel().clearSelection();
    }

    private void handleRemoveProgram(ProgramDTO program) {
        selectedPrograms.remove(program);
        updateTotalAmount();
    }

    @FXML
    private void handleRegister() {
        if (!validateRegistration()) {
            return;
        }

        showLoading(true);
        try {
            // Create DTOs
            RegistrationDTO registrationDTO = createRegistrationDTO();
            ProgramRegistrationDTO programRegDTO = createProgramRegistrationDTO();
            PaymentDTO paymentDTO = createPaymentDTO();

            // Save registration first
            boolean regSuccess = registrationBO.saveRegistration(registrationDTO);
            if (!regSuccess) {
                throw new Exception("Failed to save registration");
            }

            // Then save program registration
            programRegDTO.setRegistrationId(nextRegistrationId);
            boolean progRegSuccess = programRegistrationBO.saveProgramRegistration(programRegDTO);
            if (!progRegSuccess) {
                throw new Exception("Failed to save program registration");
            }

            // Finally save payment
            paymentDTO.setRegistrationId(nextRegistrationId);
            boolean paymentSuccess = paymentBO.savePayment(paymentDTO);
            if (!paymentSuccess) {
                throw new Exception("Failed to save payment");
            }

            // If all successful, update UI
            Platform.runLater(() -> {
                NotificationUtil.showSuccess("Registration completed successfully!");
                nextRegistrationId++;
                updateRegistrationIdLabel();
                clearForm();
                loadRegistrations();
            });

        } catch (Exception e) {
            Platform.runLater(() -> {
                NotificationUtil.showError("Registration failed: " + e.getMessage());
            });
        } finally {
            hideLoading();
        }
    }

    private boolean validateRegistration() {
        if (currentStudent == null) {
            NotificationUtil.showWarning("Please select a student");
            return false;
        }

        if (selectedPrograms.isEmpty()) {
            NotificationUtil.showWarning("Please add at least one program");
            return false;
        }

        if (cmbPaymentMethod.getValue() == null) {
            NotificationUtil.showWarning("Please select payment method");
            return false;
        }

        if (cmbPaymentStatus.getValue() == null) {
            NotificationUtil.showWarning("Please select payment status");
            return false;
        }

        try {
            double amount = Double.parseDouble(txtAmount.getText());
            double totalAmount = calculateTotalAmount();
            if (amount <= 0 || amount > totalAmount) {
                NotificationUtil.showWarning("Invalid payment amount");
                return false;
            }
        } catch (NumberFormatException e) {
            NotificationUtil.showWarning("Please enter a valid payment amount");
            return false;
        }

        return true;
    }

    private RegistrationDTO createRegistrationDTO() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setRegistrationId(nextRegistrationId);
        dto.setStudentId(currentStudent.getStudentId().toString());
        dto.setProgramId(selectedPrograms.get(0).getProgramId());
        dto.setRegistrationDate(LocalDateTime.now());
        dto.setPaymentStatus(cmbPaymentStatus.getValue());
        dto.setProgramFee(calculateTotalAmount());
        dto.setStudentName(currentStudent.getFirstName() + " " + currentStudent.getLastName());
        dto.setProgramName(selectedPrograms.get(0).getProgramName());
        return dto;
    }

    private ProgramRegistrationDTO createProgramRegistrationDTO() {
        ProgramRegistrationDTO dto = new ProgramRegistrationDTO();
        dto.setStudentId(currentStudent.getStudentId());
        dto.setProgramId(selectedPrograms.get(0).getProgramId());
        dto.setRegistrationDate(LocalDateTime.now());
        dto.setPaymentStatus(cmbPaymentStatus.getValue());
        return dto;
    }

    private PaymentDTO createPaymentDTO() {
        PaymentDTO dto = new PaymentDTO();
        dto.setAmount(BigDecimal.valueOf(Double.parseDouble(txtAmount.getText())));
        dto.setPaymentMethod(cmbPaymentMethod.getValue());
        dto.setPaymentDate(LocalDateTime.now());
        dto.setStatus(cmbPaymentStatus.getValue());
        return dto;
    }

    private void searchStudentByPhone(String phone) {
        showLoading(true);
        Platform.runLater(() -> {
            try {
                StudentDTO student = cmbStudentId.getItems().stream()
                        .filter(s -> s.getPhoneNumber().equals(phone))
                        .findFirst()
                        .orElse(null);

                if (student != null) {
                    currentStudent = student;
                    cmbStudentId.getSelectionModel().select(student);
                    fillStudentDetails(student);
                    NotificationUtil.showSuccess("Student found!");
                } else {
                    NotificationUtil.showWarning("No student found with this phone number");
                }
            } catch (Exception e) {
                NotificationUtil.showError("Error searching student: " + e.getMessage());
            } finally {
                hideLoading();
            }
        });
    }

    private void fillStudentDetails(StudentDTO student) {
        txtStudentName.setText(student.getFirstName() + " " + student.getLastName());
        txtContact.setText(student.getPhoneNumber());
        txtEmail.setText(student.getEmail());
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void clearForm() {
        txtStudentSearch.clear();
        cmbStudentId.getSelectionModel().clearSelection();
        txtStudentName.clear();
        txtContact.clear();
        txtEmail.clear();
        selectedPrograms.clear();
        cmbProgram.getSelectionModel().clearSelection();
        cmbPaymentMethod.getSelectionModel().clearSelection();
        cmbPaymentStatus.getSelectionModel().clearSelection();
        txtAmount.clear();
        updateTotalAmount();
        currentStudent = null;
    }

    private void updateTotalAmount() {
        double total = calculateTotalAmount();
        lblTotalAmount.setText(String.format("Rs. %.2f", total));
    }

    private double calculateTotalAmount() {
        return selectedPrograms.stream()
                .mapToDouble(ProgramDTO::getFee)
                .sum();
    }

    private void generateNextRegistrationId() {
        try {
            nextRegistrationId = registrationBO.getNextRegistrationId();
            updateRegistrationIdLabel();
        } catch (Exception e) {
            NotificationUtil.showError("Error generating registration ID: " + e.getMessage());
            nextRegistrationId = 1;
            updateRegistrationIdLabel();
        }
    }

    private void updateRegistrationIdLabel() {
        lblRegistrationId.setText(String.format("%03d", nextRegistrationId));
    }

    private void loadRegistrations() {
        try {
            registrationList.setAll(registrationBO.getAllRegistrations());
        } catch (Exception e) {
            NotificationUtil.showError("Error loading registrations: " + e.getMessage());
        }
    }

    private void showLoading(boolean show) {
        if (loadingPane != null) {
            loadingPane.setVisible(show);
            loadingPane.setManaged(show);
        }
    }

    private void hideLoading() {
        showLoading(false);
    }
}