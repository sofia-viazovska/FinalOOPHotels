# Hotel Booking System

This is a JavaFX application for hotel booking management. The system allows users to browse hotels, view rooms, make bookings, and manage their reservations. Administrators have additional capabilities to manage hotels, rooms, bookings, and users.

## Classes and Functions

### Models

#### User:
A class representing a user of the application with properties like name, email, etc.
- getId() - Returns the user's unique identifier
- setId(String id) - Sets the user's unique identifier
- getUsername() - Returns the user's username
- setUsername(String username) - Sets the user's username
- getPassword() - Returns the user's password
- setPassword(String password) - Sets the user's password
- getFullName() - Returns the user's full name
- setFullName(String fullName) - Sets the user's full name
- getEmail() - Returns the user's email address
- setEmail(String email) - Sets the user's email address
- getPhoneNumber() - Returns the user's phone number
- setPhoneNumber(String phoneNumber) - Sets the user's phone number
- getBookings() - Returns the list of bookings made by the user
- setBookings(List<Booking> bookings) - Sets the list of bookings for the user
- addBooking(Booking booking) - Adds a booking to the user's list of bookings
- removeBooking(Booking booking) - Removes a booking from the user's list
- isAdmin() - Returns whether the user has admin privileges
- setAdmin(boolean admin) - Updates the user's admin status
- toString() - Returns a string representation of the user

#### Hotel:
A class representing a hotel with properties like name, location, rating, etc.
- getId() - Returns the hotel's unique identifier
- setId(String id) - Sets the hotel's unique identifier
- getName() - Returns the hotel's name
- setName(String name) - Sets the hotel's name
- getLocation() - Returns the hotel's location/city
- setLocation(String location) - Sets the hotel's location
- getRating() - Returns the hotel's rating (1-5 stars)
- setRating(int rating) - Sets the hotel's rating
- getDescription() - Returns the description of the hotel
- setDescription(String description) - Sets the hotel's description
- getRooms() - Returns the list of rooms in the hotel
- setRooms(List<Room> rooms) - Sets the list of rooms for the hotel
- addRoom(Room room) - Adds a room to the hotel's list of rooms
- removeRoom(Room room) - Removes a room from the hotel's list
- compareTo(Hotel other) - Compares hotels by rating for sorting
- toString() - Returns a string representation of the hotel

#### Room:
A class representing a room in a hotel with properties like room number, type, price, etc.
- getId() - Returns the room's unique identifier
- setId(String id) - Sets the room's unique identifier
- getRoomNumber() - Returns the room number
- setRoomNumber(String roomNumber) - Sets the room number
- getType() - Returns the type of room
- setType(String type) - Sets the type of room
- getPricePerNight() - Returns the price per night
- setPricePerNight(double pricePerNight) - Sets the price per night
- isAvailable() - Returns whether the room is available for booking
- setAvailable(boolean available) - Sets the room's availability status
- getHotel() - Returns the hotel this room belongs to
- setHotel(Hotel hotel) - Sets the hotel this room belongs to
- getBookings() - Returns the list of bookings for this room
- setBookings(List<Booking> bookings) - Sets the list of bookings for this room
- addBooking(Booking booking) - Adds a booking to the room's list of bookings
- removeBooking(Booking booking) - Removes a booking from the room's list
- toString() - Returns a string representation of the room

#### Booking:
A class representing a booking made by a user for a room in a hotel.
- getId() - Returns the booking's unique identifier
- setId(String id) - Sets the booking's unique identifier
- getUser() - Returns the user who made the booking
- setUser(User user) - Sets the user who made the booking
- getRoom() - Returns the room that was booked
- setRoom(Room room) - Sets the room that was booked
- getCheckInDate() - Returns the check-in date
- setCheckInDate(LocalDate checkInDate) - Sets the check-in date
- getCheckOutDate() - Returns the check-out date
- setCheckOutDate(LocalDate checkOutDate) - Sets the check-out date
- getTotalPrice() - Returns the total price for the entire stay
- isConfirmed() - Returns whether the booking has been confirmed
- setConfirmed(boolean confirmed) - Sets the booking's confirmation status
- calculateTotalPrice() - Calculates the total price based on room price and duration
- toString() - Returns a string representation of the booking

