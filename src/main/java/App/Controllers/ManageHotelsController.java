package App.Controllers;

import Models.DataManager;
import Models.Hotel;
import Models.Utils.SearchAndSort;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * Controller for the manage hotels view.
 * Handles CRUD operations for hotels.
 */
public class ManageHotelsController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private TableView<Hotel> hotelsTable;

    @FXML
    private TableColumn<Hotel, String> idColumn;

    @FXML
    private TableColumn<Hotel, String> nameColumn;

    @FXML
    private TableColumn<Hotel, String> locationColumn;

    @FXML
    private TableColumn<Hotel, Integer> ratingColumn;

    @FXML
    private TableColumn<Hotel, String> descriptionColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField locationField;

    @FXML
    private ComboBox<Integer> ratingComboBox;

    @FXML
    private TextArea descriptionArea;

    private MainController mainController;
    private DataManager dataManager;
    private ObservableList<Hotel> hotelsList = FXCollections.observableArrayList();
    private Hotel selectedHotel;

    @FXML
    private void initialize() {
        // Set up the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Set up the sort combo box
        sortComboBox.getItems().addAll("Name (A-Z)", "Rating (High-Low)");
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                sortHotels(newVal);
            }
        });

        // Set up the rating combo box
        ratingComboBox.getItems().addAll(1, 2, 3, 4, 5);

        // Set the default sort to Rating
        sortComboBox.getSelectionModel().select("Rating (High-Low)");

        // Set up the table selection listener
        hotelsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedHotel = newVal;
                populateFields(newVal);
            }
        });
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        this.dataManager = mainController.getDataManager();
        loadHotels();
    }

    private void loadHotels() {
        List<Hotel> hotels = dataManager.getAllHotels();
        hotelsList.clear();
        hotelsList.addAll(hotels);
        hotelsTable.setItems(hotelsList);

        // Apply default sorting
        sortHotels(sortComboBox.getValue());
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadHotels(); // If the search is empty, load all hotels
            return;
        }

        // Filter hotels by name or location containing the search term
        List<Hotel> allHotels = dataManager.getAllHotels();
        hotelsList.clear();

        for (Hotel hotel : allHotels) {
            if (hotel.getName().toLowerCase().contains(searchTerm) ||
                hotel.getLocation().toLowerCase().contains(searchTerm)) {
                hotelsList.add(hotel);
            }
        }

        // Apply current sorting
        sortHotels(sortComboBox.getValue());
    }

    private void sortHotels(String sortOption) {
        if (sortOption == null) return;

        List<Hotel> sortedList;
        if (sortOption.equals("Name (A-Z)")) {
            sortedList = SearchAndSort.sortHotelsByName(hotelsList);
        } else { // Rating (High-Low)
            sortedList = SearchAndSort.sortHotelsByRating(hotelsList);
        }

        hotelsList.clear();
        hotelsList.addAll(sortedList);
    }

    @FXML
    private void handleAdd() {
        // Validate input
        if (!validateInput()) {
            return;
        }

        // Create a new hotel
        String name = nameField.getText().trim();
        String location = locationField.getText().trim();
        int rating = ratingComboBox.getValue();
        String description = descriptionArea.getText().trim();

        Hotel hotel = dataManager.createHotel(name, location, rating, description);

        // Refresh the list
        loadHotels();

        // Clear the fields
        clearFields();

        showAlert("Hotel Added", "Hotel has been added successfully.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleUpdate() {
        if (selectedHotel == null) {
            showAlert("No Selection", "Please select a hotel to update.", Alert.AlertType.WARNING);
            return;
        }

        // Validate input
        if (!validateInput()) {
            return;
        }

        // Update a hotel
        selectedHotel.setName(nameField.getText().trim());
        selectedHotel.setLocation(locationField.getText().trim());
        selectedHotel.setRating(ratingComboBox.getValue());
        selectedHotel.setDescription(descriptionArea.getText().trim());

        dataManager.updateHotel(selectedHotel);

        // Refresh the list
        loadHotels();

        showAlert("Hotel Updated", "Hotel has been updated successfully.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleDelete() {
        if (selectedHotel == null) {
            showAlert("No Selection", "Please select a hotel to delete.", Alert.AlertType.WARNING);
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete the hotel: " + selectedHotel.getName() + "?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dataManager.deleteHotel(selectedHotel.getId());
                loadHotels();
                clearFields();
                selectedHotel = null;
                showAlert("Hotel Deleted", "Hotel has been deleted successfully.", Alert.AlertType.INFORMATION);
            }
        });
    }

    @FXML
    private void handleClear() {
        clearFields();
        selectedHotel = null;
        hotelsTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleManageRooms() {
        if (selectedHotel == null) {
            showAlert("No Selection", "Please select a hotel to manage its rooms.", Alert.AlertType.WARNING);
            return;
        }

        mainController.showManageRoomsView();
    }

    @FXML
    private void handleBack() {
        mainController.showWelcomeView();
    }

    private void populateFields(Hotel hotel) {
        nameField.setText(hotel.getName());
        locationField.setText(hotel.getLocation());
        ratingComboBox.setValue(hotel.getRating());
        descriptionArea.setText(hotel.getDescription());
    }

    private void clearFields() {
        nameField.clear();
        locationField.clear();
        ratingComboBox.getSelectionModel().clearSelection();
        descriptionArea.clear();
    }

    private boolean validateInput() {
        String name = nameField.getText().trim();
        String location = locationField.getText().trim();
        Integer rating = ratingComboBox.getValue();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty() || location.isEmpty() || rating == null || description.isEmpty()) {
            showAlert("Invalid Input", "All fields are required.", Alert.AlertType.WARNING);
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
