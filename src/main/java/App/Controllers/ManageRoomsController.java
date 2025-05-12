package App.Controllers;

import Models.DataManager;
import Models.Hotel;
import Models.Room;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * Controller for the manage rooms view.
 * Handles CRUD operations for rooms.
 */
public class ManageRoomsController {

    @FXML
    private ComboBox<Hotel> hotelComboBox;

    @FXML
    private TableView<Room> roomsTable;

    @FXML
    private TableColumn<Room, String> idColumn;

    @FXML
    private TableColumn<Room, String> roomNumberColumn;

    @FXML
    private TableColumn<Room, String> typeColumn;

    @FXML
    private TableColumn<Room, Double> priceColumn;

    @FXML
    private TableColumn<Room, String> availableColumn;

    @FXML
    private TextField roomNumberField;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField priceField;

    @FXML
    private CheckBox availableCheckBox;

    private MainController mainController;
    private DataManager dataManager;
    private ObservableList<Room> roomsList = FXCollections.observableArrayList();
    private ObservableList<Hotel> hotelsList = FXCollections.observableArrayList();
    private Room selectedRoom;

    @FXML
    private void initialize() {
        // Set up the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        availableColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().isAvailable() ? "Yes" : "No"));

        // Set up the type combo box
        typeComboBox.getItems().addAll("Single", "Double", "Suite", "Family", "Deluxe", "Presidential Suite");

        // Set up the table selection listener
        roomsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedRoom = newVal;
                populateFields(newVal);
            }
        });

        // Set up the hotel combo box listener
        hotelComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadRoomsByHotel(newVal.getId());
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
        hotelComboBox.setItems(hotelsList);
    }

    private void loadRoomsByHotel(String hotelId) {
        List<Room> rooms = dataManager.getRoomsByHotel(hotelId);
        roomsList.clear();
        roomsList.addAll(rooms);
        roomsTable.setItems(roomsList);
    }

    @FXML
    private void handleRefresh() {
        loadHotels();
        roomsList.clear();
        clearFields();
    }

    @FXML
    private void handleAdd() {
        Hotel selectedHotel = hotelComboBox.getValue();
        if (selectedHotel == null) {
            showAlert("No Hotel Selected", "Please select a hotel to add a room to.", Alert.AlertType.WARNING);
            return;
        }

        // Validate input
        if (!validateInput()) {
            return;
        }

        // Create a new room
        String roomNumber = roomNumberField.getText().trim();
        String type = typeComboBox.getValue();
        double price = Double.parseDouble(priceField.getText().trim());

        Room room = dataManager.createRoom(selectedHotel.getId(), roomNumber, type, price);
        room.setAvailable(availableCheckBox.isSelected());
        dataManager.updateRoom(room);

        // Refresh the list
        loadRoomsByHotel(selectedHotel.getId());

        // Clear the fields
        clearFields();

        showAlert("Room Added", "Room has been added successfully.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleUpdate() {
        if (selectedRoom == null) {
            showAlert("No Selection", "Please select a room to update.", Alert.AlertType.WARNING);
            return;
        }

        // Validate input
        if (!validateInput()) {
            return;
        }

        // Update room
        selectedRoom.setRoomNumber(roomNumberField.getText().trim());
        selectedRoom.setType(typeComboBox.getValue());
        selectedRoom.setPricePerNight(Double.parseDouble(priceField.getText().trim()));
        selectedRoom.setAvailable(availableCheckBox.isSelected());

        dataManager.updateRoom(selectedRoom);

        // Refresh the list
        Hotel selectedHotel = hotelComboBox.getValue();
        if (selectedHotel != null) {
            loadRoomsByHotel(selectedHotel.getId());
        }

        showAlert("Room Updated", "Room has been updated successfully.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleDelete() {
        if (selectedRoom == null) {
            showAlert("No Selection", "Please select a room to delete.", Alert.AlertType.WARNING);
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete the room: " + selectedRoom.getRoomNumber() + "?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dataManager.deleteRoom(selectedRoom.getId());

                // Refresh the list
                Hotel selectedHotel = hotelComboBox.getValue();
                if (selectedHotel != null) {
                    loadRoomsByHotel(selectedHotel.getId());
                }

                clearFields();
                selectedRoom = null;
                showAlert("Room Deleted", "Room has been deleted successfully.", Alert.AlertType.INFORMATION);
            }
        });
    }

    @FXML
    private void handleClear() {
        clearFields();
        selectedRoom = null;
        roomsTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleBack() {
        mainController.showManageHotelsView();
    }

    private void populateFields(Room room) {
        roomNumberField.setText(room.getRoomNumber());
        typeComboBox.setValue(room.getType());
        priceField.setText(String.valueOf(room.getPricePerNight()));
        availableCheckBox.setSelected(room.isAvailable());
    }

    private void clearFields() {
        roomNumberField.clear();
        typeComboBox.getSelectionModel().clearSelection();
        priceField.clear();
        availableCheckBox.setSelected(true);
    }

    private boolean validateInput() {
        String roomNumber = roomNumberField.getText().trim();
        String type = typeComboBox.getValue();
        String priceText = priceField.getText().trim();

        if (roomNumber.isEmpty() || type == null || priceText.isEmpty()) {
            showAlert("Invalid Input", "Room number, type, and price are required.", Alert.AlertType.WARNING);
            return false;
        }

        try {
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                showAlert("Invalid Price", "Price must be greater than zero.", Alert.AlertType.WARNING);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Price", "Price must be a valid number.", Alert.AlertType.WARNING);
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