#### DataManager:
A central class that manages all data operations in the application.
- createUser() - Creates a new user with the given details
- getUserById() - Retrieves a user by their ID
- getUserByUsername() - Retrieves a user by their username
- getAllUsers() - Returns a list of all users
- updateUser() - Updates an existing user's information
- deleteUser() - Deletes a user by their ID
- createHotel() - Creates a new hotel with the given details
- getHotelByName() - Retrieves a hotel by its name
- getHotelById() - Retrieves a hotel by its ID
- getAllHotels() - Returns a list of all hotels
- updateHotel() - Updates an existing hotel's information
- deleteHotel() - Deletes a hotel by its ID
- createRoom() - Creates a new room with the given details
- getRoomById() - Retrieves a room by its ID
- getRoomsByHotel() - Returns a list of rooms for a specific hotel
- updateRoom() - Updates an existing room's information
- deleteRoom() - Deletes a room by its ID
- createBooking() - Creates a new booking with the given details
- getBookingById() - Retrieves a booking by its ID
- getAllBookings() - Returns a list of all bookings
- getBookingsByUser() - Returns a list of bookings for a specific user
- getBookingsByRoom() - Returns a list of bookings for a specific room
- updateBooking() - Updates an existing booking's information
- deleteBooking() - Deletes a booking by its ID
- loadData() - Loads all data from files
- restoreRelationships() - Restores relationships between entities after loading
- loadUsers() - Loads user data from file
- saveUsers() - Saves user data to file
- loadHotels() - Loads hotel data from file
- saveHotels() - Saves hotel data to file
- loadRooms() - Loads room data from file
- saveRooms() - Saves room data to file
- generateRandomNumbers() - Generates random numbers for IDs
- generateBookingFileName() - Generates a filename for a booking
- ensureBookingsFolderExists() - Ensures the bookings folder exists
- loadBookings() - Loads booking data from files
- saveBookingToFile() - Saves a booking to a file
- saveBookings() - Saves all bookings to files
- resetAndCreateSampleData() - Resets the data and creates sample data
- deleteFile() - Deletes a file
- deleteBookingFiles() - Deletes all booking files
- addToRecentlyViewedHotels() - Adds a hotel to the recently viewed list
- getRecentlyViewedHotels() - Returns the list of recently viewed hotels
- clearRecentlyViewedHotels() - Clears the list of recently viewed hotels

### Data Structures

#### LinkedList:
A custom implementation of a linked list data structure.
- add(T data) - Adds an element to the end of the list
- add(int index, T data) - Adds an element at the specified index
- remove(int index) - Removes the element at the specified index
- remove(T data) - Removes the first occurrence of the specified element
- get(int index) - Returns the element at the specified index
- set(int index, T data) - Replaces the element at the specified index
- indexOf(T data) - Returns the index of the first occurrence of the specified element
- size() - Returns the number of elements in the list
- isEmpty() - Returns whether the list is empty
- clear() - Removes all elements from the list
- iterator() - Returns an iterator over the elements in the list
- toString() - Returns a string representation of the list

### Utils

#### SearchAndSort:
A utility class for searching and sorting operations.
- mergeSort() - Sorts a list using a merge sort algorithm
- merge() - Merges two sorted lists into a single sorted list
- binarySearch() - Performs a binary search on a sorted list
- sortHotelsByRating() - Sorts a list of hotels by rating (descending)
- sortHotelsByName() - Sorts a list of hotels by name (ascending)
- searchHotelByName() - Searches for a hotel by name using binary search

#### DataFileViewer:
A utility class for viewing the contents of data files.
- viewDataFile() - Opens a file chooser to select and view a data file
- readDataFile() - Reads and interprets the content of a data file
- showDataContent() - Displays the content of a data file in a dialog
- showError() - Shows an error message dialog

