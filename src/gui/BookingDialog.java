package gui;

import dao.ReservationDAO;
import model.Room;
import util.Billing;
import util.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class BookingDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final Room room;
    private final JTextField[] nameFields;  // one field per guest the room can hold
    private final JTextField contactField = new JTextField(20);
    private final JSpinner checkInSpinner;
    private final JSpinner checkOutSpinner;
    private final JLabel priceLabel = new JLabel();
    private final JLabel errorLabel = UITheme.errorLabel();
    private boolean confirmed = false;

    public BookingDialog(JFrame parent, Room room, LocalDate checkIn, LocalDate checkOut) {
        this(parent, room, checkIn, checkOut, "", "");
    }

    public BookingDialog(JFrame parent, Room room, LocalDate checkIn, LocalDate checkOut,
                         String defaultName, String defaultPhone) {
        super(parent, "New Booking - Room " + room.getRoomNumber(), true);
        this.room = room;
        checkInSpinner = UITheme.dateSpinner(checkIn);
        checkOutSpinner = UITheme.dateSpinner(checkOut);

        setLayout(new BorderLayout());

        JLabel header = UITheme.title("Booking details");
        header.setBorder(new EmptyBorder(16, 25, 0, 25));
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(5, 25, 5, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.weightx = 1.0;
        int row = 0;

        JLabel roomLabel = new JLabel(room.toString());
        roomLabel.setForeground(UITheme.muted());
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        form.add(roomLabel, gbc);
        gbc.gridwidth = 1;

        nameFields = new JTextField[room.getMaxGuests()];
        for (int i = 0; i < nameFields.length; i++) {
            nameFields[i] = new JTextField(20);
            if (i == 0) nameFields[i].setText(defaultName == null ? "" : defaultName);
            addRow(form, gbc, row++, i == 0 ? "Guest 1 name (required):" : "Guest " + (i + 1) + " name:", nameFields[i]);
        }
        addRow(form, gbc, row++, "Contact number (10 digits):", contactField);
        contactField.setText(defaultPhone == null ? "" : defaultPhone);
        addRow(form, gbc, row++, "Check-in:", checkInSpinner);
        addRow(form, gbc, row++, "Check-out:", checkOutSpinner);

        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        priceLabel.setForeground(UITheme.TEAL);
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        form.add(priceLabel, gbc);
        gbc.gridy = row;
        form.add(errorLabel, gbc);
        add(form, BorderLayout.CENTER);

        JButton confirmButton = UITheme.button("Confirm Booking", UITheme.TEAL);
        confirmButton.addActionListener(e -> confirmBooking());
        JButton cancelButton = UITheme.button("Cancel", UITheme.NAVY_LIGHT);
        cancelButton.addActionListener(e -> dispose());
        JPanel buttons = new JPanel();
        buttons.setBorder(new EmptyBorder(5, 0, 12, 0));
        buttons.add(confirmButton);
        buttons.add(cancelButton);
        add(buttons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(confirmButton);

        // Price updates straight away whenever a date changes.
        checkInSpinner.addChangeListener(e -> updatePrice());
        checkOutSpinner.addChangeListener(e -> updatePrice());
        updatePrice();
        pack();   // size the window to fit the form (rooms for 3 guests get a taller form)
        setLocationRelativeTo(parent);
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(field, gbc);
    }

    private void updatePrice() {
        priceLabel.setText(Billing.summary(room.getPrice(), getCheckIn(), getCheckOut()));
        errorLabel.setText(" ");
    }

    private void confirmBooking() {
        if (nameFields[0].getText().isBlank()) {
            errorLabel.setText("Enter at least the first guest's name.");
            nameFields[0].requestFocusInWindow();
            return;
        }
        String error = Validator.checkReservation(getCustomerName(), getContactNumber(), getCheckIn(), getCheckOut(), true);
        if (error == null && !new ReservationDAO().isRoomFree(room.getRoomNumber(), getCheckIn(), getCheckOut(), 0)) {
            error = "Room " + room.getRoomNumber() + " is already booked for some of these dates.";
        }
        if (error != null) {
            errorLabel.setText(error);
            return;
        }
        confirmed = true;
        dispose();
    }

    // All filled-in guest names joined into one string, e.g. "Asha & Ravi".
    public String getCustomerName() {
        StringBuilder names = new StringBuilder();
        for (JTextField field : nameFields) {
            String name = field.getText().trim();
            if (!name.isEmpty()) {
                if (names.length() > 0) names.append(" & ");
                names.append(name);
            }
        }
        return names.toString();
    }

    public boolean isConfirmed() { return confirmed; }
    public String getContactNumber() { return contactField.getText().trim(); }
    public LocalDate getCheckIn() { return UITheme.getDate(checkInSpinner); }
    public LocalDate getCheckOut() { return UITheme.getDate(checkOutSpinner); }
    public double getTotal() { return Billing.total(room.getPrice(), Billing.nights(getCheckIn(), getCheckOut())); }
}
