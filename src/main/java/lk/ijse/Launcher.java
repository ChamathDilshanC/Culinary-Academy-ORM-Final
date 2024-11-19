package lk.ijse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/dashboard_form.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Get screen dimensions
        Screen screen = Screen.getPrimary();
        double screenWidth = screen.getBounds().getWidth();
        double screenHeight = screen.getBounds().getHeight();

        // Configure stage
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);

        // Set stage size to screen size
        stage.setWidth(screenWidth);
        stage.setHeight(screenHeight);

        // Make it start maximized
        stage.setMaximized(true);

        // Position the window at (0,0)
        stage.setX(0);
        stage.setY(0);

        // Prevent window movement and resizing
        stage.setResizable(false);

        // Consume mouse events to prevent window dragging
        scene.setOnMousePressed(event -> event.consume());
        scene.setOnMouseDragged(event -> event.consume());

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}