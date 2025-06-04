package Models;

import Models.DataStructures.LinkedList;
import Models.Utils.Logging.Log;
import Models.Utils.Logging.LogLevel;
import Models.Utils.Memoizer;
import java.io.*;
import java.nio.file.Files;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Handles data persistence for the application.
 * Reads and writes data to files.
 */
public class DataManager {
    private static final String USERS_FILE = "users.dat";
    private static final String HOTELS_FILE = "hotels.dat";
    private static final String ROOMS_FILE = "rooms.dat";
    private static final String BOOKINGS_FOLDER = "bookings";
    private static final String BOOKINGS_PREFIX = "resert";

    private List<User> users;
    private List<Hotel> hotels;
    private List<Room> rooms;
    private List<Booking> bookings;
    private LinkedList<Hotel> recentlyViewedHotels;

    // Cache for hotel lookups by ID
    private final Map<String, Hotel> hotelCache = new ConcurrentHashMap<>();

    public DataManager() {
        this.users = new ArrayList<>();
        this.hotels = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.bookings = new ArrayList<>();
        this.recentlyViewedHotels = new LinkedList<>();
        loadData();

        // Create default users if no users exist
        if (users.size() == 0) {
            createUser("admin", "admin123", "Administrator", "admin@hotel.com", "123-456-7890", true);
            System.out.println("Created default admin user: username=admin, password=admin123");

            // Create a test user with the username "testtt"
            createUser("testUser", "password", "Test User", "test@hotel.com", "987-654-3210");
            System.out.println("Created test user: username=testtt, password=password");
        }

        // Only create sample hotels and rooms if none exist
        if (hotels.isEmpty()) {
            // Create sample hotels - US cities
            Hotel grandHotel = createHotel("Grand Hotel", "New York", 5, "A luxury hotel in the heart of Manhattan");
            Hotel seasideResort = createHotel("Seaside Resort", "Miami", 4, "Beautiful beachfront resort with ocean views");
            Hotel mountainLodge = createHotel("Mountain Lodge", "Aspen", 4, "Cozy lodge with stunning mountain views");
            Hotel cityInn = createHotel("City Inn", "Chicago", 3, "Affordable hotel in downtown Chicago");
            Hotel sunsetHotel = createHotel("Sunset Hotel", "Los Angeles", 5, "Elegant hotel with sunset views over the Pacific");

            // Create hotels in Ukrainian cities
            Hotel kyivPalace = createHotel("Kyiv Palace", "Kyiv", 5, "Luxury hotel in the heart of Ukraine's capital");
            Hotel lvivCastle = createHotel("Lviv Castle Hotel", "Lviv", 4, "Historic hotel in the cultural capital of Ukraine");
            Hotel odesaPearl = createHotel("Odesa Pearl", "Odesa", 5, "Beachfront luxury hotel on the Black Sea");
            Hotel kharkivPlaza = createHotel("Kharkiv Plaza", "Kharkiv", 4, "Modern hotel in Ukraine's second-largest city");
            Hotel dnipro = createHotel("Dnipro Hotel", "Dnipro", 3, "Comfortable hotel overlooking the Dnipro River");
            Hotel carpathianLodge = createHotel("Carpathian Lodge", "Bukovel", 4, "Mountain resort in the Ukrainian Carpathians");
            Hotel zaporozhyeInn = createHotel("Zaporozhye Inn", "Zaporizhzhia", 3, "Cozy hotel near the Dnieper River");
            Hotel chernivtsiPalace = createHotel("Chernivtsi Palace", "Chernivtsi", 4, "Elegant hotel in a UNESCO World Heritage city");
            Hotel uzhgorodCastle = createHotel("Uzhgorod Castle View", "Uzhhorod", 4, "Hotel with views of the historic Uzhhorod Castle");
            Hotel ivanoResort = createHotel("Ivano Resort", "Ivano-Frankivsk", 4, "Resort hotel near the Carpathian Mountains");
            Hotel lutskFortress = createHotel("Lutsk Fortress Hotel", "Lutsk", 3, "Hotel near the historic Lubart's Castle");
            Hotel rivnePlaza = createHotel("Rivne Plaza", "Rivne", 3, "Modern hotel in the heart of Rivne");
            Hotel ternopilLake = createHotel("Ternopil Lake Hotel", "Ternopil", 4, "Hotel with beautiful views of Ternopil Lake");
            Hotel khmelnytskyiCentral = createHotel("Khmelnytskyi Central", "Khmelnytskyi", 3, "Central hotel in the heart of Khmelnytskyi");
            Hotel vinnytsiaSpa = createHotel("Vinnytsia Spa Resort", "Vinnytsia", 4, "Spa resort with modern amenities");
            Hotel zhytomyrInn = createHotel("Zhytomyr Inn", "Zhytomyr", 3, "Comfortable inn in historic Zhytomyr");
            Hotel cherkasyRiver = createHotel("Cherkasy River Hotel", "Cherkasy", 3, "Hotel with views of the Dnieper River");
            Hotel poltavaHistoric = createHotel("Poltava Historic Hotel", "Poltava", 4, "Hotel in the historic center of Poltava");
            Hotel sumyPark = createHotel("Sumy Park Hotel", "Sumy", 3, "Hotel surrounded by beautiful parks");
            Hotel chernihivAncient = createHotel("Chernihiv Ancient Hotel", "Chernihiv", 4, "Hotel near ancient churches and monasteries");

            // Create more international hotels
            Hotel parisLuxe = createHotel("Paris Luxe", "Paris", 5, "Luxury hotel with views of the Eiffel Tower");
            Hotel londonRoyal = createHotel("London Royal", "London", 5, "Royal experience in the heart of London");
            Hotel romaPalazzo = createHotel("Roma Palazzo", "Rome", 4, "Historic palazzo converted into a luxury hotel");
            Hotel barcelonaSea = createHotel("Barcelona Sea View", "Barcelona", 4, "Hotel with stunning Mediterranean views");
            Hotel berlinModern = createHotel("Berlin Modern", "Berlin", 4, "Contemporary hotel in Germany's capital");
            Hotel viennaClassic = createHotel("Vienna Classic", "Vienna", 5, "Classic luxury in the city of music");
            Hotel amsterdamCanal = createHotel("Amsterdam Canal House", "Amsterdam", 4, "Charming hotel on Amsterdam's famous canals");
            Hotel pragueOld = createHotel("Prague Old Town", "Prague", 4, "Hotel in the heart of Prague's Old Town");
            Hotel budapestRiver = createHotel("Budapest River Hotel", "Budapest", 4, "Hotel with views of the Danube River");
            Hotel warsawRoyal = createHotel("Warsaw Royal Palace", "Warsaw", 4, "Elegant hotel near Warsaw's Royal Castle");
            Hotel stockholmNordic = createHotel("Stockholm Nordic", "Stockholm", 4, "Nordic design hotel in Sweden's capital");
            Hotel osloFjord = createHotel("Oslo Fjord View", "Oslo", 4, "Hotel with views of the Oslo Fjord");
            Hotel copenhagenHarbor = createHotel("Copenhagen Harbor", "Copenhagen", 4, "Modern hotel at Copenhagen's harbor");
            Hotel helsinkiBay = createHotel("Helsinki Bay Hotel", "Helsinki", 4, "Hotel overlooking Helsinki Bay");
            Hotel dublinCastle = createHotel("Dublin Castle Hotel", "Dublin", 4, "Historic hotel near Dublin Castle");
            Hotel edinburghRoyal = createHotel("Edinburgh Royal Mile", "Edinburgh", 4, "Hotel on Edinburgh's famous Royal Mile");
            Hotel lisbonOcean = createHotel("Lisbon Ocean View", "Lisbon", 4, "Hotel with Atlantic Ocean views");
            Hotel madridPlaza = createHotel("Madrid Plaza Hotel", "Madrid", 4, "Elegant hotel on Madrid's grand plaza");
            Hotel athensAcropolis = createHotel("Athens Acropolis View", "Athens", 4, "Hotel with views of the Acropolis");
            Hotel istanbulBosphorus = createHotel("Istanbul Bosphorus", "Istanbul", 5, "Luxury hotel on the Bosphorus Strait");

            System.out.println("Created 40 sample hotels");

            // Create sample rooms for a Grand Hotel
            createRoom(grandHotel.getId(), "101", "Single", 150.0);
            createRoom(grandHotel.getId(), "102", "Double", 200.0);
            createRoom(grandHotel.getId(), "201", "Suite", 350.0);

            // Create sample rooms for Seaside Resort
            createRoom(seasideResort.getId(), "101", "Double", 180.0);
            createRoom(seasideResort.getId(), "102", "Double", 180.0);
            createRoom(seasideResort.getId(), "201", "Suite", 300.0);

            // Create sample rooms for Mountain Lodge
            createRoom(mountainLodge.getId(), "101", "Single", 120.0);
            createRoom(mountainLodge.getId(), "102", "Double", 160.0);
            createRoom(mountainLodge.getId(), "201", "Family", 220.0);

            // Create sample rooms for City Inn
            createRoom(cityInn.getId(), "101", "Single", 90.0);
            createRoom(cityInn.getId(), "102", "Double", 120.0);
            createRoom(cityInn.getId(), "201", "Double", 120.0);

            // Create sample rooms for Sunset Hotel
            createRoom(sunsetHotel.getId(), "101", "Double", 250.0);
            createRoom(sunsetHotel.getId(), "102", "Double", 250.0);
            createRoom(sunsetHotel.getId(), "201", "Suite", 400.0);
            createRoom(sunsetHotel.getId(), "202", "Presidential Suite", 800.0);

            // Create rooms for Ukrainian hotels
            // Kyiv Palace
            createRoom(kyivPalace.getId(), "101", "Deluxe", 200.0);
            createRoom(kyivPalace.getId(), "102", "Executive", 250.0);
            createRoom(kyivPalace.getId(), "201", "Presidential Suite", 500.0);

            // Lviv Castle Hotel
            createRoom(lvivCastle.getId(), "101", "Standard", 120.0);
            createRoom(lvivCastle.getId(), "102", "Deluxe", 180.0);
            createRoom(lvivCastle.getId(), "201", "Royal Suite", 350.0);

            // Odesa Pearl
            createRoom(odesaPearl.getId(), "101", "Sea View", 220.0);
            createRoom(odesaPearl.getId(), "102", "Deluxe Sea View", 280.0);
            createRoom(odesaPearl.getId(), "201", "Pearl Suite", 450.0);

            // Kharkiv Plaza
            createRoom(kharkivPlaza.getId(), "101", "Standard", 100.0);
            createRoom(kharkivPlaza.getId(), "102", "Business", 150.0);
            createRoom(kharkivPlaza.getId(), "201", "Executive Suite", 300.0);

            // Dnipro Hotel
            createRoom(dnipro.getId(), "101", "Standard", 80.0);
            createRoom(dnipro.getId(), "102", "River View", 120.0);
            createRoom(dnipro.getId(), "201", "Family Room", 180.0);

            // Carpathian Lodge
            createRoom(carpathianLodge.getId(), "101", "Standard", 90.0);
            createRoom(carpathianLodge.getId(), "102", "Deluxe", 140.0);
            createRoom(carpathianLodge.getId(), "201", "Family Suite", 250.0);
            // Zaporozhye Inn
            createRoom(zaporozhyeInn.getId(), "101", "Standard", 70.0);
            createRoom(zaporozhyeInn.getId(), "102", "Deluxe", 120.0);
            createRoom(zaporozhyeInn.getId(), "201", "Family Suite", 200.0);
            // Chernivtsi Palace
            createRoom(chernivtsiPalace.getId(), "101", "Standard", 110.0);
            createRoom(chernivtsiPalace.getId(), "102", "Deluxe", 160.0);
            createRoom(chernivtsiPalace.getId(), "201", "Royal Suite", 300.0);

            // Uzhgorod Castle View
            createRoom(uzhgorodCastle.getId(), "101", "Standard", 100.0);
            createRoom(uzhgorodCastle.getId(), "102", "Deluxe", 150.0);
            createRoom(uzhgorodCastle.getId(), "201", "Castle Suite", 250.0);
            // Ivano Resort
            createRoom(ivanoResort.getId(), "101", "Standard", 90.0);
            createRoom(ivanoResort.getId(), "102", "Deluxe", 140.0);
            createRoom(ivanoResort.getId(), "201", "Family Suite", 250.0);
            // Lutsk Fortress Hotel
            createRoom(lutskFortress.getId(), "101", "Standard", 80.0);
            createRoom(lutskFortress.getId(), "102", "Deluxe", 130.0);
            createRoom(lutskFortress.getId(), "201", "Family Suite", 220.0);
            // Rivne Plaza
            createRoom(rivnePlaza.getId(), "101", "Standard", 70.0);
            createRoom(rivnePlaza.getId(), "102", "Deluxe", 120.0);
            createRoom(rivnePlaza.getId(), "201", "Family Suite", 200.0);
            // Ternopil Lake Hotel
            createRoom(ternopilLake.getId(), "101", "Standard", 90.0);
            createRoom(ternopilLake.getId(), "102", "Deluxe", 140.0);
            createRoom(ternopilLake.getId(), "201", "Family Suite", 250.0);
            // Khmelnytskyi Central
            createRoom(khmelnytskyiCentral.getId(), "101", "Standard", 80.0);
            createRoom(khmelnytskyiCentral.getId(), "102", "Deluxe", 130.0);
            createRoom(khmelnytskyiCentral.getId(), "201", "Family Suite", 220.0);
            // Vinnytsia Spa Resort
            createRoom(vinnytsiaSpa.getId(), "101", "Standard", 70.0);
            createRoom(vinnytsiaSpa.getId(), "102", "Deluxe", 120.0);
            createRoom(vinnytsiaSpa.getId(), "201", "Family Suite", 200.0);
            // Zhytomyr Inn
            createRoom(zhytomyrInn.getId(), "101", "Standard", 80.0);
            createRoom(zhytomyrInn.getId(), "102", "Deluxe", 130.0);
            createRoom(zhytomyrInn.getId(), "201", "Family Suite", 220.0);
            // Cherkasy River Hotel
            createRoom(cherkasyRiver.getId(), "101", "Standard", 90.0);
            createRoom(cherkasyRiver.getId(), "102", "Deluxe", 140.0);
            createRoom(cherkasyRiver.getId(), "201", "Family Suite", 250.0);
            // Poltava Historic Hotel
            createRoom(poltavaHistoric.getId(), "101", "Standard", 100.0);
            createRoom(poltavaHistoric.getId(), "102", "Deluxe", 150.0);
            createRoom(poltavaHistoric.getId(), "201", "Family Suite", 250.0);
            // Sumy Park Hotel
            createRoom(sumyPark.getId(), "101", "Standard", 80.0);
            createRoom(sumyPark.getId(), "102", "Deluxe", 130.0);
            createRoom(sumyPark.getId(), "201", "Family Suite", 220.0);
            // Chernihiv Ancient Hotel
            createRoom(chernihivAncient.getId(), "101", "Standard", 90.0);
            createRoom(chernihivAncient.getId(), "102", "Deluxe", 140.0);
            createRoom(chernihivAncient.getId(), "201", "Family Suite", 250.0);
            // Create sample rooms for other hotels
            // Create sample rooms for Paris Luxe
            createRoom(parisLuxe.getId(), "101", "Single", 300.0);
            createRoom(parisLuxe.getId(), "102", "Double", 400.0);
            createRoom(parisLuxe.getId(), "201", "Suite", 600.0);
            // Create sample rooms for London Royal
            createRoom(londonRoyal.getId(), "101", "Single", 350.0);
            createRoom(londonRoyal.getId(), "102", "Double", 450.0);
            createRoom(londonRoyal.getId(), "201", "Suite", 700.0);
            // Create sample rooms for Roma Palazzo
            createRoom(romaPalazzo.getId(), "101", "Single", 250.0);
            createRoom(romaPalazzo.getId(), "102", "Double", 350.0);
            createRoom(romaPalazzo.getId(), "201", "Suite", 550.0);
            // Create sample rooms for the Barcelona Sea
            createRoom(barcelonaSea.getId(), "101", "Single", 280.0);
            createRoom(barcelonaSea.getId(), "102", "Double", 380.0);
            createRoom(barcelonaSea.getId(), "201", "Suite", 580.0);
            // Create sample rooms for Berlin Modern
            createRoom(berlinModern.getId(), "101", "Single", 220.0);
            createRoom(berlinModern.getId(), "102", "Double", 320.0);
            createRoom(berlinModern.getId(), "201", "Suite", 520.0);
            // Create sample rooms for Vienna Classic
            createRoom(viennaClassic.getId(), "101", "Single", 300.0);
            createRoom(viennaClassic.getId(), "102", "Double", 400.0);
            createRoom(viennaClassic.getId(), "201", "Suite", 600.0);
            // Create sample rooms for the Amsterdam Canal
            createRoom(amsterdamCanal.getId(), "101", "Single", 270.0);
            createRoom(amsterdamCanal.getId(), "102", "Double", 370.0);
            createRoom(amsterdamCanal.getId(), "201", "Suite", 570.0);
            // Create sample rooms for Prague Old Town
            createRoom(pragueOld.getId(), "101", "Single", 260.0);
            createRoom(pragueOld.getId(), "102", "Double", 360.0);
            createRoom(pragueOld.getId(), "201", "Suite", 560.0);
            // Create sample rooms for the Budapest River
            createRoom(budapestRiver.getId(), "101", "Single", 290.0);
            createRoom(budapestRiver.getId(), "102", "Double", 390.0);
            createRoom(budapestRiver.getId(), "201", "Suite", 590.0);
            // Create sample rooms for Warsaw Royal
            createRoom(warsawRoyal.getId(), "101", "Single", 240.0);
            createRoom(warsawRoyal.getId(), "102", "Double", 340.0);
            createRoom(warsawRoyal.getId(), "201", "Suite", 540.0);
            // Create sample rooms for Stockholm Nordic
            createRoom(stockholmNordic.getId(), "101", "Single", 310.0);
            createRoom(stockholmNordic.getId(), "102", "Double", 410.0);
            createRoom(stockholmNordic.getId(), "201", "Suite", 610.0);
            // Create sample rooms for Oslo Fjord
            createRoom(osloFjord.getId(), "101", "Single", 330.0);
            createRoom(osloFjord.getId(), "102", "Double", 430.0);
            createRoom(osloFjord.getId(), "201", "Suite", 630.0);
            // Create sample rooms for Copenhagen Harbor
            createRoom(copenhagenHarbor.getId(), "101", "Single", 340.0);
            createRoom(copenhagenHarbor.getId(), "102", "Double", 440.0);
            createRoom(copenhagenHarbor.getId(), "201", "Suite", 640.0);
            // Create sample rooms for Helsinki Bay
            createRoom(helsinkiBay.getId(), "101", "Single", 320.0);
            createRoom(helsinkiBay.getId(), "102", "Double", 420.0);
            createRoom(helsinkiBay.getId(), "201", "Suite", 620.0);
            // Create sample rooms for Dublin Castle
            createRoom(dublinCastle.getId(), "101", "Single", 280.0);
            createRoom(dublinCastle.getId(), "102", "Double", 380.0);
            createRoom(dublinCastle.getId(), "201", "Suite", 580.0);


            // Add basic rooms for all other hotels
            for (Hotel hotel : hotels) {
                if (hotel.getRooms().isEmpty()) {
                    createRoom(hotel.getId(), "101", "Standard", 100.0);
                    createRoom(hotel.getId(), "102", "Deluxe", 150.0);
                    createRoom(hotel.getId(), "201", "Suite", 250.0);
                }
            }
            System.out.println("Created sample rooms for all hotels");
        }
    }

