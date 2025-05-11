package App.Controllers;

import Models.DataManager;
import Models.Hotel;
import Models.Utils.SearchAndSort;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the hotels view.
 * Handles displaying, searching, and sorting hotels.
 */
public class HotelsController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private TableView<Hotel> hotelsTable;

    @FXML
    private TableColumn<Hotel, String> nameColumn;

    @FXML
    private TableColumn<Hotel, String> locationColumn;

    @FXML
    private TableColumn<Hotel, Integer> ratingColumn;

    @FXML
    private TableColumn<Hotel, String> descriptionColumn;

    private MainController mainController;
    private DataManager dataManager;
    private ObservableList<Hotel> hotelsList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Set up the table columns to display hotel properties
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Set up the sort combo box with sorting options
        sortComboBox.getItems().addAll("Name (A-Z)", "Rating (High-Low)");
        // Add listener to apply sorting when selection changes
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                sortHotels(newVal);
            }
        });

        // Set default sort to Rating (High-Low) for better user experience
        sortComboBox.getSelectionModel().select("Rating (High-Low)");
    }

    public void setMainController(MainController mainController) {
        // Store reference to main controller for navigation
        this.mainController = mainController;
        // Get data manager from main controller to access hotel data
        this.dataManager = mainController.getDataManager();
        // Load hotels from data manager
        loadHotels();
    }

    private void loadHotels() {
        hotelsList.clear();
        // Get all hotels from data manager
        List<Hotel> hotels = dataManager.getAllHotels();
        // Clear current list to avoid duplicates
        // Add all hotels to the observable list
        hotelsList.addAll(hotels);
        // Set the table to display the hotels list
        hotelsTable.setItems(hotelsList);

        // Apply default sorting based on current combo box selection
        sortHotels(sortComboBox.getValue());
    }

    @FXML
    private void handleSearch() {
        // Get search term and convert to lowercase for case-insensitive search
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadHotels(); // If search is empty, load all hotels
            return;
        }

        // Filter hotels by name or location containing the search term
        List<Hotel> allHotels = dataManager.getAllHotels();
        // Clear current list before adding filtered results
        hotelsList.clear();

        // Add hotels that match the search criteria
        for (Hotel hotel : allHotels) {
            if (hotel.getName().toLowerCase().contains(searchTerm) ||
                hotel.getLocation().toLowerCase().contains(searchTerm)) {
                hotelsList.add(hotel);
            }
        }

        // Apply current sorting to maintain consistent display order
        sortHotels(sortComboBox.getValue());
    }

    private void sortHotels(String sortOption) {
        // Return early if no sort option is selected
        if (sortOption == null) return;

        List<Hotel> sortedList;
        // Choose sorting method based on selected option
        if (sortOption.equals("Name (A-Z)")) {
            // Sort alphabetically by name
            sortedList = SearchAndSort.sortHotelsByName(hotelsList);
        } else { // Rating (High-Low)
            // Sort by rating in descending order
            sortedList = SearchAndSort.sortHotelsByRating(hotelsList);
        }

        // Update the list with sorted results
        hotelsList.clear();
        hotelsList.addAll(sortedList);
    }

    @FXML
    private void handleViewRooms() {
        // Get the selected hotel from the table
        Hotel selectedHotel = hotelsTable.getSelectionModel().getSelectedItem();
        // Check if a hotel is selected
        if (selectedHotel == null) {
            // Show warning if no hotel is selected
            showAlert("No Selection", "Please select a hotel to view its rooms.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Load the rooms view FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/Views/RoomsView.fxml"));
            Parent view = loader.load();
            // Get the rooms controller
            RoomsController controller = loader.getController();
            // Set the main controller for navigation
            controller.setMainController(mainController);
            // Pass the selected hotel to the rooms controller
            controller.setHotel(selectedHotel);
            // Display the rooms view
            mainController.setContent(view);
        } catch (IOException e) {
            // Log the error
            e.printStackTrace();
            // Show error alert if loading fails
            showAlert("Error", "Could not load rooms view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBack() {
        // Navigate back to the welcome view
        mainController.showWelcomeView();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        // Create a new alert dialog
        Alert alert = new Alert(alertType);
        // Set the alert title
        alert.setTitle(title);
        // Remove the header text for cleaner appearance
        alert.setHeaderText(null);
        // Set the alert message
        alert.setContentText(message);
        // Show the alert and wait for user response
        alert.showAndWait();
    }
}
