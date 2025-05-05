package views.admin;

import javax.swing.*;
import java.awt.*;

import views.admin.panels.*;
import utils.AdminFrameConfig;

public class AdminFrame extends JFrame {
    private JPanel navPanel;
    private JPanel contentPanel;

    public AdminFrame() {
        setTitle("Trang Quản Trị");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Toàn màn hình
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== Panel Menu bên trái =====
        navPanel = new JPanel();
        navPanel.setBackground(AdminFrameConfig.NAV_COLOR);
        navPanel.setPreferredSize(AdminFrameConfig.NAV_SIZE);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        add(navPanel, BorderLayout.WEST);

        // ===== Panel Nội dung bên phải =====
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);

        // ===== Thêm các nút menu =====
        addNavButton("Trang chủ", new JLabel("🏠 Đây là Trang chủ"));

        addNavDropdown(
                "Quản lý",
                new String[]{
                    "Người dùng",
                    "Phim",
                    "Phòng chiếu",
                    "Thời gian chiếu phim",
                    "Vé đã đặt",
                },
                new JComponent[]{
                    new UserManagementPanel(), // <--- Hiển thị bảng người dùng
                    new MovieManagementPanel(),
                    new RoomManagementPanel(),
                    new ShowtimeManagementPanel(),
                    new BookingManagementPanel(),
                }
        );


        addNavButton("Báo cáo", new JLabel("📊 Đây là trang báo cáo"));
        addNavButton("Cài đặt", new JLabel("⚙️ Đây là trang cài đặt"));
    }

    // Hàm thêm nút đơn lẻ
    private void addNavButton(String title, JComponent component) {
        JButton btn = createNavButton(title);
        btn.addActionListener(e -> showContent(component));
        navPanel.add(btn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    // Hàm thêm nút Dropdown
    private void addNavDropdown(String title, String[] subItems, JComponent[] components) {
        JButton mainBtn = createNavButton(title + " ▼"); // ▼ icon
        JPanel subPanel = new JPanel();
        subPanel.setBackground(AdminFrameConfig.NAV_DROPDOWN_COLOR);
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.setVisible(false); // Ẩn ban đầu

        for (int i = 0; i < subItems.length; i++) {
            JButton subBtn = new JButton("   → " + subItems[i]);
            subBtn.setFocusPainted(false);
            subBtn.setBackground(AdminFrameConfig.NAV_DROPDOWN_ITEM_COLOR);
            subBtn.setForeground(AdminFrameConfig.NAV_TEXT_COLOR);
            subBtn.setFont(new Font("Arial", Font.PLAIN, 14));
            subBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            subBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            int index = i;
            subBtn.addActionListener(e -> showContent(components[index]));

            subPanel.add(subBtn);
            subPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        }

        mainBtn.addActionListener(e -> {
            subPanel.setVisible(!subPanel.isVisible());
            mainBtn.setText(subPanel.isVisible() ? title + " ▲" : title + " ▼"); // đổi icon
            navPanel.revalidate();
        });

        navPanel.add(mainBtn);
        navPanel.add(subPanel);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    // Hàm để đổi nội dung bên phải
    private void showContent(JComponent component) {
        contentPanel.removeAll();
        contentPanel.add(component, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Hàm tiện ích tạo button menu
    private JButton createNavButton(String title) {
        JButton btn = new JButton(title);
        btn.setFocusPainted(false);
        btn.setBackground(AdminFrameConfig.NAV_BUTTON_COLOR);
        btn.setForeground(AdminFrameConfig.NAV_TEXT_COLOR);
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminFrame().setVisible(true);
        });
    }
}
