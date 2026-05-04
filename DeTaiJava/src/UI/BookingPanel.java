package UI;

import java.awt.*;
import javax.swing.*;
import customUI.CustomUI;
import model.BookingState;

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

        // ⚠️ TẠO SEAT PANEL TRƯỚC
        SeatMapPanel seatPanel = new SeatMapPanel(state, () -> {});

        // ⚠️ TẠO INFO PANEL SAU (có callback)
        BookingInfoPanel infoPanel = new BookingInfoPanel(
                state,
                onNext,
                roomKey -> {
                    seatPanel.rebuild(roomKey); // 👉 rebuild ghế
                }
        );

        // 👉 set callback refresh lại
        seatPanel.setOnSeatChange(infoPanel::refreshSeatInfo);

        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
        body.setOpaque(false);
        body.add(seatPanel);
        body.add(infoPanel);

        add(body, BorderLayout.CENTER);
    }
}