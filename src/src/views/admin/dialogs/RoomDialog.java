package views.admin.dialogs;

import javax.swing.*;
import java.awt.*;

import entities.Room;
import views.admin.panels.RoomManagementPanel;

public class RoomDialog extends JDialog {
    private Room room;
    private RoomManagementPanel parentPanel;
    private JTextField nameField, totalSeatsField, seatsPerRowField;
    private final boolean isEditMode;
    
    public RoomDialog(JFrame parent, Room room, RoomManagementPanel parentPanel) {
        super(parent, room != null ? "Chỉnh sửa phòng chiếu" : "Thêm phòng chiếu", true);
        this.room = room;
        this.parentPanel = parentPanel;
        this.isEditMode = room != null;

        initUI();
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        initFields(formPanel, gbc);
        initEditionModeComponents(formPanel, gbc, 3);
        add(formPanel, BorderLayout.CENTER);

        initBottomComponents();
    }

    public void initFields(JPanel formPanel, GridBagConstraints gbc) {
        Dimension fieldSize = new Dimension(300, 25);

        nameField = new JTextField(room != null ? room.getName() : "");
        nameField.setPreferredSize(fieldSize);
        totalSeatsField = new JTextField(room != null ? String.valueOf(room.getTotalSeats()) : "");
        totalSeatsField.setPreferredSize(fieldSize);
        seatsPerRowField = new JTextField(room != null ? String.valueOf(room.getSeatsPerRow()) : "");
        seatsPerRowField.setPreferredSize(fieldSize);

        String[] labels = { "Tên phòng:", "Tổng số ghế:", "Số ghế mỗi hàng:" };
        JTextField[] fields = { nameField, totalSeatsField, seatsPerRowField };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            formPanel.add(fields[i], gbc);
        }
    }

    public void initBottomComponents() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        saveButton.addActionListener(e -> saveRoom());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initEditionModeComponents(JPanel formPanel, GridBagConstraints gbc, int y) {
        if (this.isEditMode) {
            // Khoi tao nut xem anh
            JButton seatButton = new JButton("Xem chỗ ngồi");
            seatButton.addActionListener(e -> createSeatDialog());
            gbc.gridx = 0;
            gbc.gridy = y;
            formPanel.add(seatButton, gbc);

            // Khoi tao nut xem anh
            JButton showtimeButton = new JButton("Xem giờ chiếu");
            showtimeButton.addActionListener(e -> createShowtimeDialog());
            gbc.gridx = 0;
            gbc.gridy = y + 1;
            formPanel.add(showtimeButton, gbc);
        }
    }

    private void createSeatDialog() {
        JDialog seatDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Sơ đồ chỗ ngồi", true);
        seatDialog.setLayout(new BorderLayout());
        seatDialog.setSize(800, 600);
        seatDialog.setLocationRelativeTo(this);

        // Dòng chữ trên cùng
        JLabel headerLabel = new JLabel("Sơ đồ chỗ ngồi", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        seatDialog.add(headerLabel, BorderLayout.NORTH);

        // Panel chứa sơ đồ ghế
        JPanel seatPanel = new JPanel();
        int rows = (int)Math.ceil((double)room.getTotalSeats() / room.getSeatsPerRow());
        seatPanel.setLayout(new GridLayout(rows, room.getSeatsPerRow(), 10, 10)); // khoảng cách 10px

        for (int i = 0; i < room.getTotalSeats(); i++) {
            String seatLabel = String.format("%c%d", 'A' + (i / room.getSeatsPerRow()), (i % room.getSeatsPerRow()) + 1);
            JButton seatButton = new JButton(seatLabel);
            seatButton.setEnabled(true); // hoặc false nếu đã đặt
            seatPanel.add(seatButton);
        }

        JScrollPane scrollPane = new JScrollPane(seatPanel);
        seatDialog.add(scrollPane, BorderLayout.CENTER);

        // Panel dưới cùng chứa nút
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> seatDialog.dispose());
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(closeButton, BorderLayout.SOUTH);
    
        seatDialog.add(bottomPanel, BorderLayout.SOUTH);
        seatDialog.setVisible(true);
    }

    private void createShowtimeDialog() {
        JDialog showtimeDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Giờ chiếu của phim", true);
        showtimeDialog.setLayout(new BorderLayout());
        showtimeDialog.setSize(400, 300);
        showtimeDialog.setLocationRelativeTo(this);
    
        // Dòng chữ trên cùng
        JLabel headerLabel = new JLabel("Thời gian chiếu", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        showtimeDialog.add(headerLabel, BorderLayout.NORTH);
    
        // Danh sách giờ chiếu
        DefaultListModel<String> listModel = new DefaultListModel<>();
        room.getShowtimes().forEach(st -> listModel.addElement(st.toString())); // Đảm bảo Showtime.toString() rõ ràng
        JList<String> showtimeList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(showtimeList);
        showtimeDialog.add(scrollPane, BorderLayout.CENTER);
    
        // Panel dưới cùng chứa nút và chú thích
        JPanel bottomPanel = new JPanel(new BorderLayout());
    
        JLabel footerLabel = new JLabel("Cập nhật thời gian chiếu ở tab Thời gian chiếu", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        bottomPanel.add(footerLabel, BorderLayout.NORTH);
    
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> showtimeDialog.dispose());
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
    
        showtimeDialog.add(bottomPanel, BorderLayout.SOUTH);
    
        showtimeDialog.setVisible(true);
    }

    private void saveRoom() {
        if (!isEditMode) room = new Room(); // tạo mới nếu thêm mới

        try {
            room.setName(nameField.getText().trim());
            room.setTotalSeats(Integer.parseInt(totalSeatsField.getText().trim()));
            room.setSeatsPerRow(Integer.parseInt(seatsPerRowField.getText().trim()));
            room.setSeatsAvailable(room.getTotalSeats()); // reset available

            boolean success = isEditMode
                    ? parentPanel.service.updateRoom(room)
                    : parentPanel.service.createRoom(room) != null;

            if (success) {
                JOptionPane.showMessageDialog(this,
                        isEditMode ? "Cập nhật phòng chiếu thành công!" : "Thêm phòng chiếu thành công!",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                parentPanel.reload();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        isEditMode ? "Cập nhật thất bại." : "Thêm thất bại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho ghế.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu phòng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
