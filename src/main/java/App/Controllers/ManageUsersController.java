package App.Controllers;

import Models.Booking;
import Models.DataManager;
import Models.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the manage users view.
 * Handles CRUD operations for users.
 */
public class ManageUsersController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, String> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> fullNameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> phoneColumn;

    @FXML
    private TableColumn<User, String> adminColumn;

    @FXML
    private TableColumn<User, String> bookingsColumn;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private CheckBox adminCheckBox;

    private MainController mainController;
    private DataManager dataManager;
    private ObservableList<User> usersList = FXCollections.observableArrayList();
    private User selectedUser;

    @FXML
    private void initialize() {
        // Set up the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        adminColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().isAdmin() ? "Yes" : "No"));
        bookingsColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(String.valueOf(cellData.getValue().getBookings().size())));

        // Set up table selection listener
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedUser = newVal;
                populateFields(newVal);
            }
        });
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        this.dataManager = mainController.getDataManager();
        loadUsers();
    }

    private void loadUsers() {
        List<User> users = dataManager.getAllUsers();
        usersList.clear();
        usersList.addAll(users);
        usersTable.setItems(usersList);
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadUsers(); // If search is empty, load all users
            return;
        }

        // Filter users by username, full name, or email containing the search term
        List<User> allUsers = dataManager.getAllUsers();
        usersList.clear();

        for (User user : allUsers) {
            if (user.getUsername().toLowerCase().contains(searchTerm) ||
                user.getFullName().toLowerCase().contains(searchTerm) ||
                user.getEmail().toLowerCase().contains(searchTerm)) {
                usersList.add(user);
            }
        }

        usersTable.setItems(usersList);
    }

    @FXML
    private void handleAdd() {
        // Validate input
        if (!validateInput(true)) {
            return;
        }

        // Create new user
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        boolean isAdmin = adminCheckBox.isSelected();

        User user = dataManager.createUser(username, password, fullName, email, phone, isAdmin);

        // Refresh the list
        loadUsers();

        // Clear the fields
        clearFields();

        showAlert("User Added", "User has been added successfully.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleUpdate() {
        if (selectedUser == null) {
            showAlert("No Selection", "Please select a user to update.", Alert.AlertType.WARNING);
            return;
        }

        // Validate input
        if (!validateInput(false)) {
            return;
        }

        // Update user
        selectedUser.setUsername(usernameField.getText().trim());
        if (!passwordField.getText().trim().isEmpty()) {
            selectedUser.setPassword(passwordField.getText().trim());
        }
        selectedUser.setFullName(fullNameField.getText().trim());
        selectedUser.setEmail(emailField.getText().trim());
        selectedUser.setPhoneNumber(phoneField.getText().trim());
        selectedUser.setAdmin(adminCheckBox.isSelected());

        dataManager.updateUser(selectedUser);

        // Refresh the list
        loadUsers();

        showAlert("User Updated", "User has been updated successfully.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleDelete() {
        if (selectedUser == null) {
            showAlert("No Selection", "Please select a user to delete.", Alert.AlertType.WARNING);
            return;
        }

        // Prevent deleting the current user
        if (selectedUser.getId().equals(mainController.getCurrentUser().getId())) {
            showAlert("Cannot Delete", "You cannot delete your own account.", Alert.AlertType.WARNING);
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete the user: " + selectedUser.getUsername() + "?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Check if user has bookings
                if (!selectedUser.getBookings().isEmpty()) {
                    Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                    warningAlert.setTitle("User Has Bookings");
                    warningAlert.setHeaderText(null);
                    warningAlert.setContentText("This user has " + selectedUser.getBookings().size() + " bookings. Deleting this user will also delete all their bookings. Do you want to continue?");
                    warningAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

                    warningAlert.showAndWait().ifPresent(warningResponse -> {
                        if (warningResponse == ButtonType.YES) {
                            deleteUser();
                        }
                    });
                } else {
                    deleteUser();
                }
            }
        });
    }

    private void deleteUser() {
        // Delete all bookings for this user
        List<Booking> bookings = new ArrayList<>(selectedUser.getBookings());
        for (Booking booking : bookings) {
            dataManager.deleteBooking(booking.getId());
        }

        // Delete user
        dataManager.deleteUser(selectedUser.getId());

        // Refresh the list
        loadUsers();

        clearFields();
        selectedUser = null;
        showAlert("User Deleted", "User has been deleted successfully.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleClear() {
        clearFields();
        selectedUser = null;
        usersTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleViewBookings() {
        if (selectedUser == null) {
            showAlert("No Selection", "Please select a user to view their bookings.", Alert.AlertType.WARNING);
            return;
        }

        // Set the selected user as the current user temporarily to view their bookings
        User originalUser = mainController.getCurrentUser();
        mainController.setCurrentUser(selectedUser);

        // Show the bookings view
        mainController.showMyBookingsView();

        // Restore the original user
        mainController.setCurrentUser(originalUser);
    }

    @FXML
    private void handleBack() {
        mainController.showWelcomeView();
    }

    private void populateFields(User user) {
        usernameField.setText(user.getUsername());
        passwordField.clear(); // Don't show the password
        fullNameField.setText(user.getFullName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhoneNumber());
        adminCheckBox.setSelected(user.isAdmin());
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        fullNameField.clear();
        emailField.clear();
        phoneField.clear();
        adminCheckBox.setSelected(false);
    }

    private boolean validateInput(boolean isNewUser) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showAlert("Invalid Input", "Username, full name, email, and phone are required.", Alert.AlertType.WARNING);
            return false;
        }

        if (isNewUser && password.isEmpty()) {
            showAlert("Invalid Input", "Password is required for new users.", Alert.AlertType.WARNING);
            return false;
        }

        // Check if username already exists (for new users)
        if (isNewUser) {
            User existingUser = dataManager.getUserByUsername(username);
            if (existingUser != null) {
                showAlert("Username Exists", "Username already exists. Please choose a different username.", Alert.AlertType.WARNING);
                return false;
            }
        }

        // Validate email format
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert("Invalid Email", "Please enter a valid email address.", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
