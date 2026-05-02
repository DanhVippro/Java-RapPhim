package UI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import customUI.CustomUI;
import model.BookingState;
import model.CinemaData;

/**
 * SeatMapPanel – Sơ đồ ghế + chú thích màu.
 * Màu legend = CÙNG hằng số với màu nút ghế → không bao giờ lệch.
 */
public class SeatMapPanel extends JPanel {

    // ── Màu ghế (single source of truth cho cả legend lẫn nút) ───────────────
    public static final Color SEAT_EMPTY    = new Color(0x3E5065);
    public static final Color SEAT_SOLD     = new Color(0x2A3F52);
    public static final Color SEAT_SELECTED = new Color(0x0098C0); // màu này = legend "Đang chọn"
    public static final Color SEAT_VIP      = new Color(0x5B4DB8);

    private final BookingState state;
    private final Runnable     onSeatChanged;

    public SeatMapPanel(BookingState state, Runnable onSeatChanged) {
        this.state         = state;
        this.onSeatChanged = onSeatChanged;
        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildCard(), BorderLayout.CENTER);
    }

    private JPanel buildCard() {
        JPanel panel = BanVeHelper.darkCard();
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));

        // Màn hình
        JPanel screenWrap = new JPanel(new BorderLayout());
        screenWrap.setOpaque(false);
        JLabel screen = new JLabel("▬▬▬▬▬▬  MÀN HÌNH  ▬▬▬▬▬▬", JLabel.CENTER);
        screen.setFont(new Font("Monospaced", Font.BOLD, 11));
        screen.setForeground(new Color(0x90CAF9));
        screen.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0x4A7FA5)),
            BorderFactory.createEmptyBorder(4, 0, 8, 0)));
        screenWrap.add(screen, BorderLayout.CENTER);
        panel.add(screenWrap, BorderLayout.NORTH);

        // Grid ghế
        JPanel grid = new JPanel(new GridLayout(CinemaData.SEAT_ROWS.length, CinemaData.SEAT_COLS, 6, 6));
        grid.setOpaque(false);
        for (int r = 0; r < CinemaData.SEAT_ROWS.length; r++)
            for (int c = 0; c < CinemaData.SEAT_COLS; c++)
                grid.add(createSeatBtn(
                    CinemaData.SEAT_ROWS[r] + (c + 1),
                    CinemaData.SOLD[r][c],
                    CinemaData.VIP_SEATS[r][c]));
        panel.add(grid, BorderLayout.CENTER);

        // Chú thích (dùng CÙNG hằng số Color)
        panel.add(buildLegend(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 4));
        legend.setOpaque(false);
        legend.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x2D3F4F)));
        legend.add(legendItem(SEAT_EMPTY,    "Còn trống"));
        legend.add(legendItem(SEAT_SOLD,     "Đã bán"));
        legend.add(legendItem(SEAT_SELECTED, "Đang chọn")); // ← CÙNG màu với khi select
        legend.add(legendItem(SEAT_VIP,      "VIP"));
        return legend;
    }

    private JPanel legendItem(Color color, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JPanel dot = new JPanel();
        dot.setPreferredSize(new Dimension(12, 12));
        dot.setBackground(color);     // ← SAME Color constant
        dot.setOpaque(true);
        dot.setBorder(BorderFactory.createLineBorder(color.brighter(), 1));
        JLabel lbl = new JLabel(text);
        lbl.setFont(CustomUI.plain(11));
        lbl.setForeground(CustomUI.TEXT_LIGHT);
        p.add(dot); p.add(lbl);
        return p;
    }

    private JToggleButton createSeatBtn(String label, boolean isSold, boolean isVip) {
        JToggleButton btn = new JToggleButton(label);
        btn.setFont(new Font("Monospaced", Font.PLAIN, 9));
        btn.setForeground(new Color(0xCCDDEE));
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(0x374B5C)));
        btn.setPreferredSize(new Dimension(34, 32));

        if (isSold) {
            btn.setEnabled(false);
            btn.setBackground(SEAT_SOLD);     // ← CÙNG constant với legend
            btn.setForeground(new Color(0x4A6070));
        } else {
            btn.setBackground(isVip ? SEAT_VIP : SEAT_EMPTY);
            btn.addItemListener(e -> {
                if (btn.isSelected()) {
                    btn.setBackground(SEAT_SELECTED); // ← CÙNG constant với legend
                    btn.setForeground(Color.WHITE);
                    state.seats.add(label);
                    state.seatsVip.add(isVip);
                } else {
                    btn.setBackground(isVip ? SEAT_VIP : SEAT_EMPTY);
                    btn.setForeground(new Color(0xCCDDEE));
                    int i = state.seats.indexOf(label);
                    if (i >= 0) { state.seats.remove(i); state.seatsVip.remove(i); }
                }
                onSeatChanged.run();
            });
        }
        return btn;
    }
}