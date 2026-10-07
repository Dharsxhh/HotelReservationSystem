package model;

public class CustomerSummary {
    private final long id;
    private final String name;
    private final String username;
    private final String phone;
    private final String email;
    private final java.sql.Date joined;
    private final int bookings;

    public CustomerSummary(long id, String name, String username, String phone, String email, java.sql.Date joined, int bookings) {
        this.id = id; this.name = name; this.username = username; this.phone = phone; this.email = email; this.joined = joined; this.bookings = bookings;
    }
    public long getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public java.sql.Date getJoined() { return joined; }
    public int getBookings() { return bookings; }
}
