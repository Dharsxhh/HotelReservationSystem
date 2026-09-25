# Hotel Reservation System 🏨

A full-stack, desktop-based Hotel Management Application built with **Java (Swing)** and an **Oracle SQL Database**. This system provides front-desk staff with a seamless interface to manage room inventory, process new bookings, check guests out, and view historical reservation ledgers.

## 🚀 Features

* **Real-Time Inventory Dashboard:** Displays available rooms dynamically categorized by capacity (Single, Double, and Triple Sharing).
* **Dynamic Booking Engine:** Calculates total stays based on check-in/check-out dates and dynamically generates guest input fields based on room capacity.
* **Transactional Database Integrity:** Utilizes JDBC with disabled auto-commit to ensure safe, multi-table SQL transactions when creating bookings.
* **Checkout & Availability Management:** Allows staff to check guests out, instantly freeing up the room for new bookings while preserving the guest's data.
* **Historical Ledger:** A complete, queryable history of all past and present reservations using multi-table SQL `JOIN` statements.

## 🛠️ Tech Stack

* **Frontend:** Java Swing (AWT, JFrame, JDialog, JTable)
* **Backend:** Java (JDK) implementing the DAO (Data Access Object) design pattern
* **Database:** Oracle Database (SQL)
* **Connectivity:** JDBC (`ojdbc` driver)
* **Version Control:** Git / GitHub

## 🗄️ Database Architecture

The system utilizes a Header-Detail relational database schema to ensure data normalization and integrity:
1. `rooms`: Manages room inventory, pricing, availability status, and maximum guest capacity.
2. `reservations`: Stores primary guest details, date math, and payment totals.
3. `reserved_rooms`: A junction table linking specific reservations to specific rooms.

## ⚙️ Setup & Installation

1. Clone the repository:
   ```bash
   git clone [https://github.com/Dharsxhh/HotelReservationSystem.git](https://github.com/Dharsxhh/HotelReservationSystem.git)
