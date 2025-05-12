package App.Controllers;

import Models.Booking;
import Models.DataManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Controller for the manage bookings view.
 * Handles CRUD operations for bookings.
 */
public class ManageBookingsController {

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Booking> bookingsTable;

    @FXML
    private TableColumn<Booking, String> idColumn;

    @FXML
    private TableColumn<Booking, String> userColumn;

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

    @FXML
    private Label userLabel;

    @FXML
    private Label hotelLabel;

    @FXML
    private Label roomLabel;

    @FXML
    private DatePicker checkInDatePicker;

    @FXML
    private DatePicker checkOutDatePicker;

    @FXML
    private CheckBox confirmedCheckBox;

    private MainController mainController;
    private DataManager dataManager;
    private ObservableList<Booking> bookingsList = FXCollections.observableArrayList();
    private Booking selectedBooking;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @FXML
    private void initialize() {
        // Set up the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getUser().getUsername()));
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

        // Set up the filter combo box
        filterComboBox.getItems().addAll("All Bookings", "Pending Bookings", "Confirmed Bookings");
        filterComboBox.getSelectionModel().select("All Bookings");
        filterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterBookings();
            }
        });

        // Set up the table selection listener
        bookingsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedBooking = newVal;
                populateFields(newVal);
            }
        });

        // Set up date pickers
        checkInDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        checkOutDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        this.dataManager = mainController.getDataManager();
        loadBookings();
    }

    private void loadBookings() {
        List<Booking> bookings = dataManager.getAllBookings();
        bookingsList.clear();
        bookingsList.addAll(bookings);
        filterBookings();
    }

    private void filterBookings() {
        String filter = filterComboBox.getValue();
        if (filter == null) return;

        ObservableList<Booking> filteredList = FXCollections.observableArrayList();
        String searchTerm = searchField.getText().trim().toLowerCase();

        for (Booking booking : bookingsList) {
            boolean matchesFilter = true;
            boolean matchesSearch = true;

            // Apply filter
            if (filter.equals("Pending Bookings") && booking.isConfirmed()) {
                matchesFilter = false;
            } else if (filter.equals("Confirmed Bookings") && !booking.isConfirmed()) {
                matchesFilter = false;
            }

            // Apply search
            if (!searchTerm.isEmpty()) {
                boolean userMatch = booking.getUser().getUsername().toLowerCase().contains(searchTerm) ||
                                   booking.getUser().getFullName().toLowerCase().contains(searchTerm);
                boolean hotelMatch = booking.getRoom().getHotel().getName().toLowerCase().contains(searchTerm) ||
                                    booking.getRoom().getHotel().getLocation().toLowerCase().contains(searchTerm);
                boolean roomMatch = booking.getRoom().getRoomNumber().toLowerCase().contains(searchTerm) ||
                                   booking.getRoom().getType().toLowerCase().contains(searchTerm);

                matchesSearch = userMatch || hotelMatch || roomMatch;
            }

            if (matchesFilter && matchesSearch) {
                filteredList.add(booking);
            }
        }

        bookingsTable.setItems(filteredList);
    }

    @FXML
    private void handleSearch() {
        filterBookings();
    }

    @FXML
    private void handleUpdate() {
        if (selectedBooking == null) {
            showAlert("No Selection", "Please select a booking to update.", Alert.AlertType.WARNING);
            return;
        }

        // Validate input
        if (!validateInput()) {
            return;
        }

        // Update booking
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();
        boolean confirmed = confirmedCheckBox.isSelected();

        selectedBooking.setCheckInDate(checkIn);
        selectedBooking.setCheckOutDate(checkOut);
        selectedBooking.setConfirmed(confirmed);

        dataManager.updateBooking(selectedBooking);

        // Refresh the list
        loadBookings();

        showAlert("Booking Updated", "Booking has been updated successfully.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleConfirm() {
        if (selectedBooking == null) {
            showAlert("No Selection", "Please select a booking to confirm.", Alert.AlertType.WARNING);
            return;
        }

        selectedBooking.setConfirmed(true);
        dataManager.updateBooking(selectedBooking);

        // Refresh the list
        loadBookings();

        showAlert("Booking Confirmed", "Booking has been confirmed successfully.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleCancelBooking() {
        if (selectedBooking == null) {
            showAlert("No Selection", "Please select a booking to cancel.", Alert.AlertType.WARNING);
            return;
        }

        // Confirm cancellation
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Cancellation");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to cancel this booking?\n\n" +
                                   "User: " + selectedBooking.getUser().getUsername() + "\n" +
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

                // Refresh the list
                loadBookings();

                clearFields();
                selectedBooking = null;
                showAlert("Booking Cancelled", "Booking has been cancelled successfully.", Alert.AlertType.INFORMATION);
            }
        });
    }

    @FXML
    private void handleClear() {
        clearFields();
        selectedBooking = null;
        bookingsTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleBack() {
        mainController.showWelcomeView();
    }

    private void populateFields(Booking booking) {
        userLabel.setText(booking.getUser().getUsername() + " (" + booking.getUser().getFullName() + ")");
        hotelLabel.setText(booking.getRoom().getHotel().getName() + " (" + booking.getRoom().getHotel().getLocation() + ")");
        roomLabel.setText(booking.getRoom().getRoomNumber() + " (" + booking.getRoom().getType() + ")");
        checkInDatePicker.setValue(booking.getCheckInDate());
        checkOutDatePicker.setValue(booking.getCheckOutDate());
        confirmedCheckBox.setSelected(booking.isConfirmed());
    }

    private void clearFields() {
        userLabel.setText("");
        hotelLabel.setText("");
        roomLabel.setText("");
        checkInDatePicker.setValue(null);
        checkOutDatePicker.setValue(null);
        confirmedCheckBox.setSelected(false);
    }

    private void updateTotalPrice() {
        if (selectedBooking == null) return;

        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        if (checkIn == null || checkOut == null) {
            return;
        }

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            return;
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalPrice = nights * selectedBooking.getRoom().getPricePerNight();
        selectedBooking.setCheckInDate(checkIn);
        selectedBooking.setCheckOutDate(checkOut);
    }

    private boolean validateInput() {
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        if (checkIn == null || checkOut == null) {
            showAlert("Invalid Input", "Check-in and check-out dates are required.", Alert.AlertType.WARNING);
            return false;
        }

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            showAlert("Invalid Dates", "Check-out date must be after check-in date.", Alert.AlertType.WARNING);
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
