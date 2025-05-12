package views.admin.reports;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;

public class BarChartExample extends JFrame {

    public BarChartExample() {
        setTitle("Thống kê doanh thu");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 1. Tạo dữ liệu
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(100, "Doanh thu", "Tháng 1");
        dataset.addValue(150, "Doanh thu", "Tháng 2");
        dataset.addValue(80, "Doanh thu", "Tháng 3");
        dataset.addValue(120, "Doanh thu", "Tháng 4");

        // 2. Tạo biểu đồ
        JFreeChart chart = ChartFactory.createBarChart(
                "Doanh thu theo tháng",     // Tiêu đề biểu đồ
                "Tháng",                    // Trục X
                "Doanh thu (triệu VNĐ)",   // Trục Y
                dataset
        );

        // 3. Hiển thị biểu đồ trên JFrame
        ChartPanel chartPanel = new ChartPanel(chart);
        setContentPane(chartPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BarChartExample().setVisible(true);
        });
    }
}
