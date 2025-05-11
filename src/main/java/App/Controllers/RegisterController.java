package App.Controllers;

import Models.DataManager;
import Models.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for the register view.
 * Handles user registration.
 */
public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private Label errorLabel;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleRegister() {
        // Clear previous error messages
        errorLabel.setText("");

        // Get input values
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        // Validate input
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            fullName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            errorLabel.setText("All fields are required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match");
            return;
        }

        if (password.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters long");
            return;
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errorLabel.setText("Invalid email format");
            return;
        }

        // Check if username already exists
        DataManager dataManager = mainController.getDataManager();
        User existingUser = dataManager.getUserByUsername(username);
        if (existingUser != null) {
            errorLabel.setText("Username already exists");
            return;
        }

        // Create new user
        User newUser = dataManager.createUser(username, password, fullName, email, phone);

        if (newUser != null) {
            // Auto-login the new user
            mainController.setCurrentUser(newUser);

            // Show welcome view
            mainController.showWelcomeView();
        } else {
            errorLabel.setText("Failed to create user");
        }
    }

    @FXML
    private void handleCancel() {
        // Clear fields
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        fullNameField.clear();
        emailField.clear();
        phoneField.clear();
        errorLabel.setText("");

        // Return to welcome view
        mainController.showWelcomeView();
    }
}
