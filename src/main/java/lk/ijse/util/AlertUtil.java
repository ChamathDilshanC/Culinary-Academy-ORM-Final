package lk.ijse.util;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;

public class AlertUtil {
    private static final String DIALOG_STYLE = """
            .dialog-pane {
                -fx-background-color: white;
                -fx-padding: 20;
            }
            .dialog-pane > .content {
                -fx-padding: 20 10;
            }
            .dialog-pane .header-panel {
                -fx-background-color: transparent;
                -fx-padding: 0 0 10 0;
            }
            .dialog-pane .button-bar {
                -fx-padding: 20 0 0 0;
            }
            .button {
                -fx-background-radius: 5;
                -fx-padding: 8 15;
                -fx-cursor: hand;
                -fx-font-weight: bold;
            }
            .button:hover {
                -fx-opacity: 0.9;
            }
            """;

    public static boolean showConfirmation(String title, String message) {
        return showCustomAlert(title, message, Alert.AlertType.CONFIRMATION,
                FontAwesomeIcon.QUESTION_CIRCLE, "#6c757d");
    }

    public static void showError(String title, String message) {
        showCustomAlert(title, message, Alert.AlertType.ERROR,
                FontAwesomeIcon.TIMES_CIRCLE, "#dc3545");
    }

    public static void showInfo(String title, String message) {
        showCustomAlert(title, message, Alert.AlertType.INFORMATION,
                FontAwesomeIcon.INFO_CIRCLE, "#17a2b8");
    }

    public static void showWarning(String title, String message) {
        showCustomAlert(title, message, Alert.AlertType.WARNING,
                FontAwesomeIcon.EXCLAMATION_TRIANGLE, "#ffc107");
    }

    private static boolean showCustomAlert(String title, String message,
                                           Alert.AlertType type, FontAwesomeIcon icon, String color) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.initStyle(StageStyle.UNDECORATED);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStyleClass().add("custom-alert");
        dialogPane.setStyle(DIALOG_STYLE);

        // Create custom header
        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setGlyphSize(40);
        iconView.setStyle("-fx-fill: " + color + ";");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox header = new VBox(10, iconView, titleLabel);
        header.setAlignment(Pos.CENTER);
        dialogPane.setHeader(header);

        // Content
        Label contentLabel = new Label(message);
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-font-size: 14px;");
        dialogPane.setContent(contentLabel);

        // Buttons
        if (type == Alert.AlertType.CONFIRMATION) {
            dialogPane.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
            Button yesButton = (Button) dialogPane.lookupButton(ButtonType.YES);
            Button noButton = (Button) dialogPane.lookupButton(ButtonType.NO);

            yesButton.setStyle("""
                -fx-background-color: #28a745;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                """);

            noButton.setStyle("""
                -fx-background-color: #dc3545;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                """);
        } else {
            dialogPane.getButtonTypes().add(ButtonType.OK);
            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            okButton.setStyle("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                """.formatted(color));
        }

        dialog.initOwner(NotificationUtil.getActiveStage());

        // Add draggable behavior
        addDraggableSupport(dialog);

        if (type == Alert.AlertType.CONFIRMATION) {
            return dialog.showAndWait()
                    .filter(response -> response == ButtonType.YES)
                    .isPresent();
        } else {
            dialog.showAndWait();
            return false;
        }
    }

    private static void addDraggableSupport(Dialog<?> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        final double[] xOffset = {0};
        final double[] yOffset = {0};

        dialogPane.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });

        dialogPane.setOnMouseDragged(event -> {
            dialog.setX(event.getScreenX() - xOffset[0]);
            dialog.setY(event.getScreenY() - yOffset[0]);
        });
    }
}