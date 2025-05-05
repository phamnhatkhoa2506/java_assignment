package views.admin.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.util.List;
import java.util.UUID;

import entities.User;
import service.UserService;
import utils.AdminFrameConfig;
import views.admin.common.ButtonRenderer;
import views.admin.common.RecordActionButton;
import views.admin.dialogs.UserDialog;

public class UserManagementPanel extends BaseManagementPanel {
    public final UserService service = new UserService();
    private List<User> users;

    public UserManagementPanel() {
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
            "Tên đăng nhập", 
            "Họ", 
            "Tên", 
            "Ngày sinh",
            "Email", 
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
            end = Math.min(start + pageSize, this.users.size());

        for (int i = start; i < end; i++) {
            User user = this.users.get(i);
            this.tableModel.addRow(new Object[]{
                user.getId(),
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getBirthday(),
                user.getEmail(),
                "",
                ""
            });
        }

        this.pageLabel.setText("Trang " + currentPage + "/" + totalPages);
    }

    @Override
    public void loadData() {
        String key = this.searchField.getText().trim();
        this.users = key.isEmpty()
                      ? this.service.getAllUsers()
                      : this.service.searchByName(key);
        this.calcTotalPages(this.users.size());
        this.updateTable();
    }
    
    @Override
    public void actAddition() {}

    @Override
    public boolean actDeletion(UUID id) {
        return this.service.deleteUser(id);
    }

    @Override
    public boolean actEdition(UUID id) {    
        User user = this.service.getUserById(id);
        if (user == null) {
            return false;
        }

        new UserDialog((JFrame)SwingUtilities.getWindowAncestor(this), user);

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
