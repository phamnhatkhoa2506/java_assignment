package views.admin.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.util.List;
import java.util.UUID;

import entities.Booking;
import entities.Seat;
import service.BookingService;
import utils.AdminFrameConfig;
import views.admin.common.ButtonRenderer;
import views.admin.common.RecordActionButton;
// import views.admin.dialogs.BookingDialog;
// import views.admin.dialogs.MovieDialog;

public class BookingManagementPanel extends BaseManagementPanel {
    public final BookingService service = new BookingService();
    private List<Booking> bookings;

    public BookingManagementPanel() {
        setLayout(new BorderLayout());
        initUI();
    }

    @Override
    public void initUI() {
        initTopPanel(); // Search and Add
        initTable(); // Table
        initPaginationPanel(); // Pagination panel
        initLoadingPanel(); // Loading

        this.loadData();
        this.updateTable();
    }

    private void initTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchButton = new JButton("Tìm kiếm");

        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);

        this.searchButton.addActionListener(e -> this.search());

        add(topPanel, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel(new Object[]{
            "ID",
            "Ngày đặt", 
            "Giờ đặt", 
            "Tổng giá", 
            "Tên người đặt",
            "Ghế đặt", 
            "Tên phim",
            "Giờ chiếu",
            "Sửa", 
            "Xóa"
        }, 0);
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6 || column == 7;
            }
        };

        TableColumn editColumn = table.getColumn("Sửa");
        editColumn.setCellRenderer(new ButtonRenderer());
        editColumn.setMinWidth(20);
        editColumn.setMaxWidth(40);
        editColumn.setCellEditor(new RecordActionButton(new JCheckBox(), AdminFrameConfig.ActionType.EDIT, this));

        TableColumn deleteColumn = table.getColumn("Xóa");
        deleteColumn.setCellRenderer(new ButtonRenderer());
        deleteColumn.setMinWidth(20);
        deleteColumn.setMaxWidth(40);
        deleteColumn.setCellEditor(new RecordActionButton(new JCheckBox(), AdminFrameConfig.ActionType.DELETE, this));

        this.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void updateTable() {
        this.tableModel.setRowCount(0);

        int start = (this.currentPage - 1) * this.pageSize,
            end = Math.min(start + pageSize, this.bookings.size());

        for (int i = start; i < end; i++) {
            Booking booking = this.bookings.get(i);
            this.tableModel.addRow(new Object[]{
                booking.getId(),
                booking.getBookingDate().toString(),
                booking.getBookingTime().toString(),
                booking.getBookingPrice(),
                booking.getUser().getUsername(),
                createSeatString(booking.getSeats()),
                booking.getShowtime().getMovie().getTitle(),
                booking.getShowtime().getDateShow(),
                "",
                ""
            });
        }

        this.pageLabel.setText("Trang " + currentPage + "/" + totalPages);
    }

    private String createSeatString(List<Seat> seats) {
        String seatString = "";
        for (Seat seat : seats)
            seatString += (seat.getRow() + 'A') + seat.getColumn() + " ";

        return seatString;
    }

    @Override
    public void loadData() {
        this.bookings = this.service.getAllBookings();
        this.calcTotalPages(this.bookings.size());
        this.updateTable();
    }

    @Override
    public void actAddition() {
        // new BookingDialog((JFrame)SwingUtilities.getWindowAncestor(this), null, this);
    }

    @Override
    public boolean actDeletion(UUID id) {
        return this.service.deleteBooking(id);
    }

    @Override
    public boolean actEdition(UUID id) {    
        Booking booking = this.service.getBookingById(id);
        if (booking == null) {
            return false;
        }

        // new BookingDialog((JFrame)SwingUtilities.getWindowAncestor(this), booking, this);

        return true;
    }

    @Override
    public boolean actDeletion(Long id) {
        return true;
    }

    @Override
    public boolean actEdition(Long id) {    
        return true;
    }
}
