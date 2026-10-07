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
