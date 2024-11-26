package lk.ijse.util;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class InitializationTracker {
    private static final String PROGRAMS_INIT_FILE = "programs_initialized.txt";
    private static final String USERS_INIT_FILE = "users_initialized.txt";

    public static boolean isProgramsInitialized() {
        Path path = Paths.get(PROGRAMS_INIT_FILE);
        return Files.exists(path);
    }

    public static boolean isUsersInitialized() {
        Path path = Paths.get(USERS_INIT_FILE);
        return Files.exists(path);
    }

    public static void markProgramsAsInitialized() {
        writeInitializationFile(PROGRAMS_INIT_FILE, "Programs");
    }

    public static void markUsersAsInitialized() {
        writeInitializationFile(USERS_INIT_FILE, "Users");
    }

    // For backward compatibility
    public static boolean isInitialized() {
        return isProgramsInitialized() && isUsersInitialized();
    }

    public static void markAsInitialized() {
        markProgramsAsInitialized();
        markUsersAsInitialized();
    }

    private static void writeInitializationFile(String fileName, String component) {
        try {
            File file = new File(fileName);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(String.format("%s database initialized on: %s",
                        component, LocalDateTime.now()));
            }
        } catch (Exception e) {
            System.err.println("Error writing initialization file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void reset() {
        try {
            Files.deleteIfExists(Paths.get(PROGRAMS_INIT_FILE));
            Files.deleteIfExists(Paths.get(USERS_INIT_FILE));
        } catch (Exception e) {
            System.err.println("Error resetting initialization files: " + e.getMessage());
            e.printStackTrace();
        }
    }
}