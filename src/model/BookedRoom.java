package model;

import java.time.LocalDate;
import util.Billing;

// One reservation together with the room it is for.
public class BookedRoom {
    private final long reservationId;
    private final String roomNumber;
    private final String roomType;
    private final double roomPrice;
    private final String customerName;
    private final String contact;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final double subtotal;
    private final double total;
    private final String status;   // BOOKED, CHECKED_OUT or CANCELLED

    public BookedRoom(long reservationId, String roomNumber, String roomType, double roomPrice,
                      String customerName, String contact, LocalDate checkIn, LocalDate checkOut,
                      double subtotal, double total, String status) {
        this.reservationId = reservationId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.customerName = customerName;
        this.contact = contact;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.subtotal = subtotal;
        this.total = total;
        this.status = status;
    }

    public long getReservationId() { return reservationId; }
    public String getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public double getRoomPrice() { return roomPrice; }
    public String getCustomerName() { return customerName; }
    public String getContact() { return contact; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public double getSubtotal() { return subtotal; }
    public double getTotal() { return total; }
    public double getGst() { return total - subtotal; }
    public long getNights() { return Billing.nights(checkIn, checkOut); }
    public String getStatus() { return status; }
    public boolean isActive() { return "BOOKED".equals(status); }

    // Friendly text for tables: "Booked", "Checked out", "Cancelled"
    public String getStatusLabel() {
        if ("CHECKED_OUT".equals(status)) return "Checked out";
        if ("CANCELLED".equals(status)) return "Cancelled";
        return "Booked";
    }
}
