package UI;

import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.sql.Date;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import service.PhimService;

public class ThemPhimPanel extends JPanel {
    private final PhimService service = new PhimService();
    private final JTextField txtTen, txtTheLoai, txtThoiLuong, txtNgayKC, txtPoster;
    private final JTextArea txtMoTa;
    private final JComboBox<String> comboTrangThai;

    public ThemPhimPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = BanVeHelper.darkCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.weightx = 1.0;

        JLabel title = new JLabel("THÊM PHIM MỚI");
        title.setFont(customUI.CustomUI.bold(22));
        title.setForeground(customUI.CustomUI.PRIMARY);
        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(title, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        
        txtTen = createField("Tên phim");
        txtTheLoai = createField("Thể loại");
        txtThoiLuong = createField("Thời lượng (phút)");
        txtNgayKC = createField("Ngày khởi chiếu (yyyy-mm-dd)");
        txtPoster = createField("Tên file ảnh poster");
        txtPoster.setEditable(false); // Không cho nhập tay để tránh sai sót
        
        addFormItem(form, "Tên phim", txtTen, gbc);
        addFormItem(form, "Thể loại", txtTheLoai, gbc);
        addFormItem(form, "Thời lượng", txtThoiLuong, gbc);
        addFormItem(form, "Ngày khởi chiếu", txtNgayKC, gbc);
        
        // Poster item với nút chọn ảnh
        addPosterItem(form, gbc);

        // Trạng thái
        gbc.gridy++;
        JLabel lblTrangThai = new JLabel("Trạng thái");
        lblTrangThai.setFont(customUI.CustomUI.plain(13));
        lblTrangThai.setForeground(customUI.CustomUI.TEXT_LIGHT);
        form.add(lblTrangThai, gbc);
        
        gbc.gridy++;
        comboTrangThai = new JComboBox<>(new String[]{"Đang chiếu", "Sắp chiếu", "Ngừng chiếu"});
        comboTrangThai.setBackground(BanVeHelper.BG_FIELD);
        comboTrangThai.setForeground(Color.WHITE);
        form.add(comboTrangThai, gbc);

        // Mô tả
        gbc.gridy++;
        JLabel lblMoTa = new JLabel("Mô tả");
        lblMoTa.setFont(customUI.CustomUI.plain(13));
        lblMoTa.setForeground(customUI.CustomUI.TEXT_LIGHT);
        form.add(lblMoTa, gbc);
        
        gbc.gridy++;
        txtMoTa = new JTextArea(4, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setBackground(BanVeHelper.BG_FIELD);
        txtMoTa.setForeground(Color.WHITE);
        txtMoTa.setCaretColor(Color.WHITE);
        txtMoTa.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        scrollMoTa.setPreferredSize(new Dimension(0, 80));
        form.add(scrollMoTa, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0);
        JButton btnSave = BanVeHelper.primaryBtn("Lưu phim");
        btnSave.addActionListener(e -> handleSave());
        form.add(btnSave, gbc);

        // Gap để không bị bot che khuất
        gbc.gridy++;
        form.add(Box.createVerticalStrut(70), gbc);

        JScrollPane mainScroll = new JScrollPane(form);
        mainScroll.setOpaque(false);
        mainScroll.getViewport().setOpaque(false);
        mainScroll.setBorder(null);
        
        card.add(mainScroll, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
    }

    private JTextField createField(String placeholder) {
        return BanVeHelper.placeholderField(placeholder);
    }

    private void addFormItem(JPanel p, String label, JTextField field, GridBagConstraints gbc) {
        gbc.gridy++;
        JLabel l = new JLabel(label);
        l.setFont(customUI.CustomUI.plain(13));
        l.setForeground(customUI.CustomUI.TEXT_LIGHT);
        p.add(l, gbc);
        gbc.gridy++;
        p.add(field, gbc);
    }

    private void addPosterItem(JPanel p, GridBagConstraints gbc) {
        gbc.gridy++;
        JLabel l = new JLabel("Poster Phim");
        l.setFont(customUI.CustomUI.plain(13));
        l.setForeground(customUI.CustomUI.TEXT_LIGHT);
        p.add(l, gbc);

        gbc.gridy++;
        JPanel posterPanel = new JPanel(new BorderLayout(10, 0));
        posterPanel.setOpaque(false);
        posterPanel.add(txtPoster, BorderLayout.CENTER);
        
        JButton btnBrowse = BanVeHelper.primaryBtn("Chọn ảnh");
        btnBrowse.setPreferredSize(new Dimension(120, 46));
        btnBrowse.addActionListener(e -> handleBrowsePoster());
        posterPanel.add(btnBrowse, BorderLayout.EAST);
        
        p.add(posterPanel, gbc);
    }

    private void handleBrowsePoster() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn Poster Phim");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Đường dẫn lưu trữ trong project
                String targetDir = "src/resources/list_film/";
                File dir = new File(targetDir);
                if (!dir.exists()) dir.mkdirs();
                
                String fileName = selectedFile.getName();
                Path targetPath = Paths.get(targetDir + fileName);
                
                // Copy file vào thư mục tài nguyên
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                
                txtPoster.setText(fileName);
                JOptionPane.showMessageDialog(this, "Đã tải ảnh lên: " + fileName);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu ảnh: " + ex.getMessage());
            }
        }
    }

    private String getRealText(JTextField f, String ph) {
        String t = f.getText();
        return t.equals(ph) ? "" : t;
    }

    private void handleSave() {
        try {
            String ten = getRealText(txtTen, "Tên phim");
            String theLoai = getRealText(txtTheLoai, "Thể loại");
            String thoiLuongStr = getRealText(txtThoiLuong, "Thời lượng (phút)");
            String ngayKCStr = getRealText(txtNgayKC, "Ngày khởi chiếu (yyyy-mm-dd)");
            String poster = txtPoster.getText();
            String moTa = txtMoTa.getText();
            String trangThai = (String) comboTrangThai.getSelectedItem();

            if (ten.isEmpty() || thoiLuongStr.isEmpty() || ngayKCStr.isEmpty() || poster.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ các trường bắt buộc bao gồm cả Poster!");
                return;
            }

            int thoiLuong = Integer.parseInt(thoiLuongStr);
            Date ngayKC = Date.valueOf(ngayKCStr);

            if (service.addPhim(ten, theLoai, thoiLuong, ngayKC, moTa, trangThai, poster)) {
                JOptionPane.showMessageDialog(this, "Thêm phim thành công!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm phim thất bại!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi dữ liệu: " + e.getMessage());
        }
    }

    private void clearFields() {
        txtTen.setText("");
        txtTheLoai.setText("");
        txtThoiLuong.setText("");
        txtNgayKC.setText("");
        txtMoTa.setText("");
        txtPoster.setText("");
    }
}