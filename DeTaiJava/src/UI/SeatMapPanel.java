package UI;

import java.awt.*;
import java.util.Set;
import javax.swing.*;
import customUI.CustomUI;
import DAO.VeDAO;
import model.BookingState;
import model.CinemaData;

/**
 * SeatMapPanel – Sơ đồ ghế + chú thích màu.
 *
 * THAY ĐỔI SO VỚI BẢN CŨ:
 * • Không dùng CinemaData.SOLD[][] (dữ liệu tĩnh) nữa.
 * • Gọi VeDAO.getSoldSeats() mỗi lần load/rebuild để lấy ghế đã bán thực từ DB.
 */
public class SeatMapPanel extends JPanel {

    // ── Màu ghế ─────────────────────────────────────────────
    public static final Color SEAT_EMPTY = BanVeHelper.SEAT_EMPTY;
    public static final Color SEAT_SOLD = BanVeHelper.SEAT_SOLD;
    public static final Color SEAT_SELECTED = BanVeHelper.SEAT_SELECTED;
    public static final Color SEAT_VIP = BanVeHelper.SEAT_VIP;

    private final BookingState state;
    private Runnable onSeatChange;

    // ── Constructor ─────────────────────────────────────────
    public SeatMapPanel(BookingState state, Runnable onSeatChange) {
        this.state = state;
        this.onSeatChange = onSeatChange;

        setLayout(new BorderLayout());
        add(buildCard(), BorderLayout.CENTER);
    }

    public void setOnSeatChange(Runnable r) {
        this.onSeatChange = r;
    }

    // ── Reload khi đổi phòng ────────────────────────────────
    public void loadRoom(int roomKey) {
        reloadSeatMap();
    }

    public void reloadSeatMap() {
        removeAll();
        add(buildCard(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void rebuild(int roomKey) {
        loadRoom(roomKey);
    }

    // ── Card chính ──────────────────────────────────────────
    private JPanel buildCard() {
        // ── Lấy ghế đã bán từ DB ────────────────────────────
        Set<String> soldFromDB = VeDAO.getSoldSeats(
                state.phimIdx, state.phongIdx, state.suatIdx);

        JPanel panel = BanVeHelper.darkCard();
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));

        // ── Màn hình ─────────────────────────────────────────
        JLabel screen = new JLabel("▬▬▬▬▬▬  MÀN HÌNH  ▬▬▬▬▬▬", JLabel.CENTER);
        screen.setFont(new Font("Monospaced", Font.BOLD, 14));
        screen.setForeground(new Color(0x90CAF9));
        screen.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));
        panel.add(screen, BorderLayout.NORTH);

        // ── Grid ghế ─────────────────────────────────────────
        JPanel grid = new JPanel(
                new GridLayout(CinemaData.SEAT_ROWS.length, CinemaData.SEAT_COLS, 6, 6));
        grid.setOpaque(false);

        for (int r = 0; r < CinemaData.SEAT_ROWS.length; r++) {
            for (int c = 0; c < CinemaData.SEAT_COLS; c++) {
                String label = CinemaData.SEAT_ROWS[r] + (c + 1);

                // ★ Thay CinemaData.SOLD[r][c] bằng check từ DB
                boolean isSold = soldFromDB.contains(label);
                boolean isVip = CinemaData.VIP_SEATS[r][c];

                grid.add(createSeatBtn(label, isSold, isVip));
            }
        }

        panel.add(grid, BorderLayout.CENTER);
        panel.add(buildLegend(), BorderLayout.SOUTH);
        return panel;
    }

    // ── Legend ──────────────────────────────────────────────
    private JPanel buildLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 4));
        legend.setOpaque(false);
        legend.add(legendItem(SEAT_EMPTY, "Còn trống"));
        legend.add(legendItem(SEAT_SOLD, "Đã bán"));
        legend.add(legendItem(SEAT_SELECTED, "Đang chọn"));
        legend.add(legendItem(SEAT_VIP, "VIP"));
        return legend;
    }

    private JPanel legendItem(Color color, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JPanel dot = new JPanel();
        dot.setPreferredSize(new Dimension(12, 12));
        dot.setBackground(color);
        dot.setBorder(BorderFactory.createLineBorder(color.brighter(), 1));
        JLabel lbl = new JLabel(text);
        lbl.setFont(CustomUI.plain(13));
        lbl.setForeground(CustomUI.TEXT_LIGHT);
        p.add(dot);
        p.add(lbl);
        return p;
    }

    // ── Tạo ghế ─────────────────────────────────────────────
    private JToggleButton createSeatBtn(String label, boolean isSold, boolean isVip) {
        JToggleButton btn = new JToggleButton(label);

        btn.setFont(new Font("Monospaced", Font.BOLD, 12));
        btn.setForeground(new Color(0xCCDDEE));

        // 🔥 QUAN TRỌNG
        btn.setContentAreaFilled(false); // 🔥 thêm
        btn.setOpaque(true); // 🔥 thêm
        btn.setUI(new javax.swing.plaf.basic.BasicToggleButtonUI());

        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(0x374B5C)));
        btn.setPreferredSize(new Dimension(40, 38));

        if (isSold) {
            btn.setEnabled(false);
            btn.setBackground(SEAT_SOLD);
            btn.setForeground(new Color(0x4A6070));
        } else {
            btn.setBackground(isVip ? SEAT_VIP : SEAT_EMPTY);

            btn.addItemListener(e -> {
                if (btn.isSelected()) {
                    btn.setBackground(SEAT_SELECTED);
                    btn.setForeground(Color.BLACK);
                    state.seats.add(label);
                    state.seatsVip.add(isVip);
                } else {
                    btn.setBackground(isVip ? SEAT_VIP : SEAT_EMPTY);
                    btn.setForeground(new Color(0xCCDDEE));

                    int i = state.seats.indexOf(label);
                    if (i >= 0) {
                        state.seats.remove(i);
                        state.seatsVip.remove(i);
                    }
                }

                btn.repaint(); // 🔥 giữ lại
                if (onSeatChange != null)
                    onSeatChange.run();
            });
        }

        return btn;
    }
}