package Models.Utils.Logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * LogDestination implementation that logs to a file.
 */
public class FileLogDestination implements LogDestination {
    private final String logFilePath;

    /**
     * Create a new FileLogDestination.
     *
     * @param logFilePath Path to the log file
     */
    public FileLogDestination(String logFilePath) {
        this.logFilePath = logFilePath;
        ensureLogDirectoryExists();
    }

    @Override
    public void log(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFilePath, true))) {
            writer.println(message);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }

    /**
     * Ensure the directory for the log file exists.
     */
    private void ensureLogDirectoryExists() {
        Path path = Paths.get(logFilePath).getParent();
        if (path != null) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                System.err.println("Failed to create log directory: " + e.getMessage());
            }
        }
    }
}
