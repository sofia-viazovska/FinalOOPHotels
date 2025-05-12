package Models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a booking made by a user for a room in a hotel.
 * Has many-to-one relationships with both User and Room.
 */
public class Booking implements Serializable {
    private String id; // Unique identifier for the booking
    private User user; // User who made the booking (many-to-one relationship)
    private Room room; // Room that was booked (many-to-one relationship)
    private LocalDate checkInDate; // Date when the guest will check in
    private LocalDate checkOutDate; // Date when the guest will check out
    private double totalPrice; // Total price for the entire stay
    private boolean confirmed; // Whether the booking has been confirmed

    public Booking(String id, User user, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        // Initialize booking with all required properties
        this.id = id;
        this.user = user;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        // Calculate the total price based on room price and duration
        this.calculateTotalPrice();
        // New bookings are not confirmed by default
        this.confirmed = false;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
        calculateTotalPrice();
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
        calculateTotalPrice();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    // Calculate the total price based on the room price and the number of nights
    private void calculateTotalPrice() {
        // Only calculate if all required data is available
        if (checkInDate != null && checkOutDate != null && room != null) {
            // Calculate the number of nights between check-in and check-out dates
            long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            // Multiply the number of nights by the room's price per night
            totalPrice = nights * room.getPricePerNight();
        }
        // If any required data is missing, the total price remains 0
    }

    @Override
    public String toString() {
        // Return a detailed string representation of the booking
        // Including username, hotel name, room number, and dates
        return "Booking for " + user.getFullName() + " at " + room.getHotel().getName() +
               ", Room " + room.getRoomNumber() + " from " + checkInDate + " to " + checkOutDate;
    }
}
