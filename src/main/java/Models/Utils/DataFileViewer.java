package Models.Utils;

import Models.Booking;
import Models.Hotel;
import Models.Room;
import Models.User;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.util.List;

/**
 * Utility class for viewing the contents of data files (.dat and .txt).
 * Allows users to read and display serialized objects and text files in a human-readable format.
 */
public class DataFileViewer {

    /**
     * Shows a file chooser dialog and then displays the contents of the selected data file (.dat or .txt).
     * @param stage the parent stage for the file chooser dialog
     */
    public static void viewDataFile(Stage stage) {
        // Create file chooser dialog
        FileChooser fileChooser = new FileChooser();
        // Set dialog title
        fileChooser.setTitle("Open Data File");
        // Show both .dat and .txt files
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Data Files", "*.dat"/**, "*.txt"**/),
                new FileChooser.ExtensionFilter("Data Files", "*.dat")
                //new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        // Set initial directory to the project root
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        // Show file chooser dialog and get selected file
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                // Read the data from the file
                String content = readDataFile(selectedFile);
                // Display the data in a dialog
                showDataContent(selectedFile.getName(), content);
            } catch (Exception e) {
                // Show error if file can't be read
                showError("Error Reading File", "Could not read the selected file: " + e.getMessage());
            }
        }
    }

    /**
     * Reads the contents of a data file and returns it as a formatted string.
     * @param file the data file to read (.dat or .txt)
     * @return a string representation of the file contents
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    private static String readDataFile(File file) throws IOException, ClassNotFoundException {
        // Create string builder to store formatted content
        StringBuilder content = new StringBuilder();

        // Check if it's a .txt file
//        if (file.getName().endsWith(".txt")) {
//            // Read text file directly
//            content.append("File: ").append(file.getName()).append("\n\n");
//
//            List<String> lines = Files.readAllLines(file.toPath());
//            for (String line : lines) {
//                content.append(line).append("\n");
//            }
//
//            return content.toString();
//        }

        // Handle .dat files with serialized objects
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            // Read the object from file
            Object obj = ois.readObject();

            // Check if the object is a list
            if (obj instanceof List<?>) {
                List<?> list = (List<?>) obj;
                if (!list.isEmpty()) {
                    // Get first item to determine list type
                    Object firstItem = list.get(0);

                    // Add file info to content
                    content.append("File: ").append(file.getName()).append("\n");
                    content.append("Total items: ").append(list.size()).append("\n\n");

                    // Check what type of data we're dealing with
                    if (firstItem instanceof User) {
                        // Format user data
                        content.append("Users:\n");
                        for (Object item : list) {
                            User user = (User) item;
                            content.append("-----------------------------------\n");
                            content.append("ID: ").append(user.getId()).append("\n");
                            content.append("Username: ").append(user.getUsername()).append("\n");
                            content.append("Full Name: ").append(user.getFullName()).append("\n");
                            content.append("Email: ").append(user.getEmail()).append("\n");
                            content.append("Phone: ").append(user.getPhoneNumber()).append("\n");
                            content.append("Bookings: ").append(user.getBookings().size()).append("\n");
                        }
                    } else if (firstItem instanceof Hotel) {
                        // Format hotel data
                        content.append("Hotels:\n");
                        for (Object item : list) {
                            Hotel hotel = (Hotel) item;
                            content.append("-----------------------------------\n");
                            content.append("ID: ").append(hotel.getId()).append("\n");
                            content.append("Name: ").append(hotel.getName()).append("\n");
                            content.append("Location: ").append(hotel.getLocation()).append("\n");
                            content.append("Rating: ").append(hotel.getRating()).append(" stars\n");
                            content.append("Description: ").append(hotel.getDescription()).append("\n");
                            content.append("Rooms: ").append(hotel.getRooms().size()).append("\n");
                        }
                    } else if (firstItem instanceof Room) {
                        // Format room data
                        content.append("Rooms:\n");
                        for (Object item : list) {
                            Room room = (Room) item;
                            content.append("-----------------------------------\n");
                            content.append("ID: ").append(room.getId()).append("\n");
                            content.append("Room Number: ").append(room.getRoomNumber()).append("\n");
                            content.append("Type: ").append(room.getType()).append("\n");
                            content.append("Price: $").append(room.getPricePerNight()).append(" per night\n");
                            content.append("Available: ").append(room.isAvailable() ? "Yes" : "No").append("\n");
                            if (room.getHotel() != null) {
                                // Show hotel name if available
                                content.append("Hotel: ").append(room.getHotel().getName()).append("\n");
                            }
                            content.append("Bookings: ").append(room.getBookings().size()).append("\n");
                        }
                    } else if (firstItem instanceof Booking) {
                        // Format booking data
                        content.append("Bookings:\n");
                        for (Object item : list) {
                            Booking booking = (Booking) item;
                            content.append("-----------------------------------\n");
                            content.append("ID: ").append(booking.getId()).append("\n");
                            if (booking.getUser() != null) {
                                // Show user info if available
                                content.append("User: ").append(booking.getUser().getUsername()).append("\n");
                            }
                            if (booking.getRoom() != null) {
                                // Show room info if available
                                content.append("Room: ").append(booking.getRoom().getRoomNumber());
                                if (booking.getRoom().getHotel() != null) {
                                    // Include hotel name if available
                                    content.append(" at ").append(booking.getRoom().getHotel().getName());
                                }
                                content.append("\n");
                            }
                            content.append("Check-in: ").append(booking.getCheckInDate()).append("\n");
                            content.append("Check-out: ").append(booking.getCheckOutDate()).append("\n");
                            content.append("Total Price: $").append(booking.getTotalPrice()).append("\n");
                            content.append("Status: ").append(booking.isConfirmed() ? "Confirmed" : "Pending").append("\n");
                        }
                    } else {
                        // Handle unknown data types
                        content.append("Unknown data type: ").append(firstItem.getClass().getName()).append("\n");
                    }
                } else {
                    content.append("The file contains an empty list.");
                }
            } else {
                content.append("The file does not contain a list of objects. Found: ").append(obj.getClass().getName());
            }
        }

        return content.toString();
    }

    /**
     * Displays the content of a data file in a dialog.
     * @param fileName the name of the file
     * @param content the content to display
     */
    private static void showDataContent(String fileName, String content) {
        // Create info dialog to show file contents
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Data File Viewer");
        alert.setHeaderText("Contents of " + fileName);

        // Create text area to display content
        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(600);
        textArea.setPrefHeight(400);

        // Make text area resize with dialog
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        // Create grid pane to hold text area
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        // Add text area to dialog and show it
        alert.getDialogPane().setExpandableContent(expContent);
        alert.getDialogPane().setExpanded(true);

        alert.showAndWait();
    }

    /**
     * Shows an error dialog.
     * @param title the title of the dialog
     * @param message the error message
     */
    private static void showError(String title, String message) {
        // Create error dialog
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        // Show dialog and wait for user response
        alert.showAndWait();
    }
}
