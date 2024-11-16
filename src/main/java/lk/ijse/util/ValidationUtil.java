package lk.ijse.util;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;

public class ValidationUtil {

    // Regular Expression Patterns
    private static final Map<ValidationField, Pattern> PATTERNS = new HashMap<>();
    static {
        PATTERNS.put(ValidationField.NAME,
                Pattern.compile("^[A-Za-z]{2,}(?: [A-Za-z]+)*$"));
        PATTERNS.put(ValidationField.EMAIL,
                Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"));
        PATTERNS.put(ValidationField.PHONE,
                Pattern.compile("^(?:0|\\+94|94)?[0-9]{9,10}$"));
        PATTERNS.put(ValidationField.ADDRESS,
                Pattern.compile("^[A-Za-z0-9\\s,./-]{5,100}$"));
        PATTERNS.put(ValidationField.NIC,
                Pattern.compile("^([0-9]{9}[vVxX]|[0-9]{12})$"));
        PATTERNS.put(ValidationField.STUDENT_ID,
                Pattern.compile("^STU\\d{4}$"));
        PATTERNS.put(ValidationField.PROGRAM_ID,
                Pattern.compile("^CA\\d{4}$"));
        PATTERNS.put(ValidationField.PASSWORD,
                Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"));
    }

    // Validation Fields Enum
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

    // Validation Result Class
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

    // Main Validation Methods
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

    // UI Validation Methods
    public static void setupValidation(TextField field, Label errorLabel, ValidationField validationType) {
        field.textProperty().addListener((obs, oldValue, newValue) -> {
            ValidationResult result = validate(newValue, validationType);
            showValidationResult(field, errorLabel, result);
        });

        // Add focus listeners for enhanced UX
        field.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue && !field.getText().isEmpty()) {  // On focus lost
                ValidationResult result = validate(field.getText(), validationType);
                showValidationResult(field, errorLabel, result);
            }
        });
    }

    public static void showValidationResult(TextField field, Label errorLabel, ValidationResult result) {
        if (!result.isValid()) {
            showError(field, errorLabel, result.getMessage());
        } else {
            hideError(field, errorLabel);
        }
    }

    // Animation Methods
    public static void showError(TextField field, Label errorLabel, String message) {
        errorLabel.setText(message);

        if (!errorLabel.isVisible()) {
            // Setup animations
            ParallelTransition parallel = new ParallelTransition();

            // Fade in animation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), errorLabel);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            // Slide down animation
            TranslateTransition slideDown = new TranslateTransition(Duration.millis(200), errorLabel);
            slideDown.setFromY(-10);
            slideDown.setToY(0);

            parallel.getChildren().addAll(fadeIn, slideDown);

            // Show error label and play animation
            errorLabel.setVisible(true);
            parallel.play();
        }

        field.getStyleClass().add("field-error");
    }

    public static void hideError(TextField field, Label errorLabel) {
        if (errorLabel.isVisible()) {
            // Setup fade out animation
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), errorLabel);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> errorLabel.setVisible(false));
            fadeOut.play();
        }

        field.getStyleClass().remove("field-error");
    }

    // Utility Methods
    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("[<>\"']", "");
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean containsScript(String input) {
        if (input == null) return false;
        return input.toLowerCase().contains("<script") ||
                input.toLowerCase().contains("javascript:") ||
                input.toLowerCase().contains("onerror=") ||
                input.toLowerCase().contains("onload=");
    }
}