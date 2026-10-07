package model;

import util.Billing;

public class Room {

    private final String roomNumber;
    private final String roomType;
    private final double price;
    private final int maxGuests;

    public Room(String roomNumber, String roomType, double price, int maxGuests) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.maxGuests = maxGuests;
    }

    public String getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public double getPrice() { return price; }
    public int getMaxGuests() { return maxGuests; }

    public String getSharingLabel() {
        switch (maxGuests) {
            case 1: return "Single sharing";
            case 2: return "Double sharing";
            case 3: return "Triple sharing";
            default: return maxGuests + " guests";
        }
    }

    // Shown in drop-downs, e.g. "Room 201 - Deluxe (Double sharing) - ₹3,500.00/night"
    @Override
    public String toString() {
        return "Room " + roomNumber + " - " + roomType + " (" + getSharingLabel() + ") - "
            + Billing.money(price) + "/night";
    }
}
