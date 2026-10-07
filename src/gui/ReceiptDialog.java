package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.print.PrinterException;
import model.BookedRoom;
import util.Billing;

// Shows the bill for a stay (used at checkout and from History) with a Print button.
public class ReceiptDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    public ReceiptDialog(Window parent, BookedRoom stay) {
        super(parent, "Receipt - Reservation #" + stay.getReservationId(), ModalityType.APPLICATION_MODAL);
        setSize(480, 470);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JTextArea receipt = new JTextArea(buildReceipt(stay));
        receipt.setEditable(false);
        receipt.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        receipt.setBorder(new EmptyBorder(16, 18, 16, 18));
        add(new JScrollPane(receipt), BorderLayout.CENTER);

        JButton print = UITheme.button("Print", UITheme.TEAL);
        print.addActionListener(e -> {
            try {
                receipt.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Could not print: " + ex.getMessage());
            }
        });
        JButton close = UITheme.button("Close", UITheme.NAVY_LIGHT);
        close.addActionListener(e -> dispose());

        JPanel buttons = new JPanel();
        buttons.setBorder(new EmptyBorder(8, 0, 12, 0));
        buttons.add(print);
        buttons.add(close);
        add(buttons, BorderLayout.SOUTH);
    }

    private static String buildReceipt(BookedRoom s) {
        String line = "-".repeat(46) + "\n";
        int gstPercent = Billing.gstPercent(s.getRoomPrice());
        return "                 STAYEASE HOTEL\n"
            + "                  Guest Receipt\n"
            + line
            + String.format("Reservation : #%d%n", s.getReservationId())
            + String.format("Guest(s)    : %s%n", s.getCustomerName())
            + String.format("Contact     : %s%n", s.getContact())
            + String.format("Room        : %s (%s)%n", s.getRoomNumber(), s.getRoomType())
            + String.format("Check-in    : %s%n", s.getCheckIn())
            + String.format("Check-out   : %s%n", s.getCheckOut())
            + line
            + String.format("%-22s %22s%n", s.getNights() + " night(s) x " + Billing.money(s.getRoomPrice()),
                            Billing.money(s.getSubtotal()))
            + String.format("%-22s %22s%n", "GST (" + gstPercent + "%)", Billing.money(s.getGst()))
            + line
            + String.format("%-22s %22s%n", "TOTAL", Billing.money(s.getTotal()))
            + line
            + "\n          Thank you for staying with us!\n";
    }
}
