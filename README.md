# 🏨 Hotel Management System

A **Java-based console application** for managing hotel operations efficiently—covering room bookings, guest check-ins/check-outs, and room availability. Uses **MySQL** for data storage with a **color-coded console interface**.

---

## 🚀 Features

* **Room Management**

  * View all rooms and their statuses.
  * Check room availability.
  * Add or update room details.
  * Color-coded status indicators.

* **Booking Management**

  * Book rooms with date validation.
  * Check-in and check-out guests.
  * View all bookings with guest and room details.

* **Console Interface**

  * Intuitive menu-driven UI.
  * Color-coded outputs for readability.

* **Database Integration**

  * Data stored in MySQL.
  * Automatic table creation and sample data insertion on first run.

---

## 🛠️ Technology Stack

* Java 8 or higher
* MySQL Server
* MySQL JDBC Driver (`mysql-connector-java`)
* Any Java IDE (IntelliJ IDEA, Eclipse, VS Code)
* Command Prompt / Terminal

---

## ⚙️ Setup Instructions

1. **Clone the Repository**

   ```bash
   git clone https://github.com/saurabhkumar-klu/Hotel-Management-System.git
   ```

2. **Configure MySQL Database**

   * Create a database named `hotel_db`.
   * Update credentials in `HotelManagementSystem.java`:

     ```java
     private static final String JDBC_URL = "jdbc:mysql://localhost:3306/hotel_db?useSSL=false&serverTimezone=UTC";
     private static final String DB_USER = "root";
     private static final String DB_PASSWORD = "your_password";
     ```
   * Tables and sample data are created automatically on first run.

3. **Add MySQL JDBC Driver**

   * Download from [MySQL Downloads](https://dev.mysql.com/downloads/connector/j/).
   * Add the JAR to your project’s classpath.

4. **Compile and Run**

   ```bash
   javac -cp .;path\to\mysql-connector-java.jar src\HotelManagementSystem.java
   java -cp .;path\to\mysql-connector-java.jar src.HotelManagementSystem
   ```

---

## 🧭 Usage

* Follow the on-screen menu.
* Input validated for dates and numbers.
* Color-coded outputs:

  * **Green:** Available rooms
  * **Red:** Occupied/Booked rooms
  * **Yellow:** Under maintenance / Pending checkout

---

## 📄 Notes

* Ensure the **MySQL server is running** before starting the app.
* Sample data is inserted if the database is empty.
* Verify credentials and server timezone if you face connectivity issues.

---

## 🚀 Future Enhancements

* GUI using JavaFX or Swing
* Multi-hotel support
* Advanced booking analytics and reports
* Payment gateway integration

---

## 📄 License

MIT License

