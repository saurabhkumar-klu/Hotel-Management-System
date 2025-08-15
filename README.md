# Hotel-Management-System

A simple Java-based console application for managing hotel rooms and bookings, using MySQL as the backend database.\r

## Features\r

- View all rooms and their statuses\r
- View available rooms\r
- Book rooms for guests\r
- Check-in and check-out guests\r
- View all bookings\r
- Manage rooms (add new rooms, update room status)\r
- Color-coded console UI for better readability\r

## Requirements\r

- Java 8 or higher\r
- MySQL Server\r
- MySQL JDBC Driver (`mysql-connector-java`)\r
- Internet connection (for JDBC timezone sync)\r

## Setup Instructions\r

1. **Clone or Download the Repository**\r
   ```\r
   git clone <repo-url>\r
   ```\r

2. **Configure MySQL Database**\r
   - Create a database named `hotel_db`.\r
   - Update the database credentials in `HotelManagementSystem.java` if needed:\r
     ```\r
     private static final String JDBC_URL = "jdbc:mysql://localhost:3306/hotel_db?useSSL=false&serverTimezone=UTC";\r
     private static final String DB_USER = "root";\r
     private static final String DB_PASSWORD = "your_password";\r
     ```\r
   - The application will automatically create required tables and insert sample data on first run.\r

3. **Add MySQL JDBC Driver**\r
   - Download `mysql-connector-java` from [MySQL Downloads](https://dev.mysql.com/downloads/connector/j/).\r
   - Add the JAR to your project's classpath.\r

4. **Compile and Run**\r
   ```\r
   javac -cp .;path\to\mysql-connector-java.jar src\HotelManagementSystem.java\r
   java -cp .;path\to\mysql-connector-java.jar src.HotelManagementSystem\r
   ```\r

## Usage\r

- Follow the on-screen menu to perform hotel management operations.\r
- Input is validated for dates and numbers.\r
- Color codes indicate room and booking statuses.\r

## Notes\r

- Sample rooms are inserted if the database is empty.\r
- All data is stored in MySQL; ensure the server is running before starting the application.\r
- For any issues, check database connectivity and credentials.\r

## License\r

MIT License

