package gui;

import dao.ReservationDAO;
import dao.RoomDAO;
import model.BookedRoom;
import model.Room;
import util.Billing;
import util.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

// Lets staff change the guest details, dates or room of an active booking.
public class EditReservationDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private final BookedRoom reservation;
    private final JTextField nameField;
    private final JTextField contactField;
    private final JComboBox<Room> roomBox = new JComboBox<>();
    private final JSpinner checkInSpinner;
    private final JSpinner checkOutSpinner;
    private final JLabel priceLabel = new JLabel();
    private final JLabel errorLabel = UITheme.errorLabel();
    private boolean saved;
    private final boolean customerMode;

    public EditReservationDialog(Window parent, BookedRoom reservation) {
        this(parent, reservation, false);
    }

    public EditReservationDialog(Window parent, BookedRoom reservation, boolean customerMode) {
        super(parent, "Modify Reservation #" + reservation.getReservationId(), ModalityType.APPLICATION_MODAL);
        this.reservation = reservation;
        this.customerMode = customerMode;
        setSize(620, 430);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        nameField = new JTextField(reservation.getCustomerName());
        contactField = new JTextField(reservation.getContact());
        checkInSpinner = UITheme.dateSpinner(reservation.getCheckIn());
        checkOutSpinner = UITheme.dateSpinner(reservation.getCheckOut());
        for (Room room : new RoomDAO().getAllRooms()) {
            roomBox.addItem(room);
            if (room.getRoomNumber().equals(reservation.getRoomNumber())) roomBox.setSelectedItem(room);
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
        form.setBorder(new EmptyBorder(18, 20, 6, 20));
        form.add(new JLabel("Guest name(s):"));
        form.add(nameField);
        form.add(new JLabel("Contact number (10 digits):"));
        form.add(contactField);
        form.add(new JLabel("Room:"));
        form.add(roomBox);
        form.add(new JLabel("Check-in:"));
        form.add(checkInSpinner);
        form.add(new JLabel("Check-out:"));
        form.add(checkOutSpinner);

        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        priceLabel.setForeground(UITheme.TEAL);
        JPanel messages = new JPanel(new GridLayout(2, 1, 0, 4));
        messages.setBorder(new EmptyBorder(4, 20, 0, 20));
        messages.add(priceLabel);
        messages.add(errorLabel);

        JPanel center = new JPanel(new BorderLayout());
        center.add(form, BorderLayout.CENTER);
        center.add(messages, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        JButton saveButton = UITheme.button("Save Changes", UITheme.TEAL);
        saveButton.addActionListener(e -> saveChanges());
        JButton cancelButton = UITheme.button("Cancel", UITheme.NAVY_LIGHT);
        cancelButton.addActionListener(e -> dispose());
        JPanel buttons = new JPanel();
        buttons.setBorder(new EmptyBorder(8, 0, 12, 0));
        buttons.add(saveButton);
        buttons.add(cancelButton);
        add(buttons, BorderLayout.SOUTH);

        // Price updates straight away when the room or dates change.
        roomBox.addActionListener(e -> updatePrice());
        checkInSpinner.addChangeListener(e -> updatePrice());
        checkOutSpinner.addChangeListener(e -> updatePrice());
        updatePrice();
    }

    private void updatePrice() {
        if (getRoom() == null) return;   // room list could not be loaded
        priceLabel.setText(Billing.summary(getRoom().getPrice(), getCheckIn(), getCheckOut()));
        errorLabel.setText(" ");
    }

    private void saveChanges() {
        if (getRoom() == null) {
            errorLabel.setText("Could not load the room list - check the database connection.");
            return;
        }
        String error = Validator.checkReservation(getCustomerName(), getContactNumber(), getCheckIn(), getCheckOut(), customerMode);
        if (error == null && !new ReservationDAO().isRoomFree(getRoom().getRoomNumber(), getCheckIn(), getCheckOut(),
                                                               reservation.getReservationId())) {
            error = "Room " + getRoom().getRoomNumber() + " is already booked for some of these dates.";
        }
        if (error != null) {
            errorLabel.setText(error);
            return;
        }
        saved = true;
        dispose();
    }

    public boolean isSaved() { return saved; }
    public String getCustomerName() { return nameField.getText().trim(); }
    public String getContactNumber() { return contactField.getText().trim(); }
    public Room getRoom() { return (Room) roomBox.getSelectedItem(); }
    public LocalDate getCheckIn() { return UITheme.getDate(checkInSpinner); }
    public LocalDate getCheckOut() { return UITheme.getDate(checkOutSpinner); }
}
