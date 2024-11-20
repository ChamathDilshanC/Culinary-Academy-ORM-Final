package lk.ijse.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import lk.ijse.dao.custom.StudentDAO;
import lk.ijse.dao.DAOFactory;

import lk.ijse.util.NotificationUtil;

public class DashboardContentFormController implements Initializable {

    @FXML private Label totalStudentsLabel;
    @FXML private Label activeProgramsLabel;
    @FXML private Label newRegistrationsLabel;
    @FXML private Label revenueLabel;
    @FXML private BorderPane contentArea;

    // DAOs
    private final StudentDAO studentDAO;

    // Reference to main dashboard controller
    private DashboardFormController mainController;

    public DashboardContentFormController() {
        // Get DAO instances using DAOFactory
        studentDAO = (StudentDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.STUDENT);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadStatistics();
    }

    public void setMainController(DashboardFormController controller) {
        this.mainController = controller;
    }

    private void loadStatistics() {
        try {
/*            // Get statistics using DAOs
            int totalStudents = studentDAO.getTotalCount();
            int activePrograms = programDAO.getActiveCount();
            int newRegistrations = registrationDAO.getNewRegistrationsCount();
            double revenue = registrationDAO.getTotalRevenue();*/

/*            // Format and display statistics
            totalStudentsLabel.setText(String.format("%,d", totalStudents));
            activeProgramsLabel.setText(String.valueOf(activePrograms));
            newRegistrationsLabel.setText(String.format("%,d", newRegistrations));
            revenueLabel.setText(String.format("Rs. %.1fM", revenue / 1000000.0));*/
        } catch (Exception e) {
            NotificationUtil.showError("Failed to load statistics: " + e.getMessage());
            // Set default values in case of error
            totalStudentsLabel.setText("N/A");
            activeProgramsLabel.setText("N/A");
            newRegistrationsLabel.setText("N/A");
            revenueLabel.setText("N/A");
        }
    }

    @FXML
    private void handleNewStudent() {
        try {
            loadFXML("StudentForm.fxml", "New Student Registration");
        } catch (IOException e) {
            NotificationUtil.showError("Failed to open student registration form");
        }
    }

    @FXML
    private void handleNewRegistration() {
        try {
            loadFXML("CourseRegistrationForm.fxml", "New Course Registration");
        } catch (IOException e) {
            NotificationUtil.showError("Failed to open course registration form");
        }
    }

    @FXML
    private void handleViewReports() {
        if (mainController != null) {
            mainController.loadReports();
        } else {
            NotificationUtil.showWarning("Reports module coming soon!");
        }
    }

    @FXML
    private void handleManagePrograms() {
        try {
            loadFXML("CourseRegistrationForm.fxml", "New Course Registration");
        } catch (IOException e) {
            NotificationUtil.showError("Failed to open course registration form");
        }
    }

    @FXML
    private void handleStudentRecords() {
        try {
            loadFXML("StudentForm.fxml", "Student Records");
        } catch (IOException e) {
            NotificationUtil.showError("Failed to open course registration form");
        }
    }

    @FXML
    private void handleSettings() {
        if (mainController != null) {
            mainController.loadSettings(null);
        } else {
            NotificationUtil.showWarning("Settings module coming soon!");
        }
    }

    private void loadFXML(String fxmlFile, String title) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxmlFile));
            Parent root = loader.load();

            if (mainController != null) {
                mainController.loadFXML(fxmlFile);
            } else {
                // Find and use the content area
                BorderPane mainContent = findMainContentArea(totalStudentsLabel);
                if (mainContent != null) {
                    mainContent.setCenter(root);
                } else {
                    throw new IOException("Cannot find main content area");
                }
            }
            NotificationUtil.showSuccess("Loaded " + title);
        } catch (IOException e) {
            NotificationUtil.showError("Failed to load " + title);
            throw e;
        }
    }

    private BorderPane findMainContentArea(Node node) {
        while (node != null) {
            if (node instanceof BorderPane) {
                return (BorderPane) node;
            }
            node = node.getParent();
        }
        return null;
    }
}