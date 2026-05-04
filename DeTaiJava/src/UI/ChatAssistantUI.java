package UI;

import customUI.CustomUI;
import service.GeminiService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;

/**
 * ChatAssistantUI – Trợ lý ảo AI cho ứng dụng MEGADE Cinema.
 */
public class ChatAssistantUI extends JPanel {

    private final JPanel chatBody;
    private final JTextField inputField;
    private final JScrollPane scrollPane;
    private final GeminiService geminiService;
    private boolean isExpanded = false;

    private static final int WIDTH_EXPANDED = 360;
    private static final int HEIGHT_EXPANDED = 500;
    private static final int ICON_SIZE = 70;

    private JPanel botIconPanel;
    private JPanel expandedPanel;
    
    private boolean isUserDragged = false;

    public boolean isDragged() {
        return isUserDragged;
    }

    public ChatAssistantUI() {
        this.geminiService = new GeminiService();
        setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
        setLayout(new BorderLayout());
        setOpaque(false);

        // --- 1. Bot Icon Panel (Collapsed) ---
        botIconPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Glow effect
                g2.setColor(new Color(108, 99, 255, 50));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(CustomUI.PRIMARY);
                g2.fillOval(4, 4, getWidth() - 8, getHeight() - 8);
                g2.dispose();
            }
        };
        botIconPanel.setOpaque(false);
        botIconPanel.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
        botIconPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        URL botUrl = getClass().getResource("/resources/icons/bot.png");
        if (botUrl != null) {
            ImageIcon icon = new ImageIcon(new ImageIcon(botUrl).getImage()
                    .getScaledInstance(ICON_SIZE - 20, ICON_SIZE - 20, Image.SCALE_SMOOTH));
            JLabel lblIcon = new JLabel(icon, JLabel.CENTER);
            botIconPanel.add(lblIcon, BorderLayout.CENTER);
        } else {
            JLabel lblFallback = new JLabel("🤖", JLabel.CENTER);
            lblFallback.setFont(new Font("SansSerif", Font.PLAIN, 32));
            lblFallback.setForeground(Color.WHITE);
            botIconPanel.add(lblFallback, BorderLayout.CENTER);
        }

        MouseAdapter dragAdapter = new MouseAdapter() {
            private Point initialClick;
            private boolean isDragging = false;

            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                isDragging = false;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                isDragging = true;
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                int newX = thisX + xMoved;
                int newY = thisY + yMoved;
                
                if (getParent() != null) {
                    newX = Math.max(0, Math.min(newX, getParent().getWidth() - getWidth()));
                    newY = Math.max(0, Math.min(newY, getParent().getHeight() - getHeight()));
                }

                setLocation(newX, newY);
                isUserDragged = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isDragging && e.getComponent() == botIconPanel) {
                    toggleExpand();
                }
                isDragging = false;
            }
        };

        botIconPanel.addMouseListener(dragAdapter);
        botIconPanel.addMouseMotionListener(dragAdapter);

        // --- 2. Expanded Chat Panel ---
        expandedPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(26, 35, 46));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                // Shadow / Border
                g2.setColor(new Color(255, 255, 255, 30));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.dispose();
            }
        };
        expandedPanel.setOpaque(false);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));

        JLabel title = new JLabel("Trợ Lý AI - MEGADE");
        title.setFont(CustomUI.bold(14));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JButton btnClose = new JButton("×");
        btnClose.setFont(new Font("Arial", Font.PLAIN, 26));
        btnClose.setForeground(new Color(0x90A8BF));
        btnClose.setBorder(null);
        btnClose.setContentAreaFilled(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> toggleExpand());
        header.add(btnClose, BorderLayout.EAST);
        
        header.addMouseListener(dragAdapter);
        header.addMouseMotionListener(dragAdapter);

        expandedPanel.add(header, BorderLayout.NORTH);

        // Chat Body
        chatBody = new JPanel();
        chatBody.setLayout(new BoxLayout(chatBody, BoxLayout.Y_AXIS));
        chatBody.setBackground(new Color(0x131A22));
        chatBody.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JPanel chatWrapper = new JPanel(new BorderLayout());
        chatWrapper.setBackground(new Color(0x131A22));
        chatWrapper.add(chatBody, BorderLayout.NORTH);

        scrollPane = new JScrollPane(chatWrapper);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setBackground(new Color(0x131A22));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        expandedPanel.add(scrollPane, BorderLayout.CENTER);

        // Input Area
        JPanel inputArea = new JPanel(new BorderLayout(10, 0));
        inputArea.setBackground(new Color(0x1A232E));
        inputArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));

        inputField = new JTextField();
        inputField.setBackground(new Color(0x242F3D));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3A4C5E)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        inputField.setFont(CustomUI.plain(14));
        inputField.addActionListener(e -> handleSend());

        JButton btnSend = new JButton("➤");
        btnSend.setFont(new Font("Serif", Font.BOLD, 18));
        btnSend.setForeground(CustomUI.PRIMARY);
        btnSend.setContentAreaFilled(false);
        btnSend.setBorder(null);
        btnSend.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSend.addActionListener(e -> handleSend());

        inputArea.add(inputField, BorderLayout.CENTER);
        inputArea.add(btnSend, BorderLayout.EAST);
        expandedPanel.add(inputArea, BorderLayout.SOUTH);

        // Initial state
        add(botIconPanel, BorderLayout.CENTER);

        // Welcome message
        addMessage("Xin chào! Tôi là trợ lý ảo MEGADE. Tôi có thể giúp gì cho bạn?", false);
    }

    private void toggleExpand() {
        isExpanded = !isExpanded;
        removeAll();

        if (isExpanded) {
            setPreferredSize(new Dimension(WIDTH_EXPANDED, HEIGHT_EXPANDED));
            add(expandedPanel, BorderLayout.CENTER);
        } else {
            setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
            add(botIconPanel, BorderLayout.CENTER);
        }
        
        if (getParent() != null) {
            int w = isExpanded ? WIDTH_EXPANDED : ICON_SIZE;
            int h = isExpanded ? HEIGHT_EXPANDED : ICON_SIZE;
            int newX = Math.min(getX(), getParent().getWidth() - w);
            int newY = Math.min(getY(), getParent().getHeight() - h);
            newX = Math.max(0, newX);
            newY = Math.max(0, newY);
            if (newX != getX() || newY != getY()) {
                setLocation(newX, newY);
            }
        }

        revalidate();
        repaint();
        if (getParent() != null) {
            getParent().revalidate();
            getParent().repaint();
        }
    }

    private void handleSend() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        addMessage(text, true);
        inputField.setText("");

        addMessage("...", false);

        new Thread(() -> {
            String response = geminiService.askGemini(text);
            SwingUtilities.invokeLater(() -> {
                int count = chatBody.getComponentCount();
                if (count >= 2) {
                    chatBody.remove(count - 1); // Xóa strut
                    chatBody.remove(count - 2); // Xóa row
                }
                addMessage(response, false);
                chatBody.revalidate();
                chatBody.repaint();
            });
        }).start();
    }

    private void addMessage(String text, boolean isUser) {
        JTextArea area = new JTextArea(text);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setFocusable(false);
        area.setFont(CustomUI.plain(13));
        area.setForeground(Color.WHITE);
        area.setBackground(isUser ? new Color(21, 101, 192) : new Color(38, 56, 80));
        area.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // Map cứng chiều rộng 280px (rộng hơn trước) và tính chiều cao cần thiết
        area.setSize(new Dimension(280, Short.MAX_VALUE));
        Dimension fixedSize = new Dimension(280, area.getPreferredSize().height);
        area.setPreferredSize(fixedSize);
        area.setMaximumSize(fixedSize);

        JPanel bubble = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize(); // Khóa cứng kích thước không cho BoxLayout kéo giãn
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(area.getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        bubble.setOpaque(false);
        bubble.add(area, BorderLayout.CENTER);

        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        if (isUser) {
            row.add(Box.createHorizontalGlue());
            row.add(bubble);
        } else {
            row.add(bubble);
            row.add(Box.createHorizontalGlue());
        }

        chatBody.add(row);
        chatBody.add(Box.createVerticalStrut(10));
        chatBody.revalidate();
        chatBody.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
