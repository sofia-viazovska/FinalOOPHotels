package Models.Utils.Logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Default implementation of LogFormatter that formats logs as human-readable text.
 */
public class DefaultLogFormatter implements LogFormatter {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String formatEntry(LogLevel level, String className, String methodName, Map<String, Object> args) {
        StringBuilder sb = new StringBuilder();
        sb.append(getCurrentTimestamp())
          .append(" [").append(level).append("] ")
          .append(className).append(".").append(methodName)
          .append(" - ENTRY - Args: ");

        if (args != null && !args.isEmpty()) {
            args.forEach((name, value) ->
                sb.append(name).append("=").append(formatValue(value)).append(", "));
            sb.setLength(sb.length() - 2); // Remove trailing comma and space
        } else {
            sb.append("none");
        }

        return sb.toString();
    }

    @Override
    public String formatExit(LogLevel level, String className, String methodName, Object result, long executionTimeMs) {
        return getCurrentTimestamp() +
               " [" + level + "] " +
               className + "." + methodName +
               " - EXIT - Result: " + formatValue(result) +
               " (execution time: " + executionTimeMs + "ms)";
    }

    @Override
    public String formatError(LogLevel level, String className, String methodName, Throwable error, long executionTimeMs) {
        return getCurrentTimestamp() +
               " [" + level + "] " +
               className + "." + methodName +
               " - ERROR - " + error.getClass().getSimpleName() + ": " + error.getMessage() +
               " (execution time: " + executionTimeMs + "ms)";
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }

        // Special handling for arrays
        if (value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < array.length; i++) {
                sb.append(formatValue(array[i]));
                if (i < array.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            return sb.toString();
        }

        return value.toString();
    }
}
