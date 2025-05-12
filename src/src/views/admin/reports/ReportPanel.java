package views.admin.reports;

import java.time.LocalDate;

import utils.AdminFrameConfig;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Map;

import service.BookingService;

public class ReportPanel extends JPanel {
    private JComboBox<Integer> yearBox;
    private JComboBox<Integer> monthBox;
    private JPanel chartContainer;

    private BookingService service = new BookingService();

    public ReportPanel() {
        setBackground(AdminFrameConfig.NAV_COLOR);
        setPreferredSize(AdminFrameConfig.NAV_SIZE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setLayout(new BorderLayout());

        // Top control panel
        JPanel controlPanel = new JPanel();
        yearBox = new JComboBox<>();
        for (int y = 2020; y <= LocalDate.now().getYear(); y++) {
            yearBox.addItem(y);
        }

        monthBox = new JComboBox<>();
        for (int m = 1; m <= 12; m++) {
            monthBox.addItem(m);
        }

        JButton loadBtn = new JButton("Tải biểu đồ");
        loadBtn.addActionListener(e -> loadChart());

        controlPanel.add(new JLabel("Năm:"));
        controlPanel.add(yearBox);
        controlPanel.add(new JLabel("Tháng:"));
        controlPanel.add(monthBox);
        controlPanel.add(loadBtn);

        add(controlPanel, BorderLayout.NORTH);

        // Chart area
        chartContainer = new JPanel(new BorderLayout());
        add(chartContainer, BorderLayout.CENTER);
    }

    private void loadChart() {
        int year = (int) yearBox.getSelectedItem();
        int month = (int) monthBox.getSelectedItem();

        Map<LocalDate, BigDecimal> revenueMap = this.service.getDailyRevenueByMonth(year, month);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<LocalDate, BigDecimal> entry : revenueMap.entrySet()) {
            dataset.addValue(entry.getValue(), "Doanh thu", entry.getKey().getDayOfMonth());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Doanh thu theo ngày - " + month + "/" + year,
                "Ngày",
                "Doanh thu (VND)",
                dataset
        );

        chartContainer.removeAll();
        chartContainer.add(new ChartPanel(chart), BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }
}
