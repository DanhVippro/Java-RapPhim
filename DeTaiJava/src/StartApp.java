import customUI.CustomUI;
import UI.dangNhapUI;
import UI.dashboardUI;
import entity.TaiKhoan;

import javax.swing.*;

public class StartApp {
    public static void main(String[] args) {
        CustomUI.applyTheme();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MEGADE Cinema  Đăng Nhập");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 580);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            dangNhapUI loginPanel = new dangNhapUI();
            loginPanel.setLoginCallback((TaiKhoan tk) -> {
                frame.dispose();
                dashboardUI dash = new dashboardUI(tk);
                dash.setVisible(true);
            });

            frame.setContentPane(loginPanel);
            frame.setVisible(true);
        });
    }
}
