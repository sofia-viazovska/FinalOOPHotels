package App.Controllers;

import Models.DataManager;
import Models.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for the login view.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password cannot be empty");
            return;
        }

        // Get the data manager from the main controller
        DataManager dataManager = mainController.getDataManager();

        // Check if a user exists
        User user = dataManager.getUserByUsername(username);

        if (user == null) {
            errorLabel.setText("User not found");
            return;
        }

        // Validate password
        if (!user.getPassword().equals(password)) {
            errorLabel.setText("Invalid password");
            return;
        }

        // Login successful
        mainController.setCurrentUser(user);
        mainController.showWelcomeView();
    }

    @FXML
    private void handleCancel() {
        // Clear fields
        usernameField.clear();
        passwordField.clear();
        errorLabel.setText("");

        // Return to welcome view
        mainController.showWelcomeView();
    }
}
