package gui;

import javax.swing.*;
import dao.ReservationDAO;
import java.awt.*;
import java.util.List;
import dao.RoomDAO;
import model.Room;

public class MainFrame extends JFrame {
    
    private static final long serialVersionUID = 1L;
    private RoomDAO roomDAO;

    public MainFrame() {
        roomDAO = new RoomDAO();
        
        setTitle("Hotel Reservation System - Front Desk");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout(10, 10)); 
        
        JLabel headerLabel = new JLabel("Available Rooms", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(headerLabel, BorderLayout.NORTH);
        
        DefaultListModel<Room> listModel = new DefaultListModel<>();
        List<Room> availableRooms = roomDAO.getAvailableRooms();
        for (Room room : availableRooms) {
            listModel.addElement(room);
        }
        
        JList<Room> roomList = new JList<>(listModel);
        roomList.setFont(new Font("Arial", Font.PLAIN, 16));
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(roomList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        add(scrollPane, BorderLayout.CENTER);
        
        JButton bookButton = new JButton("Book Selected Room");
        bookButton.setFont(new Font("Arial", Font.BOLD, 14));
        bookButton.setFocusPainted(false);
        
        // THIS IS THE UPDATED PART: It now opens the BookingDialog
        bookButton.addActionListener(e -> {
            Room selectedRoom = roomList.getSelectedValue();
            if (selectedRoom != null) {
                BookingDialog dialog = new BookingDialog(this, selectedRoom);
                dialog.setVisible(true);
                
                // NEW CODE: Check if they clicked Confirm and process the booking
                if (dialog.isConfirmed()) {
                    ReservationDAO resDAO = new ReservationDAO();
                    boolean success = resDAO.createReservation(
                        dialog.getCustomerName(),
                        dialog.getContactNumber(),
                        dialog.getCheckIn(),
                        dialog.getCheckOut(),
                        dialog.getTotalPrice(),
                        selectedRoom.getRoomNumber()
                    );
                    
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Booking Successful! Room " + selectedRoom.getRoomNumber() + " is now reserved.");
                        
                        // Refresh the visual list so the booked room disappears!
                        listModel.clear();
                        for (Room room : roomDAO.getAvailableRooms()) {
                            listModel.addElement(room);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Database Error: Could not complete booking.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a room from the list first.", "No Room Selected", JOptionPane.WARNING_MESSAGE);
            }
        });
        
     // Create the new View button
        JButton viewBookingsButton = new JButton("View Current Bookings");
        viewBookingsButton.setFont(new Font("Arial", Font.BOLD, 14));
        viewBookingsButton.setFocusPainted(false);
        viewBookingsButton.addActionListener(e -> {
            ViewBookingsDialog viewDialog = new ViewBookingsDialog(this);
            viewDialog.setVisible(true); // Program pauses here while dialog is open
            
            // NEW CODE: As soon as the dialog closes, refresh the main available rooms list
            listModel.clear();
            for (Room room : roomDAO.getAvailableRooms()) {
                listModel.addElement(room);
            }
        });
     // Create the new History button
        JButton historyButton = new JButton("Booking History");
        historyButton.setFont(new Font("Arial", Font.BOLD, 14));
        historyButton.setFocusPainted(false);
        historyButton.addActionListener(e -> {
            HistoryDialog historyDialog = new HistoryDialog(this);
            historyDialog.setVisible(true);
        });

        // Add ALL THREE buttons to the bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        bottomPanel.add(bookButton);
        bottomPanel.add(Box.createHorizontalStrut(15)); 
        bottomPanel.add(viewBookingsButton);
        bottomPanel.add(Box.createHorizontalStrut(15));
        bottomPanel.add(historyButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}