package UI;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;
import javax.swing.border.*;
import customUI.CustomUI;
import model.BookingState;
import model.CinemaData;

public class SnackPanel extends JPanel {

    private final BookingState state;
    private final Runnable onBack;
    private int[] qty;
    private JLabel[] qtyLabels;

    private JLabel lblSnackTotal, lblGrandTotal;

    public SnackPanel(BookingState state, Runnable onBack) {
        this.state = state;
        this.onBack = onBack;
        state.resetSnack();
        this.qty = new int[CinemaData.SNACK_DATA.length];
        this.qtyLabels = new JLabel[CinemaData.SNACK_DATA.length];

        setOpaque(false);
        setLayout(new BorderLayout(0, 14));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    // ── Top bar ──────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 44));

        JButton back = new JButton("← Quay lại chọn ghế");
        back.setForeground(new Color(0x00B8D4));
        back.setFont(CustomUI.bold(12));
        back.setBorder(BorderFactory.createEmptyBorder());
        back.setOpaque(false);
        back.setContentAreaFilled(false);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> onBack.run());

        JLabel title = new JLabel("Bắp & Nước");
        title.setFont(CustomUI.bold(22));
        title.setForeground(CustomUI.TEXT_LIGHT);

        bar.add(back, BorderLayout.WEST);
        bar.add(title, BorderLayout.CENTER);
        return bar;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
        body.setOpaque(false);
        body.add(buildSnackGrid());
        body.add(buildOrderDetail());
        return body;
    }

    private JPanel buildSnackGrid() {
        JPanel grid = new JPanel(new GridLayout(4, 2, 10, 10));
        grid.setOpaque(false);
        for (int i = 0; i < CinemaData.SNACK_DATA.length; i++)
            grid.add(buildSnackItem(i));
        return grid;
    }

    private JPanel buildSnackItem(int idx) {
        String icon  = (String)  CinemaData.SNACK_DATA[idx][0];
        String name  = (String)  CinemaData.SNACK_DATA[idx][1];
        String desc  = (String)  CinemaData.SNACK_DATA[idx][2];
        String price = (String)  CinemaData.SNACK_DATA[idx][3];
        Color  color = new Color((int) CinemaData.SNACK_DATA[idx][5]);
        Object imgPath = CinemaData.SNACK_DATA[idx][6];
        boolean isCombo = name.startsWith("Combo");

        // --- Card container ---
        JPanel card = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1A2A3A));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (isCombo) {
                    g2.setStroke(new BasicStroke(2f));
                    g2.setColor(color);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                }
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Ảnh đồ ăn ---
        JLabel imgLbl;
        if (imgPath != null) {
            java.net.URL url = getClass().getResource((String) imgPath);
            if (url != null) {
                ImageIcon raw = new ImageIcon(url);
                Image scaled  = raw.getImage().getScaledInstance(52, 52, Image.SCALE_SMOOTH);
                imgLbl = new JLabel(new ImageIcon(scaled)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        // Bo tròn ảnh
                        g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, 52, 52));
                        g2.drawImage(((ImageIcon) getIcon()).getImage(), 0, 0, null);
                        g2.dispose();
                    }
                };
            } else {
                imgLbl = new JLabel(icon, JLabel.CENTER);
                imgLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
            }
        } else {
            // Combo: vẽ badge đặc biệt
            imgLbl = new JLabel(icon, JLabel.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(color.darker().darker());
                    g2.fillOval(0, 0, 52, 52);
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(1, 1, 50, 50);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            imgLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
            imgLbl.setHorizontalAlignment(JLabel.CENTER);
        }
        imgLbl.setPreferredSize(new Dimension(52, 52));
        imgLbl.setOpaque(false);
        card.add(imgLbl, BorderLayout.WEST);

        // --- Info ---
        JPanel info = new JPanel(new BorderLayout(0, 3));
        info.setOpaque(false);

        JPanel texts = new JPanel(new GridLayout(3, 1, 0, 1));
        texts.setOpaque(false);
        JLabel nl = new JLabel(name);
        nl.setFont(CustomUI.bold(11));
        nl.setForeground(isCombo ? color : CustomUI.TEXT_WHITE);
        JLabel dl = new JLabel(desc);
        dl.setFont(CustomUI.plain(9));
        dl.setForeground(CustomUI.TEXT_LIGHT);
        JLabel pl = new JLabel(price);
        pl.setFont(CustomUI.bold(10));
        pl.setForeground(color);
        texts.add(nl); texts.add(dl); texts.add(pl);
        info.add(texts, BorderLayout.CENTER);

        JPanel stepper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        stepper.setOpaque(false);
        JButton minus = stepperBtn("−");
        qtyLabels[idx] = new JLabel("0", JLabel.CENTER);
        qtyLabels[idx].setFont(CustomUI.bold(13));
        qtyLabels[idx].setForeground(CustomUI.TEXT_WHITE);
        qtyLabels[idx].setPreferredSize(new Dimension(24, 24));
        JButton plus = stepperBtn("+");

        int fi = idx;
        minus.addActionListener(e -> {
            if (qty[fi] > 0) {
                qty[fi]--;
                state.snackQty[fi] = qty[fi];
                qtyLabels[fi].setText(String.valueOf(qty[fi]));
                refreshTotals();
            }
        });
        plus.addActionListener(e -> {
            qty[fi]++;
            state.snackQty[fi] = qty[fi];
            qtyLabels[fi].setText(String.valueOf(qty[fi]));
            refreshTotals();
        });

        stepper.add(minus);
        stepper.add(qtyLabels[idx]);
        stepper.add(plus);
        info.add(stepper, BorderLayout.SOUTH);
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    // ── Chi tiết đơn hàng (phải) ─────────────────────────────────────────────
    private JPanel buildOrderDetail() {
        JPanel outer = BanVeHelper.darkCard();
        outer.setLayout(new BorderLayout(0, 0));

        // Scrollable content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        content.add(sectionLbl("📋  CHI TIẾT ĐẶT VÉ"));
        content.add(vgap(10));

        content.add(sumRow("🎬  Phim", CinemaData.PHIM_LIST[state.phimIdx]));
        content.add(vgap(5));
        content.add(sumRow("⏰  Suất chiếu", state.gioBatDau() + " – " + state.gioKetThuc()));
        content.add(vgap(5));
        content.add(sumRow("📅  Ngày", state.thuDisplay() + ", " + state.ngayChieu()));
        content.add(vgap(5));
        content.add(sumRow("🏛️  Phòng", CinemaData.PHONG_BY_PHIM[state.phimIdx][state.phongIdx]));
        content.add(vgap(5));
        content.add(sumRow("🪑  Ghế", state.gheDisplay() + "  (" + state.loaiGheDisplay() + ")"));
        content.add(vgap(5));
        content.add(sumRow("💰  Tiền vé", BanVeHelper.formatVND(state.tienVe())));
        content.add(vgap(10));
        content.add(divider());
        content.add(vgap(10));

        // ── Thông tin người nhận ─────────────────────────────────────────────
        content.add(sectionLbl("📨  THÔNG TIN NGƯỜI NHẬN"));
        content.add(vgap(6));
        content.add(sumRow("👤  Họ tên", emptyOrDash(state.tenKhachHang)));
        content.add(vgap(4));
        content.add(sumRow("📱  Điện thoại", emptyOrDash(state.soDienThoai)));
        content.add(vgap(4));
        content.add(sumRow("📧  Email", emptyOrDash(state.email)));
        content.add(vgap(4));

        JLabel gmailNote = new JLabel("✉️  Vé sẽ được gửi qua email sau khi xác nhận");
        gmailNote.setFont(CustomUI.plain(10));
        gmailNote.setForeground(new Color(0x607D8B));
        gmailNote.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        gmailNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(gmailNote);
        content.add(vgap(10));
        content.add(divider());
        content.add(vgap(10));

        // ── Tổng snack & vé ──────────────────────────────────────────────────
        lblSnackTotal = new JLabel("0 đ");
        lblSnackTotal.setFont(CustomUI.bold(12));
        lblSnackTotal.setForeground(new Color(0xF59E0B));
        content.add(sumRowDynamic("🍿  Bắp & Nước", lblSnackTotal));
        content.add(vgap(5));

        JLabel tienVeLbl = new JLabel(BanVeHelper.formatVND(state.tienVe()));
        tienVeLbl.setFont(CustomUI.bold(12));
        tienVeLbl.setForeground(CustomUI.TEXT_WHITE);
        content.add(sumRowDynamic("🎫  Tiền vé", tienVeLbl));
        content.add(vgap(8));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        outer.add(scroll, BorderLayout.CENTER);

        // ── Footer tạm tính + Xác nhận ───────────────────────────────────────
        JPanel footer = new JPanel(new BorderLayout(0, 8));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x2D3F4F)),
                BorderFactory.createEmptyBorder(10, 16, 12, 16)));

        JPanel grandRow = new JPanel(new BorderLayout());
        grandRow.setOpaque(false);
        JLabel gl = new JLabel("TẠM TÍNH");
        gl.setFont(CustomUI.bold(12));
        gl.setForeground(CustomUI.TEXT_LIGHT);
        lblGrandTotal = new JLabel(BanVeHelper.formatVND(state.tienVe()));
        lblGrandTotal.setFont(CustomUI.bold(20));
        lblGrandTotal.setForeground(new Color(0x00B8D4));
        grandRow.add(gl, BorderLayout.WEST);
        grandRow.add(lblGrandTotal, BorderLayout.EAST);
        footer.add(grandRow, BorderLayout.NORTH);

        JButton btnPay = CustomUI.createPrimaryButton("Xác nhận thanh toán →");
        btnPay.setPreferredSize(new Dimension(0, 42));
        btnPay.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(this);
            ConfirmDialog dlg = new ConfirmDialog(win, state);
            dlg.setVisible(true);
        });
        footer.add(btnPay, BorderLayout.SOUTH);

        outer.add(footer, BorderLayout.SOUTH);
        return outer;
    }

    // ── Bottom bar: Làm mới ───────────────────────────────────────────────────
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bar.setOpaque(false);

        JButton btnReset = new JButton("🔄  Làm mới");
        btnReset.setFont(CustomUI.plain(12));
        btnReset.setForeground(CustomUI.TEXT_LIGHT);
        btnReset.setBackground(new Color(0x1A2A39));
        btnReset.setBorder(BorderFactory.createLineBorder(new Color(0x3A4C5E)));
        btnReset.setFocusPainted(false);
        btnReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> resetAll());

        bar.add(btnReset);
        return bar;
    }

    private void resetAll() {
        qty = new int[CinemaData.SNACK_DATA.length];
        state.resetSnack();
        for (int i = 0; i < qtyLabels.length; i++)
            if (qtyLabels[i] != null)
                qtyLabels[i].setText("0");
        refreshTotals();
    }

    private void refreshTotals() {
        long snack = state.tienSnack();
        long grand = state.tongCong();
        if (lblSnackTotal != null)
            lblSnackTotal.setText(BanVeHelper.formatVND(snack));
        if (lblGrandTotal != null)
            lblGrandTotal.setText(BanVeHelper.formatVND(grand));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private JButton stepperBtn(String t) {
        JButton b = new JButton(t);
        b.setPreferredSize(new Dimension(28, 28));
        b.setFont(new Font("Arial", Font.BOLD, 16));  // Arial chắc chắn có + và -
        b.setForeground(CustomUI.TEXT_WHITE);
        b.setBackground(new Color(0x243447));
        b.setOpaque(true);
        b.setBorder(BorderFactory.createLineBorder(new Color(0x3A5070)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JLabel sectionLbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(CustomUI.bold(11));
        l.setForeground(new Color(0x90CAF9));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel sumRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setFont(CustomUI.plain(11));
        l.setForeground(CustomUI.TEXT_LIGHT);
        l.setPreferredSize(new Dimension(130, 20));
        JLabel v = new JLabel(value);
        v.setFont(CustomUI.bold(11));
        v.setForeground(CustomUI.TEXT_WHITE);
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.CENTER);
        return row;
    }

    private JPanel sumRowDynamic(String label, JLabel valLbl) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setFont(CustomUI.plain(11));
        l.setForeground(CustomUI.TEXT_LIGHT);
        row.add(l, BorderLayout.WEST);
        row.add(valLbl, BorderLayout.EAST);
        return row;
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

    private String emptyOrDash(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
}