### App

#### Main:
The entry point of the JavaFX application.
- main() - The main method that launches the application
- start() - Initializes the application and loads the main view
- getDataManager() - Returns the application's data manager instance

### Controllers

#### MainController:
The main controller for the application that manages navigation and user interface.
- initialize() - Initializes the controller
- showWelcomeView() - Shows the welcome view
- handleViewDataFiles() - Handles the action to view data files
- handleExit() - Handles the exit action
- showHotelsView() - Shows the hotels view
- showMyBookingsView() - Shows the user's bookings view
- showLoginView() - Shows the login view
- showRegisterView() - Shows the registration view
- handleLogout() - Handles the logout action
- showManageHotelsView() - Shows the hotel management view
- showManageRoomsView() - Shows the room management view
- showManageBookingsView() - Shows the booking management view
- showManageUsersView() - Shows the user management view
- showAdminRequiredAlert() - Shows an alert when admin privileges are required
- setCurrentUser() - Sets the current logged-in user
- getCurrentUser() - Returns the current logged-in user
- getDataManager() - Returns the data manager
- updateStatusLabel() - Updates the status label with user information
- showLoginRequiredAlert() - Shows an alert when login is required
- showAlert() - Shows an alert dialog
- setContent() - Sets the content of the main view

#### LoginController:
Controller for the login view.
- setMainController(MainController mainController) - Sets the main controller reference
- handleLogin() - Handles the login action, validating credentials and logging in the user
- handleCancel() - Handles the cancel action, clearing fields and returning to the welcome view

#### RegisterController:
Controller for the user registration view.
- setMainController(MainController mainController) - Sets the main controller reference
- handleRegister() - Handles the registration action, validating input and creating a new user
- handleCancel() - Handles the cancel action, clearing fields and returning to the welcome view

#### HotelsController:
Controller for the hotels view.
- initialize() - Initializes the controller, setting up the table view and other UI components
- setMainController(MainController mainController) - Sets the main controller reference
- updateRecentlyViewedHotels() - Updates the list of recently viewed hotels
- loadHotels() - Loads the list of hotels from the data manager
- handleSearch() - Handles the search action, filtering hotels based on search criteria
- performContainsSearch(List<Hotel> hotels, String searchTerm) - Performs a search for hotels containing the search term
- sortHotels(String sortOption) - Sorts the list of hotels based on the specified sort option
- handleViewRooms() - Handles the action to view rooms for a selected hotel
- handleBack() - Handles the back action, returning to the previous view
- showAlert(String title, String message, AlertType alertType) - Shows an alert dialog with the specified title, message, and type

#### RoomsController:
Controller for the rooms view.
- initialize() - Initializes the controller, setting up the table view and other UI components
- setMainController(MainController mainController) - Sets the main controller reference
- setHotel(Hotel hotel) - Sets the hotel for which rooms are being displayed
- loadRooms() - Loads the list of rooms for the current hotel
- handleShowBookingForm() - Handles the action to show the booking form for a selected room
- updateTotalPrice() - Updates the total price based on the selected dates
- handleBookRoom() - Handles the action to book a room
- handleCancelBooking() - Handles the action to cancel the booking process
- handleBack() - Handles the back action, returning to the previous view
- showAlert(String title, String message, AlertType alertType) - Shows an alert dialog with the specified title, message, and type

#### MyBookingsController:
Controller for the user's bookings view.
- initialize() - Initializes the controller, setting up the table columns
- setMainController(MainController mainController) - Sets the main controller reference and loads bookings
- loadBookings() - Loads the list of bookings for the current user
- handleCancelBooking() - Handles the action to cancel a selected booking
- handleBack() - Handles the back action, returning to the welcome view
- showAlert(String title, String message, Alert.AlertType alertType) - Shows an alert dialog with the specified title, message, and type

