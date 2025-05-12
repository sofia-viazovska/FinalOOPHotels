package Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a hotel with properties like name, location, rating, etc.
 * Has a one-to-many relationship with Room.
 */
public class Hotel implements Serializable, Comparable<Hotel> {
    private static final long serialVersionUID = -1672556827133170865L;

    private String id; // Unique identifier for the hotel
    private String name; // Name of the hotel
    private String location; // Location/city of the hotel
    private int rating; // Rating of the hotel (1-5 stars)
    private String description; // Description of the hotel
    private List<Room> rooms; // List of rooms in the hotel (one-to-many relationship)

    public Hotel(String id, String name, String location, int rating, String description) {
        // Initialize the hotel with all required properties
        this.id = id;
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.description = description;
        this.rooms = new ArrayList<>(); // Initialize the empty rooms list
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public void addRoom(Room room) {
        // Add room to the hotel's list of rooms
        rooms.add(room);
        // Set this hotel as the owner of the room (bidirectional relationship)
        room.setHotel(this);
    }

    public void removeRoom(Room room) {
        // Remove room from the hotel's list
        rooms.remove(room);
        // Remove hotel reference from the room (bidirectional relationship)
        room.setHotel(null);
    }

    @Override
    public int compareTo(Hotel other) {
        // Compare hotels by rating (for sorting)
        // Using Integer.compare for proper handling of edge cases
        // Note: other.rating comes first for descending order (higher ratings first)
        return Integer.compare(other.rating, this.rating); // Descending order
    }

    @Override
    public String toString() {
        // Return a string representation of the hotel (name, rating, and location)
        return name + " (" + rating + " stars) - " + location;
    }
}
