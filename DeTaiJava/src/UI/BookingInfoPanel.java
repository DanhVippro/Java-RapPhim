package UI;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;
import customUI.CustomUI;
import model.BookingState;
import model.CinemaData;

/**
 * BookingInfoPanel – Panel thông tin đặt vé bên phải.
 * Có JScrollPane để scroll khi tab nhỏ.
 * Hiển thị: phim/suất/phòng/ghế/loại + form khách hàng + tổng tiền + nút xác nhận.
 */
public class BookingInfoPanel extends JPanel {

    private final BookingState state;
    private final Runnable     onConfirm;

    // Dynamic labels cập nhật khi chọn ghế
    private JLabel lblGhe, lblLoai, lblTong;

    // Poster + combos
    private JLabel            posterLabel;
    private JComboBox<String> comboRap, comboPhim, comboSuat, comboPhong;

    // Form fields
    private JTextField fieldTen, fieldPhone, fieldEmail;

    public BookingInfoPanel(BookingState state, Runnable onConfirm) {
        this.state     = state;
        this.onConfirm = onConfirm;
        setOpaque(false);
        setLayout(new BorderLayout());

        JPanel inner = buildInner();

        // ── Bọc trong JScrollPane để scroll khi tab nhỏ ───────────────────
        JScrollPane scroll = new JScrollPane(inner);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        // Thanh scroll style tối
        scroll.getVerticalScrollBar().setBackground(new Color(0x192330));

        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildInner() {
        JPanel card = BanVeHelper.darkCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // ── Section: Chọn phim / rạp / suất / phòng ─────────────────────────
        content.add(sectionLabel("🎬  CHỌN PHIM & SUẤT CHIẾU"));
        content.add(vgap(10));
        content.add(buildSelectionSection());
        content.add(vgap(16));

        // ── Divider ──────────────────────────────────────────────────────────
        content.add(divider());
        content.add(vgap(12));

        // ── Section: Thông tin ghế (dynamic) ─────────────────────────────────
        content.add(sectionLabel("🪑  THÔNG TIN GHẾ"));
        content.add(vgap(8));

        lblGhe  = boldVal("-");
        lblLoai = boldVal("-");
        content.add(infoRow("Ghế đã chọn", lblGhe));
        content.add(vgap(5));
        content.add(infoRow("Loại ghế", lblLoai));
        content.add(vgap(16));

        // ── Divider ──────────────────────────────────────────────────────────
        content.add(divider());
        content.add(vgap(12));

        // ── Section: Thông tin khách hàng ────────────────────────────────────
        content.add(sectionLabel("👤  THÔNG TIN KHÁCH HÀNG"));
        content.add(vgap(10));

        fieldTen   = styledField("Nhập họ và tên...");
        fieldPhone = styledField("Nhập số điện thoại...");
        fieldEmail = styledField("Nhập email nhận vé...");

        content.add(labeledField("Họ và tên", fieldTen));
        content.add(vgap(8));
        content.add(labeledField("Số điện thoại", fieldPhone));
        content.add(vgap(8));
        content.add(labeledField("Email", fieldEmail));
        content.add(vgap(16));

        // ── Divider ──────────────────────────────────────────────────────────
        content.add(divider());
        content.add(vgap(12));

        // ── Tổng tiền ────────────────────────────────────────────────────────
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        JLabel tlbl = new JLabel("Tổng cộng");
        tlbl.setFont(CustomUI.bold(14));
        tlbl.setForeground(CustomUI.TEXT_LIGHT);
        lblTong = new JLabel("0 đ");
        lblTong.setFont(CustomUI.bold(20));
        lblTong.setForeground(new Color(0x00B8D4));
        totalRow.add(tlbl, BorderLayout.WEST);
        totalRow.add(lblTong, BorderLayout.EAST);
        content.add(totalRow);
        content.add(vgap(16));

        // ── Nút Xác nhận ─────────────────────────────────────────────────────
        JButton btnConfirm = CustomUI.createPrimaryButton("Xác nhận → Chọn bắp & nước");
        btnConfirm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnConfirm.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnConfirm.addActionListener(e -> handleConfirm());
        content.add(btnConfirm);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    // ── Selection section (poster + 4 combos) ────────────────────────────────
    private JPanel buildSelectionSection() {
        JPanel wrap = new JPanel(new BorderLayout(12, 0));
        wrap.setOpaque(false);
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // Poster nhỏ bên trái
        posterLabel = new JLabel(buildPosterIcon(0, 80, 115));
        posterLabel.setPreferredSize(new Dimension(80, 115));
        wrap.add(posterLabel, BorderLayout.WEST);

        // Combos 2x2 bên phải
        JPanel combos = new JPanel(new GridLayout(2, 2, 10, 10));
        combos.setOpaque(false);

        comboRap = styledCombo(CinemaData.RAP_LIST);
        comboRap.addActionListener(e -> state.rapIdx = comboRap.getSelectedIndex());
        combos.add(wrapCombo("Rạp chiếu", comboRap));

        comboPhim = styledCombo(CinemaData.PHIM_LIST);
        comboPhim.addActionListener(e -> {
            state.phimIdx = comboPhim.getSelectedIndex();
            state.suatIdx = 0; state.phongIdx = 0;
            comboSuat.setModel(new DefaultComboBoxModel<>(CinemaData.SUAT_BY_PHIM[state.phimIdx]));
            comboPhong.setModel(new DefaultComboBoxModel<>(CinemaData.PHONG_BY_PHIM[state.phimIdx]));
            posterLabel.setIcon(buildPosterIcon(state.phimIdx, 80, 115));
        });
        combos.add(wrapCombo("Phim", comboPhim));

        comboSuat = styledCombo(CinemaData.SUAT_BY_PHIM[0]);
        comboSuat.addActionListener(e -> state.suatIdx = Math.max(0, comboSuat.getSelectedIndex()));
        combos.add(wrapCombo("Suất chiếu", comboSuat));

        comboPhong = styledCombo(CinemaData.PHONG_BY_PHIM[0]);
        comboPhong.addActionListener(e -> state.phongIdx = Math.max(0, comboPhong.getSelectedIndex()));
        combos.add(wrapCombo("Phòng chiếu", comboPhong));

        wrap.add(combos, BorderLayout.CENTER);
        return wrap;
    }

    // ── Validate + sync state → gọi callback ─────────────────────────────────
    private void handleConfirm() {
        if (state.seats.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn ít nhất một ghế trước khi tiếp tục.",
                "Chưa chọn ghế", JOptionPane.WARNING_MESSAGE);
            return;
        }
        state.tenKhachHang = fieldTen.getText().trim();
        state.soDienThoai  = fieldPhone.getText().trim();
        state.email        = fieldEmail.getText().trim();
        onConfirm.run();
    }

    /** Gọi từ ngoài khi ghế thay đổi để cập nhật labels */
    public void refreshSeatInfo() {
        if (state.seats.isEmpty()) {
            lblGhe.setText("-");
            lblLoai.setText("-");
            lblTong.setText("0 đ");
        } else {
            lblGhe.setText(state.gheDisplay());
            lblLoai.setText(state.loaiGheDisplay());
            lblTong.setText(BanVeHelper.formatVND(state.tienVe()));
        }
    }

    // ── Poster ───────────────────────────────────────────────────────────────
    private ImageIcon buildPosterIcon(int idx, int w, int h) {
    try {
        ImageIcon icon = new ImageIcon(getClass().getResource("/" + CinemaData.POSTER_PATH[idx]));
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    } catch (Exception e) {
        System.out.println("Lỗi load ảnh poster: " + e.getMessage());
        return new ImageIcon();
    }
}

    // ── UI Helpers ────────────────────────────────────────────────────────────
    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(CustomUI.bold(12));
        l.setForeground(new Color(0x90CAF9));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel boldVal(String text) {
        JLabel l = new JLabel(text);
        l.setFont(CustomUI.bold(13));
        l.setForeground(CustomUI.TEXT_WHITE);
        return l;
    }

    private JPanel infoRow(String label, JLabel val) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setFont(CustomUI.plain(12));
        l.setForeground(CustomUI.TEXT_LIGHT);
        row.add(l, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    private JPanel labeledField(String labelText, JTextField field) {
        JPanel wrap = new JPanel(new BorderLayout(0, 4));
        wrap.setOpaque(false);
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(labelText);
        l.setFont(CustomUI.plain(11));
        l.setForeground(CustomUI.TEXT_LIGHT);
        wrap.add(l, BorderLayout.NORTH);
        wrap.add(field, BorderLayout.CENTER);
        return wrap;
    }

    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField();
        f.setToolTipText(placeholder);
        // Placeholder effect
        f.setText(placeholder);
        f.setForeground(new Color(0x607D8B));
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(CustomUI.TEXT_WHITE); }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(placeholder); f.setForeground(new Color(0x607D8B)); }
            }
        });
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x3A4C5E)),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        f.setBackground(new Color(0x1A2A39));
        f.setFont(CustomUI.plain(12));
        f.setCaretColor(CustomUI.TEXT_WHITE);
        return f;
    }

    private JPanel divider() {
        JPanel d = new JPanel();
        d.setOpaque(true);
        d.setBackground(new Color(0x2D3F4F));
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        d.setPreferredSize(new Dimension(0, 1));
        d.setAlignmentX(Component.LEFT_ALIGNMENT);
        return d;
    }

    private Component vgap(int h) { return Box.createVerticalStrut(h); }

    private <T> JComboBox<T> styledCombo(T[] items) {
        JComboBox<T> c = new JComboBox<>(items);
        c.setBackground(new Color(0x16212A));
        c.setForeground(CustomUI.TEXT_LIGHT);
        c.setFont(CustomUI.plain(11));
        c.setBorder(BorderFactory.createLineBorder(new Color(0x2D3F4F)));
        return c;
    }

    private JPanel wrapCombo(String label, JComboBox<?> combo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(CustomUI.bold(10));
        l.setForeground(CustomUI.TEXT_LIGHT);
        p.add(l, BorderLayout.NORTH);
        p.add(combo, BorderLayout.CENTER);
        return p;
    }
}