package App;

import Models.DataManager;
import Models.Utils.Logging.ConsoleLogDestination;
import Models.Utils.Logging.DefaultLogFormatter;
import Models.Utils.Logging.FileLogDestination;
import Models.Utils.Logging.LogLevel;
import Models.Utils.Logging.LogManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static DataManager dataManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize the logging system
        initializeLogging();

        // Initialize the data manager
        dataManager = new DataManager();

        try {
            // Load the main view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/Views/MainView.fxml"));
            BorderPane root = loader.load();

            // Set up the scene
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/App/Styles/styles.css").toExternalForm());

            // Set up the stage
            primaryStage.setTitle("Hotel Booking System");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DataManager getDataManager() {
        return dataManager;
    }

    /**
     * Initialize the logging system with console and file destinations.
     */
    private void initializeLogging() {
        // Get the singleton instance
        LogManager logManager = LogManager.getInstance();

        // Clear any existing destinations
        logManager.clearDestinations();

        // Add console destination
        logManager.addDestination(new ConsoleLogDestination());

        // Add file destination with date in filename
        String logFileName = "logs/application-" +
                             java.time.LocalDate.now().toString() + ".log";
        logManager.addDestination(new FileLogDestination(logFileName));

        // Set default formatter
        logManager.setFormatter(new DefaultLogFormatter());

        // Set minimum log level
        logManager.setMinLevel(LogLevel.INFO);

        // Log initialization
        logManager.log("Logging system initialized at " +
                       java.time.LocalDateTime.now().format(
                           java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
