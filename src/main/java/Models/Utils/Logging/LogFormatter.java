package Models.Utils.Logging;

import java.util.Map;

/**
 * Interface for formatting log messages.
 */
public interface LogFormatter {
    /**
     * Format a log entry for method entry.
     *
     * @param level The log level
     * @param className The class name
     * @param methodName The method name
     * @param args Method arguments
     * @return Formatted log message
     */
    String formatEntry(LogLevel level, String className, String methodName, Map<String, Object> args);

    /**
     * Format a log entry for method exit.
     *
     * @param level The log level
     * @param className The class name
     * @param methodName The method name
     * @param result Method result
     * @param executionTimeMs Execution time in milliseconds
     * @return Formatted log message
     */
    String formatExit(LogLevel level, String className, String methodName, Object result, long executionTimeMs);

    /**
     * Format a log entry for method error.
     *
     * @param level The log level
     * @param className The class name
     * @param methodName The method name
     * @param error The exception
     * @param executionTimeMs Execution time in milliseconds
     * @return Formatted log message
     */
    String formatError(LogLevel level, String className, String methodName, Throwable error, long executionTimeMs);
}
