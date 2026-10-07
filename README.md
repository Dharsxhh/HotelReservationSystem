# Hotel Reservation System 🏨 (StayEase)

A desktop hotel front-desk application built with **Java Swing** and an **Oracle database** over **JDBC**. Staff can view free rooms, enter guest details, make, search, modify and cancel reservations, check guests out with a printed bill, and browse the full booking history.

## 🚀 Features

* **Role-based login** with a default admin account and customer registration. Customers only see and manage their own reservations.
* **Room availability by date:** pick check-in/check-out dates and a sharing type (single / double / triple). The table lists only the rooms free for those exact dates, so advance bookings work.
* **Booking:** the form shows one guest-name field per bed. Dates are picked with date pickers, and the price (nights × rate + GST) updates live.
* **Validation** with clear messages inside the form:
  * guest name required
  * 10-digit contact number
  * check-out after check-in
  * no past check-in for new bookings
  * at most 30 nights
  * room not already booked
* **Search** while you type, by guest name, contact number, room number or reservation ID.
* **Secure account storage:** customer passwords are salted and SHA-256 hashed; ownership is enforced in JDBC queries.
* **Modify** an active booking: guest name, contact, dates or room. The price is recalculated.
* **Cancel** an active booking. It stays in history marked *Cancelled*.
* **Check out & bill:** checking a guest out shows a receipt (room charge, GST, total) that can be printed.
* **History:** every reservation with its status (Booked / Checked out / Cancelled). Open any receipt from here. All tables sort by clicking a column header.
* **Safe transactions:**
  * Bookings and edits run in a JDBC transaction.
  * The room row is locked (`SELECT … FOR UPDATE`), so two desks can't book the same room for overlapping dates.

## 🛠️ Tech Stack

* **GUI:** Java Swing (`JFrame`, `JDialog`, `JTable`, `JSpinner` date pickers)
* **Backend:** Java with the DAO (Data Access Object) pattern
* **Database:** Oracle Database XE
* **Connectivity:** JDBC (`lib/ojdbc17.jar`)

## 📁 Project Structure

```
src/
  model/   Room, BookedRoom              – plain data classes
  dao/     DatabaseHelper, RoomDAO,      – all SQL / JDBC code
           ReservationDAO
  util/    Billing (prices + GST),       – shared rules used by every screen
           Validator (input checks)
  gui/     LoginFrame (start here), MainFrame (admin dashboard), CustomerDashboard,
           CustomerBookingsDialog, BookingDialog,
           EditReservationDialog, ReservationSearchDialog, ViewBookingsDialog,
           HistoryDialog, ReceiptDialog, ReservationTable, UITheme
database_setup.sql      – creates the tables and 15 sample rooms
db.properties.example   – template for your database login
```

## 🗄️ Database

1. `rooms`: room number, type, nightly price (INR), max guests.
2. `reservations`: guest name(s), contact, check-in/out dates, subtotal, total including GST, and `status` (`BOOKED`, `CHECKED_OUT` or `CANCELLED`).
3. `reserved_rooms`: links each reservation to its room.

A room is *available* for some dates when it has no `BOOKED` reservation overlapping them.

GST is 5% for rooms up to ₹7,500/night and 18% above that. The rates are set in `util/Billing.java`.

## ⚙️ Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/Dharsxhh/HotelReservationSystem.git
   ```
2. **Create the tables.** Open `database_setup.sql` in SQL Developer and press **F5** (Run Script), connected as the user the app will use.
   > Re-running the script deletes existing reservations.
3. **If upgrading an older database**, run `database_migration.sql` instead of dropping your existing data. It adds reservation status, user accounts and ownership columns without deleting reservations.
4. **Set your database login.** Copy `db.properties.example` to `db.properties` in the project folder and fill in your URL, user and password. `db.properties` is git-ignored, so passwords never get committed.
5. **Drivers:** `lib/ojdbc17.jar` and `lib/flatlaf-3.5.4.jar` are already on the classpath (`.classpath`).
6. **Run** `gui.LoginFrame`. Sample login: `admin` / `admin123`.

### Running in VS Code
Install the **Extension Pack for Java**, then use **File → Open Folder** on the project. Open `src/gui/LoginFrame.java` and click **Run**. Run from the project folder so `db.properties` is found.

### Running from a terminal (Windows)
```bat
javac -encoding UTF-8 -cp "lib\ojdbc17.jar;lib\flatlaf-3.5.4.jar" -d out src\model\*.java src\util\*.java src\dao\*.java src\gui\*.java
java -cp "out;lib\ojdbc17.jar;lib\flatlaf-3.5.4.jar" gui.LoginFrame
```
On macOS/Linux, use `/` in the paths and `:` instead of `;` in the classpath.

To test only the database connection, run `dao.DatabaseHelper`.

## Roles

* **Admin:** `admin` / `admin123` — full dashboard, walk-in bookings, checkout, history, customer accounts and room management.
* **Customer:** create an account from the login screen — view rooms, book, search, modify, cancel and view receipts for their own reservations.

## Screenshots

`![Login screen](docs/screenshots/login.png)`

`![Admin dashboard](docs/screenshots/admin-dashboard.png)`

`![Customer home](docs/screenshots/customer-home.png)`

## Requirement map

| Requirement | Implementation |
|---|---|
| View available rooms | `RoomDAO.getAvailableRooms`, `MainFrame`, `CustomerDashboard` |
| Enter guest details | `BookingDialog`, `Validator` |
| Make a reservation | `ReservationDAO.createReservation` |
| Search reservations | `ReservationDAO.searchReservations`, `CustomerBookingsDialog` |
| Modify reservations | `ReservationDAO.updateReservation`, `updateReservationForUser` |
| Cancel reservations | `ReservationDAO.cancelReservation`, `cancelReservationForUser` |
| GUI and event handling | Swing frames/dialogs, listeners and table actions in `gui` |
| Input validation | `util.Validator` and live `LoginFrame` registration validation |
| JDBC persistence | `DatabaseHelper`, all SQL in `dao` with `PreparedStatement` |
| Role and ownership security | `UserDAO`, `Session`, user-scoped reservation DAO methods |
