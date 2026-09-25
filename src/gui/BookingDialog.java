package gui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import model.Room;

public class BookingDialog extends JDialog {
    
    private static final long serialVersionUID = 1L;
    private JTextField nameField, contactField, checkInField, checkOutField;
    private JLabel nightsLabel, totalLabel;
    private Room selectedRoom;
    private boolean isConfirmed = false;
    private double calculatedTotal = 0.0;

    public BookingDialog(JFrame parent, Room room) {
        super(parent, "New Booking - Room " + room.getRoomNumber(), true); 
        this.selectedRoom = room;
        
        setSize(550, 480);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));
        
        // 1. Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        JLabel headerLabel = new JLabel("Booking Details");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Main Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(5, 25, 5, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        // Row 1: Room Details
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(new JLabel("Confirm Room: " + room.getRoomType() + " (Room " + room.getRoomNumber() + ") - ₹" + room.getPrice() + "/night"), gbc);

        // Row 2: Name
        gbc.gridwidth = 1; gbc.gridy = 1;
        formPanel.add(new JLabel("Customer Name:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1; formPanel.add(nameField, gbc);

        // Row 3: Contact
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Contact Number:"), gbc);
        contactField = new JTextField(20);
        gbc.gridx = 1; formPanel.add(contactField, gbc);

        // Row 4: Check-in
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Check-in (YYYY-MM-DD):"), gbc);
        checkInField = new JTextField("2026-10-01"); 
        gbc.gridx = 1; formPanel.add(checkInField, gbc);

        // Row 5: Check-out
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Check-out (YYYY-MM-DD):"), gbc);
        checkOutField = new JTextField("2026-10-05");
        gbc.gridx = 1; formPanel.add(checkOutField, gbc);
        
        // Row 6: Calculation Panel
        JPanel calcPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton calcButton = new JButton("Calculate Total");
        nightsLabel = new JLabel("  Nights: 0  |");
        totalLabel = new JLabel("  Total: ₹0.00");
        
        calcButton.addActionListener(e -> calculatePrice());
        
        calcPanel.add(calcButton);
        calcPanel.add(nightsLabel);
        calcPanel.add(totalLabel);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(calcPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // 3. Bottom Buttons
        JPanel bottomPanel = new JPanel();
        JButton confirmButton = new JButton("Confirm Booking");
        confirmButton.addActionListener(e -> {
            isConfirmed = true; 
            dispose(); 
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            isConfirmed = false;
            dispose();
        });
        
        bottomPanel.add(confirmButton);
        bottomPanel.add(cancelButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    // The Math Logic
    private void calculatePrice() {
        try {
            LocalDate inDate = LocalDate.parse(checkInField.getText());
            LocalDate outDate = LocalDate.parse(checkOutField.getText());
            long nights = ChronoUnit.DAYS.between(inDate, outDate);
            
            if (nights > 0) {
                nightsLabel.setText("  Nights: " + nights + "  |");
                calculatedTotal = nights * selectedRoom.getPrice();
                totalLabel.setText("  Total: ₹" + calculatedTotal);
            } else {
                JOptionPane.showMessageDialog(this, "Check-out date must be after Check-in date.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter dates in exact YYYY-MM-DD format.");
        }
    }
    
    // Getters for the DAO to use later
    public boolean isConfirmed() { return isConfirmed; }
    public String getCustomerName() { return nameField.getText(); }
    public String getContactNumber() { return contactField.getText(); }
    public String getCheckIn() { return checkInField.getText(); }
    public String getCheckOut() { return checkOutField.getText(); }
    public double getTotalPrice() { return calculatedTotal; }
}