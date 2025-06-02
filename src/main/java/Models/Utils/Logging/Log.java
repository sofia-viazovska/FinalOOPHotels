package Models.Utils.Logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Annotation for logging method calls.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    /**
     * The log level.
     */
    LogLevel level() default LogLevel.INFO;

    /**
     * Whether to log method arguments.
     */
    boolean logArgs() default true;

    /**
     * Whether to log method result.
     */
    boolean logResult() default true;

    /**
     * Whether to log execution time.
     */
    boolean logExecutionTime() default true;

    /**
     * Utility class for applying the logging decorator programmatically.
     */
    class Decorator {
        /**
         * Wraps a method call with logging.
         *
         * @param level The log level
         * @param callable The method to call
         * @param methodName The method name for logging
         * @param className The class name for logging
         * @param args Method arguments
         * @return The result of the method call
         * @throws Exception If the method throws an exception
         */
        public static <T> T withLogging(LogLevel level, Callable<T> callable,
                                       String methodName, String className,
                                       Map<String, Object> args) throws Exception {
            LogManager logManager = LogManager.getInstance();

            if (!logManager.isLevelEnabled(level)) {
                return callable.call();
            }

            LogFormatter formatter = logManager.getFormatter();

            // Log method entry
            logManager.log(formatter.formatEntry(level, className, methodName, args));

            long startTime = System.currentTimeMillis();
            try {
                // Execute the method
                T result = callable.call();

                // Log method exit
                long executionTime = System.currentTimeMillis() - startTime;
                logManager.log(formatter.formatExit(level, className, methodName, result, executionTime));

                return result;
            } catch (Exception e) {
                // Log error
                long executionTime = System.currentTimeMillis() - startTime;
                logManager.log(formatter.formatError(level, className, methodName, e, executionTime));
                throw e;
            }
        }

        /**
         * Wraps a void method call with logging.
         *
         * @param level The log level
         * @param runnable The method to call
         * @param methodName The method name for logging
         * @param className The class name for logging
         * @param args Method arguments
         * @throws Exception If the method throws an exception
         */
        public static void withLoggingVoid(LogLevel level, Runnable runnable,
                                          String methodName, String className,
                                          Map<String, Object> args) throws Exception {
            withLogging(level, () -> {
                runnable.run();
                return null;
            }, methodName, className, args);
        }

        /**
         * Creates a map of method arguments from parameter names and values.
         *
         * @param method The method
         * @param args The argument values
         * @return A map of parameter names to values
         */
        public static Map<String, Object> createArgsMap(Method method, Object[] args) {
            Map<String, Object> argsMap = new HashMap<>();
            Parameter[] parameters = method.getParameters();

            for (int i = 0; i < parameters.length && i < args.length; i++) {
                argsMap.put(parameters[i].getName(), args[i]);
            }

            return argsMap;
        }
    }
}
