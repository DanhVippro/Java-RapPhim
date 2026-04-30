package UI;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import customUI.CustomUI;

/**
 * BanVeUI – Trang bán vé rạp chiếu phim
 *
 * Tính năng:
 * 1. Chọn phim → cập nhật suất & phòng, hiển thị ảnh poster bên cạnh
 * 2. Click ghế → cập nhật ghế + loại + tổng tiền ở bảng thông tin đặt vé
 * 3. Chú thích màu ghế khớp với màu trên sơ đồ
 * 4. Xác nhận → màn hình chọn bắp & nước (có nút bỏ qua)
 */
public class BanVeUI extends JPanel {

    // ══════════════════════════════════════════════════════════════════════════
    // DỮ LIỆU TĨNH
    // ══════════════════════════════════════════════════════════════════════════

    private static final String[] PHIM_LIST = {
            "Avatar 2", "Kẻ Cắp Giấc Mơ", "Spider-Man"
    };
    private static final String[][] SUAT_BY_PHIM = {
            { "19:00  Thứ 6, 21/6/2024", "14:00  Thứ 7, 22/6/2024", "21:30  Thứ 7, 22/6/2024" },
            { "15:00  Thứ 6, 21/6/2024", "20:00  Thứ 6, 21/6/2024" },
            { "10:00  Thứ 7, 22/6/2024", "16:30  Thứ 7, 22/6/2024", "22:00  CN, 23/6/2024" }
    };
    private static final String[][] PHONG_BY_PHIM = {
            { "Phòng chiếu 3  (2D)", "Phòng chiếu 5  (IMAX)" },
            { "Phòng chiếu 1  (2D)", "Phòng chiếu 4  (3D)" },
            { "Phòng chiếu 2  (3D)", "Phòng chiếu 6  (IMAX)" }
    };
    private static final Color[][] POSTER_GRAD = {
            { new Color(0x0D3B6E), new Color(0x00C6FB) },
            { new Color(0x2D1B4E), new Color(0xF7971E) },
            { new Color(0x7B0028), new Color(0xFF4E6A) }
    };

    // Giá vé
    private static final int GIA_THUONG = 90_000;
    private static final int GIA_VIP = 130_000;

    // Màu ghế – dùng cả trong legend lẫn trong nút (hoàn toàn khớp nhau)
    private static final Color COLOR_EMPTY = new Color(0x3E5065);
    private static final Color COLOR_SOLD = new Color(0x264359);
    private static final Color COLOR_SELECTED = new Color(0x00B4D8);
    private static final Color COLOR_VIP = new Color(0x5B4DB8);

    // ══════════════════════════════════════════════════════════════════════════
    // DỮ LIỆU BẮP & NƯỚC
    // ══════════════════════════════════════════════════════════════════════════
    // icon, tên, mô tả, giá hiển thị, giá int, màu
    private static final Object[][] SNACK_DATA = {
            { "🍿", "Bắp Nhỏ", "50g  |  Bơ / Caramel", "30.000 đ", 30_000, new Color(0xF59E0B) },
            { "🍿", "Bắp Vừa", "80g  |  Bơ / Caramel", "45.000 đ", 45_000, new Color(0xF59E0B) },
            { "🍿", "Bắp Lớn", "120g |  Bơ / Caramel", "60.000 đ", 60_000, new Color(0xF59E0B) },
            { "🥤", "Nước Nhỏ", "250ml|  Cola / Sprite / Fanta", "25.000 đ", 25_000, new Color(0x06B6D4) },
            { "🥤", "Nước Vừa", "400ml|  Cola / Sprite / Fanta", "35.000 đ", 35_000, new Color(0x06B6D4) },
            { "🥤", "Nước Lớn", "550ml|  Cola / Sprite / Fanta", "45.000 đ", 45_000, new Color(0x06B6D4) },
            { "🎬", "Combo 1", "Bắp Vừa + Nước Vừa", "70.000 đ", 70_000, new Color(0x8B5CF6) },
            { "🎬", "Combo 2", "Bắp Lớn + 2 Nước Vừa", "110.000 đ", 110_000, new Color(0x8B5CF6) },
    };

    // ══════════════════════════════════════════════════════════════════════════
    // STATE
    // ══════════════════════════════════════════════════════════════════════════
    private int selectedPhimIdx = 0;
    private final List<String> selectedSeats = new ArrayList<>();
    private final List<Boolean> selectedSeatsVip = new ArrayList<>();

