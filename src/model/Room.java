package model;

public class Room {
    
    private String roomNumber;
    private String roomType;
    private double price;
    private boolean isAvailable;

    // Constructor to easily create a Room object
    public Room(String roomNumber, String roomType, double price, boolean isAvailable) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters so other parts of the app can read/change these values
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean isAvailable) { this.isAvailable = isAvailable; }

    // This determines how the room looks if we print it or put it in a GUI list/dropdown
    @Override
    public String toString() {
        return roomType + " (Room " + roomNumber + ") - ₹" + price;
    }
}