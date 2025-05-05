package views.admin.dialogs;

import javax.swing.*;
import java.awt.*;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;
import java.util.Properties;
import java.util.Set;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jdatepicker.impl.*;

import entities.Booking;
import entities.Movie;
import entities.Room;
import entities.Seat;
import entities.Showtime;
import service.MovieService;
import service.RoomService;
import views.admin.panels.ShowtimeManagementPanel;

public class ShowtimeDialog extends JDialog {
    private Showtime showtime;
    private ShowtimeManagementPanel parentPanel;
    private MovieService movieService = new MovieService();
    private RoomService roomService = new RoomService();
    private JSpinner startTimeSpinner, endTimeSpinner;
    private JDatePickerImpl datePicker;
    private JComboBox<Movie> movieComboBox;
    private JComboBox<Room> roomComboBox;
    private final boolean isEditMode;

    public ShowtimeDialog(JFrame parent, Showtime showtime, ShowtimeManagementPanel parentPanel) {
        super(parent, showtime == null ? "Thêm giờ chiếu" : "Chỉnh sửa giờ chiếu", true);
        this.showtime = showtime;
        this.parentPanel = parentPanel;
        this.isEditMode = showtime != null;

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
        initEditModeComponents(formPanel, gbc, 5);
        add(formPanel, BorderLayout.CENTER);

        initBottomComponents();
    }