    // Booking-card dynamic refs
    private JLabel moviePosterLabel;
    private JComboBox<String> comboSuat, comboPhong;
    private JLabel infoGhe, infoLoai, infoTong;

    // Snack-card dynamic refs
    private int[] snackQty;
    private JLabel[] snackQtyLabels;
    private JLabel snackTotalLabel;

    // CardLayout
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardHost = new JPanel(cardLayout);

    // ══════════════════════════════════════════════════════════════════════════
    // CONSTRUCTOR
    // ══════════════════════════════════════════════════════════════════════════
    public BanVeUI() {
        setLayout(new BorderLayout(0, 20));
        setBackground(CustomUI.SIDEBAR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        cardHost.setOpaque(false);
        cardHost.add(buildBookingCard(), "booking");

        add(buildHeader(), BorderLayout.NORTH);
        add(cardHost, BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HEADER
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        h.setPreferredSize(new Dimension(0, 60));
        JLabel title = new JLabel("Bán Vé");
        title.setFont(CustomUI.bold(28));
        title.setForeground(CustomUI.TEXT_LIGHT);
        h.add(title, BorderLayout.WEST);
        return h;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CARD 1 – ĐẶT VÉ
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildBookingCard() {
        JPanel page = new JPanel(new BorderLayout(0, 16));
        page.setOpaque(false);
        page.add(buildSelectionBanner(), BorderLayout.NORTH);
        page.add(buildBookingArea(), BorderLayout.CENTER);
        return page;
    }

    // ── Banner chọn phim + poster ─────────────────────────────────────────────
    private JPanel buildSelectionBanner() {
        JPanel banner = createDarkCard();
        banner.setLayout(new BorderLayout(18, 0));
        banner.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Poster bên trái
        moviePosterLabel = new JLabel(buildPosterIcon(0, 100, 140));
        moviePosterLabel.setPreferredSize(new Dimension(100, 140));
        banner.add(moviePosterLabel, BorderLayout.WEST);

        // Combo boxes bên phải
        JPanel combosPanel = new JPanel(new GridLayout(3, 1, 0, 12));
        combosPanel.setOpaque(false);

        JComboBox<String> comboPhim = new JComboBox<>(PHIM_LIST);
        styleCombo(comboPhim);
        comboPhim.addActionListener(e -> {
            selectedPhimIdx = comboPhim.getSelectedIndex();
            comboSuat.setModel(new DefaultComboBoxModel<>(SUAT_BY_PHIM[selectedPhimIdx]));
            comboPhong.setModel(new DefaultComboBoxModel<>(PHONG_BY_PHIM[selectedPhimIdx]));
            moviePosterLabel.setIcon(buildPosterIcon(selectedPhimIdx, 100, 140));
        });
        combosPanel.add(wrapCombo("Phim", comboPhim));

        comboSuat = new JComboBox<>(SUAT_BY_PHIM[0]);
        styleCombo(comboSuat);
        combosPanel.add(wrapCombo("Suất chiếu", comboSuat));

        comboPhong = new JComboBox<>(PHONG_BY_PHIM[0]);
        styleCombo(comboPhong);
        combosPanel.add(wrapCombo("Phòng chiếu", comboPhong));

        banner.add(combosPanel, BorderLayout.CENTER);
        return banner;
    }

    /** Tạo poster gradient giả theo phim */
    private ImageIcon buildPosterIcon(int idx, int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setPaint(new GradientPaint(0, 0, POSTER_GRAD[idx][0], w, h, POSTER_GRAD[idx][1]));
        g2.fillRoundRect(0, 0, w, h, 12, 12);

        // Film-strip holes
        g2.setColor(new Color(0, 0, 0, 70));
        for (int y = 6; y < h - 8; y += 16) {
            g2.fillRoundRect(4, y, 9, 9, 3, 3);
            g2.fillRoundRect(w - 13, y, 9, 9, 3, 3);
        }

        // Title bar
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, h - 36, w, 36);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 10));
        FontMetrics fm = g2.getFontMetrics();
        String name = PHIM_LIST[idx];
        int tx = Math.max(4, (w - fm.stringWidth(name)) / 2);
        g2.drawString(name, tx, h - 16);

        g2.dispose();
        return new ImageIcon(img);
    }

    // ── Khu vực ghế + thông tin ───────────────────────────────────────────────
    private JPanel buildBookingArea() {
        JPanel area = new JPanel(new GridLayout(1, 2, 16, 0));
        area.setOpaque(false);
        area.add(buildSeatPanel());
        area.add(buildInfoPanel());
        return area;
    }

