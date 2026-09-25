package model;

import java.sql.Date;

public class BookedRoom {
    private String roomNumber;
    private String roomType;
    private String customerName;
    private String contact;
    private Date checkIn;
    private Date checkOut;

    public BookedRoom(String roomNumber, String roomType, String customerName, String contact, Date checkIn, Date checkOut) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.customerName = customerName;
        this.contact = contact;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public String getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public String getCustomerName() { return customerName; }
    public String getContact() { return contact; }
    public Date getCheckIn() { return checkIn; }
    public Date getCheckOut() { return checkOut; }
}