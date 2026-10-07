package model;

public class User {
    private final long id;
    private final String username;
    private final String fullName;
    private final String phone;
    private final String email;
    private final String role;

    public User(long id, String username, String fullName, String phone, String email, String role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.role = role;
    }

    public long getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isAdmin() { return "ADMIN".equals(role); }
}
