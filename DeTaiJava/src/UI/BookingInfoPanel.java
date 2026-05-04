package UI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import customUI.CustomUI;
import model.BookingState;
import model.CinemaData;

/**
 * BookingInfoPanel – Panel thông tin đặt vé bên phải.
 *
 * Thêm mới: comboPhong khi thay đổi sẽ gọi onRoomChange để
 * SeatMapPanel rebuild lại sơ đồ ghế theo phòng mới.
 * Phòng 3 (roomKey=2) → FULL, toàn bộ ghế disabled.
 */
public class BookingInfoPanel extends JPanel {

    /** Callback khi đổi phòng: truyền roomKey để rebuild seat map */
    public interface RoomChangeListener {
        void onRoomChanged(int roomKey);
    }

    private final BookingState state;
    private final Runnable onConfirm;
    private final RoomChangeListener onRoomChange;

    // Dynamic labels
    private JTextArea lblGhe;
    private JLabel lblLoai, lblTong;

    // Poster + combos
    private JLabel posterLabel;
    private JComboBox<String> comboRap, comboPhim, comboSuat, comboPhong;

    // Form fields
    private JTextField fieldTen, fieldPhone, fieldEmail;

    // ── Constructor ───────────────────────────────────────────────────────────
    public BookingInfoPanel(BookingState state, Runnable onConfirm,
            RoomChangeListener onRoomChange) {
        this.state = state;
        this.onConfirm = onConfirm;
        this.onRoomChange = onRoomChange;
        setOpaque(false);
        setLayout(new BorderLayout());

        JScrollPane scroll = new JScrollPane(buildInner());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.getVerticalScrollBar().setBackground(new Color(0x192330));
        add(scroll, BorderLayout.CENTER);
    }

    // ── Build nội dung chính ──────────────────────────────────────────────────
    private JPanel buildInner() {
        JPanel card = BanVeHelper.darkCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        content.add(sectionLabel("🎬  CHỌN PHIM & SUẤT CHIẾU"));
        content.add(vgap(10));
        content.add(buildSelectionSection());
        content.add(vgap(16));
        content.add(divider());
        content.add(vgap(12));

        // ── Ghế đã chọn ──────────────────────────────────────────────────────
        content.add(sectionLabel("🪑  THÔNG TIN GHẾ"));
        content.add(vgap(8));

        lblGhe = new JTextArea("-");
        lblGhe.setFont(CustomUI.bold(12));
        lblGhe.setForeground(CustomUI.TEXT_WHITE);
        lblGhe.setOpaque(false);
        lblGhe.setEditable(false);
        lblGhe.setFocusable(false);
        lblGhe.setLineWrap(true);
        lblGhe.setWrapStyleWord(true);
        lblGhe.setBorder(BorderFactory.createEmptyBorder());
        lblGhe.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblLoai = boldVal("-");
        content.add(seatRow("Ghế đã chọn", lblGhe));
        content.add(vgap(5));
        content.add(infoRow("Loại ghế", lblLoai));
        content.add(vgap(16));
        content.add(divider());
        content.add(vgap(12));

        // ── Thông tin khách hàng ─────────────────────────────────────────────
        content.add(sectionLabel("👤  THÔNG TIN KHÁCH HÀNG"));
        content.add(vgap(10));

        fieldTen = styledField("Nhập họ và tên...");
        fieldPhone = styledField("Nhập số điện thoại...");
        fieldEmail = styledField("Nhập email nhận vé...");

        content.add(labeledField("Họ và tên", fieldTen));
        content.add(vgap(8));
        content.add(labeledField("Số điện thoại", fieldPhone));
        content.add(vgap(8));
        content.add(labeledField("Email", fieldEmail));
        content.add(vgap(16));
        content.add(divider());
        content.add(vgap(12));

        // ── Tổng tiền ────────────────────────────────────────────────────────
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setAlignmentX(Component.LEFT_ALIGNMENT);
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

    // ── Selection: poster + 4 combos ─────────────────────────────────────────
    private JPanel buildSelectionSection() {
        JPanel wrap = new JPanel(new BorderLayout(12, 0));
        wrap.setOpaque(false);
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);

        posterLabel = new JLabel(buildPosterIcon(0, 80, 115));
        posterLabel.setPreferredSize(new Dimension(80, 115));
        wrap.add(posterLabel, BorderLayout.WEST);

        JPanel combos = new JPanel(new GridLayout(2, 2, 10, 10));
        combos.setOpaque(false);

        // Rạp
        comboRap = styledCombo(CinemaData.RAP_LIST);
        comboRap.addActionListener(e -> state.rapIdx = comboRap.getSelectedIndex());
        combos.add(wrapCombo("Rạp chiếu", comboRap));

        // Phim → cập nhật suất + phòng + poster
        comboPhim = styledCombo(CinemaData.PHIM_LIST);
        comboPhim.addActionListener(e -> {
            state.phimIdx = comboPhim.getSelectedIndex();
            state.suatIdx = 0;
            state.phongIdx = 0;
            comboSuat.setModel(new DefaultComboBoxModel<>(
                    CinemaData.SUAT_BY_PHIM[state.phimIdx]));
            rebuildPhongCombo(state.phimIdx);
            posterLabel.setIcon(buildPosterIcon(state.phimIdx, 80, 115));
        });
        combos.add(wrapCombo("Phim", comboPhim));

        // Suất chiếu
        comboSuat = styledCombo(CinemaData.SUAT_BY_PHIM[0]);
        comboSuat.addActionListener(e -> state.suatIdx = Math.max(0, comboSuat.getSelectedIndex()));
        combos.add(wrapCombo("Suất chiếu", comboSuat));

        // Phòng chiếu
        comboPhong = styledCombo(CinemaData.PHONG_BY_PHIM[0]);
        comboPhong.addActionListener(e -> handlePhongChange());
        combos.add(wrapCombo("Phòng chiếu", comboPhong));

        wrap.add(combos, BorderLayout.CENTER);
        return wrap;
    }

