import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class HotelManagementSystem {
    // Database connection details
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/hotel_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Saur@1234";

    // ANSI color codes for better UI
    private static final String RESET = "\033[0m";
    private static final String RED = "\033[0;31m";
    private static final String GREEN = "\033[0;32m";
    private static final String YELLOW = "\033[0;33m";
    private static final String BLUE = "\033[0;34m";
    private static final String CYAN = "\033[0;36m";

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeDatabase();

        while (true) {
            displayMainMenu();
            int choice = getValidIntegerInput("Choose an option: ");

            switch (choice) {
                case 1: viewAllRooms(); break;
                case 2: viewAvailableRooms(); break;
                case 3: bookRoom(); break;
                case 4: checkIn(); break;
                case 5: checkOut(); break;
                case 6: viewBookings(); break;
                case 7: manageRooms(); break;
                case 8:
                    System.out.println(GREEN + "Thank you for using Hotel Management System!" + RESET);
                    System.exit(0);
                default:
                    System.out.println(RED + "Invalid choice! Please try again." + RESET);
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n" + BLUE + "=== HOTEL MANAGEMENT SYSTEM ===" + RESET);
        System.out.println("1. View All Rooms");
        System.out.println("2. View Available Rooms");
        System.out.println("3. Book Room");
        System.out.println("4. Check-In Guest");
        System.out.println("5. Check-Out Guest");
        System.out.println("6. View Bookings");
        System.out.println("7. Manage Rooms");
        System.out.println("8. Exit");
        System.out.println(BLUE + "===============================" + RESET);
    }

    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            // Create rooms table if not exists
            String createRoomsTable = """
                CREATE TABLE IF NOT EXISTS rooms (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    room_number VARCHAR(10) UNIQUE NOT NULL,
                    type ENUM('Standard', 'Deluxe', 'Suite') NOT NULL,
                    price DECIMAL(10,2) NOT NULL,
                    status ENUM('Available', 'Booked', 'Occupied', 'Maintenance') DEFAULT 'Available',
                    capacity INT DEFAULT 2,
                    amenities TEXT
                )""";

            // Create bookings table if not exists
            String createBookingsTable = """
                CREATE TABLE IF NOT EXISTS bookings (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    room_id INT,
                    guest_name VARCHAR(100) NOT NULL,
                    guest_phone VARCHAR(15),
                    check_in_date DATE,
                    check_out_date DATE,
                    total_amount DECIMAL(10,2),
                    status ENUM('Confirmed', 'Checked-In', 'Checked-Out', 'Cancelled') DEFAULT 'Confirmed',
                    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
                )""";

            Statement stmt = conn.createStatement();
            stmt.execute(createRoomsTable);
            stmt.execute(createBookingsTable);

            // Insert sample data if tables are empty
            insertSampleData(conn);

        } catch (SQLException e) {
            System.err.println(RED + "Database initialization error: " + e.getMessage() + RESET);
        }
    }

    private static void insertSampleData(Connection conn) throws SQLException {
        // Check if rooms table is empty
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM rooms");
        rs.next();

        if (rs.getInt(1) == 0) {
            String insertRooms = """
                INSERT INTO rooms (room_number, type, price, capacity, amenities) VALUES
                ('101', 'Standard', 2500.00, 2, 'AC, TV, WiFi'),
                ('102', 'Standard', 2500.00, 2, 'AC, TV, WiFi'),
                ('201', 'Deluxe', 4000.00, 3, 'AC, TV, WiFi, Mini-bar'),
                ('202', 'Deluxe', 4000.00, 3, 'AC, TV, WiFi, Mini-bar'),
                ('301', 'Suite', 7500.00, 4, 'AC, TV, WiFi, Mini-bar, Jacuzzi')
                """;
            stmt.execute(insertRooms);
            System.out.println(GREEN + "Sample rooms data inserted successfully!" + RESET);
        }
    }

    private static void viewAllRooms() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM rooms ORDER BY room_number";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n" + CYAN + "ALL ROOMS INVENTORY" + RESET);
            System.out.println(BLUE + "---------------------------------------------------------------" + RESET);
            System.out.printf("%-6s %-10s %-12s %-10s %-12s %-8s %-20s%n",
                    "ID", "Room No", "Type", "Price", "Status", "Capacity", "Amenities");
            System.out.println(BLUE + "---------------------------------------------------------------" + RESET);

            while (rs.next()) {
                String statusColor = getStatusColor(rs.getString("status"));
                System.out.printf("%-6d %-10s %-12s ₹%-9.2f %s%-12s%s %-8d %-20s%n",
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getString("type"),
                        rs.getDouble("price"),
                        statusColor,
                        rs.getString("status"),
                        RESET,
                        rs.getInt("capacity"),
                        rs.getString("amenities"));
            }
        } catch (SQLException e) {
            System.err.println(RED + "Error viewing rooms: " + e.getMessage() + RESET);
        }
    }

    private static void viewAvailableRooms() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM rooms WHERE status = 'Available' ORDER BY room_number";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n" + GREEN + "AVAILABLE ROOMS" + RESET);
            System.out.println(BLUE + "---------------------------------------------------------------" + RESET);
            System.out.printf("%-10s %-12s %-10s %-8s %-20s%n",
                    "Room No", "Type", "Price", "Capacity", "Amenities");
            System.out.println(BLUE + "---------------------------------------------------------------" + RESET);

            boolean hasAvailable = false;
            while (rs.next()) {
                hasAvailable = true;
                System.out.printf("%-10s %-12s ₹%-9.2f %-8d %-20s%n",
                        rs.getString("room_number"),
                        rs.getString("type"),
                        rs.getDouble("price"),
                        rs.getInt("capacity"),
                        rs.getString("amenities"));
            }

            if (!hasAvailable) {
                System.out.println(YELLOW + "No rooms available at the moment." + RESET);
            }
        } catch (SQLException e) {
            System.err.println(RED + "Error viewing available rooms: " + e.getMessage() + RESET);
        }
    }

    private static void bookRoom() {
        viewAvailableRooms();

        System.out.print("\nEnter Room Number to Book: ");
        String roomNumber = scanner.nextLine().trim();

        System.out.print("Enter Guest Name: ");
        String guestName = scanner.nextLine().trim();

        System.out.print("Enter Guest Phone: ");
        String guestPhone = scanner.nextLine().trim();

        LocalDate checkIn = getValidDate("Enter Check-in Date (yyyy-mm-dd): ");
        LocalDate checkOut = getValidDate("Enter Check-out Date (yyyy-mm-dd): ");

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            System.out.println(RED + "Check-out date must be after check-in date!" + RESET);
            return;
        }

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            // Check room availability
            String checkQuery = "SELECT id, price FROM rooms WHERE room_number = ? AND status = 'Available'";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, roomNumber);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int roomId = rs.getInt("id");
                double pricePerNight = rs.getDouble("price");
                long nights = checkIn.until(checkOut).getDays();
                double totalAmount = pricePerNight * nights;

                // Insert booking
                String bookingQuery = """
                    INSERT INTO bookings (room_id, guest_name, guest_phone, 
                                        check_in_date, check_out_date, total_amount) 
                    VALUES (?, ?, ?, ?, ?, ?)""";
                PreparedStatement bookingStmt = conn.prepareStatement(bookingQuery);
                bookingStmt.setInt(1, roomId);
                bookingStmt.setString(2, guestName);
                bookingStmt.setString(3, guestPhone);
                bookingStmt.setDate(4, Date.valueOf(checkIn));
                bookingStmt.setDate(5, Date.valueOf(checkOut));
                bookingStmt.setDouble(6, totalAmount);

                bookingStmt.executeUpdate();

                // Update room status
                String updateQuery = "UPDATE rooms SET status = 'Booked' WHERE room_number = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, roomNumber);
                updateStmt.executeUpdate();

                System.out.println(GREEN + "\n✓ Booking Successful!" + RESET);
                System.out.println("Room: " + roomNumber);
                System.out.println("Guest: " + guestName);
                System.out.println("Duration: " + nights + " nights");
                System.out.println("Total Amount: ₹" + totalAmount);
            } else {
                System.out.println(RED + "Room " + roomNumber + " is not available for booking." + RESET);
            }
        } catch (SQLException e) {
            System.err.println(RED + "Booking error: " + e.getMessage() + RESET);
        }
    }

    private static void checkIn() {
        System.out.print("Enter Room Number for Check-in: ");
        String roomNumber = scanner.nextLine().trim();

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            String query = """
                SELECT b.id, b.guest_name 
                FROM bookings b 
                JOIN rooms r ON b.room_id = r.id 
                WHERE r.room_number = ? AND b.status = 'Confirmed'
                """;
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, roomNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int bookingId = rs.getInt("id");
                String guestName = rs.getString("guest_name");

                // Update booking status to Checked-In
                String updateBooking = "UPDATE bookings SET status = 'Checked-In' WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateBooking);
                updateStmt.setInt(1, bookingId);
                updateStmt.executeUpdate();

                // Update room status to Occupied
                String updateRoom = "UPDATE rooms SET status = 'Occupied' WHERE room_number = ?";
                PreparedStatement roomStmt = conn.prepareStatement(updateRoom);
                roomStmt.setString(1, roomNumber);
                roomStmt.executeUpdate();

                System.out.println(GREEN + "✓ Check-in successful for " + guestName + " in room " + roomNumber + RESET);
            } else {
                System.out.println(RED + "No confirmed booking found for room " + roomNumber + RESET);
            }
        } catch (SQLException e) {
            System.err.println(RED + "Check-in error: " + e.getMessage() + RESET);
        }
    }

    private static void checkOut() {
        System.out.print("Enter Room Number for Check-out: ");
        String roomNumber = scanner.nextLine().trim();

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            String query = """
                SELECT b.id, b.guest_name, b.total_amount 
                FROM bookings b 
                JOIN rooms r ON b.room_id = r.id 
                WHERE r.room_number = ? AND b.status = 'Checked-In'
                """;
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, roomNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int bookingId = rs.getInt("id");
                String guestName = rs.getString("guest_name");
                double totalAmount = rs.getDouble("total_amount");

                // Update booking status to Checked-Out
                String updateBooking = "UPDATE bookings SET status = 'Checked-Out' WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateBooking);
                updateStmt.setInt(1, bookingId);
                updateStmt.executeUpdate();

                // Update room status to Available
                String updateRoom = "UPDATE rooms SET status = 'Available' WHERE room_number = ?";
                PreparedStatement roomStmt = conn.prepareStatement(updateRoom);
                roomStmt.setString(1, roomNumber);
                roomStmt.executeUpdate();

                System.out.println(GREEN + "\n✓ Check-out successful!" + RESET);
                System.out.println("Guest: " + guestName);
                System.out.println("Room: " + roomNumber);
                System.out.println("Total Bill: ₹" + totalAmount);
                System.out.println(CYAN + "Thank you for staying with us!" + RESET);
            } else {
                System.out.println(RED + "No checked-in guest found for room " + roomNumber + RESET);
            }
        } catch (SQLException e) {
            System.err.println(RED + "Check-out error: " + e.getMessage() + RESET);
        }
    }

    private static void viewBookings() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            String query = """
                SELECT b.id, r.room_number, b.guest_name, b.guest_phone, 
                       b.check_in_date, b.check_out_date, b.total_amount, b.status
                FROM bookings b 
                JOIN rooms r ON b.room_id = r.id 
                ORDER BY b.check_in_date DESC
                """;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n" + CYAN + "ALL BOOKINGS" + RESET);
            System.out.println(BLUE + "----------------------------------------------------------------------------------------" + RESET);
            System.out.printf("%-4s %-8s %-20s %-12s %-12s %-12s %-10s %-12s%n",
                    "ID", "Room", "Guest Name", "Phone", "Check-in", "Check-out", "Amount", "Status");
            System.out.println(BLUE + "----------------------------------------------------------------------------------------" + RESET);

            while (rs.next()) {
                String statusColor = getBookingStatusColor(rs.getString("status"));
                System.out.printf("%-4d %-8s %-20s %-12s %-12s %-12s ₹%-9.2f %s%-12s%s%n",
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getString("guest_name"),
                        rs.getString("guest_phone"),
                        rs.getDate("check_in_date"),
                        rs.getDate("check_out_date"),
                        rs.getDouble("total_amount"),
                        statusColor,
                        rs.getString("status"),
                        RESET);
            }
        } catch (SQLException e) {
            System.err.println(RED + "Error viewing bookings: " + e.getMessage() + RESET);
        }
    }

    private static void manageRooms() {
        System.out.println("\n" + CYAN + "Room Management" + RESET);
        System.out.println("1. Add New Room");
        System.out.println("2. Update Room Status");
        System.out.println("3. Back to Main Menu");

        int choice = getValidIntegerInput("Choose an option: ");

        switch (choice) {
            case 1: addNewRoom(); break;
            case 2: updateRoomStatus(); break;
            case 3: return;
            default: System.out.println(RED + "Invalid choice!" + RESET);
        }
    }

    private static void addNewRoom() {
        System.out.print("Enter Room Number: ");
        String roomNumber = scanner.nextLine().trim();

        System.out.println("Select Room Type:");
        System.out.println("1. Standard");
        System.out.println("2. Deluxe");
        System.out.println("3. Suite");

        int typeChoice = getValidIntegerInput("Choose type: ");
        String[] types = {"", "Standard", "Deluxe", "Suite"};
        String type = (typeChoice >= 1 && typeChoice <= 3) ? types[typeChoice] : "Standard";

        double price = getValidDoubleInput("Enter Price per night: ₹");
        int capacity = getValidIntegerInput("Enter Room Capacity: ");

        System.out.print("Enter Amenities (comma separated): ");
        String amenities = scanner.nextLine().trim();

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO rooms (room_number, type, price, capacity, amenities) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, roomNumber);
            stmt.setString(2, type);
            stmt.setDouble(3, price);
            stmt.setInt(4, capacity);
            stmt.setString(5, amenities);

            stmt.executeUpdate();
            System.out.println(GREEN + "✓ Room " + roomNumber + " added successfully!" + RESET);
        } catch (SQLException e) {
            System.err.println(RED + "Error adding room: " + e.getMessage() + RESET);
        }
    }

    private static void updateRoomStatus() {
        viewAllRooms();
        System.out.print("\nEnter Room ID to update status: ");
        int roomId = getValidIntegerInput("");

        System.out.println("Select New Status:");
        System.out.println("1. Available");
        System.out.println("2. Booked");
        System.out.println("3. Occupied");
        System.out.println("4. Maintenance");

        int statusChoice = getValidIntegerInput("Choose status: ");
        String[] statuses = {"", "Available", "Booked", "Occupied", "Maintenance"};
        String status = (statusChoice >= 1 && statusChoice <= 4) ? statuses[statusChoice] : "Available";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE rooms SET status = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setInt(2, roomId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(GREEN + "✓ Room status updated successfully!" + RESET);
            } else {
                System.out.println(RED + "Room not found with ID: " + roomId + RESET);
            }
        } catch (SQLException e) {
            System.err.println(RED + "Error updating room status: " + e.getMessage() + RESET);
        }
    }

    // Helper methods
    private static String getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "available": return GREEN;
            case "booked": return YELLOW;
            case "occupied": return BLUE;
            case "maintenance": return RED;
            default: return RESET;
        }
    }

    private static String getBookingStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "confirmed": return YELLOW;
            case "checked-in": return GREEN;
            case "checked-out": return CYAN;
            case "cancelled": return RED;
            default: return RESET;
        }
    }

    private static int getValidIntegerInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid input! Please enter a valid number." + RESET);
            }
        }
    }

    private static double getValidDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid input! Please enter a valid number." + RESET);
            }
        }
    }

    private static LocalDate getValidDate(String prompt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            try {
                System.out.print(prompt);
                String dateString = scanner.nextLine().trim();
                return LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                System.out.println(RED + "Invalid date format! Please use yyyy-mm-dd format." + RESET);
            }
        }
    }
}