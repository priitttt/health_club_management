package healthClubManagement.gui;

import healthClubManagement.db.Admin;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private final Admin admin;

    public AdminDashboard(Admin admin) {
        this.admin = admin;

        setTitle("FitZone Club – Admin Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        /* ============================
               SIDEBAR (NAVY BLUE)
           ============================ */
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(5, 20, 75)); // SAME AS MEMBER DASHBOARD
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("FitZone Club");
        logo.setFont(new Font("Inter", Font.BOLD, 20));
        logo.setForeground(Color.WHITE);
        logo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(navButton("Dashboard", true));
        sidebar.add(navButton("Room Booking", false));
        sidebar.add(navButton("Equipment", false));
        sidebar.add(navButton("Classes", false));
        sidebar.add(navButton("Billing", false));

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setForeground(new Color(5, 20, 75));
        logoutBtn.setFont(new Font("Inter", Font.BOLD, 14));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(160, 40));

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(20));

        add(sidebar, BorderLayout.WEST);

        /* ============================
                MAIN PANEL
           ============================ */
        JPanel main = new JPanel();
        main.setBackground(new Color(245, 246, 250));
        main.setLayout(new BorderLayout());
        add(main, BorderLayout.CENTER);

        /* ============================
                TOP BAR
           ============================ */
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

        JLabel greeting = new JLabel("Welcome, Admin 👋");
        greeting.setFont(new Font("Inter", Font.BOLD, 22));
        topBar.add(greeting, BorderLayout.WEST);

        JLabel emailLabel = new JLabel(admin.getEmail());
        emailLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        emailLabel.setForeground(Color.GRAY);
        topBar.add(emailLabel, BorderLayout.EAST);

        main.add(topBar, BorderLayout.NORTH);

        /* ============================
                CONTENT AREA
           ============================ */
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
        content.setLayout(new BorderLayout(20, 20));
        main.add(content, BorderLayout.CENTER);

        /* --- TOP SUMMARY CARDS --- */
        JPanel statsRow = new JPanel();
        statsRow.setOpaque(false);
        statsRow.setLayout(new GridLayout(1, 4, 15, 0));

        statsRow.add(statCard("Total Rooms", "12", "All rooms available"));
        statsRow.add(statCard("Equipment Issues", "3", "Need maintenance"));
        statsRow.add(statCard("Upcoming Classes", "8", "Next 7 days"));
        statsRow.add(statCard("Pending Payments", "5", "Awaiting confirmation"));

        content.add(statsRow, BorderLayout.NORTH);

        /* --- CENTER AREA --- */
        JPanel middleRow = new JPanel(new BorderLayout(20, 0));
        middleRow.setOpaque(false);
        middleRow.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        content.add(middleRow, BorderLayout.CENTER);

        /* LEFT – Room Booking Panel */
        JPanel roomCard = new JPanel();
        roomCard.setBackground(Color.WHITE);
        roomCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        roomCard.setLayout(new BoxLayout(roomCard, BoxLayout.Y_AXIS));

        JLabel roomTitle = new JLabel("Room Booking");
        roomTitle.setFont(new Font("Inter", Font.BOLD, 16));
        roomTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roomDesc = new JLabel("Assign rooms for classes & PT sessions");
        roomDesc.setFont(new Font("Inter", Font.PLAIN, 12));
        roomDesc.setForeground(Color.GRAY);
        roomDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton manageRoomsBtn = new JButton("Manage Rooms");
        manageRoomsBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        manageRoomsBtn.setFocusPainted(false);

        roomCard.add(roomTitle);
        roomCard.add(Box.createVerticalStrut(5));
        roomCard.add(roomDesc);
        roomCard.add(Box.createVerticalStrut(10));
        roomCard.add(manageRoomsBtn);

        middleRow.add(roomCard, BorderLayout.WEST);

        /* RIGHT – Equipment + Classes + Billing */
        JPanel rightCol = new JPanel();
        rightCol.setOpaque(false);
        rightCol.setLayout(new GridLayout(3, 1, 0, 15));

        rightCol.add(actionCard("Equipment Maintenance", "Log and resolve equipment issues"));
        rightCol.add(actionCard("Class Management", "Create or update gym classes"));
        rightCol.add(actionCard("Billing & Payments", "Generate bills and record payments"));

        middleRow.add(rightCol, BorderLayout.CENTER);
    }

    /* ============================
         Sidebar Button Style
       ============================ */
    private JButton navButton(String text, boolean selected) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setFont(new Font("Inter", selected ? Font.BOLD : Font.PLAIN, 14));

        if (selected) {
            btn.setBackground(Color.WHITE);
            btn.setForeground(new Color(5, 20, 75));
        } else {
            btn.setBackground(new Color(5, 20, 75));
            btn.setForeground(Color.WHITE);
        }

        return btn;
    }

    /* ============================
           Small White Stat Cards
       ============================ */
    private JPanel statCard(String title, String main, String subtitle) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Inter", Font.PLAIN, 12));
        t.setForeground(Color.GRAY);

        JLabel m = new JLabel(main);
        m.setFont(new Font("Inter", Font.BOLD, 20));

        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("Inter", Font.PLAIN, 11));
        s.setForeground(Color.GRAY);

        card.add(t);
        card.add(Box.createVerticalStrut(6));
        card.add(m);
        card.add(Box.createVerticalStrut(4));
        card.add(s);

        return card;
    }

    /* ============================
         Action Cards (Right Side)
       ============================ */
    private JPanel actionCard(String title, String desc) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Inter", Font.BOLD, 16));
        t.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel d = new JLabel(desc);
        d.setFont(new Font("Inter", Font.PLAIN, 12));
        d.setForeground(Color.GRAY);
        d.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton openBtn = new JButton("Open");
        openBtn.setFocusPainted(false);
        openBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(t);
        card.add(Box.createVerticalStrut(8));
        card.add(d);
        card.add(Box.createVerticalStrut(10));
        card.add(openBtn);

        return card;
    }
}
