package Models.Utils.Logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Implementation of LogFormatter that formats logs as JSON for structured logging.
 */
public class JsonLogFormatter implements LogFormatter {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public String formatEntry(LogLevel level, String className, String methodName, Map<String, Object> args) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"timestamp\":\"").append(getCurrentTimestamp()).append("\",");
        sb.append("\"level\":\"").append(level).append("\",");
        sb.append("\"class\":\"").append(className).append("\",");
        sb.append("\"method\":\"").append(methodName).append("\",");
        sb.append("\"event\":\"ENTRY\",");
        sb.append("\"args\":{");

        if (args != null && !args.isEmpty()) {
            int count = 0;
            for (Map.Entry<String, Object> entry : args.entrySet()) {
                sb.append("\"").append(entry.getKey()).append("\":");
                sb.append(formatJsonValue(entry.getValue()));
                if (++count < args.size()) {
                    sb.append(",");
                }
            }
        }

        sb.append("}}");
        return sb.toString();
    }

    @Override
    public String formatExit(LogLevel level, String className, String methodName, Object result, long executionTimeMs) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"timestamp\":\"").append(getCurrentTimestamp()).append("\",");
        sb.append("\"level\":\"").append(level).append("\",");
        sb.append("\"class\":\"").append(className).append("\",");
        sb.append("\"method\":\"").append(methodName).append("\",");
        sb.append("\"event\":\"EXIT\",");
        sb.append("\"result\":").append(formatJsonValue(result)).append(",");
        sb.append("\"executionTimeMs\":").append(executionTimeMs);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String formatError(LogLevel level, String className, String methodName, Throwable error, long executionTimeMs) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"timestamp\":\"").append(getCurrentTimestamp()).append("\",");
        sb.append("\"level\":\"").append(level).append("\",");
        sb.append("\"class\":\"").append(className).append("\",");
        sb.append("\"method\":\"").append(methodName).append("\",");
        sb.append("\"event\":\"ERROR\",");
        sb.append("\"errorType\":\"").append(error.getClass().getSimpleName()).append("\",");
        sb.append("\"errorMessage\":\"").append(escapeJson(error.getMessage())).append("\",");
        sb.append("\"executionTimeMs\":").append(executionTimeMs);
        sb.append("}");
        return sb.toString();
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }

    private String formatJsonValue(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof Number) {
            return value.toString();
        }

        if (value instanceof Boolean) {
            return value.toString();
        }

        if (value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < array.length; i++) {
                sb.append(formatJsonValue(array[i]));
                if (i < array.length - 1) {
                    sb.append(",");
                }
            }
            sb.append("]");
            return sb.toString();
        }

        return "\"" + escapeJson(value.toString()) + "\"";
    }

    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }

        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
