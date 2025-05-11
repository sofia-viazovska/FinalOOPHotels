package App.Controllers;

import Models.Booking;
import Models.DataManager;
import Models.Hotel;
import Models.Room;
import Models.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Controller for the rooms view.
 * Handles displaying rooms for a selected hotel and booking rooms.
 */
public class RoomsController {

    @FXML
    private Label hotelNameLabel;

    @FXML
    private Label hotelInfoLabel;

    @FXML
    private TableView<Room> roomsTable;

    @FXML
    private TableColumn<Room, String> roomNumberColumn;

    @FXML
    private TableColumn<Room, String> roomTypeColumn;

    @FXML
    private TableColumn<Room, Double> priceColumn;

    @FXML
    private TableColumn<Room, String> availabilityColumn;

    @FXML
    private VBox bookingPane;

    @FXML
    private DatePicker checkInDatePicker;

    @FXML
    private DatePicker checkOutDatePicker;

    @FXML
    private Label totalPriceLabel;

    private MainController mainController;
    private DataManager dataManager;
    private Hotel selectedHotel;
    private ObservableList<Room> roomsList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Set up the table columns
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        availabilityColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().isAvailable() ? "Yes" : "No"));

        // Set up date pickers
        LocalDate today = LocalDate.now();
        checkInDatePicker.setValue(today);
        checkOutDatePicker.setValue(today.plusDays(1));

        // Add listeners to update total price when dates change
        checkInDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        checkOutDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        this.dataManager = mainController.getDataManager();
    }

    public void setHotel(Hotel hotel) {
        this.selectedHotel = hotel;
        hotelNameLabel.setText(hotel.getName());
        hotelInfoLabel.setText(hotel.getLocation() + " • " + hotel.getRating() + " stars");
        loadRooms();
    }

    private void loadRooms() {
        List<Room> rooms = dataManager.getRoomsByHotel(selectedHotel.getId());
        roomsList.clear();
        roomsList.addAll(rooms);
        roomsTable.setItems(roomsList);
    }

    @FXML
    private void handleShowBookingForm() {
        Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showAlert("No Selection", "Please select a room to book.", Alert.AlertType.WARNING);
            return;
        }

        if (!selectedRoom.isAvailable()) {
            showAlert("Room Not Available", "This room is not available for booking.", Alert.AlertType.WARNING);
            return;
        }

        // Check if user is logged in
        User currentUser = mainController.getCurrentUser();
        if (currentUser == null) {
            showAlert("Login Required", "You must be logged in to book a room.", Alert.AlertType.WARNING);
            mainController.showLoginView();
            return;
        }

        // Show booking pane
        bookingPane.setVisible(true);
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) return;

        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        if (checkIn == null || checkOut == null) {
            totalPriceLabel.setText("Please select dates");
            return;
        }

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            totalPriceLabel.setText("Invalid dates");
            return;
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalPrice = nights * selectedRoom.getPricePerNight();
        totalPriceLabel.setText(String.format("$%.2f", totalPrice));
    }

    @FXML
    private void handleBookRoom() {
        Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        User currentUser = mainController.getCurrentUser();
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        if (selectedRoom == null || currentUser == null || checkIn == null || checkOut == null) {
            return;
        }

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            showAlert("Invalid Dates", "Check-out date must be after check-in date.", Alert.AlertType.WARNING);
            return;
        }

        // Create booking
        Booking booking = dataManager.createBooking(currentUser.getId(), selectedRoom.getId(), checkIn, checkOut);

        if (booking != null) {
            // Mark room as unavailable
            selectedRoom.setAvailable(false);
            dataManager.updateRoom(selectedRoom);

            // Refresh rooms list
            loadRooms();

            // Hide booking pane
            bookingPane.setVisible(false);

            showAlert("Booking Confirmed",
                    "Your booking has been confirmed.\nHotel: " + selectedHotel.getName() +
                    "\nRoom: " + selectedRoom.getRoomNumber() +
                    "\nDates: " + checkIn + " to " + checkOut,
                    Alert.AlertType.INFORMATION);
        } else {
            showAlert("Booking Failed", "There was an error creating your booking.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancelBooking() {
        bookingPane.setVisible(false);
    }

    @FXML
    private void handleBack() {
        mainController.showHotelsView();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
