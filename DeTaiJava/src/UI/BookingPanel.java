package UI;

import java.awt.*;
import javax.swing.*;
import customUI.CustomUI;
import model.BookingState;

/**
 * BookingPanel – Card 1: sơ đồ ghế (trái) + thông tin đặt vé (phải).
 * Khi ghế thay đổi → BookingInfoPanel.refreshSeatInfo() được gọi.
 */
public class BookingPanel extends JPanel {

    public BookingPanel(BookingState state, Runnable onNext) {
        setLayout(new BorderLayout(0, 14));
        setOpaque(false);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 52));
        JLabel title = new JLabel("Bán Vé");
        title.setFont(CustomUI.bold(26));
        title.setForeground(CustomUI.TEXT_LIGHT);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Tạo infoPanel trước để truyền ref vào SeatMapPanel
        BookingInfoPanel infoPanel = new BookingInfoPanel(state, onNext);

        // SeatMapPanel nhận callback → notify infoPanel refresh
        SeatMapPanel seatPanel = new SeatMapPanel(state, infoPanel::refreshSeatInfo);

        // Body: ghế trái, thông tin phải
        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
        body.setOpaque(false);
        body.add(seatPanel);
        body.add(infoPanel);

        add(body, BorderLayout.CENTER);
    }
}