    /** Xử lý khi đổi phòng */
    private void handlePhongChange() {
        int sel = Math.max(0, comboPhong.getSelectedIndex());
        state.phongIdx = sel;
        // Xoá ghế đang chọn khi đổi phòng
        state.seats.clear();
        state.seatsVip.clear();
        refreshSeatInfo();
        // Notify parent để rebuild seat map
        if (onRoomChange != null) {
            int roomKey = getRoomKey(state.phimIdx, sel);
            onRoomChange.onRoomChanged(roomKey);
        }
    }

    /** Rebuild danh sách phòng khi đổi phim, sau đó trigger room change */
    private void rebuildPhongCombo(int phimIdx) {
        // Xoá listener tạm thời tránh trigger khi đang set model
        var listeners = comboPhong.getActionListeners();
        for (var l : listeners)
            comboPhong.removeActionListener(l);

        comboPhong.setModel(new DefaultComboBoxModel<>(
                CinemaData.PHONG_BY_PHIM[phimIdx]));
        comboPhong.setSelectedIndex(0);
        state.phongIdx = 0;

        // Gắn lại listener
        for (var l : listeners)
            comboPhong.addActionListener(l);

        // Trigger thủ công
        state.seats.clear();
        state.seatsVip.clear();
        refreshSeatInfo();
        if (onRoomChange != null) {
            onRoomChange.onRoomChanged(getRoomKey(phimIdx, 0));
        }
    }

    /** Tính roomKey từ phimIdx + phongIdx */
    private int getRoomKey(int phimIdx, int phongIdx) {
        if (phimIdx >= CinemaData.PHONG_ROOM_KEY.length)
            return 0;

        int[] keys = CinemaData.PHONG_ROOM_KEY[phimIdx];

        if (phongIdx >= keys.length)
            return 0;

        return keys[phongIdx];
    }

    /** Lấy roomKey hiện tại (dùng khi SeatMapPanel khởi tạo) */
    public int getCurrentRoomKey() {
        return getRoomKey(state.phimIdx, state.phongIdx);
    }

    // ── Validate + sync state ─────────────────────────────────────────────────
    private void handleConfirm() {
        if (state.seats.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn ít nhất một ghế trước khi tiếp tục.",
                    "Chưa chọn ghế", JOptionPane.WARNING_MESSAGE);
            return;
        }
        state.tenKhachHang = realText(fieldTen, "Nhập họ và tên...");
        state.soDienThoai = realText(fieldPhone, "Nhập số điện thoại...");
        state.email = realText(fieldEmail, "Nhập email nhận vé...");
        onConfirm.run();
    }

    private String realText(JTextField f, String placeholder) {
        String t = f.getText().trim();
        return t.equals(placeholder) ? "" : t;
    }

    /** Gọi từ ngoài khi ghế thay đổi */
    public void refreshSeatInfo() {
        if (state.seats.isEmpty()) {
            lblGhe.setText("-");
            if (lblLoai != null)
                lblLoai.setText("-");
            if (lblTong != null)
                lblTong.setText("0 đ");
        } else {
            lblGhe.setText(state.gheDisplay());
            lblLoai.setText(state.loaiGheDisplay());
            lblTong.setText(BanVeHelper.formatVND(state.tienVe()));
        }
        revalidate();
        repaint();
    }

    // ── Poster ───────────────────────────────────────────────────────────────
    private ImageIcon buildPosterIcon(int idx, int w, int h) {
        try {
            ImageIcon icon = new ImageIcon(
                    getClass().getResource("/" + CinemaData.POSTER_PATH[idx]));
            Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return new ImageIcon();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // UI HELPERS
    // ══════════════════════════════════════════════════════════════════════════
    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(CustomUI.bold(12));
        l.setForeground(new Color(0x90CAF9));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel boldVal(String text) {
        JLabel l = new JLabel(text);
        l.setFont(CustomUI.bold(12));
        l.setForeground(CustomUI.TEXT_WHITE);
        return l;
    }

    private JPanel infoRow(String label, JLabel val) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setFont(CustomUI.plain(12));
        l.setForeground(CustomUI.TEXT_LIGHT);
        row.add(l, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    private JPanel seatRow(String labelText, JTextArea area) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints cL = new GridBagConstraints();
        cL.gridx = 0;
        cL.gridy = 0;
        cL.anchor = GridBagConstraints.NORTHWEST;
        cL.insets = new Insets(2, 0, 0, 8);
        JLabel l = new JLabel(labelText);
        l.setFont(CustomUI.plain(12));
        l.setForeground(CustomUI.TEXT_LIGHT);
        row.add(l, cL);
        GridBagConstraints cR = new GridBagConstraints();
        cR.gridx = 1;
        cR.gridy = 0;
        cR.weightx = 1.0;
        cR.fill = GridBagConstraints.HORIZONTAL;
        cR.anchor = GridBagConstraints.NORTHEAST;
        row.add(area, cR);
        return row;
    }

    private JPanel labeledField(String labelText, JTextField field) {
        JPanel wrap = new JPanel(new BorderLayout(0, 4));
        wrap.setOpaque(false);
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
        f.setText(placeholder);
        f.setForeground(new Color(0x607D8B));
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText("");
                    f.setForeground(CustomUI.TEXT_WHITE);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setText(placeholder);
                    f.setForeground(new Color(0x607D8B));
                }
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

    private Component vgap(int h) {
        return Box.createVerticalStrut(h);
    }

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