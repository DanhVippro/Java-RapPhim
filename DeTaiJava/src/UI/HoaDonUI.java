package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class HoaDonUI extends JPanel {
    public HoaDonUI() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = BanVeHelper.darkCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("CHỨC NĂNG HÓA ĐƠN ĐANG PHÁT TRIỂN", SwingConstants.CENTER);
        title.setFont(customUI.CustomUI.bold(22));
        title.setForeground(customUI.CustomUI.PRIMARY);
        
        card.add(title, BorderLayout.CENTER);
        
        add(card, BorderLayout.CENTER);
    }
}