#### ManageHotelsController:
Controller for the hotel management view.
- initialize() - Initializes the controller, setting up the table view and other UI components
- setMainController(MainController mainController) - Sets the main controller reference
- loadHotels() - Loads the list of hotels from the data manager
- handleSearch() - Handles the search action, filtering hotels based on search criteria
- sortHotels(String sortOption) - Sorts the list of hotels based on the specified sort option
- handleAdd() - Handles the action to add a new hotel
- handleUpdate() - Handles the action to update an existing hotel
- handleDelete() - Handles the action to delete a hotel
- handleClear() - Handles the action to clear the input fields
- handleManageRooms() - Handles the action to manage rooms for a selected hotel
- handleBack() - Handles the back action, returning to the previous view
- populateFields(Hotel hotel) - Populates the input fields with the selected hotel's data
- clearFields() - Clears all input fields
- validateInput() - Validates the input fields
- showAlert(String title, String message, AlertType alertType) - Shows an alert dialog with the specified title, message, and type

#### ManageRoomsController:
Controller for the room management view.
- initialize() - Initializes the controller, setting up the table view and other UI components
- setMainController(MainController mainController) - Sets the main controller reference
- loadHotels() - Loads the list of hotels for the hotel selection combobox
- loadRoomsByHotel(String hotelId) - Loads the list of rooms for a specific hotel
- handleRefresh() - Handles the refresh action, reloading rooms for the selected hotel
- handleAdd() - Handles the action to add a new room
- handleUpdate() - Handles the action to update an existing room
- handleDelete() - Handles the action to delete a room
- handleClear() - Handles the action to clear the input fields
- handleBack() - Handles the back action, returning to the previous view
- populateFields(Room room) - Populates the input fields with the selected room's data
- clearFields() - Clears all input fields
- validateInput() - Validates the input fields
- showAlert(String title, String message, AlertType alertType) - Shows an alert dialog with the specified title, message, and type

#### ManageBookingsController:
Controller for the booking management view.
- initialize() - Initializes the controller, setting up the table view and other UI components
- setMainController(MainController mainController) - Sets the main controller reference
- loadBookings() - Loads the list of all bookings from the data manager
- filterBookings() - Filters bookings based on the selected filter criteria
- handleSearch() - Handles the search action, filtering bookings based on search criteria
- handleUpdate() - Handles the action to update an existing booking
- handleConfirm() - Handles the action to confirm a booking
- handleCancelBooking() - Handles the action to cancel a booking
- handleClear() - Handles the action to clear the input fields
- handleBack() - Handles the back action, returning to the previous view
- populateFields(Booking booking) - Populates the input fields with the selected booking's data
- clearFields() - Clears all input fields
- updateTotalPrice() - Updates the total price based on the selected dates
- validateInput() - Validates the input fields
- showAlert(String title, String message, AlertType alertType) - Shows an alert dialog with the specified title, message, and type

#### ManageUsersController:
Controller for the user management view.
- initialize() - Initializes the controller, setting up the table view and other UI components
- setMainController(MainController mainController) - Sets the main controller reference
- loadUsers() - Loads the list of all users from the data manager
- handleSearch() - Handles the search action, filtering users based on search criteria
- handleAdd() - Handles the action to add a new user
- handleUpdate() - Handles the action to update an existing user
- handleDelete() - Handles the action to delete a user
- deleteUser() - Deletes a user and their associated bookings
- handleClear() - Handles the action to clear the input fields
- handleViewBookings() - Handles the action to view bookings for a selected user
- handleBack() - Handles the back action, returning to the previous view
- populateFields(User user) - Populates the input fields with the selected user's data
- clearFields() - Clears all input fields
- validateInput(boolean isNewUser) - Validates the input fields
- showAlert(String title, String message, AlertType alertType) - Shows an alert dialog with the specified title, message, and type

#### WelcomeController:
Controller for the welcome view.
- setMainController(MainController mainController) - Sets the main controller reference
- showHotelsView() - Handles the action to show the hotels view
- showLoginView() - Handles the action to show the login view
- showRegisterView() - Handles the action to show the registration view