    // ── Sơ đồ ghế ─────────────────────────────────────────────────────────────
    private final boolean[][] sold = {
            { false, false, true, true, false, false, false, false, false, false, false },
            { false, false, false, true, true, false, false, true, false, false, false },
            { false, false, false, false, false, false, false, false, false, false, false },
            { false, false, false, false, true, true, false, false, false, false, false },
            { false, false, false, false, false, false, false, false, false, true, true },
            { false, false, false, false, false, false, false, false, false, false, false },
            { false, false, false, false, false, false, false, false, false, false, false }
    };
    private final boolean[][] vip = {
            { false, false, false, false, false, false, false, false, false, false, true },
            { false, false, false, false, false, false, false, false, false, true, true },
            { false, false, false, false, false, false, false, false, false, false, false },
            { false, false, false, false, false, false, false, false, false, false, false },
            { false, false, false, false, false, false, false, false, false, false, false },
            { false, false, false, false, false, false, false, false, false, false, false },
            { false, false, false, false, false, false, false, false, false, false, false }
    };

    private JPanel buildSeatPanel() {
        JPanel panel = createDarkCard();
        panel.setLayout(new BorderLayout(0, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 14, 18));

        JLabel screen = new JLabel("▬▬▬▬▬  MÀN HÌNH  ▬▬▬▬▬", JLabel.CENTER);
        screen.setFont(new Font("Monospaced", Font.BOLD, 11));
        screen.setForeground(new Color(0x90CAF9));
        screen.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0x90CAF9)));
        panel.add(screen, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(7, 11, 7, 7));
        grid.setOpaque(false);
        String[] rows = { "A", "B", "C", "D", "E", "F", "G" };
        for (int r = 0; r < rows.length; r++)
            for (int c = 0; c < 11; c++)
                grid.add(createSeatButton(rows[r] + (c + 1), sold[r][c], vip[r][c]));
        panel.add(grid, BorderLayout.CENTER);

        // Chú thích màu – dùng cùng hằng số màu với nút ghế
        panel.add(buildLegend(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 6));
        legend.setOpaque(false);
        legend.add(legendItem(COLOR_EMPTY, "Còn trống"));
        legend.add(legendItem(COLOR_SOLD, "Đã bán"));
        legend.add(legendItem(COLOR_SELECTED, "Đang chọn"));
        legend.add(legendItem(COLOR_VIP, "VIP"));
        return legend;
    }

    private JPanel legendItem(Color color, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        JPanel dot = new JPanel();
        dot.setPreferredSize(new Dimension(13, 13));
        dot.setBackground(color);
        dot.setOpaque(true);
        dot.setBorder(BorderFactory.createLineBorder(color.brighter(), 1));
        JLabel lbl = new JLabel(text);
        lbl.setFont(CustomUI.plain(11));
        lbl.setForeground(CustomUI.TEXT_LIGHT);
        p.add(dot);
        p.add(lbl);
        return p;
    }

    // ── Nút ghế ──────────────────────────────────────────────────────────────
    private JToggleButton createSeatButton(String label, boolean isSold, boolean isVip) {
        JToggleButton btn = new JToggleButton(label);
        btn.setFont(new Font("Monospaced", Font.PLAIN, 9));
        btn.setForeground(CustomUI.TEXT_LIGHT);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(0x374B5C)));
        btn.setPreferredSize(new Dimension(34, 34));

        if (isSold) {
            btn.setEnabled(false);
            btn.setBackground(COLOR_SOLD); // exact same Color constant
            btn.setForeground(new Color(0x5A7A90));
        } else {
            btn.setBackground(isVip ? COLOR_VIP : COLOR_EMPTY);
            btn.addItemListener(e -> {
                if (btn.isSelected()) {
                    btn.setBackground(COLOR_SELECTED); // exact same Color constant
                    btn.setForeground(Color.WHITE);
                    selectedSeats.add(label);
                    selectedSeatsVip.add(isVip);
                } else {
                    btn.setBackground(isVip ? COLOR_VIP : COLOR_EMPTY);
                    btn.setForeground(CustomUI.TEXT_LIGHT);
                    int i = selectedSeats.indexOf(label);
                    if (i >= 0) {
                        selectedSeats.remove(i);
                        selectedSeatsVip.remove(i);
                    }
                }
                refreshSeatInfo();
            });
        }
        return btn;
    }

    private void refreshSeatInfo() {
        if (selectedSeats.isEmpty()) {
            infoGhe.setText("-");
            infoLoai.setText("-");
            infoTong.setText("0 đ");
            return;
        }
        infoGhe.setText(String.join(", ", selectedSeats));
        long vipCnt = selectedSeatsVip.stream().filter(b -> b).count();
        long thuongCnt = selectedSeats.size() - vipCnt;
        infoLoai.setText(vipCnt > 0 && thuongCnt > 0 ? "Thường + VIP"
                : vipCnt > 0 ? "VIP" : "Thường");
        infoTong.setText(formatVND(thuongCnt * GIA_THUONG + vipCnt * GIA_VIP));
    }

    // ── Panel thông tin đặt vé ────────────────────────────────────────────────
    private JPanel buildInfoPanel() {
        JPanel panel = createDarkCard();
        panel.setLayout(new BorderLayout(0, 14));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel hdr = new JLabel("THÔNG TIN ĐẶT VÉ");
        hdr.setFont(CustomUI.bold(13));
        hdr.setForeground(CustomUI.TEXT_LIGHT);
        panel.add(hdr, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        content.add(infoRowStatic("Phim", PHIM_LIST[0]));
        content.add(vgap(7));
        content.add(infoRowStatic("Suất", "19:00"));
        content.add(vgap(7));
        content.add(infoRowStatic("Phòng", "Phòng chiếu 3 (2D)"));
        content.add(vgap(7));

        infoGhe = boldLabel("-");
        content.add(infoRowDynamic("Ghế", infoGhe));
        content.add(vgap(7));
        infoLoai = boldLabel("-");
        content.add(infoRowDynamic("Loại", infoLoai));
        content.add(vgap(14));

        content.add(formField("Tên khách hàng..."));
        content.add(vgap(8));
        content.add(formField("Số điện thoại..."));
        content.add(vgap(14));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        JLabel tl = new JLabel("Tổng cộng");
        tl.setFont(CustomUI.bold(15));
        tl.setForeground(CustomUI.TEXT_LIGHT);
        infoTong = new JLabel("0 đ");
        infoTong.setFont(CustomUI.bold(18));
        infoTong.setForeground(COLOR_SELECTED);
        totalRow.add(tl, BorderLayout.WEST);
        totalRow.add(infoTong, BorderLayout.EAST);
        content.add(totalRow);

        panel.add(content, BorderLayout.CENTER);

        JButton confirm = CustomUI.createPrimaryButton("Xác nhận → Chọn bắp & nước");
        confirm.setPreferredSize(new Dimension(0, 44));
        confirm.addActionListener(e -> {
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn ít nhất một ghế trước khi tiếp tục.",
                        "Chưa chọn ghế", JOptionPane.WARNING_MESSAGE);
                return;
            }
            openSnackScreen();
        });
        panel.add(confirm, BorderLayout.SOUTH);
        return panel;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CARD 2 – CHỌN BẮP & NƯỚC
    // ══════════════════════════════════════════════════════════════════════════
    private void openSnackScreen() {
        snackQty = new int[SNACK_DATA.length];
        snackQtyLabels = new JLabel[SNACK_DATA.length];
        snackTotalLabel = null;

        JPanel snackCard = buildSnackCard();
        cardHost.add(snackCard, "snack");
        cardLayout.show(cardHost, "snack");
    }

    private JPanel buildSnackCard() {
        JPanel page = new JPanel(new BorderLayout(0, 16));
        page.setOpaque(false);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setOpaque(false);

        JButton back = new JButton("← Quay lại chọn ghế");
        back.setForeground(COLOR_SELECTED);
        back.setFont(CustomUI.bold(12));
        back.setBorder(BorderFactory.createEmptyBorder());
        back.setOpaque(false);
        back.setContentAreaFilled(false);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> cardLayout.show(cardHost, "booking"));

        JLabel title = new JLabel("Chọn Bắp & Nước");
        title.setFont(CustomUI.bold(22));
        title.setForeground(CustomUI.TEXT_LIGHT);

        topBar.add(back, BorderLayout.WEST);
        topBar.add(title, BorderLayout.CENTER);
        page.add(topBar, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
        body.setOpaque(false);

        JPanel grid = new JPanel(new GridLayout(4, 2, 12, 12));
        grid.setOpaque(false);
        for (int i = 0; i < SNACK_DATA.length; i++)
            grid.add(buildSnackItem(i));

        body.add(grid);
        body.add(buildSnackSummary());
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildSnackItem(int idx) {
        String icon = (String) SNACK_DATA[idx][0];
        String name = (String) SNACK_DATA[idx][1];
        String desc = (String) SNACK_DATA[idx][2];
        String price = (String) SNACK_DATA[idx][3];
        Color color = (Color) SNACK_DATA[idx][5];

        JPanel card = createDarkCard();
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel iconLabel = new JLabel(icon, JLabel.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        iconLabel.setPreferredSize(new Dimension(50, 50));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(color.darker().darker());
        iconLabel.setBorder(BorderFactory.createLineBorder(color, 2));
        card.add(iconLabel, BorderLayout.WEST);

        JPanel info = new JPanel(new BorderLayout(0, 4));
        info.setOpaque(false);

        JPanel texts = new JPanel(new GridLayout(3, 1, 0, 2));
        texts.setOpaque(false);
        JLabel nl = new JLabel(name);
        nl.setFont(CustomUI.bold(12));
        nl.setForeground(CustomUI.TEXT_WHITE);
        JLabel dl = new JLabel(desc);
        dl.setFont(CustomUI.plain(10));
        dl.setForeground(CustomUI.TEXT_LIGHT);
        JLabel pl = new JLabel(price);
        pl.setFont(CustomUI.bold(11));
        pl.setForeground(color);
        texts.add(nl);
        texts.add(dl);
        texts.add(pl);
        info.add(texts, BorderLayout.CENTER);

        // Stepper +/−
        JPanel stepper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        stepper.setOpaque(false);
        JButton minus = stepperBtn("−");
        snackQtyLabels[idx] = new JLabel("0", JLabel.CENTER);
        snackQtyLabels[idx].setFont(CustomUI.bold(14));
        snackQtyLabels[idx].setForeground(CustomUI.TEXT_WHITE);
        snackQtyLabels[idx].setPreferredSize(new Dimension(26, 26));
        JButton plus = stepperBtn("+");

        int fi = idx;
        minus.addActionListener(e -> {
            if (snackQty[fi] > 0) {
                snackQty[fi]--;
                snackQtyLabels[fi].setText(String.valueOf(snackQty[fi]));
                refreshSnackTotal();
            }
        });
        plus.addActionListener(e -> {
            snackQty[fi]++;
            snackQtyLabels[fi].setText(String.valueOf(snackQty[fi]));
            refreshSnackTotal();
        });

        stepper.add(minus);
        stepper.add(snackQtyLabels[idx]);
        stepper.add(plus);
        info.add(stepper, BorderLayout.SOUTH);
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    private JButton stepperBtn(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(28, 28));
        btn.setFont(CustomUI.bold(14));
        btn.setForeground(CustomUI.TEXT_WHITE);
        btn.setBackground(new Color(0x243447));
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(new Color(0x3A5070)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel buildSnackSummary() {
        JPanel panel = createDarkCard();
        panel.setLayout(new BorderLayout(0, 14));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel hdr = new JLabel("TÓM TẮT ĐƠN HÀNG");
        hdr.setFont(CustomUI.bold(13));
        hdr.setForeground(CustomUI.TEXT_LIGHT);
        panel.add(hdr, BorderLayout.NORTH);

        JPanel summary = new JPanel();
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setOpaque(false);

        String gheText = selectedSeats.isEmpty() ? "-" : String.join(", ", selectedSeats);
        long vipCnt = selectedSeatsVip.stream().filter(b -> b).count();
        long thuongCnt = selectedSeats.size() - vipCnt;
        long totalVe = thuongCnt * GIA_THUONG + vipCnt * GIA_VIP;

        summary.add(sumRow("Phim", PHIM_LIST[selectedPhimIdx]));
        summary.add(vgap(8));
        summary.add(sumRow("Ghế", gheText));
        summary.add(vgap(8));
        summary.add(sumRow("Tiền vé", formatVND(totalVe)));
        summary.add(vgap(8));

        snackTotalLabel = new JLabel("0 đ");
        snackTotalLabel.setFont(CustomUI.bold(13));
        snackTotalLabel.setForeground(COLOR_SELECTED);
        JPanel snRow = new JPanel(new BorderLayout());
        snRow.setOpaque(false);
        snRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        JLabel snLbl = new JLabel("Bắp & Nước");
        snLbl.setFont(CustomUI.plain(12));
        snLbl.setForeground(CustomUI.TEXT_LIGHT);
        snRow.add(snLbl, BorderLayout.WEST);
        snRow.add(snackTotalLabel, BorderLayout.EAST);
        summary.add(snRow);
        summary.add(vgap(14));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x2D3F4F));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        summary.add(sep);
        summary.add(vgap(10));

        JPanel grandRow = new JPanel(new BorderLayout());
        grandRow.setOpaque(false);
        grandRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel gl = new JLabel("TỔNG CỘNG");
        gl.setFont(CustomUI.bold(14));
        gl.setForeground(CustomUI.TEXT_LIGHT);
        JLabel gv = new JLabel(formatVND(totalVe));
        gv.setFont(CustomUI.bold(18));
        gv.setForeground(COLOR_SELECTED);
        grandRow.add(gl, BorderLayout.WEST);
        grandRow.add(gv, BorderLayout.EAST);
        summary.add(grandRow);

        panel.add(summary, BorderLayout.CENTER);

        // Nút thanh toán + bỏ qua
        JPanel btns = new JPanel(new GridLayout(2, 1, 0, 10));
        btns.setOpaque(false);

        JButton pay = CustomUI.createPrimaryButton("✓  Thanh toán");
        pay.setPreferredSize(new Dimension(0, 44));
        pay.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "🎉  Đặt vé thành công!\nCảm ơn bạn đã sử dụng dịch vụ.",
                "Thành công", JOptionPane.INFORMATION_MESSAGE));

        JButton skip = new JButton("Bỏ qua – không chọn bắp/nước");
        skip.setFont(CustomUI.plain(12));
        skip.setForeground(CustomUI.TEXT_LIGHT);
        skip.setBackground(new Color(0x1A2A39));
        skip.setBorder(BorderFactory.createLineBorder(new Color(0x3A4C5E)));
        skip.setFocusPainted(false);
        skip.setPreferredSize(new Dimension(0, 36));
        skip.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "🎉  Đặt vé thành công!\nCảm ơn bạn đã sử dụng dịch vụ.",
                "Thành công", JOptionPane.INFORMATION_MESSAGE));

        btns.add(pay);
        btns.add(skip);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshSnackTotal() {
        if (snackTotalLabel == null || snackQty == null)
            return;
        long total = 0;
        for (int i = 0; i < snackQty.length; i++)
            total += (long) snackQty[i] * (int) SNACK_DATA[i][4];
        snackTotalLabel.setText(formatVND(total));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HELPERS DÙNG CHUNG
    // ══════════════════════════════════════════════════════════════════════════
    private JLabel boldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(CustomUI.bold(13));
        l.setForeground(CustomUI.TEXT_WHITE);
        return l;
    }

    private JPanel infoRowStatic(String label, String value) {
        return infoRowDynamic(label, boldLabel(value));
    }

    private JPanel infoRowDynamic(String label, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        JLabel l = new JLabel(label);
        l.setFont(CustomUI.plain(12));
        l.setForeground(CustomUI.TEXT_LIGHT);
        row.add(l, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    private JPanel sumRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        JLabel l = new JLabel(label);
        l.setFont(CustomUI.plain(12));
        l.setForeground(CustomUI.TEXT_LIGHT);
        JLabel v = new JLabel(value);
        v.setFont(CustomUI.bold(13));
        v.setForeground(CustomUI.TEXT_WHITE);
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    private Component vgap(int h) {
        return Box.createVerticalStrut(h);
    }

    private JPanel formField(String placeholder) {
        JPanel w = new JPanel(new BorderLayout());
        w.setOpaque(false);
        w.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        JTextField f = new JTextField(placeholder);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3A4C5E)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        f.setBackground(new Color(0x1A2A39));
        f.setForeground(CustomUI.TEXT_LIGHT);
        f.setFont(CustomUI.plain(12));
        f.setCaretColor(CustomUI.TEXT_WHITE);
        w.add(f, BorderLayout.CENTER);
        return w;
    }

    private JPanel wrapCombo(String label, JComboBox<String> combo) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(CustomUI.bold(11));
        l.setForeground(CustomUI.TEXT_LIGHT);
        p.add(l, BorderLayout.NORTH);
        p.add(combo, BorderLayout.CENTER);
        return p;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setBackground(new Color(0x16212A));
        combo.setForeground(CustomUI.TEXT_LIGHT);
        combo.setFont(CustomUI.plain(12));
        combo.setBorder(BorderFactory.createLineBorder(new Color(0x2D3F4F)));
    }

    private JPanel createDarkCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x192330));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(new Color(255, 255, 255, 18));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        return card;
    }

    private String formatVND(long amount) {
        return String.format("%,d đ", amount).replace(',', '.');
    }
}