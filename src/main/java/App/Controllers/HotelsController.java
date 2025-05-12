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

    @FXML
    private ListView<Hotel> recentlyViewedListView;

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
        // Add a listener to apply sorting when selection changes
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                sortHotels(newVal);
            }
        });

        // Set the default sort to Rating (High-Low) for better user experience
        sortComboBox.getSelectionModel().select("Rating (High-Low)");

        // Set up the recently viewed hotel list view
        recentlyViewedListView.setCellFactory(param -> new ListCell<Hotel>() {
            @Override
            protected void updateItem(Hotel hotel, boolean empty) {
                super.updateItem(hotel, empty);
                if (empty || hotel == null) {
                    setText(null);
                } else {
                    setText(hotel.getName() + " (" + hotel.getRating() + "★) - " + hotel.getLocation());
                }
            }
        });

        // Add a listener to handle selection in the recently viewed list
        recentlyViewedListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Select the hotel in the main table
                for (Hotel hotel : hotelsTable.getItems()) {
                    if (hotel.getId().equals(newVal.getId())) {
                        hotelsTable.getSelectionModel().select(hotel);
                        hotelsTable.scrollTo(hotel);
                        break;
                    }
                }
            }
        });
    }

    public void setMainController(MainController mainController) {
        // Store reference to the main controller for navigation
        this.mainController = mainController;
        // Get data manager from the main controller to access hotel data
        this.dataManager = mainController.getDataManager();
        // Load hotels from data manager
        loadHotels();
        // Update the recently viewed hotels list
        updateRecentlyViewedHotels();
    }

    /**
     * Updates the recently viewed the hotels list view with hotels from the data manager.
     */
    private void updateRecentlyViewedHotels() {
        // Get the recently viewed hotels from the data manager
        Models.DataStructures.LinkedList<Hotel> recentlyViewed = dataManager.getRecentlyViewedHotels();

        // Clear the list view
        ObservableList<Hotel> recentlyViewedList = FXCollections.observableArrayList();

        // Add all recently viewed hotels to the observable list
        for (Hotel hotel : recentlyViewed) {
            recentlyViewedList.add(hotel);
        }

        // Set the list view items
        recentlyViewedListView.setItems(recentlyViewedList);
    }

    private void loadHotels() {
        hotelsList.clear();
        // Get all hotels from the data manager
        List<Hotel> hotels = dataManager.getAllHotels();
        // Clear current list to avoid duplicates
        // Add all hotels to the observable list
        hotelsList.addAll(hotels);
        // Set the table to display the hotel list
        hotelsTable.setItems(hotelsList);

        // Apply default sorting based on the current combo box selection
        sortHotels(sortComboBox.getValue());
    }

    @FXML
    private void handleSearch() {
        // Get search term and convert to lowercase for case-insensitive search
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadHotels(); // If a search is empty, load all hotels
            return;
        }

        // Get all hotels from the data manager
        List<Hotel> allHotels = dataManager.getAllHotels();
        // Clear the current list before adding filtered results
        hotelsList.clear();

        // First, try exact name search using binary search if sorting by name
        if (sortComboBox.getValue() != null && sortComboBox.getValue().equals("Name (A-Z)")) {
            // Sort the list by name for binary search
            List<Hotel> sortedByName = SearchAndSort.sortHotelsByName(allHotels);

            // Perform binary search for the exact name match
            int index = SearchAndSort.searchHotelByName(sortedByName, searchTerm);

            if (index >= 0) {
                // Found an exact match
                hotelsList.add(sortedByName.get(index));
                System.out.println("Found hotel using binary search: " + sortedByName.get(index).getName());
            } else {
                // No exact match found, fall back contains search
                System.out.println("No exact match found with binary search, falling back to contains search");
                performContainsSearch(allHotels, searchTerm.toLowerCase());
            }
        } else {
            // If not sorted by name, use contains search
            System.out.println("Not sorted by name, using contains search");
            performContainsSearch(allHotels, searchTerm.toLowerCase());
        }

        // Apply current sorting to maintain the consistent display order
        sortHotels(sortComboBox.getValue());
    }

    /**
     * Helper method to perform a contains search on hotel name or location
     *
     * @param hotels the list of hotels to search
     * @param searchTerm the term to search for
     */
    private void performContainsSearch(List<Hotel> hotels, String searchTerm) {
        for (Hotel hotel : hotels) {
            if (hotel.getName().toLowerCase().contains(searchTerm) ||
                hotel.getLocation().toLowerCase().contains(searchTerm)) {
                hotelsList.add(hotel);
            }
        }
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
            // Show the warning if no hotel is selected
            showAlert("No Selection", "Please select a hotel to view its rooms.", Alert.AlertType.WARNING);
            return;
        }

        // Add the selected hotel to the recently viewed list
        dataManager.addToRecentlyViewedHotels(selectedHotel);
        // Update the recently viewed hotels list
        updateRecentlyViewedHotels();

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
        alert.setHeaderText(null);
        // Set the alert message
        alert.setContentText(message);
        alert.showAndWait();
    }
}
