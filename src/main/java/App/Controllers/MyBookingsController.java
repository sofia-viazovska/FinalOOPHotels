package App.Controllers;

import Models.Booking;
import Models.DataManager;
import Models.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for the my bookings view.
 * Handles displaying and managing user bookings.
 */
public class MyBookingsController {

    @FXML
    private Text subtitleText;

    @FXML
    private TableView<Booking> bookingsTable;

    @FXML
    private TableColumn<Booking, String> hotelColumn;

    @FXML
    private TableColumn<Booking, String> roomColumn;

    @FXML
    private TableColumn<Booking, String> checkInColumn;

    @FXML
    private TableColumn<Booking, String> checkOutColumn;

    @FXML
    private TableColumn<Booking, String> priceColumn;

    @FXML
    private TableColumn<Booking, String> statusColumn;

    private MainController mainController;
    private DataManager dataManager;
    private ObservableList<Booking> bookingsList = FXCollections.observableArrayList();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @FXML
    private void initialize() {
        // Set up the table columns
        hotelColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getRoom().getHotel().getName()));

        roomColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getRoom().getRoomNumber() + " (" +
                                    cellData.getValue().getRoom().getType() + ")"));

        checkInColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCheckInDate().format(dateFormatter)));

        checkOutColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCheckOutDate().format(dateFormatter)));

        priceColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getTotalPrice())));

        statusColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().isConfirmed() ? "Confirmed" : "Pending"));
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        this.dataManager = mainController.getDataManager();

        User currentUser = mainController.getCurrentUser();
        if (currentUser != null) {
            subtitleText.setText("Bookings for " + currentUser.getFullName());
            loadBookings();
        }
    }

    private void loadBookings() {
        User currentUser = mainController.getCurrentUser();
        if (currentUser == null) return;

        List<Booking> bookings = dataManager.getBookingsByUser(currentUser.getId());
        bookingsList.clear();
        bookingsList.addAll(bookings);
        bookingsTable.setItems(bookingsList);
    }

    @FXML
    private void handleCancelBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert("No Selection", "Please select a booking to cancel.", Alert.AlertType.WARNING);
            return;
        }

        // Confirm cancellation
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Cancellation");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to cancel this booking?\n\n" +
                                   "Hotel: " + selectedBooking.getRoom().getHotel().getName() + "\n" +
                                   "Room: " + selectedBooking.getRoom().getRoomNumber() + "\n" +
                                   "Dates: " + selectedBooking.getCheckInDate().format(dateFormatter) +
                                   " to " + selectedBooking.getCheckOutDate().format(dateFormatter));

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Make room available again
                selectedBooking.getRoom().setAvailable(true);
                dataManager.updateRoom(selectedBooking.getRoom());

                // Delete booking
                dataManager.deleteBooking(selectedBooking.getId());

                // Refresh bookings list
                loadBookings();

                showAlert("Booking Cancelled", "Your booking has been cancelled successfully.", Alert.AlertType.INFORMATION);
            }
        });
    }

    @FXML
    private void handleBack() {
        mainController.showWelcomeView();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
