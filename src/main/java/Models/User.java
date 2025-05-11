package Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user of the application with properties like name, email, etc.
 * Has a one-to-many relationship with Booking.
 */
public class User implements Serializable {
    private String id; // Unique identifier for the user
    private String username; // Username for login
    private String password; // Password for authentication
    private String fullName; // User's full name
    private String email; // User's email address
    private String phoneNumber; // User's phone number
    private boolean isAdmin; // Flag indicating if user has admin privileges
    private List<Booking> bookings; // One-to-many relationship with Booking

    public User(String id, String username, String password, String fullName, String email, String phoneNumber) {
        // Initialize user with basic information
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isAdmin = false; // Default to regular user
        this.bookings = new ArrayList<>(); // Initialize empty bookings list
    }

    public User(String id, String username, String password, String fullName, String email, String phoneNumber, boolean isAdmin) {
        // Initialize user with admin status specified
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isAdmin = isAdmin; // Set admin status based on parameter
        this.bookings = new ArrayList<>(); // Initialize empty bookings list
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void addBooking(Booking booking) {
        // Add booking to user's list of bookings
        bookings.add(booking);
        // Set this user as the owner of the booking (bidirectional relationship)
        booking.setUser(this);
    }

    public void removeBooking(Booking booking) {
        // Remove booking from user's list
        bookings.remove(booking);
        // Remove user reference from booking (bidirectional relationship)
        booking.setUser(null);
    }

    public boolean isAdmin() {
        // Return whether user has admin privileges
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        // Update user's admin status
        isAdmin = admin;
    }

    @Override
    public String toString() {
        // Return a string representation of the user (full name and username)
        return fullName + " (" + username + ")";
    }
}
