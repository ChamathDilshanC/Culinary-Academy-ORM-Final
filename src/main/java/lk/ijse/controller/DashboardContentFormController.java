package lk.ijse.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import lk.ijse.util.NotificationUtil;
import lk.ijse.util.AlertUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardContentFormController implements Initializable {

    @FXML private Label totalStudentsLabel;
    @FXML private Label activeProgramsLabel;
    @FXML private Label newRegistrationsLabel;
    @FXML private Label revenueLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadStatistics();
    }

    private void loadStatistics() {
        // Load actual statistics from your database
        // For now using dummy data
        totalStudentsLabel.setText("2,584");
        activeProgramsLabel.setText("5");
        newRegistrationsLabel.setText("156");
        revenueLabel.setText("Rs. 2.8M");
    }

    @FXML
    private void handleNewStudent() {
        NotificationUtil.showInfo("Opening student registration form");
    }

    @FXML
    private void handleNewRegistration() {
        NotificationUtil.showInfo("Opening registration form");
    }

    @FXML
    private void handleViewReports() {
        NotificationUtil.showWarning("Reports module coming soon!");
    }

    @FXML
    private void handleManagePrograms() {
        NotificationUtil.showInfo("Opening program management");
    }

    @FXML
    private void handleStudentRecords() {
        NotificationUtil.showInfo("Opening student records");
    }

    @FXML
    private void handleSettings() {
        NotificationUtil.showWarning("Settings module coming soon!");
    }
}