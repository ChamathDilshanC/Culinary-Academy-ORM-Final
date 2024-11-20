package lk.ijse.util;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InitializationTracker {
    private static final String INIT_FILE = "db_initialized.txt";

    public static boolean isInitialized() {
        Path path = Paths.get(INIT_FILE);
        return Files.exists(path);
    }

    public static void markAsInitialized() {
        try {
            File file = new File(INIT_FILE);
            FileWriter writer = new FileWriter(file);
            writer.write("Database initialized on: " + java.time.LocalDateTime.now());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}