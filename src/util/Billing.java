package util;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

// All money maths lives here so the booking, edit and receipt screens always agree.
public final class Billing {

    private static final NumberFormat RUPEES = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"));

    private Billing() { }

    public static long nights(LocalDate checkIn, LocalDate checkOut) {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    // GST on hotel rooms in India: 5% when the tariff is up to Rs 7,500 a night, 18% above that.
    // Change these numbers here if the rates change.
    public static double gstRate(double nightlyPrice) {
        return nightlyPrice <= 7500 ? 0.05 : 0.18;
    }

    public static double subtotal(double nightlyPrice, long nights) {
        return round(nightlyPrice * nights);
    }

    public static double gst(double nightlyPrice, long nights) {
        return round(subtotal(nightlyPrice, nights) * gstRate(nightlyPrice));
    }

    public static double total(double nightlyPrice, long nights) {
        return round(subtotal(nightlyPrice, nights) + gst(nightlyPrice, nights));
    }

    // 3500.0 -> "₹3,500.00"
    public static String money(double amount) {
        return RUPEES.format(amount);
    }

    public static int gstPercent(double nightlyPrice) {
        return (int) Math.round(gstRate(nightlyPrice) * 100);
    }

    // One-line price breakdown shown live in the booking and edit dialogs.
    public static String summary(double nightlyPrice, LocalDate checkIn, LocalDate checkOut) {
        long nights = nights(checkIn, checkOut);
        if (nights <= 0) {
            return "Pick a check-out date after the check-in date";
        }
        return nights + (nights == 1 ? " night" : " nights")
            + "  •  " + money(subtotal(nightlyPrice, nights))
            + " + GST " + gstPercent(nightlyPrice) + "% " + money(gst(nightlyPrice, nights))
            + "  =  " + money(total(nightlyPrice, nights));
    }

    private static double round(double value) {
        return Math.round(value * 100) / 100.0;
    }
}
