package healthClubManagement.gui;


import healthClubManagement.db.Trainer;
import healthClubManagement.db.Availability;
import healthClubManagement.db.Member;
import healthClubManagement.db.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.swing.*;
import java.awt.*;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.sql.Date;

public class TrainerDashboard extends JFrame {

    private final Trainer trainer;

    public TrainerDashboard(Trainer trainer) {
        this.trainer = trainer;

        setTitle("FitZone Club – Trainer Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ========== SIDEBAR ==========
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(0x0F1C3F));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("FitZone Club");
        logo.setFont(new Font("Inter", Font.BOLD, 20));
        logo.setForeground(new Color(255,255,255,180));
        logo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(navButton("Dashboard", true));
        sidebar.add(navButton("Set Availability", false));
        sidebar.add(navButton("My Schedule", false));
        sidebar.add(navButton("Member Lookup", false));

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBackground(new Color(240, 248, 255));
        logoutBtn.setForeground(new Color(0x0F1C3F));
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

        // ========== MAIN AREA ==========
        JPanel main = new JPanel();
        main.setBackground(new Color(0xF5F5F5));
        main.setLayout(new BorderLayout());
        add(main, BorderLayout.CENTER);

        // --- TOP BAR ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

        JLabel greeting = new JLabel("Welcome, Coach " + trainer.getFirstName() + " 👋");
        greeting.setFont(new Font("Inter", Font.BOLD, 22));
        topBar.add(greeting, BorderLayout.WEST);

        JLabel emailLabel = new JLabel(trainer.getEmail());
        emailLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        emailLabel.setForeground(Color.GRAY);
        topBar.add(emailLabel, BorderLayout.EAST);

        main.add(topBar, BorderLayout.NORTH);

        // --- MAIN CONTENT AREA ---
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
        content.setLayout(new BorderLayout(20, 20));
        main.add(content, BorderLayout.CENTER);

        // ========== LEFT COLUMN (Schedule + Availability Summary) ==========
        JPanel leftColumn = new JPanel();
        leftColumn.setOpaque(false);
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));

        // --- SCHEDULE CARD ---
        JPanel scheduleCard = new JPanel();
        scheduleCard.setBackground(Color.WHITE);
        scheduleCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(15,15,15,15)
        ));
        scheduleCard.setLayout(new BorderLayout());

        JLabel schedTitle = new JLabel("My Schedule");
        schedTitle.setFont(new Font("Inter", Font.BOLD, 16));
        scheduleCard.add(schedTitle, BorderLayout.NORTH);

        JTextArea scheduleBox = new JTextArea(

        );
        scheduleBox.setEditable(false);
        scheduleBox.setBackground(Color.WHITE);
        scheduleBox.setFont(new Font("Inter", Font.PLAIN, 13));

        scheduleCard.add(scheduleBox, BorderLayout.CENTER);
        leftColumn.add(scheduleCard);

        leftColumn.add(Box.createVerticalStrut(20));

        // --- AVAILABILITY SUMMARY CARD ---
        JPanel availCard = new JPanel();
        availCard.setBackground(Color.WHITE);
        availCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(15,15,15,15)
        ));
        availCard.setLayout(new BoxLayout(availCard, BoxLayout.Y_AXIS));

        JLabel availTitle = new JLabel("Your Availability");
        availTitle.setFont(new Font("Inter", Font.BOLD, 16));

        JLabel a1 = new JLabel("Mon: 9 AM – 2 PM");
        JLabel a2 = new JLabel("Wed: 10 AM – 4 PM");
        JLabel a3 = new JLabel("Fri: 1 PM – 6 PM");

        availCard.add(availTitle);
        availCard.add(Box.createVerticalStrut(10));
        availCard.add(a1);
        availCard.add(a2);
        availCard.add(a3);

        leftColumn.add(availCard);

        content.add(leftColumn, BorderLayout.CENTER);

        // ========== RIGHT COLUMN (Member Lookup + Availability Form) ==========
        JPanel rightColumn = new JPanel();
        rightColumn.setOpaque(false);
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));

        // --- MEMBER LOOKUP CARD ---
        JPanel lookupCard = new JPanel();
        lookupCard.setBackground(Color.WHITE);
        lookupCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(15,15,15,15)
        ));
        lookupCard.setLayout(new BoxLayout(lookupCard, BoxLayout.Y_AXIS));

        JLabel lookupTitle = new JLabel("Member Lookup");
        lookupTitle.setFont(new Font("Inter", Font.BOLD, 16));
        lookupTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        searchField.setFont(new Font("Inter", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(0xE94560));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea resultBox = new JTextArea();
        resultBox.setEditable(false);
        resultBox.setBackground(Color.WHITE);
        resultBox.setFont(new Font("Inter", Font.PLAIN, 13));

        searchBtn.addActionListener(e -> {
            String name = searchField.getText().trim();
            if (name.isEmpty()) return;

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Member> q = session.createQuery(
                        "FROM Member WHERE LOWER(firstName) LIKE :x OR LOWER(lastName) LIKE :x",
                        Member.class
                );
                q.setParameter("x", "%" + name.toLowerCase() + "%");

                Member m = q.uniqueResult();

                if (m == null) {
                    resultBox.setText("No member found.");
                } else {
                    resultBox.setText(
                            "Name: " + m.getFirstName() + " " + m.getLastName() + "\n" +
                                    "Email: " + m.getEmail() + "\n" +
                                    "Gender: " + m.getGender() + "\n" +
                                    "Last Metric: (Coming soon)\n" +
                                    "Goal: (Coming soon)\n"
                    );
                }
            }
        });

        lookupCard.add(lookupTitle);
        lookupCard.add(Box.createVerticalStrut(10));
        lookupCard.add(searchField);
        lookupCard.add(Box.createVerticalStrut(8));
        lookupCard.add(searchBtn);
        lookupCard.add(Box.createVerticalStrut(10));
        lookupCard.add(resultBox);

        rightColumn.add(lookupCard);
        rightColumn.add(Box.createVerticalStrut(20));

        // --- AVAILABILITY FORM ---
        JPanel formCard = new JPanel();
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(15,15,15,15)
        ));
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JLabel formTitle = new JLabel("Set Availability");
        formTitle.setFont(new Font("Inter", Font.BOLD, 16));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField dateField = new JTextField("YYYY-MM-DD");
        JTextField startField = new JTextField("Start (HH:MM)");
        JTextField endField = new JTextField("End (HH:MM)");

        dateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        startField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        endField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JButton saveAvailBtn = new JButton("Save Availability");
        saveAvailBtn.setBackground(new Color(0xE94560));
        saveAvailBtn.setForeground(Color.WHITE);
        saveAvailBtn.setFocusPainted(false);

        saveAvailBtn.addActionListener(e -> {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();

                Availability a = new Availability();
                a.getTrainer();
                a.setDate(Date.valueOf(LocalDate.parse(dateField.getText().trim())).toLocalDate());
                a.setStartTime((Time.valueOf(LocalTime.parse(startField.getText().trim()))).toLocalTime());
                a.setEndTime((Time.valueOf(LocalTime.parse(endField.getText().trim()))).toLocalTime());

                session.persist(a);
                session.getTransaction().commit();

                JOptionPane.showMessageDialog(this, "Availability saved!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        });

        formCard.add(formTitle);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(dateField);
        formCard.add(Box.createVerticalStrut(8));
        formCard.add(startField);
        formCard.add(Box.createVerticalStrut(8));
        formCard.add(endField);
        formCard.add(Box.createVerticalStrut(12));
        formCard.add(saveAvailBtn);

        rightColumn.add(formCard);

        content.add(rightColumn, BorderLayout.EAST);
    }

    public TrainerDashboard(Trainer trainer, Trainer trainer1) {

        this.trainer = trainer1;
    }

    // Sidebar item
    private JButton navButton(String text, boolean selected) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setFont(new Font("Inter", selected ? Font.BOLD : Font.PLAIN, 14));

        if (selected) {
            btn.setBackground(new Color(240,248,255));
            btn.setForeground(new Color(0x0F1C3F));
        } else {
            btn.setBackground(new Color(0x0F1C3F));
            btn.setForeground(new Color(255,255,255,180));
        }

        return btn;
    }
}
