package healthClubManagement.gui;

import healthClubManagement.db.Member;


import javax.swing.*;
import java.awt.*;

public class MemberDashboard extends JFrame {

    private final Member member;

    public MemberDashboard(Member member) {
        this.member = member;

        setTitle("FitZone Club – Member Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ========== 1. SIDEBAR ==========
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(0x0F1C3F));   // updated dark blue
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("FitZone Club");
        logo.setFont(new Font("Inter", Font.BOLD, 20));
        logo.setForeground(new Color(255, 255, 255, 180));   // updated tagline color
        logo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(navButton("Dashboard", true));
        sidebar.add(navButton("Health Metrics", false));
        sidebar.add(navButton("Fitness Goals", false));
        sidebar.add(navButton("PT Sessions", false));
        sidebar.add(navButton("Classes", false));
        sidebar.add(navButton("Schedule", false));

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBackground(new Color(240, 248, 255));
        logoutBtn.setForeground(new Color(0x0F1C3F)); // updated to theme blue
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

        // ========== 2. MAIN PANEL ==========
        JPanel main = new JPanel();
        main.setBackground(new Color(245, 246, 250));
        main.setLayout(new BorderLayout());
        add(main, BorderLayout.CENTER);

        // --- TOP BAR ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

        JLabel greeting = new JLabel("Welcome back, " + member.getFirstName() + " 👋");
        greeting.setFont(new Font("Inter", Font.BOLD, 22));
        topBar.add(greeting, BorderLayout.WEST);

        JLabel emailLabel = new JLabel(member.getEmail());
        emailLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        emailLabel.setForeground(Color.GRAY);
        topBar.add(emailLabel, BorderLayout.EAST);

        main.add(topBar, BorderLayout.NORTH);

        // --- CENTER CONTENT ---
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
        content.setLayout(new BorderLayout(20, 20));
        main.add(content, BorderLayout.CENTER);

        // 2.1 Quick stats row
        JPanel statsRow = new JPanel();
        statsRow.setOpaque(false);
        statsRow.setLayout(new GridLayout(1, 4, 15, 0));

        statsRow.add(statCard("Latest Weight", "72 kg", "Updated recently"));
        statsRow.add(statCard("Resting Heart Rate", "68 bpm", "Yesterday"));
        statsRow.add(statCard("Steps (Today)", "8,540", "Goal: 10,000"));
        statsRow.add(statCard("Goal Progress", "60%", "Overall completion"));

        content.add(statsRow, BorderLayout.NORTH);

        // 2.2 Activity + Right-side summary
        JPanel middleRow = new JPanel(new BorderLayout(20, 0));
        middleRow.setOpaque(false);
        middleRow.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        content.add(middleRow, BorderLayout.CENTER);

        // LEFT: Activity "graph"
        JPanel activityCard = new JPanel();
        activityCard.setBackground(Color.WHITE);
        activityCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        activityCard.setLayout(new BorderLayout());

        JLabel activityTitle = new JLabel("Weekly Activity");
        activityTitle.setFont(new Font("Inter", Font.BOLD, 16));
        activityCard.add(activityTitle, BorderLayout.NORTH);

        activityCard.add(new SimpleActivityChart(), BorderLayout.CENTER);

        middleRow.add(activityCard, BorderLayout.CENTER);

        // RIGHT: Profile / schedule summary
        JPanel rightColumn = new JPanel();
        rightColumn.setOpaque(false);
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));

        JPanel profileCard = new JPanel();
        profileCard.setBackground(Color.WHITE);
        profileCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        profileCard.setLayout(new BoxLayout(profileCard, BoxLayout.Y_AXIS));

        JLabel profileTitle = new JLabel("My Profile");
        profileTitle.setFont(new Font("Inter", Font.BOLD, 16));
        profileTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel name = new JLabel(member.getFirstName() + " " + member.getLastName());
        name.setFont(new Font("Inter", Font.PLAIN, 14));
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel gender = new JLabel("Gender: " + member.getGender());
        gender.setFont(new Font("Inter", Font.PLAIN, 13));
        gender.setForeground(Color.GRAY);
        gender.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel phone = new JLabel("Phone: " + member.getPhoneNumber());
        phone.setFont(new Font("Inter", Font.PLAIN, 13));
        phone.setForeground(Color.GRAY);
        phone.setAlignmentX(Component.LEFT_ALIGNMENT);

        profileCard.add(profileTitle);
        profileCard.add(Box.createVerticalStrut(10));
        profileCard.add(name);
        profileCard.add(Box.createVerticalStrut(4));
        profileCard.add(gender);
        profileCard.add(Box.createVerticalStrut(4));
        profileCard.add(phone);
        profileCard.add(Box.createVerticalStrut(12));

        JButton editProfileBtn = new JButton("Edit Profile");
        editProfileBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        editProfileBtn.setFocusPainted(false);
        editProfileBtn.setFont(new Font("Inter", Font.PLAIN, 13));
        profileCard.add(editProfileBtn);

        rightColumn.add(profileCard);
        rightColumn.add(Box.createVerticalStrut(20));

        JPanel scheduleCard = new JPanel();
        scheduleCard.setBackground(Color.WHITE);
        scheduleCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        scheduleCard.setLayout(new BoxLayout(scheduleCard, BoxLayout.Y_AXIS));

        JLabel schedTitle = new JLabel("Today’s Schedule");
        schedTitle.setFont(new Font("Inter", Font.BOLD, 16));
        schedTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sched1 = new JLabel("• 7:00 AM – Morning Cardio (Class)");
        sched1.setFont(new Font("Inter", Font.PLAIN, 13));
        JLabel sched2 = new JLabel("• 6:00 PM – PT Session with Trainer");
        sched2.setFont(new Font("Inter", Font.PLAIN, 13));

        scheduleCard.add(schedTitle);
        scheduleCard.add(Box.createVerticalStrut(8));
        scheduleCard.add(sched1);
        scheduleCard.add(Box.createVerticalStrut(4));
        scheduleCard.add(sched2);

        rightColumn.add(scheduleCard);

        middleRow.add(rightColumn, BorderLayout.EAST);
    }

    public MemberDashboard(Member member, Member member1) {

        this.member = member1;
    }

    // Sidebar items
    private JButton navButton(String text, boolean selected) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setFont(new Font("Inter", selected ? Font.BOLD : Font.PLAIN, 14));

        if (selected) {
            btn.setBackground(new Color(240, 248, 255));
            btn.setForeground(new Color(0x0F1C3F));  // theme blue
        } else {
            btn.setBackground(new Color(0x0F1C3F));  // theme blue
            btn.setForeground(new Color(255, 255, 255, 180));  // updated sidebar text color
        }

        return btn;
    }

    // Small stat card
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

    // Very simple pseudo-chart
    static class SimpleActivityChart extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(245, 246, 250));
            g2.fillRoundRect(0, 0, w, h, 16, 16);

            g2.setColor(new Color(210, 210, 210));
            int baseY = h - 40;
            g2.drawLine(30, baseY, w - 20, baseY);

            int[] values = {30, 60, 45, 80, 55, 70, 50};
            int step = (w - 60) / (values.length - 1);
            int maxHeight = 80;

            g2.setStroke(new BasicStroke(2f));
            g2.setColor(new Color(0x0F1C3F));  // theme blue for line graph

            int prevX = 30;
            int prevY = baseY - values[0] * maxHeight / 100;

            for (int i = 1; i < values.length; i++) {
                int x = 30 + i * step;
                int y = baseY - values[i] * maxHeight / 100;
                g2.drawLine(prevX, prevY, x, y);
                prevX = x;
                prevY = y;
            }
        }
    }
}
