package views.admin.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.util.List;
import java.util.UUID;

import entities.Movie;
import service.MovieService;
import utils.AdminFrameConfig;
import views.admin.common.ButtonRenderer;
import views.admin.common.RecordActionButton;
import views.admin.dialogs.MovieDialog;

public class MovieManagementPanel extends BaseManagementPanel {
    public final MovieService service = new MovieService();
    private List<Movie> movies;

    public MovieManagementPanel() {
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
        addButton = new JButton("➕ Thêm phim");

        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(Box.createHorizontalStrut(20)); // khoảng cách
        topPanel.add(addButton);

        this.searchButton.addActionListener(e -> this.search());
        this.addButton.addActionListener(e -> this.actAddition());

        add(topPanel, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel(new Object[]{
            "ID",
            "Tên phim", 
            "Đạo diễn", 
            "Thể loại", 
            "Ngày phát hành",
            "Giá", 
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
            end = Math.min(start + pageSize, this.movies.size());

        for (int i = start; i < end; i++) {
            Movie movie = this.movies.get(i);
            this.tableModel.addRow(new Object[]{
                movie.getId(),
                movie.getTitle(),
                movie.getDirectorName(),
                movie.getGenre(),
                movie.getReleaseDate(),
                movie.getPrice(),
                "",
                ""
            });
        }

        this.pageLabel.setText("Trang " + currentPage + "/" + totalPages);
    }

    @Override
    public void loadData() {
        String key = this.searchField.getText().trim();
        this.movies = key.isEmpty()
                      ? this.service.getAllMovies()
                      : this.service.searchByName(key);
        this.calcTotalPages(this.movies.size());
        this.updateTable();
    }

    @Override
    public void actAddition() {
        new MovieDialog((JFrame)SwingUtilities.getWindowAncestor(this), null, this);
    }

    @Override
    public boolean actDeletion(UUID id) {
        return this.service.deleteMovie(id);
    }

    @Override
    public boolean actEdition(UUID id) {    
        Movie movie = this.service.getMovieById(id);
        if (movie == null) {
            return false;
        }

        new MovieDialog((JFrame)SwingUtilities.getWindowAncestor(this), movie, this);

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
