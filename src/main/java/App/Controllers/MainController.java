package App.Controllers;

import App.Main;
import Models.DataManager;
import Models.User;
import Models.Utils.DataFileViewer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the main view of the application.
 * Handles navigation between different views.
 */
public class MainController {
    @FXML
    private StackPane contentArea;

    @FXML
    private Label statusLabel;

    private DataManager dataManager;
    private User currentUser;

    @FXML
    private void initialize() {
        dataManager = Main.getDataManager();
        updateStatusLabel();
        showWelcomeView();
    }

    public void showWelcomeView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/Views/WelcomeView.fxml"));
            Parent view = loader.load();
            WelcomeController controller = loader.getController();
            controller.setMainController(this);
            setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewDataFiles() {
        // Get the current stage from the contentArea
        Stage stage = (Stage) contentArea.getScene().getWindow();
        // Show the data file viewer
        DataFileViewer.viewDataFile(stage);
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    @FXML
    public void showHotelsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/Views/HotelsView.fxml"));
            Parent view = loader.load();
            HotelsController controller = loader.getController();
            controller.setMainController(this);
            setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load hotels view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void showMyBookingsView() {
        if (currentUser == null) {
            showLoginRequiredAlert();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/Views/MyBookingsView.fxml"));
            Parent view = loader.load();
            MyBookingsController controller = loader.getController();
            controller.setMainController(this);
            setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load my bookings view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/Views/LoginView.fxml"));
            Parent view = loader.load();
            LoginController controller = loader.getController();
            controller.setMainController(this);
            setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load login view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void showRegisterView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/Views/RegisterView.fxml"));
            Parent view = loader.load();
            RegisterController controller = loader.getController();
            controller.setMainController(this);
            setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load register view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleLogout() {
        currentUser = null;
        updateStatusLabel();
        showAlert("Logout", "You have been logged out successfully.", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void showManageHotelsView() {
        if (currentUser == null) {
            showLoginRequiredAlert();
            return;
        }

        if (!currentUser.isAdmin()) {
            showAdminRequiredAlert();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/Views/ManageHotelsView.fxml"));
            Parent view = loader.load();
            ManageHotelsController controller = loader.getController();
            controller.setMainController(this);
            setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load manage hotels view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void showManageRoomsView() {
        if (currentUser == null) {
            showLoginRequiredAlert();
            return;
        }

        if (!currentUser.isAdmin()) {
            showAdminRequiredAlert();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/Views/ManageRoomsView.fxml"));
            Parent view = loader.load();
            ManageRoomsController controller = loader.getController();
            controller.setMainController(this);
            setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load manage rooms view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void showManageBookingsView() {
        if (currentUser == null) {
            showLoginRequiredAlert();
            return;
        }

        if (!currentUser.isAdmin()) {
            showAdminRequiredAlert();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/Views/ManageBookingsView.fxml"));
            Parent view = loader.load();
            ManageBookingsController controller = loader.getController();
            controller.setMainController(this);
            setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load manage bookings view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void showManageUsersView() {
        if (currentUser == null) {
            showLoginRequiredAlert();
            return;
        }

        if (!currentUser.isAdmin()) {
            showAdminRequiredAlert();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/Views/ManageUsersView.fxml"));
            Parent view = loader.load();
            ManageUsersController controller = loader.getController();
            controller.setMainController(this);
            setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load manage users view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAdminRequiredAlert() {
        showAlert("Admin Required", "You must be an administrator to access this feature.", Alert.AlertType.WARNING);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateStatusLabel();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    private void updateStatusLabel() {
        if (currentUser != null) {
            statusLabel.setText("Logged in as: " + currentUser.getUsername());
        } else {
            statusLabel.setText("Not logged in");
        }
    }

    private void showNotImplementedAlert(String feature) {
        showAlert("Not Implemented", feature + " is not implemented yet.", Alert.AlertType.INFORMATION);
    }

    private void showLoginRequiredAlert() {
        showAlert("Login Required", "You must be logged in to access this feature.", Alert.AlertType.WARNING);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setContent(Parent content) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
    }
}
