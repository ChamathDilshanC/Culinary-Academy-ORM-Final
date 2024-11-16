package lk.ijse.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class StageManager {
    private static double xOffset = 0;
    private static double yOffset = 0;

    public static void changeScene(Stage stage, String fxmlPath, String title) throws IOException {
        URL resource = StageManager.class.getResource(fxmlPath);
        if (resource == null) {
            throw new RuntimeException("FXML file not found: " + fxmlPath);
        }

        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);

        // Add mouse event handlers for window dragging
        root.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        stage.setScene(scene);
        stage.setTitle(title);
        stage.centerOnScreen();
    }
}