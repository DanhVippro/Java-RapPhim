package UI;

import java.awt.*;
import java.sql.Date;
import javax.swing.*;
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

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        JLabel title = new JLabel("THÊM PHIM MỚI");
        title.setFont(customUI.CustomUI.bold(22));
        title.setForeground(customUI.CustomUI.PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(title);
        form.add(Box.createVerticalStrut(20));

        txtTen = createField("Tên phim");
        txtTheLoai = createField("Thể loại");
        txtThoiLuong = createField("Thời lượng (phút)");
        txtNgayKC = createField("Ngày khởi chiếu (yyyy-mm-dd)");
        txtPoster = createField("Tên file ảnh poster (e.g. poster1.jpg)");
        
        form.add(createInputGroup("Tên phim", txtTen));
        form.add(Box.createVerticalStrut(12));
        form.add(createInputGroup("Thể loại", txtTheLoai));
        form.add(Box.createVerticalStrut(12));
        form.add(createInputGroup("Thời lượng", txtThoiLuong));
        form.add(Box.createVerticalStrut(12));
        form.add(createInputGroup("Ngày khởi chiếu", txtNgayKC));
        form.add(Box.createVerticalStrut(12));
        form.add(createInputGroup("Poster", txtPoster));
        form.add(Box.createVerticalStrut(12));

        JLabel lblTrangThai = new JLabel("Trạng thái");
        lblTrangThai.setFont(customUI.CustomUI.plain(13));
        lblTrangThai.setForeground(customUI.CustomUI.TEXT_LIGHT);
        form.add(lblTrangThai);
        form.add(Box.createVerticalStrut(4));
        comboTrangThai = new JComboBox<>(new String[]{"Đang chiếu", "Sắp chiếu", "Ngừng chiếu"});
        comboTrangThai.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        form.add(comboTrangThai);
        form.add(Box.createVerticalStrut(12));

        JLabel lblMoTa = new JLabel("Mô tả");
        lblMoTa.setFont(customUI.CustomUI.plain(13));
        lblMoTa.setForeground(customUI.CustomUI.TEXT_LIGHT);
        form.add(lblMoTa);
        form.add(Box.createVerticalStrut(4));
        txtMoTa = new JTextArea(4, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setBackground(BanVeHelper.BG_FIELD);
        txtMoTa.setForeground(Color.WHITE);
        txtMoTa.setCaretColor(Color.WHITE);
        txtMoTa.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        scrollMoTa.setBorder(BorderFactory.createLineBorder(new Color(0x3A4C5E)));
        scrollMoTa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        form.add(scrollMoTa);
        form.add(Box.createVerticalStrut(25));

        JButton btnSave = BanVeHelper.primaryBtn("Lưu phim");
        btnSave.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSave.addActionListener(e -> handleSave());
        form.add(btnSave);

        card.add(form, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
    }

    private JTextField createField(String placeholder) {
        return BanVeHelper.placeholderField(placeholder);
    }

    private JPanel createInputGroup(String label, JTextField field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel l = new JLabel(label);
        l.setFont(customUI.CustomUI.plain(13));
        l.setForeground(customUI.CustomUI.TEXT_LIGHT);
        p.add(l);
        p.add(Box.createVerticalStrut(4));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p.add(field);
        return p;
    }

    private void handleSave() {
        try {
            String ten = txtTen.getText();
            String theLoai = txtTheLoai.getText();
            int thoiLuong = Integer.parseInt(txtThoiLuong.getText());
            Date ngayKC = Date.valueOf(txtNgayKC.getText());
            String moTa = txtMoTa.getText();
            String trangThai = (String) comboTrangThai.getSelectedItem();
            String poster = txtPoster.getText();

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