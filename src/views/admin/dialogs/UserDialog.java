package views.admin.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.util.List;

import entities.Booking;
import entities.Seat;
import entities.User;

public class UserDialog extends JDialog {
    private User user;

    public UserDialog(JFrame parent, User user) {
        super(parent, "Chi tiết người dùng", true);
        this.user = user;

        initUI();
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        initLabels();
        initBottomComponents();
    }

    private void initLabels() {
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {
            "ID:", 
            "Họ",
            "Tên:", 
            "Tên đăng nhập:",
            "Email:",
            "Ngày sinh:",
        };
        String[] values = {
            user.getId().toString(),
            user.getFirstname(),
            user.getLastname(),
            user.getUsername(),
            user.getEmail(),
            user.getBirthday().toString()
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            infoPanel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            JLabel valueLabel = new JLabel(values[i]);
            valueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            infoPanel.add(valueLabel, gbc);
        }

        JButton bookedTicketsButton = new JButton("Xem lịch sử đặt vé");
        bookedTicketsButton.addActionListener(e -> createBookingDialog());
        gbc.gridx = 0;
        gbc.gridy = labels.length;

        infoPanel.add(bookedTicketsButton, gbc);

        add(infoPanel, BorderLayout.CENTER);
    }

    private void initBottomComponents() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createBookingDialog() {
        JDialog bookingDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Giờ chiếu của phim", true);
        bookingDialog.setLayout(new BorderLayout());
        bookingDialog.setSize(1200, 600);
        bookingDialog.setLocationRelativeTo(this);

        // Tiêu đề
        JLabel headerLabel = new JLabel("Danh sách đặt chỗ", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        bookingDialog.add(headerLabel, BorderLayout.NORTH);

        // Tạo dữ liệu bảng
        List<Booking> bookings = user.getBookings();
        String[] columnNames = {"Ngày đặt", "Giờ đặt", "Giá", "Giờ chiếu", "Ghế"};
        Object[][] data = new Object[bookings.size()][columnNames.length];
    
        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            data[i][0] = b.getBookingDate();
            data[i][1] = b.getBookingTime();
            data[i][2] = b.getBookingPrice();
            data[i][3] = b.getShowtime() != null ? b.getShowtime().toString() : "Không có";
            data[i][4] = createSeatString(b.getSeats());
        }

        JTable bookingTable = new JTable(data, columnNames);
        bookingTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        bookingTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        bookingDialog.add(scrollPane, BorderLayout.CENTER);

        // Nút đóng
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> bookingDialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        bookingDialog.add(buttonPanel, BorderLayout.SOUTH);

        bookingDialog.setVisible(true);
    }

    private String createSeatString(List<Seat> seats) {
        String seatString = "";
        for (Seat seat : seats)
            seatString += (seat.getRow() + 'A') + seat.getColumn() + " ";

        return seatString;
    }
}
