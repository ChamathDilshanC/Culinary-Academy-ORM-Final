package lk.ijse.util;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

public class NotificationUtil {

    // SVG paths for icons
    private static final String SUCCESS_ICON = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z";
    private static final String ERROR_ICON = "M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z";
    private static final String INFO_ICON = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z";
    private static final String WARNING_ICON = "M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z";
    private static final String QUESTION_ICON = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 17h-2v-2h2v2zm2.07-7.75l-.9.92C13.45 12.9 13 13.5 13 15h-2v-.5c0-1.1.45-2.1 1.17-2.83l1.24-1.26c.37-.36.59-.86.59-1.41 0-1.1-.9-2-2-2s-2 .9-2 2H8c0-2.21 1.79-4 4-4s4 1.79 4 4c0 .88-.36 1.68-.93 2.25z";

    public static void showSuccess(String message) {
        show(message, "#28a745", SUCCESS_ICON);
    }

    public static void showError(String message) {
        show(message, "#dc3545", ERROR_ICON);
    }

    public static void showInfo(String message) {
        show(message, "#17a2b8", INFO_ICON);
    }

    public static void showWarning(String message) {
        show(message, "#ffc107", WARNING_ICON);
    }

    private static void show(String message, String color, String iconPath) {
        try {
            Stage stage = getActiveStage();
            if (stage == null) return;

            Popup popup = new Popup();
            popup.setAutoFix(true);
            popup.setAutoHide(true);

            // Main container
            StackPane content = new StackPane();
            content.setStyle("-fx-background-color: " + color + ";" +
                    "-fx-padding: 10 20;" +
                    "-fx-background-radius: 5;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);");

            // Create HBox for icon and message
            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.CENTER_LEFT);

            // Create SVG icon
            SVGPath icon = new SVGPath();
            icon.setContent(iconPath);
            icon.setStyle("-fx-fill: white;");
            icon.setScaleX(0.7);
            icon.setScaleY(0.7);

            // Add message
            Label label = new Label(message);
            label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

            hbox.getChildren().addAll(icon, label);
            content.getChildren().add(hbox);

            popup.getContent().add(content);

            // Calculate position
            content.applyCss();
            content.layout();
            double popupX = stage.getX() + (stage.getWidth() - content.prefWidth(-1)) / 2;
            double popupY = stage.getY() + 50;

            popup.show(stage, popupX, popupY);

            // Auto hide with fade out
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(2.5), new KeyValue(content.opacityProperty(), 1)),
                    new KeyFrame(Duration.seconds(3), new KeyValue(content.opacityProperty(), 0))
            );
            timeline.setOnFinished(e -> popup.hide());
            timeline.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Stage getActiveStage() {
        // Get the currently focused window
        for (Window window : Stage.getWindows()) {
            if (window instanceof Stage && window.isFocused()) {
                return (Stage) window;
            }
        }

        // If no focused window, get the first showing window
        for (Window window : Stage.getWindows()) {
            if (window instanceof Stage && window.isShowing()) {
                return (Stage) window;
            }
        }

        return null;
    }

    public static boolean showConfirmation(String message) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Confirmation");

        DialogPane dialogPane = dialog.getDialogPane();

        // Create content with icon
        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);

        // Create SVG icon
        SVGPath icon = new SVGPath();
        icon.setContent(QUESTION_ICON);
        icon.setStyle("-fx-fill: #6c757d;");
        icon.setScaleX(0.7);
        icon.setScaleY(0.7);

        Label label = new Label(message);
        label.setStyle("-fx-font-size: 14px;");

        content.getChildren().addAll(icon, label);
        dialogPane.setContent(content);
        dialogPane.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        dialogPane.lookupButton(ButtonType.YES).setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        dialogPane.lookupButton(ButtonType.NO).setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

        dialog.initOwner(getActiveStage());
        dialog.setResultConverter(dialogButton -> dialogButton);

        ButtonType result = dialog.showAndWait().orElse(ButtonType.NO);
        return result == ButtonType.YES;
    }

    public static boolean getConfirmation(String title, String header, String content) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        // Create content with icon
        HBox contentBox = new HBox(10);
        contentBox.setAlignment(Pos.CENTER_LEFT);

        // Create SVG icon
        SVGPath icon = new SVGPath();
        icon.setContent(QUESTION_ICON);
        icon.setStyle("-fx-fill: #6c757d;");
        icon.setScaleX(0.7);
        icon.setScaleY(0.7);

        Label contentLabel = new Label(content);
        contentLabel.setStyle("-fx-font-size: 14px;");

        contentBox.getChildren().addAll(icon, contentLabel);
        dialog.getDialogPane().setContent(contentBox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        dialog.getDialogPane().lookupButton(ButtonType.YES).setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        dialog.getDialogPane().lookupButton(ButtonType.NO).setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

        dialog.initOwner(getActiveStage());
        dialog.setResultConverter(dialogButton -> dialogButton);

        ButtonType result = dialog.showAndWait().orElse(ButtonType.NO);
        return result == ButtonType.YES;
    }
}