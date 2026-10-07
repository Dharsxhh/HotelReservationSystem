package util;

import java.time.LocalDate;

// Shared input checks for the booking and edit dialogs.
// Each method returns an error message, or null when the input is fine.
public final class Validator {

    public static final int MAX_NIGHTS = 30;

    private Validator() { }

    public static String checkGuestName(String name) {
        if (name == null || name.isBlank()) return "Enter the guest name.";
        if (name.length() > 200) return "Guest name is too long.";
        // \p{L} = letters in any language, \p{M} = accent/vowel marks (needed for Tamil, Hindi, etc.)
        if (!name.matches("[\\p{L}\\p{M} .'&]+")) return "Guest names can only contain letters, spaces, dots and apostrophes.";
        return null;
    }

    public static String checkContact(String contact) {
        if (contact == null || !contact.matches("\\d{10}")) return "Contact number must be exactly 10 digits.";
        return null;
    }

    public static String checkUsername(String username) {
        if (username == null || !username.matches("[A-Za-z0-9_]{4,20}")) return "Username must be 4-20 letters, digits or _";
        return null;
    }

    public static String checkPassword(String password) {
        if (password == null || !password.matches("(?=.*[A-Za-z])(?=.*\\d).{6,}")) return "Password needs 6+ characters, a letter and a digit";
        return null;
    }

    public static String checkEmail(String email) {
        if (email == null || email.isBlank()) return null;
        return email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") ? null : "Enter a valid email address";
    }

    // newBooking = true also stops check-in dates in the past.
    // (When editing, a guest who already arrived keeps their past check-in date.)
    public static String checkDates(LocalDate checkIn, LocalDate checkOut, boolean newBooking) {
        if (!checkOut.isAfter(checkIn)) return "Check-out must be after check-in.";
        if (newBooking && checkIn.isBefore(LocalDate.now())) return "Check-in cannot be in the past.";
        if (Billing.nights(checkIn, checkOut) > MAX_NIGHTS) return "A booking can be at most " + MAX_NIGHTS + " nights.";
        return null;
    }

    // Runs all checks in order and returns the first problem found.
    public static String checkReservation(String name, String contact, LocalDate checkIn, LocalDate checkOut,
                                          boolean newBooking) {
        String error = checkGuestName(name);
        if (error == null) error = checkContact(contact);
        if (error == null) error = checkDates(checkIn, checkOut, newBooking);
        return error;
    }
}