    // CRUD operations for User
    public User createUser(String username, String password, String fullName, String email, String phoneNumber) {
        return createUser(username, password, fullName, email, phoneNumber, false);
    }

    public User createUser(String username, String password, String fullName, String email, String phoneNumber, boolean isAdmin) {
        String id = UUID.randomUUID().toString();
        User user = new User(id, username, password, fullName, email, phoneNumber, isAdmin);
        users.add(user);
        saveUsers();
        return user;
    }

    public User getUserById(String id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                saveUsers();
                return;
            }
        }
    }

    public void deleteUser(String id) {
        users.removeIf(user -> user.getId().equals(id));
        saveUsers();
    }

    // CRUD operations for Hotel
    @Log(level = LogLevel.INFO)
    public Hotel createHotel(String name, String location, int rating, String description) {
        try {
            return Log.Decorator.withLogging(
                LogLevel.INFO,
                () -> {
                    // Check if a hotel with the same name already exists
                    Hotel existingHotel = getHotelByName(name);
                    if (existingHotel != null) {
                        return existingHotel;
                    }

                    // Create a new hotel if one with the same name doesn't exist
                    String id = UUID.randomUUID().toString();
                    Hotel hotel = new Hotel(id, name, location, rating, description);
                    hotels.add(hotel);
                    // Add to cache when created
                    hotelCache.put(id, hotel);
                    saveHotels();
                    return hotel;
                },
                "createHotel",
                "DataManager",
                Map.of("name", name, "location", location, "rating", rating, "description", description)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create hotel", e);
        }
    }

    public Hotel getHotelByName(String name) {
        for (Hotel hotel : hotels) {
            if (hotel.getName().equals(name)) {
                return hotel;
            }
        }
        return null;
    }

    /**
     * Gets a hotel by its ID with memoization.
     * This method caches results to improve performance for repeated lookups.
     *
     * @param id the ID of the hotel to find
     * @return the hotel with the given ID, or null if not found
     */
    public Hotel getHotelById(String id) {
        // Check the cache first
        if (hotelCache.containsKey(id)) {
            return hotelCache.get(id);
        }

        // If not in cache, search in the hotels list
        for (Hotel hotel : hotels) {
            if (hotel.getId().equals(id)) {
                // Store in cache for future lookups
                hotelCache.put(id, hotel);
                return hotel;
            }
        }

        // Not found
        return null;
    }

    public List<Hotel> getAllHotels() {
        return new ArrayList<>(hotels);
    }

    public void updateHotel(Hotel hotel) {
        for (int i = 0; i < hotels.size(); i++) {
            if (hotels.get(i).getId().equals(hotel.getId())) {
                hotels.set(i, hotel);
                // Update the cache with the new hotel
                hotelCache.put(hotel.getId(), hotel);
                saveHotels();
                return;
            }
        }
    }

    public void deleteHotel(String id) {
        hotels.removeIf(hotel -> hotel.getId().equals(id));
        // Remove from cache when deleted
        hotelCache.remove(id);
        saveHotels();
    }

    // CRUD operations for Room
    @Log(level = LogLevel.INFO)
    public Room createRoom(String hotelId, String roomNumber, String type, double pricePerNight) {
        try {
            return Log.Decorator.withLogging(
                LogLevel.INFO,
                () -> {
                    String id = UUID.randomUUID().toString();
                    Room room = new Room(id, roomNumber, type, pricePerNight);
                    Hotel hotel = getHotelById(hotelId);
                    if (hotel != null) {
                        hotel.addRoom(room);
                    }
                    rooms.add(room);
                    saveRooms();
                    return room;
                },
                "createRoom",
                "DataManager",
                Map.of("hotelId", hotelId, "roomNumber", roomNumber, "type", type, "pricePerNight", pricePerNight)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create room", e);
        }
    }

    public Room getRoomById(String id) {
        for (Room room : rooms) {
            if (room.getId().equals(id)) {
                return room;
            }
        }
        return null;
    }

    public List<Room> getRoomsByHotel(String hotelId) {
        List<Room> hotelRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getHotel() != null && room.getHotel().getId().equals(hotelId)) {
                hotelRooms.add(room);
            }
        }
        return hotelRooms;
    }

    public void updateRoom(Room room) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getId().equals(room.getId())) {
                rooms.set(i, room);
                saveRooms();
                return;
            }
        }
    }

    public void deleteRoom(String id) {
        Room room = getRoomById(id);
        if (room != null && room.getHotel() != null) {
            room.getHotel().removeRoom(room);
        }
        rooms.removeIf(r -> r.getId().equals(id));
        saveRooms();
    }

    // CRUD operations for Booking
    @Log(level = LogLevel.INFO)
    public Booking createBooking(String userId, String roomId, java.time.LocalDate checkInDate, java.time.LocalDate checkOutDate) {
        try {
            return Log.Decorator.withLogging(
                LogLevel.INFO,
                () -> {
                    String id = UUID.randomUUID().toString();
                    User user = getUserById(userId);
                    Room room = getRoomById(roomId);

                    if (user == null || room == null) {
                        return null;
                    }

                    // Calculate total price based on number of nights
                    long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
                    double totalPrice = nights * room.getPricePerNight();

                    Booking booking = new Booking(id, user, room, checkInDate, checkOutDate);
                    user.addBooking(booking);
                    room.addBooking(booking);
                    bookings.add(booking);
                    saveBookings();
                    saveBookingToFile(booking);

                    // Check if we need to clear caches based on the number of bookings
                    // If there are more than 15 bookings in the system, clear caches using LRU strategy
                    Memoizer.checkAndClearCaches(bookings.size(), 15, Memoizer.CacheStrategy.LRU, 30);

                    return booking;
                },
                "createBooking",
                "DataManager",
                Map.of("userId", userId, "roomId", roomId, "checkInDate", checkInDate, "checkOutDate", checkOutDate)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create booking", e);
        }
    }

    public Booking getBookingById(String id) {
        for (Booking booking : bookings) {
            if (booking.getId().equals(id)) {
                return booking;
            }
        }
        return null;
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public List<Booking> getBookingsByUser(String userId) {
        List<Booking> userBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getUser() != null && booking.getUser().getId().equals(userId)) {
                userBookings.add(booking);
            }
        }
        return userBookings;
    }

    public List<Booking> getBookingsByRoom(String roomId) {
        List<Booking> roomBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getRoom() != null && booking.getRoom().getId().equals(roomId)) {
                roomBookings.add(booking);
            }
        }
        return roomBookings;
    }

    public void updateBooking(Booking booking) {
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getId().equals(booking.getId())) {
                bookings.set(i, booking);
                saveBookings();
                return;
            }
        }
    }

    public void deleteBooking(String id) {
        Booking booking = getBookingById(id);
        if (booking != null) {
            if (booking.getUser() != null) {
                booking.getUser().removeBooking(booking);
            }
            if (booking.getRoom() != null) {
                booking.getRoom().removeBooking(booking);
            }
        }
        bookings.removeIf(b -> b.getId().equals(id));
        saveBookings();

        // Check if we need to clear caches based on the number of bookings
        // This is less likely to be needed when deleting, but included for consistency
        // Use LFU strategy to keep frequently used entries
        Memoizer.checkAndClearCaches(bookings.size(), 15, Memoizer.CacheStrategy.LFU, 25);
    }

    // File I/O operations
    private void loadData() {
        loadUsers();
        loadHotels();
        loadRooms();
        loadBookings();

        // Restore relationships
        restoreRelationships();
    }

    private void restoreRelationships() {
        // Restore Hotel-Room relationships
        for (Room room : rooms) {
            for (Hotel hotel : hotels) {
                if (room.getHotel() != null && room.getHotel().getId().equals(hotel.getId())) {
                    room.setHotel(hotel);
                    if (!hotel.getRooms().contains(room)) {
                        hotel.getRooms().add(room);
                    }
                    break;
                }
            }
        }

        // Restore User-Booking and Room-Booking relationships
        for (Booking booking : bookings) {
            // Restore the User-Booking relationship
            for (User user : users) {
                if (booking.getUser() != null && booking.getUser().getId().equals(user.getId())) {
                    booking.setUser(user);
                    if (!user.getBookings().contains(booking)) {
                        user.getBookings().add(booking);
                    }
                    break;
                }
            }

            // Restore Room-Booking relationship
            for (Room room : rooms) {
                if (booking.getRoom() != null && booking.getRoom().getId().equals(room.getId())) {
                    booking.setRoom(room);
                    if (!room.getBookings().contains(booking)) {
                        room.getBookings().add(booking);
                    }
                    break;
                }
            }
        }
    }

    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            users = (List<User>) ois.readObject();
        } catch (FileNotFoundException e) {
            users = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            users = new ArrayList<>();
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadHotels() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HOTELS_FILE))) {
            List<Hotel> loadedHotels = (List<Hotel>) ois.readObject();

            // Filter out duplicate hotels (hotels with the same name)
            hotels = new ArrayList<>();
            for (Hotel hotel : loadedHotels) {
                boolean isDuplicate = false;
                for (Hotel existingHotel : hotels) {
                    if (existingHotel.getName().equals(hotel.getName())) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate) {
                    hotels.add(hotel);
                    // Add to cache when loaded
                    hotelCache.put(hotel.getId(), hotel);
                }
            }

            System.out.println("Loaded " + hotels.size() + " unique hotels (filtered from " + loadedHotels.size() + " total)");
        } catch (FileNotFoundException e) {
            hotels = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            hotels = new ArrayList<>();
        }
    }

    private void saveHotels() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HOTELS_FILE))) {
            oos.writeObject(hotels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadRooms() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ROOMS_FILE))) {
            rooms = (List<Room>) ois.readObject();
        } catch (FileNotFoundException e) {
            rooms = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            rooms = new ArrayList<>();
        }
    }

    private void saveRooms() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ROOMS_FILE))) {
            oos.writeObject(rooms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a random 5-digit number for booking file names.
     * @return a random 5-digit number as a string
     */
    private String generateRandomNumbers() {
        Random random = new Random();
        // Generate a random number between 10000 and 99999 (5 digits)
        int randomNum = 10000 + random.nextInt(90000);
        return String.valueOf(randomNum);
    }

    /**
     * Generates a booking file name in the format "resert(username)(5 random numbers).txt".
     * @param username the username of the booking user
     * @return the generated file name
     */
    private String generateBookingFileName(String username) {
        return BOOKINGS_PREFIX + username + generateRandomNumbers() + ".txt";
    }

    /**
     * Ensures the bookings folder exists.
     */
    private void ensureBookingsFolderExists() {
        File folder = new File(BOOKINGS_FOLDER);
        if (!folder.exists()) {
            boolean created = folder.mkdir();
            if (!created) {
                System.err.println("Failed to create bookings folder");
            }
        }
    }

    /**
     * Loads bookings from .txt files in the bookings folder.
     */
    private void loadBookings() {
        bookings = new ArrayList<>();
        ensureBookingsFolderExists();

        File folder = new File(BOOKINGS_FOLDER);
        File[] files = folder.listFiles((dir, name) -> name.startsWith(BOOKINGS_PREFIX) && name.endsWith(".txt"));

        if (files == null) {
            return;
        }

        for (File file : files) {
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                if (lines.size() >= 7) {
                    String id = lines.get(0).split(": ")[1];
                    String userId = lines.get(1).split(": ")[1];
                    String roomId = lines.get(2).split(": ")[1];
                    String checkInDateStr = lines.get(3).split(": ")[1];
                    String checkOutDateStr = lines.get(4).split(": ")[1];

                    User user = getUserById(userId);
                    Room room = getRoomById(roomId);

                    if (user != null && room != null) {
                        java.time.LocalDate checkInDate = java.time.LocalDate.parse(checkInDateStr);
                        java.time.LocalDate checkOutDate = java.time.LocalDate.parse(checkOutDateStr);

                        Booking booking = new Booking(id, user, room, checkInDate, checkOutDate);
                        bookings.add(booking);
                    }
                }
            } catch (IOException | ArrayIndexOutOfBoundsException e) {
                System.err.println("Error reading booking file: " + file.getName());
                e.printStackTrace();
            }
        }

        // Check if we need to clear caches based on the number of bookings after loading
        // Use TIME_BASED strategy to clear oldest entries when loading bookings
        Memoizer.checkAndClearCaches(bookings.size(), 15, Memoizer.CacheStrategy.TIME_BASED, 40);
    }

    /**
     * Saves a booking to a .txt file.
     * @param booking the booking to save
     */
    private void saveBookingToFile(Booking booking) {
        if (booking.getUser() == null) {
            return;
        }

        ensureBookingsFolderExists();

        String fileName = generateBookingFileName(booking.getUser().getUsername());
        File file = new File(BOOKINGS_FOLDER, fileName);

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("Booking ID: " + booking.getId());
            writer.println("User ID: " + booking.getUser().getId());
            writer.println("Room ID: " + booking.getRoom().getId());
            writer.println("Check-in Date: " + booking.getCheckInDate());
            writer.println("Check-out Date: " + booking.getCheckOutDate());
            writer.println("Total Price: " + booking.getTotalPrice());
            writer.println("User Email: " + booking.getUser().getEmail());
            writer.println("User Full Name: " + booking.getUser().getFullName());

            if (booking.getRoom().getHotel() != null) {
                writer.println("Hotel: " + booking.getRoom().getHotel().getName());
                writer.println("Room Number: " + booking.getRoom().getRoomNumber());
                writer.println("Room Type: " + booking.getRoom().getType());
            }
        } catch (IOException e) {
            System.err.println("Error writing booking to file: " + fileName);
            e.printStackTrace();
        }
    }

    /**
     * Saves all bookings to .txt files.
     * Only confirmed bookings are saved to .txt files.
     */
    private void saveBookings() {
        for (Booking booking : bookings) {
            if (booking.isConfirmed()) {
                saveBookingToFile(booking);
            }
        }
    }

    /**
     * Resets all data and recreates sample hotels and rooms.
     * Use this when there's an issue with missing rooms in hotels.
     */
    public void resetAndCreateSampleData() {
        // Clear all data from memory
        users.clear();
        hotels.clear();
        rooms.clear();
        bookings.clear();

        // Clear the cache
        hotelCache.clear();

        // Remove data files to start fresh
        deleteFile(HOTELS_FILE);
        deleteFile(ROOMS_FILE);
        deleteBookingFiles();

        // Create sample hotels in US cities
        Hotel grandHotel = createHotel("Grand Hotel", "New York", 5, "A luxury hotel in the heart of Manhattan");
        Hotel seasideResort = createHotel("Seaside Resort", "Miami", 4, "Beautiful beachfront resort with ocean views");
        Hotel mountainLodge = createHotel("Mountain Lodge", "Aspen", 4, "Cozy lodge with stunning mountain views");
        Hotel cityInn = createHotel("City Inn", "Chicago", 3, "Affordable hotel in downtown Chicago");
        Hotel sunsetHotel = createHotel("Sunset Hotel", "Los Angeles", 5, "Elegant hotel with sunset views over the Pacific");

        // Add rooms to the Grand Hotel
        createRoom(grandHotel.getId(), "101", "Single", 150.0);
        createRoom(grandHotel.getId(), "102", "Double", 200.0);
        createRoom(grandHotel.getId(), "201", "Suite", 350.0);

        // Add rooms to Seaside Resort
        createRoom(seasideResort.getId(), "101", "Double", 180.0);
        createRoom(seasideResort.getId(), "102", "Double", 180.0);
        createRoom(seasideResort.getId(), "201", "Suite", 300.0);

        // Add rooms to Mountain Lodge
        createRoom(mountainLodge.getId(), "101", "Single", 120.0);
        createRoom(mountainLodge.getId(), "102", "Double", 160.0);
        createRoom(mountainLodge.getId(), "201", "Family", 220.0);

        // Add rooms to City Inn
        createRoom(cityInn.getId(), "101", "Single", 90.0);
        createRoom(cityInn.getId(), "102", "Double", 120.0);
        createRoom(cityInn.getId(), "201", "Double", 120.0);

        // Add rooms to Sunset Hotel
        createRoom(sunsetHotel.getId(), "101", "Double", 250.0);
        createRoom(sunsetHotel.getId(), "102", "Double", 250.0);
        createRoom(sunsetHotel.getId(), "201", "Suite", 400.0);
        createRoom(sunsetHotel.getId(), "202", "Presidential Suite", 800.0);

        // Log success message
        System.out.println("Sample data recreated successfully with " + hotels.size() + " hotels and " + rooms.size() + " rooms");
    }

    /**
     * Deletes a file if it exists.
     *
     * @param filePath path to the file to delete
     */
    private void deleteFile(String filePath) {
        // Create the file object
        File file = new File(filePath);
        // Check if the file exists before trying to delete
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                // Log success
                System.out.println("Deleted file: " + filePath);
            } else {
                // Log failure
                System.out.println("Failed to delete file: " + filePath);
            }
        }
    }

    /**
     * Deletes all booking files in the bookings folder.
     */
    private void deleteBookingFiles() {
        ensureBookingsFolderExists();

        File folder = new File(BOOKINGS_FOLDER);
        File[] files = folder.listFiles((dir, name) -> name.startsWith(BOOKINGS_PREFIX) && name.endsWith(".txt"));

        if (files != null) {
            for (File file : files) {
                boolean deleted = file.delete();
                if (deleted) {
                    System.out.println("Deleted booking file: " + file.getName());
                } else {
                    System.out.println("Failed to delete booking file: " + file.getName());
                }
            }
        }
    }

    /**
     * Adds a hotel to the recently viewed hotels list.
     * If the hotel is already in the list, it is moved to the front.
     * The list is limited to the 5 most recently viewed hotels.
     * @param hotel the hotel to add to the recently viewed list
     */
    public void addToRecentlyViewedHotels(Hotel hotel) {
        // Remove the hotel if it's already in the list
        recentlyViewedHotels.remove(hotel);

        // Add the hotel to the front of the list
        recentlyViewedHotels.add(0, hotel);

        // Limit the list to 5 hotels
        while (recentlyViewedHotels.size() > 5) {
            recentlyViewedHotels.remove(recentlyViewedHotels.size() - 1);
        }
    }

    /**
     * Gets the list of recently viewed hotels.
     * @return the list of recently viewed hotels
     */
    public LinkedList<Hotel> getRecentlyViewedHotels() {
        return recentlyViewedHotels;
    }

    /**
     * Clears the recently viewed hotels list.
     */
    public void clearRecentlyViewedHotels() {
        recentlyViewedHotels.clear();
    }
}
