package Models.Utils.Logging;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton manager for logging configuration and operations.
 */
public class LogManager {
    private static LogManager instance;
    private final List<LogDestination> destinations = new ArrayList<>();
    private LogFormatter formatter = new DefaultLogFormatter();
    private LogLevel minLevel = LogLevel.INFO;

    /**
     * Private constructor for singleton pattern.
     */
    private LogManager() {
        // Default to console logging
        destinations.add(new ConsoleLogDestination());

        // Also log to file by default
        destinations.add(new FileLogDestination("logs/application.log"));
    }

    /**
     * Get the singleton instance.
     *
     * @return The LogManager instance
     */
    public static synchronized LogManager getInstance() {
        if (instance == null) {
            instance = new LogManager();
        }
        return instance;
    }

    /**
     * Set the log formatter.
     *
     * @param formatter The formatter to use
     */
    public void setFormatter(LogFormatter formatter) {
        this.formatter = formatter;
    }

    /**
     * Get the current log formatter.
     *
     * @return The current formatter
     */
    public LogFormatter getFormatter() {
        return formatter;
    }

    /**
     * Set the minimum log level.
     *
     * @param level The minimum level to log
     */
    public void setMinLevel(LogLevel level) {
        this.minLevel = level;
    }

    /**
     * Get the minimum log level.
     *
     * @return The minimum log level
     */
    public LogLevel getMinLevel() {
        return minLevel;
    }

    /**
     * Add a log destination.
     *
     * @param destination The destination to add
     */
    public void addDestination(LogDestination destination) {
        destinations.add(destination);
    }

    /**
     * Clear all log destinations.
     */
    public void clearDestinations() {
        destinations.clear();
    }

    /**
     * Log a message to all destinations.
     *
     * @param message The message to log
     */
    public void log(String message) {
        for (LogDestination destination : destinations) {
            destination.log(message);
        }
    }

    /**
     * Check if a log level is enabled.
     *
     * @param level The level to check
     * @return True if the level is enabled
     */
    public boolean isLevelEnabled(LogLevel level) {
        return level.ordinal() >= minLevel.ordinal();
    }
}