    private void initFields(JPanel formPanel, GridBagConstraints gbc) {
        Dimension fieldSize = new Dimension(200, 25);

        // Date picker setup
        UtilDateModel model = new UtilDateModel();
        if (showtime != null) model.setValue(java.sql.Date.valueOf(showtime.getDateShow()));
        Properties p = new Properties();
        p.put("text.today", "Hôm nay");
        p.put("text.month", "Tháng");
        p.put("text.year", "Năm");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        // Time spinners
        SpinnerDateModel startModel = new SpinnerDateModel();
        SpinnerDateModel endModel = new SpinnerDateModel();

        startTimeSpinner = new JSpinner(startModel);
        endTimeSpinner = new JSpinner(endModel);

        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));
        endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));

        if (showtime != null) {
            startTimeSpinner.setValue(java.sql.Time.valueOf(showtime.getStartTime()));
            endTimeSpinner.setValue(java.sql.Time.valueOf(showtime.getEndTime()));
        } else {
            startTimeSpinner.setValue(new Date());
            endTimeSpinner.setValue(new Date());
        }

        startTimeSpinner.setPreferredSize(fieldSize);
        endTimeSpinner.setPreferredSize(fieldSize);

        // Movie ComboBox
        List<Movie> movies = this.movieService.getAllMovies(); // cần có method này
        movieComboBox = new JComboBox<>(movies.toArray(new Movie[0]));
        movieComboBox.setPreferredSize(fieldSize);
        if (showtime != null && showtime.getMovie() != null) {
            movieComboBox.setSelectedItem(showtime.getMovie());
        }

        // Room ComboBox
        List<Room> rooms = this.roomService.getAllRooms(); // cần có method này
        roomComboBox = new JComboBox<>(rooms.toArray(new Room[0]));
        roomComboBox.setPreferredSize(fieldSize);
        if (showtime != null && showtime.getRoom() != null) {
            roomComboBox.setSelectedItem(showtime.getRoom());
        }

        ///
        String[] labels = { "Ngày chiếu:", "Giờ bắt đầu:", "Giờ kết thúc:" , "Phim:", "Phòng:"};
        JComponent[] fields = { datePicker, startTimeSpinner, endTimeSpinner, movieComboBox, roomComboBox };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            formPanel.add(fields[i], gbc);
        }
    }

    private void initBottomComponents() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        saveButton.addActionListener(e -> saveShowtime());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initEditModeComponents(JPanel formPanel, GridBagConstraints gbc, int y) {
        if (this.isEditMode) {
            JButton bookedTicketsButton = new JButton("Xem lịch sử đặt vé");
            bookedTicketsButton.addActionListener(e -> createBookingDialog());
            gbc.gridx = 0;
            gbc.gridy = y;
            formPanel.add(bookedTicketsButton, gbc);

            JButton seatButton = new JButton("Xem ghế đã đặt");
            seatButton.addActionListener(e -> createSeatDialog());
            gbc.gridx = 0;
            gbc.gridy = y + 1;
            formPanel.add(seatButton, gbc);
        }
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
        List<Booking> bookings = showtime.getBookings();
        String[] columnNames = {"Người đặt", "Ngày đặt", "Giờ đặt", "Giá", "Giờ chiếu", "Ghế"};
        Object[][] data = new Object[bookings.size()][columnNames.length];
    
        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            data[i][0] = b.getUser().getUsername();
            data[i][1] = b.getBookingDate();
            data[i][2] = b.getBookingTime();
            data[i][3] = b.getBookingPrice();
            data[i][4] = b.getShowtime() != null ? b.getShowtime().toString() : "Không có";
            data[i][5] = createSeatString(b.getSeats());
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

    private void createSeatDialog() {
        JDialog seatDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Sơ đồ chỗ ngồi", true);
        seatDialog.setLayout(new BorderLayout());
        seatDialog.setSize(800, 600);
        seatDialog.setLocationRelativeTo(this);

        // Lay ghe da dat
        List<Booking> bookings = this.showtime.getBookings();
        Room room = this.showtime.getRoom();
        Set<String> bookedSeatIds = new HashSet<>();
        for (Booking booking : bookings) {
            for (Seat seat : booking.getSeats()) {
                bookedSeatIds.add(seat.getId());
            }
        }

        // Dòng chữ trên cùng
        JLabel headerLabel = new JLabel("Sơ đồ chỗ ngồi", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        seatDialog.add(headerLabel, BorderLayout.NORTH);

        // Panel chứa sơ đồ ghế
        JPanel seatPanel = new JPanel();
        int rows = (int)Math.ceil((double)room.getTotalSeats() / room.getSeatsPerRow());
        seatPanel.setLayout(new GridLayout(rows, room.getSeatsPerRow(), 10, 10)); // khoảng cách 10px

        for (int i = 0; i < room.getTotalSeats(); i++) {
            int row = i / room.getSeatsPerRow();
            int col = i % room.getSeatsPerRow();
            String seatId = room.getId() + "-" + row + "-" + col;
            String seatLabel = String.format("%c%d", 'A' + row, col + 1);
        
            JButton seatButton = new JButton(seatLabel);
        
            if (bookedSeatIds.contains(seatId)) {
                seatButton.setBackground(Color.RED);       // màu đỏ cho ghế đã đặt
                seatButton.setForeground(Color.WHITE);     // chữ trắng cho dễ nhìn
                seatButton.setEnabled(false);              // không cho chọn
            } else {
                seatButton.setBackground(Color.GREEN);     // ghế trống màu xanh
            }
        
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

    private String createSeatString(List<Seat> seats) {
        String seatString = "";
        for (Seat seat : seats)
            seatString += (seat.getRow() + 'A') + seat.getColumn() + " ";

        return seatString;
    }

    private void saveShowtime() {
        if (!isEditMode) {
            showtime = new Showtime();
            showtime.setId(UUID.randomUUID());
        }

        try {
            java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
            if (selectedDate == null) throw new Exception("Vui lòng chọn ngày chiếu.");
            showtime.setDateShow(new java.sql.Date(selectedDate.getTime()).toLocalDate());

            Date startDate = (Date) startTimeSpinner.getValue();
            Date endDate = (Date) endTimeSpinner.getValue();
            Movie selectedMovie = (Movie) movieComboBox.getSelectedItem();
            Room selectedRoom = (Room) roomComboBox.getSelectedItem();
            if (selectedMovie == null || selectedRoom == null) {
               JOptionPane.showMessageDialog(this, "Vui lòng chọn phim và phòng chiếu.");
            }

            showtime.setMovie(selectedMovie);
            showtime.setRoom(selectedRoom);
            showtime.setStartTime(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
            showtime.setEndTime(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());

            boolean success = isEditMode
                    ? parentPanel.service.updateShowtime(showtime)
                    : parentPanel.service.createShowtime(showtime) != null;

            if (success) {
                JOptionPane.showMessageDialog(this,
                        isEditMode ? "Cập nhật giờ chiếu thành công!" : "Thêm giờ chiếu thành công!",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                parentPanel.reload();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        isEditMode ? "Cập nhật thất bại." : "Thêm thất bại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parse(text);
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                java.util.Calendar cal = (java.util.Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }
}
