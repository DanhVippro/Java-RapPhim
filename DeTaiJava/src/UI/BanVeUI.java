package UI;

import java.awt.*;
import javax.swing.*;
import model.BookingState;

public class BanVeUI extends JPanel {

    private final BookingState state = new BookingState();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardHost = new JPanel(cardLayout);
    private SnackPanel currentSnackPanel;

    public BanVeUI() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        cardHost.setOpaque(false);
        cardHost.add(new BookingPanel(state, this::goToSnack), "booking");

        add(cardHost, BorderLayout.CENTER);
    }

    public void goToSnack() {
        if (currentSnackPanel != null)
            cardHost.remove(currentSnackPanel);
        currentSnackPanel = new SnackPanel(state, this::goToBooking);
        cardHost.add(currentSnackPanel, "snack");
        cardLayout.show(cardHost, "snack");
    }

    public void goToBooking() {
        cardLayout.show(cardHost, "booking");
    }
}