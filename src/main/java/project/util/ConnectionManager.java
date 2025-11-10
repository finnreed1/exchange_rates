package project.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Prepares a writable SQLite database copy in the user's home directory so the app keeps working
 * when it is packaged as a WAR and deployed outside the project root.
 */
public final class ConnectionManager {
    private static final String DRIVER_KEY = "db.driver";
    private static final String DB_FILE_KEY = "db.filename";
    private static final Path STORAGE_DIR = Paths.get(System.getProperty("user.home"), ".exchange_rates");

    private static String jdbcUrl;

    static {
        prepareDatabaseFile();
        loadDriver();
    }

    private ConnectionManager() {
    }

    public static Connection get() {
        try {
            return DriverManager.getConnection(jdbcUrl);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to obtain DB connection", e);
        }
    }

    private static void prepareDatabaseFile() {
        try {
            Files.createDirectories(STORAGE_DIR);
            String fileName = PropertiesUtil.get(DB_FILE_KEY);
            if (fileName == null || fileName.isBlank()) {
                throw new IllegalStateException("Property 'db.filename' is not configured.");
            }
            Path destination = STORAGE_DIR.resolve(fileName);
            if (Files.notExists(destination)) {
                try (InputStream input = ConnectionManager.class.getClassLoader().getResourceAsStream(fileName)) {
                    if (input == null) {
                        throw new IllegalStateException(
                                "Seed database '%s' is missing from the classpath.".formatted(fileName));
                    }
                    Files.copy(input, destination);
                }
            }
            jdbcUrl = "jdbc:sqlite:" + destination.toAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("Unable to prepare SQLite database file", e);
        }
    }

    private static void loadDriver() {
        try {
            Class.forName(PropertiesUtil.get(DRIVER_KEY));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to load DB driver", e);
        }
    }
}
