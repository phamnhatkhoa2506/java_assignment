package views.admin.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import entities.Showtime;
import service.ShowtimeService;
import utils.AdminFrameConfig;
import views.admin.common.ButtonRenderer;
import views.admin.common.RecordActionButton;
import views.admin.dialogs.ShowtimeDialog;

public class ShowtimeManagementPanel extends BaseManagementPanel {
    public final ShowtimeService service = new ShowtimeService();
    private List<Showtime> showtimes;

    private JComboBox<LocalDate> dateFilter;
    private JComboBox<LocalTime> timeFilter;
    private JComboBox<String> movieFilter;

    public ShowtimeManagementPanel() {
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

        // Bộ lọc theo ngày
        topPanel.add(new JLabel("Ngày:"));
        dateFilter = createDateFilterBox();
        topPanel.add(dateFilter);

        JButton searchByDateButton = new JButton("Tìm kiếm");
        searchByDateButton.addActionListener(e -> searchByDate());
        topPanel.add(searchByDateButton);
        topPanel.add(Box.createHorizontalStrut(20)); 

         // Bộ lọc theo giờ
        topPanel.add(new JLabel("Giờ:"));
        timeFilter = createTimeFilterBox();
        topPanel.add(timeFilter);

        JButton searchByTimeButton = new JButton("Tìm kiếm");
        searchByTimeButton.addActionListener(e -> searchByTime());
        topPanel.add(searchByTimeButton);
        topPanel.add(Box.createHorizontalStrut(20)); 

        // Bo loc theo ten phim
        topPanel.add(new JLabel("Phim:"));
        movieFilter = createMovieNameFilterBox();
        topPanel.add(movieFilter);

        JButton searchByMovieNameButton = new JButton("Tìm kiếm");
        searchByMovieNameButton.addActionListener(e -> searchByMovieName());
        topPanel.add(searchByMovieNameButton);
        topPanel.add(Box.createHorizontalStrut(20)); 

        // Nut load lai data
        JButton reloadButton = new JButton("Tải lại");
        reloadButton.addActionListener(e -> reload());
        topPanel.add(reloadButton);
        topPanel.add(Box.createHorizontalStrut(20));

        // Spacer và nút Thêm
        addButton = new JButton("Thêm giờ chiếu");
        addButton.addActionListener(e -> this.actAddition());
        topPanel.add(addButton);

        add(topPanel, BorderLayout.NORTH);
    }

    private JComboBox<LocalTime> createTimeFilterBox() {
        JComboBox<LocalTime> filter = new JComboBox<>();
        for (LocalTime time : service.getAllTimes()) {
            filter.addItem(time);
        }

        return filter;
    }

    private JComboBox<LocalDate> createDateFilterBox() {
        JComboBox<LocalDate> filter = new JComboBox<>();
        for (LocalDate date : service.getAllDates()) {
            filter.addItem(date);
        }
        
        return filter;
    }

    private JComboBox<String> createMovieNameFilterBox() {
        JComboBox<String> filter = new JComboBox<>();
        for (String movieName : service.getAllMovieNames()) {
            filter.addItem(movieName);
        }

        return filter;
    }

    private void initTable() {
        tableModel = new DefaultTableModel(new Object[]{
            "ID",
            "Ngày chiếu", 
            "Thời gian bắt đầu", 
            "Thời gian kết thúc", 
            "Tên phim",
            "Tên phòng", 
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
            end = Math.min(start + pageSize, this.showtimes.size());

        for (int i = start; i < end; i++) {
            Showtime showtime = this.showtimes.get(i);
            this.tableModel.addRow(new Object[]{
                showtime.getId(),
                showtime.getDateShow(),
                showtime.getStartTime(),
                showtime.getEndTime(),
                showtime.getMovie().getTitle(),
                showtime.getRoom().getName(),
                "",
                ""
            });
        }

        this.pageLabel.setText("Trang " + currentPage + "/" + totalPages);
    }

    @Override
    public void loadData() {
        this.showtimes = this.service.getAllShowtimes();
        this.calcTotalPages(this.showtimes.size());
        this.updateTable();
    }

    public void searchByDate() {
        LocalDate selectedDate = (LocalDate) dateFilter.getSelectedItem();

        // Lấy tất cả dữ liệu
        List<Showtime> all = service.getAllShowtimes();

        // Lọc theo từ khóa, ngày, và giờ
        this.showtimes = all.stream()
            .filter(s -> (selectedDate == null || s.getDateShow().equals(selectedDate)))
            .toList();

        this.calcTotalPages(this.showtimes.size());
        this.currentPage = 1;
        this.updateTable();
    }

    public void searchByTime() {
        LocalTime selectedTime = (LocalTime) timeFilter.getSelectedItem();

        // Lấy tất cả dữ liệu
        List<Showtime> all = service.getAllShowtimes();

        // Lọc theo từ khóa, ngày, và giờ
        this.showtimes = all.stream()
            .filter(s -> (selectedTime == null || s.getStartTime().equals(selectedTime)))
            .toList();

        this.calcTotalPages(this.showtimes.size());
        this.currentPage = 1;
        this.updateTable();
    }

    public void searchByMovieName() {
        String selectedMovieName = (String) movieFilter.getSelectedItem();

        List<Showtime> all = service.getAllShowtimes();

        this.showtimes = all.stream()
            .filter(s -> (selectedMovieName == null || s.getMovie().getTitle().equals(selectedMovieName)))
            .toList();

        this.calcTotalPages(this.showtimes.size());
        this.currentPage = 1;
        this.updateTable();
    }

    @Override
    public void actAddition() {
        new ShowtimeDialog((JFrame)SwingUtilities.getWindowAncestor(this), null, this);
    }

    @Override
    public boolean actDeletion(UUID id) {
        return this.service.deleteShowtime(id);
    }

    @Override
    public boolean actEdition(UUID id) {    
        Showtime showtime = this.service.getShowtimeById(id);
        if (showtime == null) {
            return false;
        }

        new ShowtimeDialog((JFrame)SwingUtilities.getWindowAncestor(this), showtime, this);

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

    private void updateDateFilterBox() {
        dateFilter.removeAllItems();
        for (LocalDate date : service.getAllDates()) {
            dateFilter.addItem(date);
        }
    }
    
    private void updateTimeFilterBox() {
        timeFilter.removeAllItems();
        for (LocalTime time : service.getAllTimes()) {
            timeFilter.addItem(time);
        }
    }
    
    private void updateMovieNameFilterBox() {
        movieFilter.removeAllItems();
        for (String name : service.getAllMovieNames()) {
            movieFilter.addItem(name);
        }
    }    

    @Override
    public void reload() {
        this.loadData();
        this.updateTable();
        
        // Cập nhật lại dữ liệu trong JComboBox thay vì tạo mới
        updateDateFilterBox();
        updateTimeFilterBox();
        updateMovieNameFilterBox();
    }
}
