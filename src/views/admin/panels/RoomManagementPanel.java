package views.admin.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.util.List;
import java.util.UUID;

import entities.Room;
import service.RoomService;
import utils.AdminFrameConfig;
import views.admin.common.ButtonRenderer;
import views.admin.common.RecordActionButton;
import views.admin.dialogs.RoomDialog;

public class RoomManagementPanel extends BaseManagementPanel {
    public final RoomService service = new RoomService();
    private List<Room> rooms;

    public RoomManagementPanel() {
        setLayout(new BorderLayout());
        initUI();
    }

    @Override
    public void initUI() {
        initTopPanel(); // Top panel include add-button
        initTable(); // Table
        initPaginationPanel(); // Pagination panel
        initLoadingPanel(); // Loading

        this.loadData();
        this.updateTable();
    }

    private void initTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("➕ Thêm phòng chiếu");
        topPanel.add(addButton);

        this.addButton.addActionListener(e -> this.actAddition());

        add(topPanel, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel(new Object[]{
            "ID",
            "Tên phòng", 
            "Tổng số chỗ ngồi", 
            "Số chỗ ngồi mỗi hàng", 
            "Số ngồi còn trống",
            "Sửa", 
            "Xóa"
        }, 0);
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6 || column == 5;
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

        TableColumn idColumn = table.getColumn("ID");
        idColumn.setMinWidth(20);
        idColumn.setMaxWidth(40);

        this.add(new JScrollPane(table), BorderLayout.CENTER);
    }
    
    @Override
    public void updateTable() {
        this.tableModel.setRowCount(0);

        int start = (this.currentPage - 1) * this.pageSize,
            end = Math.min(start + pageSize, this.rooms.size());

        for (int i = start; i < end; i++) {
            Room room = this.rooms.get(i);
            this.tableModel.addRow(new Object[]{
                room.getId(),
                room.getName(),
                room.getTotalSeats(),
                room.getSeatsPerRow(),
                room.getSeatsAvailable(),
                "",
                ""
            });
        }

        this.pageLabel.setText("Trang " + currentPage + "/" + totalPages);
    }

    @Override
    public void loadData() {
        this.rooms = this.service.getAllRooms();
        this.calcTotalPages(this.rooms.size());
        this.updateTable();
    }

    @Override
    public void actAddition() {
        new RoomDialog((JFrame)SwingUtilities.getWindowAncestor(this), null, this);
    }

    @Override
    public boolean actEdition(UUID id) {
        return false;
    }

    @Override
    public boolean actDeletion(UUID id) {
        return false;
    }

    @Override
    public boolean actDeletion(Long id) {
        return this.service.deleteRoom(id);
    }

    @Override
    public boolean actEdition(Long id) {    
        Room room = this.service.getRoomById(id);
        if (room == null) {
            return false;
        }

        new RoomDialog((JFrame)SwingUtilities.getWindowAncestor(this), room, this);

        return true;
    }
}
