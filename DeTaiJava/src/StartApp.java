import javax.swing.SwingUtilities;

import UI.dashboardUI;
import customUI.CustomUI;

public class StartApp {
    public static void main(String[] args) {
        CustomUI.applyTheme();
        SwingUtilities.invokeLater(() -> new dashboardUI().setVisible(true));
    }
}
