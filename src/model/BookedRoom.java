package model;

import java.sql.Date;

public class BookedRoom {
    private long reservationId;
    private String roomNumber;
    private String roomType;
    private String customerName;
    private String contact;
    private Date checkIn;
    private Date checkOut;
    private double roomPrice;

    public BookedRoom(String roomNumber, String roomType, String customerName, String contact, Date checkIn, Date checkOut) {
        this(0, roomNumber, roomType, customerName, contact, checkIn, checkOut);
    }

    public BookedRoom(long reservationId, String roomNumber, String roomType, String customerName, String contact, Date checkIn, Date checkOut) {
        this(reservationId, roomNumber, roomType, customerName, contact, checkIn, checkOut, 0.0);
    }

    public BookedRoom(long reservationId, String roomNumber, String roomType, String customerName, String contact, Date checkIn, Date checkOut, double roomPrice) {
        this.reservationId = reservationId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.customerName = customerName;
        this.contact = contact;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.roomPrice = roomPrice;
    }

    public long getReservationId() { return reservationId; }
    public String getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public String getCustomerName() { return customerName; }
    public String getContact() { return contact; }
    public Date getCheckIn() { return checkIn; }
    public Date getCheckOut() { return checkOut; }
    public double getRoomPrice() { return roomPrice; }
}
