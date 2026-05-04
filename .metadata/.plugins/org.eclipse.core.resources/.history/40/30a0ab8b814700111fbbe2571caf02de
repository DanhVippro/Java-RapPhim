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
        add(header, BorderLayout.NORTH);

        // --- 2. Xử lý logic liên kết giữa 2 Panel ---
        
        // Sử dụng mảng để giữ tham chiếu của SeatMapPanel vì biến trong Lambda/Callback phải là final
        SeatMapPanel[] seatPanelHolder = new SeatMapPanel[1];

        // Khởi tạo BookingInfoPanel với đầy đủ 3 tham số
        BookingInfoPanel infoPanel = new BookingInfoPanel(state, onNext, roomKey -> {
            if (seatPanelHolder[0] != null) {
                // Khi người dùng đổi phòng ở ComboBox bên phải -> Yêu cầu sơ đồ ghế bên trái vẽ lại
                seatPanelHolder[0].loadRoom(roomKey); 
            }
        });

        // Khởi tạo SeatMapPanel
        // Khi người dùng click chọn ghế trên sơ đồ -> Yêu cầu InfoPanel cập nhật lại tiền và danh sách ghế
        seatPanelHolder[0] = new SeatMapPanel(state, infoPanel::refreshSeatInfo);
        
        // Load sơ đồ ghế mặc định ban đầu
        seatPanelHolder[0].loadRoom(infoPanel.getCurrentRoomKey());

        // --- 3. Body Layout ---
        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
        body.setOpaque(false);
        body.add(seatPanelHolder[0]);
        body.add(infoPanel);

        add(body, BorderLayout.CENTER);
    }
}