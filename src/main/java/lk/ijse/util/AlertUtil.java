package lk.ijse.util;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class AlertUtil {
    // SVG paths for icons
    private static final String ERROR_ICON = "M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z";
    private static final String SUCCESS_ICON = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z";
    private static final String WARNING_ICON = "M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z";
    private static final String INFO_ICON = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z";

    private static final String BASE_DIALOG_STYLE =
            "-fx-padding: 20;" +
                    "-fx-background-color: white;" +
                    "-fx-background-insets: 0;" +
                    "-fx-background-radius: 15;" +
                    "-fx-border-radius: 15;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);" +
                    "-fx-border-color: rgba(0,0,0,0.1);" +
                    "-fx-border-width: 1;";

    private static final String CONTENT_BOX_STYLE =
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
                    "-fx-background-radius: 15;" +
                    "-fx-padding: 20;" +
                    "-fx-border-radius: 15;";

    private static final String BUTTON_BASE_STYLE =
            "-fx-background-radius: 20;" +
                    "-fx-padding: 8 30;" +
                    "-fx-cursor: hand;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-color: %s;" +
                    "-fx-text-fill: white;";

    public static void showError(String title, String message) {
        showCustomAlert(title, message, "error", ERROR_ICON, "#dc3545");
    }

    public static void showSuccess(String title, String message) {
        showCustomAlert(title, message, "success", SUCCESS_ICON, "#28a745");
    }

    public static void showWarning(String title, String message) {
        showCustomAlert(title, message, "warning", WARNING_ICON, "#ffc107");
    }

    public static void showInfo(String title, String message) {
        showCustomAlert(title, message, "info", INFO_ICON, "#17a2b8");
    }

    public static boolean showConfirmation(String title, String message) {
        return showConfirmDialog(title, message, WARNING_ICON, "#ffc107");
    }

    private static void showCustomAlert(String title, String message, String type, String iconPath, String color) {
        Dialog<ButtonType> dialog = createBaseDialog();
        styleDialog(dialog);

        VBox contentBox = createContentBox(title, message, iconPath, color);
        dialog.getDialogPane().setContent(contentBox);

        // Add OK button
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(okButton);
        styleButton(dialog, okButton, color);

        // Show dialog with animation
        showAnimatedDialog(dialog);
    }

    private static boolean showConfirmDialog(String title, String message, String iconPath, String color) {
        Dialog<ButtonType> dialog = createBaseDialog();
        styleDialog(dialog);

        VBox contentBox = createContentBox(title, message, iconPath, color);
        dialog.getDialogPane().setContent(contentBox);

        // Add buttons
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().addAll(yesButton, noButton);

        // Style buttons
        styleButton(dialog, yesButton, "#28a745");
        styleButton(dialog, noButton, "#dc3545");

        // Show dialog with animation and get result
        return showAnimatedDialog(dialog)
                .filter(response -> response == yesButton)
                .isPresent();
    }

    private static Dialog<ButtonType> createBaseDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.initModality(Modality.APPLICATION_MODAL);
        return dialog;
    }

    private static void styleDialog(Dialog<ButtonType> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle(BASE_DIALOG_STYLE);
        dialogPane.getScene().setFill(null);

        // Add drop shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.1));
        dropShadow.setRadius(10);
        dropShadow.setOffsetY(5);
        dialogPane.setEffect(dropShadow);

        // Remove default styling
        dialogPane.getStylesheets().clear();
        dialogPane.getStyleClass().add("modern-dialog");

        // Make header transparent if it exists
        Region header = (Region) dialogPane.lookup(".header-panel");
        if (header != null) {
            header.setStyle("-fx-background-color: transparent;");
        }

        // Style the button bar
        ButtonBar buttonBar = (ButtonBar) dialogPane.lookup(".button-bar");
        if (buttonBar != null) {
            buttonBar.setStyle("-fx-background-color: transparent; -fx-padding: 20 0 0 0;");
        }
    }

    private static VBox createContentBox(String title, String message, String iconPath, String color) {
        // Create icon
        SVGPath icon = new SVGPath();
        icon.setContent(iconPath);
        StackPane iconContainer = new StackPane(icon);
        iconContainer.setMinSize(60, 60);
        iconContainer.setMaxSize(60, 60);
        iconContainer.setStyle(String.format("-fx-background-color: %s; -fx-background-radius: 30;", color));
        icon.setStyle("-fx-fill: white;");
        icon.setScaleX(0.7);
        icon.setScaleY(0.7);

        // Create title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2b2b2b;");

        // Create message
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);

        // Create content container
        VBox contentBox = new VBox(15);
        contentBox.setStyle(CONTENT_BOX_STYLE);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getChildren().addAll(iconContainer, titleLabel, messageLabel);

        return contentBox;
    }

    private static void styleButton(Dialog<ButtonType> dialog, ButtonType buttonType, String color) {
        Button button = (Button) dialog.getDialogPane().lookupButton(buttonType);
        button.setStyle(String.format(BUTTON_BASE_STYLE, color));

        // Add hover effect
        button.setOnMouseEntered(e -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });

        // Add click effect
        button.setOnMousePressed(e -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
            scaleTransition.setToX(0.95);
            scaleTransition.setToY(0.95);
            scaleTransition.play();
        });

        button.setOnMouseReleased(e -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });
    }

    private static java.util.Optional<ButtonType> showAnimatedDialog(Dialog<ButtonType> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();

        // Set initial state
        dialogPane.setOpacity(0);
        dialogPane.setScaleX(0.8);
        dialogPane.setScaleY(0.8);

        // Create entrance animation
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(dialogPane.opacityProperty(), 0),
                        new KeyValue(dialogPane.scaleXProperty(), 0.8),
                        new KeyValue(dialogPane.scaleYProperty(), 0.8)
                ),
                new KeyFrame(Duration.millis(250),
                        new KeyValue(dialogPane.opacityProperty(), 1, Interpolator.EASE_OUT),
                        new KeyValue(dialogPane.scaleXProperty(), 1, Interpolator.EASE_OUT),
                        new KeyValue(dialogPane.scaleYProperty(), 1, Interpolator.EASE_OUT)
                )
        );

        fadeIn.play();

        // Show dialog and return result
        return dialog.showAndWait();
    }
}