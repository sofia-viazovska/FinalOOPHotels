package Models.Utils.Logging;

/**
 * LogDestination implementation that logs to the console.
 */
public class ConsoleLogDestination implements LogDestination {
    @Override
    public void log(String message) {
        System.out.println(message);
    }
}
