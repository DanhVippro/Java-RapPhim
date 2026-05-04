package UI;

import java.awt.*;
import javax.swing.*;
import customUI.CustomUI;
import model.BookingState;
import model.CinemaData;

/**
 * BanVeUI – Panel điều phối toàn bộ luồng bán vé.
 *
 * Cấu trúc class (đã tách):
 *   BanVeUI            ← Orchestrator + CardLayout (file này)
 *   BookingPanel       ← Card 1: chọn ghế + thông tin
 *   SnackPanel         ← Card 2: chọn bắp & nước + chi tiết đặt vé
 *   ConfirmDialog      ← Modal xác nhận cuối cùng
 *   SeatMapPanel       ← Component sơ đồ ghế
 *   BookingInfoPanel   ← Component thông tin đặt vé (có scroll)
 *   model/BookingState ← State dùng chung
 *   model/CinemaData   ← Dữ liệu tĩnh
 */
public class BanVeUI extends JPanel {

    private final BookingState state      = new BookingState();
    private final CardLayout   cardLayout = new CardLayout();
    private final JPanel       cardHost   = new JPanel(cardLayout);

    // Giữ ref để rebuild snack card mỗi lần
    private SnackPanel currentSnackPanel;

    public BanVeUI() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        cardHost.setOpaque(false);
        cardHost.add(new BookingPanel(state, this::goToSnack), "booking");

        add(cardHost, BorderLayout.CENTER);
    }

    /** Callback từ BookingPanel khi bấm "Xác nhận → Chọn bắp & nước" */
    public void goToSnack() {
        // Rebuild SnackPanel mỗi lần để lấy state mới nhất
        if (currentSnackPanel != null) cardHost.remove(currentSnackPanel);
        currentSnackPanel = new SnackPanel(state, this::goToBooking);
        cardHost.add(currentSnackPanel, "snack");
        cardLayout.show(cardHost, "snack");
    }

    /** Callback từ SnackPanel khi bấm "← Quay lại" */
    public void goToBooking() {
        cardLayout.show(cardHost, "booking");
    }
}