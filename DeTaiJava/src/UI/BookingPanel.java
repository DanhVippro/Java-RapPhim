package UI;

import java.awt.*;
import javax.swing.*;
import customUI.CustomUI;
import model.BookingState;

public class BookingPanel extends JPanel {

    public BookingPanel(BookingState state, Runnable onNext) {
        setLayout(new BorderLayout(0, 14));
        setOpaque(false);

        // --- 1. Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 52));

        JLabel title = new JLabel("Bán Vé");
        title.setFont(CustomUI.bold(26));
        title.setForeground(CustomUI.TEXT_LIGHT);

        header.add(title, BorderLayout.WEST);

        JButton btnAI = new JButton("💡 Gợi ý phim bằng AI") {
            {
                setFont(CustomUI.bold(12));
                setForeground(new Color(0x00B8D4));
                setOpaque(false);
                setContentAreaFilled(false);
                setBorder(BorderFactory.createLineBorder(new Color(0x00B8D4), 1, true));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        };
        btnAI.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Hãy sử dụng khung chat ✨ Trợ Lý AI ở góc dưới bên phải màn hình\n" +
                "để nhận được gợi ý phim và suất chiếu phù hợp nhất từ Gemini!",
                "Trợ lý ảo thông minh", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightHeader.setOpaque(false);
        rightHeader.add(btnAI);
        header.add(rightHeader, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ⚠️ TẠO SEAT PANEL TRƯỚC
        SeatMapPanel seatPanel = new SeatMapPanel(state, () -> {
        });

        // ⚠️ TẠO INFO PANEL SAU (có callback)
        BookingInfoPanel infoPanel = new BookingInfoPanel(
                state,
                onNext,
                roomKey -> {
                    seatPanel.rebuild(roomKey); // 👉 rebuild ghế
                });

        // 👉 set callback refresh lại
        seatPanel.setOnSeatChange(infoPanel::refreshSeatInfo);

        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
        body.setOpaque(false);
        body.add(seatPanel);
        body.add(infoPanel);

        add(body, BorderLayout.CENTER);
    }
}