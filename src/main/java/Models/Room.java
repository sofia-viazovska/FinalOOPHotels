package Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a room in a hotel with properties like room number, type, price, etc.
 * Has a many-to-one relationship with Hotel and a one-to-many relationship with Booking.
 */
public class Room implements Serializable {
    private String id; // Unique identifier for the room
    private String roomNumber; // Room number (e.g., "101", "202")
    private String type; // Type of room (e.g., "Single", "Double", "Suite")
    private double pricePerNight; // Price per night in dollars
    private boolean available; // Whether the room is available for booking
    private Hotel hotel; // The hotel this room belongs to (many-to-one relationship)
    private List<Booking> bookings; // List of bookings for this room (one-to-many relationship)

    public Room(String id, String roomNumber, String type, double pricePerNight) {
        // Initialize room with all required properties
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.available = true; // Room is available by default
        this.bookings = new ArrayList<>(); // Initialize empty bookings list
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void addBooking(Booking booking) {
        // Add booking to room's list of bookings
        bookings.add(booking);
        // Set this room as the room for the booking (bidirectional relationship)
        booking.setRoom(this);
    }

    public void removeBooking(Booking booking) {
        // Remove booking from room's list
        bookings.remove(booking);
        // Remove room reference from booking (bidirectional relationship)
        booking.setRoom(null);
    }

    @Override
    public String toString() {
        // Return a string representation of the room (room number, type, and price)
        return "Room " + roomNumber + " (" + type + ") - $" + pricePerNight + " per night";
    }
}
