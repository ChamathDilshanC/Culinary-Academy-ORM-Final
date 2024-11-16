package lk.ijse.util;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

public class NotificationUtil {
    private static final int ANIMATION_DURATION = 2500;
    private static final int FADE_OUT_DURATION = 500;
    private static final String BASE_STYLE = """
            -fx-background-radius: 10;
            -fx-padding: 15 20;
            -fx-spacing: 10;
            -fx-alignment: center-left;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);
            """;

    public static void showSuccess(String message) {
        show(message, "#28a745", FontAwesomeIcon.CHECK_CIRCLE);
    }

    public static void showError(String message) {
        show(message, "#dc3545", FontAwesomeIcon.EXCLAMATION_CIRCLE);
    }

    public static void showInfo(String message) {
        show(message, "#17a2b8", FontAwesomeIcon.INFO_CIRCLE);
    }

    public static void showWarning(String message) {
        show(message, "#ffc107", FontAwesomeIcon.EXCLAMATION_TRIANGLE);
    }

    private static void show(String message, String color, FontAwesomeIcon icon) {
        try {
            Stage stage = getActiveStage();
            if (stage == null) return;

            Popup popup = new Popup();
            popup.setAutoFix(true);
            popup.setAutoHide(true);

            HBox content = new HBox();
            content.setStyle(BASE_STYLE + "-fx-background-color: " + color + ";");

            // Icon
            FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
            iconView.setSize("20");
            iconView.setStyle("-fx-fill: white;");

            // Message
            Label label = new Label(message);
            label.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

            content.getChildren().addAll(iconView, label);
            popup.getContent().add(content);

            // Position calculation
            double popupX = stage.getX() + stage.getWidth() - content.prefWidth(-1) - 20;
            double popupY = stage.getY() + 50;

            // Show with slide-in animation
            content.setTranslateX(100);
            content.setOpacity(0);

            popup.show(stage, popupX, popupY);

            // Animations
            ParallelTransition parallelTransition = new ParallelTransition();
            parallelTransition.getChildren().addAll(
                    new Timeline(
                            new KeyFrame(Duration.ZERO,
                                    new KeyValue(content.translateXProperty(), 100),
                                    new KeyValue(content.opacityProperty(), 0)
                            ),
                            new KeyFrame(Duration.millis(200),
                                    new KeyValue(content.translateXProperty(), 0),
                                    new KeyValue(content.opacityProperty(), 1)
                            )
                    )
            );

            parallelTransition.setOnFinished(e -> {
                PauseTransition pause = new PauseTransition(Duration.millis(ANIMATION_DURATION));
                pause.setOnFinished(event -> {
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(FADE_OUT_DURATION), content);
                    fadeOut.setFromValue(1);
                    fadeOut.setToValue(0);
                    fadeOut.setOnFinished(fadeEvent -> popup.hide());
                    fadeOut.play();
                });
                pause.play();
            });

            parallelTransition.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static Stage getActiveStage() {
        for (Window window : Stage.getWindows()) {
            if (window instanceof Stage && window.isFocused()) {
                return (Stage) window;
            }
        }
        return Stage.getWindows().stream()
                .filter(window -> window instanceof Stage && window.isShowing())
                .map(Stage.class::cast)
                .findFirst()
                .orElse(null);
    }
}