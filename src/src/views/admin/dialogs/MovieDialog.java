package views.admin.dialogs;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import entities.Movie;
import views.admin.panels.MovieManagementPanel;

public class MovieDialog extends JDialog {
    private Movie movie;
    private MovieManagementPanel parentPanel;
    private JTextField titleField, directorNameField, genreField, durationField, countryField, languageField,
                        descriptionField, forAgeField, priceField, releasedDateField, imgUrlField;
    private final boolean isEditMode;

    public MovieDialog(JFrame parent, Movie movie, MovieManagementPanel parentPanel) {
        super(parent, movie == null ? "Thêm phim mới" : "Chỉnh sửa phim", true);
        this.movie = movie;
        this.parentPanel = parentPanel;
        this.isEditMode = movie != null;

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
        gbc.anchor = GridBagConstraints.WEST;

        initFields(formPanel, gbc); // Khoi tao cac fields
        initEditModeComponents(formPanel, gbc, 11); // Khoi tao phan cua edit mode

        add(formPanel, BorderLayout.CENTER);

        initBottomComponents();
    }

    private void initFields(JPanel formPanel, GridBagConstraints gbc) {
        Dimension fieldSize = new Dimension(600, 25); // Kích thước tối thiểu cho các input

        titleField = new JTextField(movie != null ? movie.getTitle() : ""); titleField.setPreferredSize(fieldSize);
        directorNameField = new JTextField(movie != null ? movie.getDirectorName() : ""); directorNameField.setPreferredSize(fieldSize);
        durationField = new JTextField(movie != null ? String.valueOf(movie.getDuration()) : ""); durationField.setPreferredSize(fieldSize);
        genreField = new JTextField(movie != null ? movie.getGenre() : ""); genreField.setPreferredSize(fieldSize);
        countryField = new JTextField(movie != null ? movie.getCountry() : ""); countryField.setPreferredSize(fieldSize);
        languageField = new JTextField(movie != null ? movie.getLanguage() : ""); languageField.setPreferredSize(fieldSize);
        forAgeField = new JTextField(movie != null ? String.valueOf(movie.getForAge()) : ""); forAgeField.setPreferredSize(fieldSize);
        priceField = new JTextField(movie != null ? movie.getPrice().toString() : ""); priceField.setPreferredSize(fieldSize);
        descriptionField = new JTextField(movie != null ? movie.getDescription() : ""); descriptionField.setPreferredSize(fieldSize);
        releasedDateField = new JTextField(movie != null ? movie.getReleaseDate().toString() : ""); releasedDateField.setPreferredSize(fieldSize);
        imgUrlField = new JTextField(movie != null ? movie.getPosterURL() : ""); imgUrlField.setPreferredSize(fieldSize);

        String[] labels = {
                "Tên phim:", "Đạo diễn:", "Thời lượng (phút):", "Thể loại:", "Quốc gia:",
                "Ngôn ngữ:", "Giới hạn tuổi:", "Giá vé:", "Nội dung:", "Ngày phát hành (yyyy-mm-dd):", "URL ảnh:"
        };

        JTextField[] fields = {
                titleField, directorNameField, durationField, genreField, countryField,
                languageField, forAgeField, priceField, descriptionField, releasedDateField, imgUrlField
        };

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

        saveButton.addActionListener(e -> saveMovie());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initEditModeComponents(JPanel formPanel, GridBagConstraints gbc, int y) {
        if (this.isEditMode) {
            // Khoi tao nut xem anh
            JButton posterButton = new JButton("Xem ảnh");
            posterButton.addActionListener(e -> toImageUrl());
            gbc.gridx = 0;
            gbc.gridy = y; 
            formPanel.add(posterButton, gbc);

            // Khoi tao nut xem thoi gian chieu
            JButton showtimeButton = new JButton("Xem giờ chiếu");
            showtimeButton.addActionListener(e -> createShowtimeDialog());
            gbc.gridx = 0;
            gbc.gridy = y + 1;
            formPanel.add(showtimeButton, gbc);
        }
    }

    private void toImageUrl() {
        {
            String url = imgUrlField.getText().trim();
            if (!url.isEmpty()) {
                try {
                    Desktop.getDesktop().browse(new java.net.URI(url));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Không thể mở ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void createShowtimeDialog() {
        JDialog showtimeDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Giờ chiếu của phim", true);
        showtimeDialog.setLayout(new BorderLayout());
        showtimeDialog.setSize(400, 300);
        showtimeDialog.setLocationRelativeTo(this);
    
        // Dòng chữ trên cùng
        JLabel headerLabel = new JLabel("Thời gian chiếu", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        showtimeDialog.add(headerLabel, BorderLayout.NORTH);
    
        // Danh sách giờ chiếu
        DefaultListModel<String> listModel = new DefaultListModel<>();
        movie.getShowtimes().forEach(st -> listModel.addElement(st.toString())); // Đảm bảo Showtime.toString() rõ ràng
        JList<String> showtimeList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(showtimeList);
        showtimeDialog.add(scrollPane, BorderLayout.CENTER);
    
        // Panel dưới cùng chứa nút và chú thích
        JPanel bottomPanel = new JPanel(new BorderLayout());
    
        JLabel footerLabel = new JLabel("Cập nhật thời gian chiếu ở tab Thời gian chiếu", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        bottomPanel.add(footerLabel, BorderLayout.NORTH);
    
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> showtimeDialog.dispose());
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
    
        showtimeDialog.add(bottomPanel, BorderLayout.SOUTH);
        showtimeDialog.setVisible(true);
    }
    
    private void saveMovie() {
        if (!isEditMode) {
            movie = new Movie(); // tạo mới nếu là chế độ thêm
            movie.setId(UUID.randomUUID());
        }

        try {
            movie.setTitle(titleField.getText().trim());
            movie.setDirectorName(directorNameField.getText().trim());
            movie.setDuration(Integer.parseInt(durationField.getText().trim()));
            movie.setGenre(genreField.getText().trim());
            movie.setCountry(countryField.getText().trim());
            movie.setLanguage(languageField.getText().trim());
            movie.setForAge(Integer.parseInt(forAgeField.getText().trim()));
            movie.setPrice(new BigDecimal(priceField.getText().trim()));
            movie.setDescription(descriptionField.getText().trim());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            movie.setReleaseDate(LocalDate.parse(releasedDateField.getText().trim(), formatter));

            movie.setPosterURL(imgUrlField.getText().trim());

            boolean success = this.isEditMode 
                              ? this.parentPanel.service.updateMovie(movie)
                              : this.parentPanel.service.createMovie(movie) != null;       

            if (success) {
                JOptionPane.showMessageDialog(this,
                    isEditMode ? "Cập nhật phim thành công!" : "Thêm phim thành công!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                parentPanel.reload();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    isEditMode ? "Cập nhật thất bại." : "Thêm phim thất bại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}