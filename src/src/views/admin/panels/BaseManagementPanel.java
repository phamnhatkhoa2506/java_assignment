package views.admin.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.UUID;

public abstract class BaseManagementPanel extends JPanel {
    protected JTable table;
    protected DefaultTableModel tableModel;
    protected JTextField searchField;
    protected JButton searchButton, prevButton, nextButton, addButton;
    protected JLabel pageLabel;
    protected JPanel loadingPanel;

    protected int currentPage = 1;
    protected final int pageSize = 10;
    protected int totalPages = 1;

    protected BaseManagementPanel() {
        setLayout(new BorderLayout());
    }

    public abstract void initUI();

    protected void initLoadingPanel() {
        loadingPanel = new JPanel();
        loadingPanel.add(new JLabel("⏳ Đang tải dữ liệu..."));
        loadingPanel.setVisible(false);
        add(loadingPanel, BorderLayout.EAST);
    }

    protected void initPaginationPanel() {
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        prevButton = new JButton("⬅️ Trước");
        nextButton = new JButton("Tiếp ➡️");
        pageLabel = new JLabel("Trang 1/1");
        paginationPanel.add(prevButton);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextButton);

        this.prevButton.addActionListener(e -> toPrevPage());
        this.nextButton.addActionListener(e -> toNextPage());

        add(paginationPanel, BorderLayout.SOUTH);
    }

    public abstract void updateTable();
    public abstract void loadData();
    
    protected void search() {
        this.loadData();
        this.resetPage();
    }

    public void reload() {
        this.loadData();
    }

    private void toNextPage() {
        this.currentPage = (this.currentPage + 1) % totalPages + 1;
        this.updateTable();
    }

    private void toPrevPage() {
        this.currentPage--;
        if (this.currentPage == 0) {
            this.currentPage = totalPages;
        }
        this.updateTable();
    }

    protected void resetPage() {
        this.currentPage = 1;
        this.updateTable();
    }
    
    protected void calcTotalPages(int size) {
        totalPages = (int) Math.ceil((double) size / pageSize);
        if (totalPages < 1) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;
    }

    public abstract void actAddition();
    public abstract boolean actDeletion(UUID id);
    public abstract boolean actEdition(UUID id);
    public abstract boolean actDeletion(Long id);
    public abstract boolean actEdition(Long id);
}
