package Models.Utils.Logging;

/**
 * Interface for log output destinations.
 */
public interface LogDestination {
    /**
     * Log a message to the destination.
     *
     * @param message The formatted log message
     */
    void log(String message);
}
