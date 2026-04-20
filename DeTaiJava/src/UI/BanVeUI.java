package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class BanVeUI extends JPanel {
    public BanVeUI() {
       
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
      setBackground(Color.YELLOW);
     
        add(pnMain);
        JPanel pnNorth = new JPanel();
        pnNorth.setLayout(new BorderLayout());
        JLabel lblTitleBanVe = new JLabel("Bán Vé");
        lblTitleBanVe.setFont(new Font("Arial", Font.BOLD, 40));
        lblTitleBanVe.setForeground(Color.WHITE);
        pnNorth.add(lblTitleBanVe, BorderLayout.CENTER);
        pnMain.add(pnNorth, BorderLayout.NORTH);
        
    }
}
