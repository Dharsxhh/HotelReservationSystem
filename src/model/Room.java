package model;

public class Room {
    
    private String roomNumber;
    private String roomType;
    private double price;
    private boolean isAvailable;
    private int maxGuests; // NEW FIELD

    // Updated Constructor
    public Room(String roomNumber, String roomType, double price, boolean isAvailable, int maxGuests) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.isAvailable = isAvailable;
        this.maxGuests = maxGuests;
    }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean isAvailable) { this.isAvailable = isAvailable; }

    // New Getter and Setter for Capacity
    public int getMaxGuests() { return maxGuests; }
    public void setMaxGuests(int maxGuests) { this.maxGuests = maxGuests; }

    // This updates your MainFrame list automatically!
    @Override
    public String toString() {
        String sharingType = "";
        if (maxGuests == 1) sharingType = "Single Sharing";
        else if (maxGuests == 2) sharingType = "Double Sharing";
        else if (maxGuests == 3) sharingType = "Triple Sharing";
        else sharingType = maxGuests + " Guests";

        return roomType + " (Room " + roomNumber + ") - " + sharingType + " - ₹" + price;
    }
}