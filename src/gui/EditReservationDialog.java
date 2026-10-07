package gui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import model.BookedRoom;

public class EditReservationDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private final JTextField nameField;
    private final JTextField contactField;
    private final JTextField checkInField;
    private final JTextField checkOutField;
    private final double roomPrice;
    private boolean saved;
    private double total;

    public EditReservationDialog(Window parent, BookedRoom reservation) {
        super(parent, "Modify Reservation #" + reservation.getReservationId(), ModalityType.APPLICATION_MODAL);
        roomPrice = reservation.getRoomPrice();
        setSize(460, 330);
        setLocationRelativeTo(parent);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        form.add(new JLabel("Room:"));
        form.add(new JLabel(reservation.getRoomType() + " - " + reservation.getRoomNumber()));
        form.add(new JLabel("Guest name:"));
        nameField = new JTextField(reservation.getCustomerName());
        form.add(nameField);
        form.add(new JLabel("Contact number:"));
        contactField = new JTextField(reservation.getContact());
        form.add(contactField);
        form.add(new JLabel("Check-in (YYYY-MM-DD):"));
        checkInField = new JTextField(reservation.getCheckIn().toString());
        form.add(checkInField);
        form.add(new JLabel("Check-out (YYYY-MM-DD):"));
        checkOutField = new JTextField(reservation.getCheckOut().toString());
        form.add(checkOutField);
        form.add(new JLabel("Nightly price:"));
        form.add(new JLabel(String.format("%.2f", roomPrice)));
        add(form, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> saveChanges());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        JPanel buttons = new JPanel();
        buttons.add(saveButton);
        buttons.add(cancelButton);
        add(buttons, BorderLayout.SOUTH);
    }

    private void saveChanges() {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        try {
            if (name.isEmpty() || contact.isEmpty()) {
                throw new IllegalArgumentException("Guest name and contact number are required.");
            }
            LocalDate checkIn = LocalDate.parse(checkInField.getText().trim());
            LocalDate checkOut = LocalDate.parse(checkOutField.getText().trim());
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            if (nights <= 0) {
                throw new IllegalArgumentException("Check-out must be after check-in.");
            }
            total = nights * roomPrice;
            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage() == null
                ? "Use dates in YYYY-MM-DD format." : ex.getMessage(),
                "Invalid reservation details", JOptionPane.WARNING_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
    public String getCustomerName() { return nameField.getText().trim(); }
    public String getContactNumber() { return contactField.getText().trim(); }
    public String getCheckIn() { return checkInField.getText().trim(); }
    public String getCheckOut() { return checkOutField.getText().trim(); }
    public double getTotal() { return total; }
}
