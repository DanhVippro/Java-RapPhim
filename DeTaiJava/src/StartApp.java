import javax.swing.SwingUtilities;

import UI.dashboardUI;
import customerUI.CustomerUI;

public class StartApp {
    public static void main(String[] args) {
        CustomerUI.applyTheme();
        SwingUtilities.invokeLater(() -> new dashboardUI().setVisible(true));
    }
}
