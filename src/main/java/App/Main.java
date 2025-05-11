package App;

import Models.DataManager;
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
}
