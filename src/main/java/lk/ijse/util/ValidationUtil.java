package lk.ijse.util;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Map<ValidationField, Pattern> PATTERNS = new HashMap<>();
    static {
        PATTERNS.put(ValidationField.NAME, Pattern.compile("^[A-Za-z]{2,}(?: [A-Za-z]+)*$"));
        PATTERNS.put(ValidationField.EMAIL, Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"));
        PATTERNS.put(ValidationField.PHONE, Pattern.compile("^(?:0|\\+94|94)?[0-9]{9,10}$"));
        PATTERNS.put(ValidationField.ADDRESS, Pattern.compile("^[A-Za-z0-9\\s,./-]{5,100}$"));
        PATTERNS.put(ValidationField.NIC, Pattern.compile("^([0-9]{9}[vVxX]|[0-9]{12})$"));
        PATTERNS.put(ValidationField.STUDENT_ID, Pattern.compile("^STU\\d{4}$"));
        PATTERNS.put(ValidationField.PROGRAM_ID, Pattern.compile("^CA\\d{4}$"));
        PATTERNS.put(ValidationField.PASSWORD, Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"));
    }

    // SVG paths for success and error icons
    private static final String CHECK_MARK_PATH = "M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z";
    private static final String ERROR_MARK_PATH = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z";

    public enum ValidationField {
        NAME("Name", "Must be at least 2 characters and contain only letters and spaces"),
        EMAIL("Email", "Must be a valid email address"),
        PHONE("Phone", "Must be a valid Sri Lankan phone number"),
        ADDRESS("Address", "Must be between 5 and 100 characters"),
        NIC("NIC", "Must be a valid Sri Lankan NIC number"),
        STUDENT_ID("Student ID", "Must follow the format STU####"),
        PROGRAM_ID("Program ID", "Must follow the format CA####"),
        PASSWORD("Password", "Must contain at least 8 characters, including uppercase, lowercase, number and special character");

        private final String fieldName;
        private final String description;

        ValidationField(String fieldName, String description) {
            this.fieldName = fieldName;
            this.description = description;
        }

        public String getFieldName() { return fieldName; }
        public String getDescription() { return description; }
    }

    public static class ValidationContainer extends VBox {
        private final TextField textField;
        private final HBox feedbackContainer;
        private final Label messageLabel;
        private final SVGPath iconPath;
        private final StackPane iconContainer;

        public ValidationContainer(TextField textField) {
            this.textField = textField;
            this.setSpacing(4);

            // Create feedback container
            feedbackContainer = new HBox();
            feedbackContainer.setSpacing(8);
            feedbackContainer.setAlignment(Pos.CENTER_LEFT);
            feedbackContainer.setVisible(false);
            feedbackContainer.setManaged(false);  // Changed from setManageableState to setManaged

            // Create message label
            messageLabel = new Label();
            messageLabel.getStyleClass().add("validation-message");

            // Create icon
            iconPath = new SVGPath();
            iconContainer = new StackPane(iconPath);
            iconContainer.setMinSize(16, 16);
            iconContainer.setMaxSize(16, 16);

            // Add components to feedback container
            feedbackContainer.getChildren().addAll(iconContainer, messageLabel);

            // Add text field and feedback container to VBox
            getChildren().addAll(textField, feedbackContainer);
        }

        public TextField getTextField() { return textField; }
        public Label getMessageLabel() { return messageLabel; }
        public SVGPath getIconPath() { return iconPath; }
        public HBox getFeedbackContainer() { return feedbackContainer; }
    }

    public static ValidationContainer createValidationContainer(TextField textField, ValidationField validationType) {
        ValidationContainer container = new ValidationContainer(textField);
        setupValidation(container, validationType);
        return container;
    }

    public static void setupValidation(ValidationContainer container, ValidationField validationType) {
        TextField field = container.getTextField();

        field.textProperty().addListener((obs, oldValue, newValue) -> {
            ValidationResult result = validate(newValue, validationType);
            showValidationResult(container, result);
        });

        field.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue && !field.getText().isEmpty()) {
                ValidationResult result = validate(field.getText(), validationType);
                showValidationResult(container, result);
            }
        });
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        private final ValidationField field;

        public ValidationResult(boolean valid, String message, ValidationField field) {
            this.valid = valid;
            this.message = message;
            this.field = field;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public ValidationField getField() { return field; }
    }

    public static ValidationResult validate(String value, ValidationField field) {
        if (value == null || value.trim().isEmpty()) {
            return new ValidationResult(false, field.getFieldName() + " is required", field);
        }

        Pattern pattern = PATTERNS.get(field);
        if (pattern == null) {
            throw new IllegalArgumentException("No pattern defined for field: " + field);
        }

        boolean matches = pattern.matcher(value.trim()).matches();
        return new ValidationResult(
                matches,
                matches ? "Valid " + field.getFieldName().toLowerCase() : field.getDescription(),
                field
        );
    }

    private static void showValidationResult(ValidationContainer container, ValidationResult result) {
        TextField field = container.getTextField();
        Label messageLabel = container.getMessageLabel();
        SVGPath iconPath = container.getIconPath();
        HBox feedbackContainer = container.getFeedbackContainer();

        if (result.isValid()) {
            // Show success state
            messageLabel.setText(result.getMessage());
            messageLabel.setTextFill(Color.GREEN);
            iconPath.setContent(CHECK_MARK_PATH);
            iconPath.setFill(Color.GREEN);
            field.getStyleClass().remove("field-error");
            field.getStyleClass().add("field-success");
        } else {
            // Show error state
            messageLabel.setText(result.getMessage());
            messageLabel.setTextFill(Color.RED);
            iconPath.setContent(ERROR_MARK_PATH);
            iconPath.setFill(Color.RED);
            field.getStyleClass().remove("field-success");
            field.getStyleClass().add("field-error");
        }

        // Show feedback with animation if not already visible
        if (!feedbackContainer.isVisible()) {
            ParallelTransition parallel = new ParallelTransition();

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), feedbackContainer);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            TranslateTransition slideDown = new TranslateTransition(Duration.millis(200), feedbackContainer);
            slideDown.setFromY(-10);
            slideDown.setToY(0);

            parallel.getChildren().addAll(fadeIn, slideDown);

            feedbackContainer.setVisible(true);
            feedbackContainer.setManaged(true);  // Changed from setManageableState to setManaged
            parallel.play();
        }
    }

    // CSS styles to be added to your stylesheet
    private static final String CSS_STYLES = """
        .field-error {
            -fx-border-color: #ff0000;
            -fx-border-width: 1px;
            -fx-border-radius: 4px;
        }
        
        .field-success {
            -fx-border-color: #00ff00;
            -fx-border-width: 1px;
            -fx-border-radius: 4px;
        }
        
        .validation-message {
            -fx-font-size: 12px;
        }
    """;

    public static String getDefaultStyles() {
        return CSS_STYLES;
    }
}