package gui;

import javax.swing.*;
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
            } else {
                JOptionPane.showMessageDialog(this, "Please select a room from the list first.", "No Room Selected", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        bottomPanel.add(bookButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}