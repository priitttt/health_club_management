package healthClubManagement.gui;

import healthClubManagement.db.*;
import healthClubManagement.db.Class;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TrainerDashboard extends JFrame {

    private final Trainer trainer;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton selectedNavButton;

    // Colors
    private static final Color SIDEBAR_BG = new Color(0x0F1C3F);
    private static final Color ACCENT_COLOR = new Color(0xE94560);
    private static final Color BG_COLOR = new Color(0xF5F5F5);

    public TrainerDashboard(Trainer trainer) {
        this.trainer = trainer;

        setTitle("FitZone Club – Trainer Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ========== SIDEBAR ==========
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("FitZone Club");
        logo.setFont(new Font("Inter", Font.BOLD, 20));
        logo.setForeground(new Color(255, 255, 255, 180));
        logo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(10));

        // Navigation buttons
        JButton dashboardBtn = navButton("Dashboard");
        JButton availabilityBtn = navButton("Set Availability");
        JButton scheduleBtn = navButton("My Schedule");
        JButton memberLookupBtn = navButton("Member Lookup");

        selectedNavButton = dashboardBtn;
        highlightNavButton(dashboardBtn);

        sidebar.add(dashboardBtn);
        sidebar.add(availabilityBtn);
        sidebar.add(scheduleBtn);
        sidebar.add(memberLookupBtn);

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBackground(new Color(240, 248, 255));
        logoutBtn.setForeground(SIDEBAR_BG);
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
        main.setBackground(BG_COLOR);
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

        // --- CARD LAYOUT FOR CONTENT ---
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));

        // Add all panels
        contentPanel.add(createDashboardPanel(), "Dashboard");
        contentPanel.add(createAvailabilityPanel(), "Set Availability");
        contentPanel.add(createSchedulePanel(), "My Schedule");
        contentPanel.add(createMemberLookupPanel(), "Member Lookup");

        main.add(contentPanel, BorderLayout.CENTER);

        // Wire navigation buttons
        dashboardBtn.addActionListener(e -> switchPanel("Dashboard", dashboardBtn));
        availabilityBtn.addActionListener(e -> switchPanel("Set Availability", availabilityBtn));
        scheduleBtn.addActionListener(e -> switchPanel("My Schedule", scheduleBtn));
        memberLookupBtn.addActionListener(e -> switchPanel("Member Lookup", memberLookupBtn));
    }

    private void switchPanel(String panelName, JButton btn) {
        cardLayout.show(contentPanel, panelName);
        if (btn != null) {
            resetNavButton(selectedNavButton);
            highlightNavButton(btn);
            selectedNavButton = btn;
        }

        // Refresh the panel when switching
        contentPanel.remove(contentPanel.getComponent(getComponentIndex(panelName)));
        switch (panelName) {
            case "Dashboard" -> contentPanel.add(createDashboardPanel(), "Dashboard", 0);
            case "Set Availability" -> contentPanel.add(createAvailabilityPanel(), "Set Availability", 1);
            case "My Schedule" -> contentPanel.add(createSchedulePanel(), "My Schedule", 2);
            case "Member Lookup" -> contentPanel.add(createMemberLookupPanel(), "Member Lookup", 3);
        }
        cardLayout.show(contentPanel, panelName);
    }

    private int getComponentIndex(String name) {
        return switch (name) {
            case "Dashboard" -> 0;
            case "Set Availability" -> 1;
            case "My Schedule" -> 2;
            case "Member Lookup" -> 3;
            default -> 0;
        };
    }

    // ==================== DASHBOARD PANEL ====================
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        // Fetch stats
        int totalSessions = 0;
        int upcomingSessions = 0;
        int totalClasses = 0;
        int totalAvailabilitySlots = 0;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Total PT sessions
            Query<Long> sessionsQuery = session.createQuery(
                    "SELECT COUNT(s) FROM PTSession s WHERE s.trainer = :trainer",
                    Long.class
            );
            sessionsQuery.setParameter("trainer", trainer);
            totalSessions = sessionsQuery.uniqueResult().intValue();

            // Upcoming PT sessions
            Query<Long> upcomingQuery = session.createQuery(
                    "SELECT COUNT(s) FROM PTSession s WHERE s.trainer = :trainer AND s.startTime > :now",
                    Long.class
            );
            upcomingQuery.setParameter("trainer", trainer);
            upcomingQuery.setParameter("now", java.time.LocalDateTime.now());
            upcomingSessions = upcomingQuery.uniqueResult().intValue();

            // Total classes
            Query<Long> classesQuery = session.createQuery(
                    "SELECT COUNT(c) FROM Class c WHERE c.trainer = :trainer",
                    Long.class
            );
            classesQuery.setParameter("trainer", trainer);
            totalClasses = classesQuery.uniqueResult().intValue();

            // Availability slots
            Query<Long> availQuery = session.createQuery(
                    "SELECT COUNT(a) FROM Availability a WHERE a.trainer = :trainer",
                    Long.class
            );
            availQuery.setParameter("trainer", trainer);
            totalAvailabilitySlots = availQuery.uniqueResult().intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Stats row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        statsRow.setOpaque(false);

        statsRow.add(statCard("Total PT Sessions", String.valueOf(totalSessions), "All time"));
        statsRow.add(statCard("Upcoming Sessions", String.valueOf(upcomingSessions), "Scheduled"));
        statsRow.add(statCard("Classes Teaching", String.valueOf(totalClasses), "Group classes"));
        statsRow.add(statCard("Availability Slots", String.valueOf(totalAvailabilitySlots), "Set up"));

        panel.add(statsRow, BorderLayout.NORTH);

        // Middle content
        JPanel middleRow = new JPanel(new GridLayout(1, 2, 20, 0));
        middleRow.setOpaque(false);
        middleRow.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Today's schedule
        JPanel todayCard = createCard("Today's Schedule");
        JTextArea scheduleText = new JTextArea();
        scheduleText.setEditable(false);
        scheduleText.setFont(new Font("Inter", Font.PLAIN, 13));
        scheduleText.setBackground(Color.WHITE);

        StringBuilder scheduleBuilder = new StringBuilder();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // PT Sessions today
            Query<PTSession> ptQuery = session.createQuery(
                    "FROM PTSession WHERE trainer = :trainer AND CAST(startTime AS LocalDate) = :today ORDER BY startTime",
                    PTSession.class
            );
            ptQuery.setParameter("trainer", trainer);
            ptQuery.setParameter("today", LocalDate.now());
            List<PTSession> todaySessions = ptQuery.getResultList();

            if (!todaySessions.isEmpty()) {
                scheduleBuilder.append("PT Sessions:\n");
                for (PTSession s : todaySessions) {
                    scheduleBuilder.append("• ").append(s.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                            .append(" - ").append(s.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                            .append(" with ").append(s.getMember().getFirstName()).append(" ")
                            .append(s.getMember().getLastName())
                            .append(" (Room: ").append(s.getRoom().getName()).append(")\n");
                }
            }

            // Classes today
            Query<Class> classQuery = session.createQuery(
                    "FROM Class WHERE trainer = :trainer AND CAST(schedule AS LocalDate) = :today ORDER BY schedule",
                    Class.class
            );
            classQuery.setParameter("trainer", trainer);
            classQuery.setParameter("today", LocalDate.now());
            List<Class> todayClasses = classQuery.getResultList();

            if (!todayClasses.isEmpty()) {
                if (scheduleBuilder.length() > 0) scheduleBuilder.append("\n");
                scheduleBuilder.append("Classes:\n");
                for (Class c : todayClasses) {
                    scheduleBuilder.append("• ").append(c.getSchedule().format(DateTimeFormatter.ofPattern("HH:mm")))
                            .append(" - ").append(c.getName())
                            .append(" (Room: ").append(c.getRoom().getName()).append(")\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (scheduleBuilder.length() == 0) {
            scheduleBuilder.append("No sessions or classes scheduled for today.");
        }

        scheduleText.setText(scheduleBuilder.toString());
        todayCard.add(new JScrollPane(scheduleText), BorderLayout.CENTER);

        // Your availability today
        JPanel availCard = createCard("Your Availability Today");
        JTextArea availText = new JTextArea();
        availText.setEditable(false);
        availText.setFont(new Font("Inter", Font.PLAIN, 13));
        availText.setBackground(Color.WHITE);

        StringBuilder availBuilder = new StringBuilder();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Availability> availQuery = session.createQuery(
                    "FROM Availability WHERE trainer = :trainer AND date = :today ORDER BY startTime",
                    Availability.class
            );
            availQuery.setParameter("trainer", trainer);
            availQuery.setParameter("today", LocalDate.now());
            List<Availability> todayAvail = availQuery.getResultList();

            for (Availability a : todayAvail) {
                availBuilder.append("• ").append(a.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                        .append(" - ").append(a.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                        .append(" (").append(a.getStatus()).append(")\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (availBuilder.length() == 0) {
            availBuilder.append("No availability set for today.\nGo to 'Set Availability' to add slots.");
        }

        availText.setText(availBuilder.toString());
        availCard.add(new JScrollPane(availText), BorderLayout.CENTER);

        middleRow.add(todayCard);
        middleRow.add(availCard);

        panel.add(middleRow, BorderLayout.CENTER);

        return panel;
    }

    // ==================== SET AVAILABILITY PANEL ====================
    private JPanel createAvailabilityPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        JLabel title = new JLabel("Set Availability");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setOpaque(false);

        // Left: Add availability form
        JPanel formCard = createCard("Add Availability Slot");
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JTextField dateField = new JTextField("YYYY-MM-DD");
        dateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JTextField startField = new JTextField("HH:MM (e.g., 09:00)");
        startField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JTextField endField = new JTextField("HH:MM (e.g., 17:00)");
        endField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        String[] statuses = {"Available", "Unavailable"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JButton saveBtn = new JButton("Save Availability");
        saveBtn.setBackground(ACCENT_COLOR);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);

        formCard.add(createFormLabel("Date"));
        formCard.add(dateField);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFormLabel("Start Time"));
        formCard.add(startField);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFormLabel("End Time"));
        formCard.add(endField);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFormLabel("Status"));
        formCard.add(statusCombo);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(saveBtn);

        saveBtn.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                LocalTime startTime = LocalTime.parse(startField.getText().trim());
                LocalTime endTime = LocalTime.parse(endField.getText().trim());
                String status = (String) statusCombo.getSelectedItem();

                if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
                    JOptionPane.showMessageDialog(this, "End time must be after start time.");
                    return;
                }

                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    // Check for overlapping availability
                    Query<Availability> overlapQuery = session.createQuery(
                            "FROM Availability WHERE trainer = :trainer AND date = :date " +
                                    "AND startTime < :end AND endTime > :start",
                            Availability.class
                    );
                    overlapQuery.setParameter("trainer", trainer);
                    overlapQuery.setParameter("date", date);
                    overlapQuery.setParameter("start", startTime);
                    overlapQuery.setParameter("end", endTime);

                    if (!overlapQuery.getResultList().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "This time slot overlaps with existing availability!");
                        return;
                    }

                    session.beginTransaction();

                    Availability availability = new Availability();
                    availability.setTrainer(trainer);
                    availability.setDate(date);
                    availability.setStartTime(startTime);
                    availability.setEndTime(endTime);
                    availability.setStatus(status);

                    session.persist(availability);
                    session.getTransaction().commit();

                    JOptionPane.showMessageDialog(this, "Availability saved successfully!");
                    dateField.setText("YYYY-MM-DD");
                    startField.setText("HH:MM (e.g., 09:00)");
                    endField.setText("HH:MM (e.g., 17:00)");

                    switchPanel("Set Availability", selectedNavButton);
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date/time format. Use YYYY-MM-DD for date and HH:MM for time.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving availability: " + ex.getMessage());
            }
        });

        // Right: Current availability
        JPanel availCard = createCard("Your Availability");
        String[] columns = {"Date", "Start", "End", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Availability> query = session.createQuery(
                    "FROM Availability WHERE trainer = :trainer ORDER BY date ASC, startTime ASC",
                    Availability.class
            );
            query.setParameter("trainer", trainer);
            List<Availability> availabilities = query.getResultList();

            for (Availability a : availabilities) {
                model.addRow(new Object[]{
                        a.getDate().toString(),
                        a.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                        a.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                        a.getStatus()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable availTable = new JTable(model);
        availTable.setRowHeight(30);
        availCard.add(new JScrollPane(availTable), BorderLayout.CENTER);

        content.add(formCard);
        content.add(availCard);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    // ==================== MY SCHEDULE PANEL ====================
    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        JLabel title = new JLabel("My Schedule");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setOpaque(false);

        // Left: PT Sessions
        JPanel ptCard = createCard("PT Sessions");
        String[] ptColumns = {"Date", "Time", "Member", "Room", "Status"};
        DefaultTableModel ptModel = new DefaultTableModel(ptColumns, 0);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PTSession> query = session.createQuery(
                    "FROM PTSession WHERE trainer = :trainer ORDER BY startTime ASC",
                    PTSession.class
            );
            query.setParameter("trainer", trainer);
            List<PTSession> sessions = query.getResultList();

            for (PTSession s : sessions) {
                ptModel.addRow(new Object[]{
                        s.getStartTime().toLocalDate().toString(),
                        s.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " +
                                s.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                        s.getMember().getFirstName() + " " + s.getMember().getLastName(),
                        s.getRoom().getName(),
                        s.getStatus()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable ptTable = new JTable(ptModel);
        ptTable.setRowHeight(30);
        ptCard.add(new JScrollPane(ptTable), BorderLayout.CENTER);

        // Right: Classes
        JPanel classCard = createCard("Classes");
        String[] classColumns = {"Date", "Time", "Class Name", "Room", "Capacity"};
        DefaultTableModel classModel = new DefaultTableModel(classColumns, 0);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Class> query = session.createQuery(
                    "FROM Class WHERE trainer = :trainer ORDER BY schedule ASC",
                    Class.class
            );
            query.setParameter("trainer", trainer);
            List<Class> classes = query.getResultList();

            for (Class c : classes) {
                // Get current enrollment
                Query<Long> countQuery = session.createQuery(
                        "SELECT COUNT(mc) FROM MemberClass mc WHERE mc.gymClass = :c",
                        Long.class
                );
                countQuery.setParameter("c", c);
                Long enrolled = countQuery.uniqueResult();

                classModel.addRow(new Object[]{
                        c.getSchedule().toLocalDate().toString(),
                        c.getSchedule().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                        c.getName(),
                        c.getRoom().getName(),
                        enrolled + "/" + c.getCapacity()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable classTable = new JTable(classModel);
        classTable.setRowHeight(30);
        classCard.add(new JScrollPane(classTable), BorderLayout.CENTER);

        content.add(ptCard);
        content.add(classCard);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    // ==================== MEMBER LOOKUP PANEL ====================
    private JPanel createMemberLookupPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        JLabel title = new JLabel("Member Lookup");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(20, 0));
        content.setOpaque(false);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);

        JTextField searchField = new JTextField(30);
        searchField.setFont(new Font("Inter", Font.PLAIN, 14));

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(ACCENT_COLOR);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);

        searchPanel.add(new JLabel("Search by name: "));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        content.add(searchPanel, BorderLayout.NORTH);

        // Results area
        JPanel resultsCard = createCard("Search Results");
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Inter", Font.PLAIN, 14));
        resultArea.setBackground(Color.WHITE);
        resultArea.setText("Enter a member name to search (case-insensitive).\n\n" +
                "You can view:\n• Member profile information\n• Current fitness goals\n• Latest health metrics");

        resultsCard.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        content.add(resultsCard, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            String name = searchField.getText().trim();
            if (name.isEmpty()) {
                resultArea.setText("Please enter a name to search.");
                return;
            }

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Member> query = session.createQuery(
                        "FROM Member WHERE LOWER(firstName) LIKE :name OR LOWER(lastName) LIKE :name",
                        Member.class
                );
                query.setParameter("name", "%" + name.toLowerCase() + "%");
                List<Member> members = query.getResultList();

                if (members.isEmpty()) {
                    resultArea.setText("No members found matching '" + name + "'");
                    return;
                }

                StringBuilder result = new StringBuilder();
                result.append("Found ").append(members.size()).append(" member(s):\n\n");

                for (Member m : members) {
                    result.append("═══════════════════════════════════════════\n");
                    result.append("📋 MEMBER PROFILE\n");
                    result.append("═══════════════════════════════════════════\n");
                    result.append("Name: ").append(m.getFirstName()).append(" ").append(m.getLastName()).append("\n");
                    result.append("Email: ").append(m.getEmail()).append("\n");
                    result.append("Phone: ").append(m.getPhoneNumber()).append("\n");
                    result.append("Gender: ").append(m.getGender()).append("\n");
                    result.append("Date of Birth: ").append(m.getDateOfBirth()).append("\n\n");

                    // Get current fitness goals
                    Query<FitnessGoal> goalQuery = session.createQuery(
                            "FROM FitnessGoal WHERE member = :member ORDER BY deadline ASC",
                            FitnessGoal.class
                    );
                    goalQuery.setParameter("member", m);
                    List<FitnessGoal> goals = goalQuery.getResultList();

                    result.append("🎯 FITNESS GOALS\n");
                    result.append("───────────────────────────────────────────\n");
                    if (goals.isEmpty()) {
                        result.append("No fitness goals set.\n\n");
                    } else {
                        for (FitnessGoal g : goals) {
                            result.append("• ").append(g.getGoalType()).append(": ")
                                    .append(g.getValue()).append(" (Deadline: ")
                                    .append(g.getDeadline()).append(")\n");
                        }
                        result.append("\n");
                    }

                    // Get latest health metrics
                    Query<HealthMetric> metricQuery = session.createQuery(
                            "FROM HealthMetric WHERE member = :member ORDER BY timestamp DESC",
                            HealthMetric.class
                    );
                    metricQuery.setParameter("member", m);
                    metricQuery.setMaxResults(5);
                    List<HealthMetric> metrics = metricQuery.getResultList();

                    result.append("📊 RECENT HEALTH METRICS\n");
                    result.append("───────────────────────────────────────────\n");
                    if (metrics.isEmpty()) {
                        result.append("No health metrics recorded.\n\n");
                    } else {
                        for (HealthMetric metric : metrics) {
                            result.append("• ").append(metric.getMetricType()).append(": ")
                                    .append(metric.getValue()).append(" (")
                                    .append(metric.getTimestamp().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")))
                                    .append(")\n");
                        }
                        result.append("\n");
                    }

                    // Get class attendance
                    Query<Long> classCount = session.createQuery(
                            "SELECT COUNT(mc) FROM MemberClass mc WHERE mc.member = :member",
                            Long.class
                    );
                    classCount.setParameter("member", m);
                    Long classesRegistered = classCount.uniqueResult();

                    // Get PT session count
                    Query<Long> sessionCount = session.createQuery(
                            "SELECT COUNT(s) FROM PTSession s WHERE s.member = :member",
                            Long.class
                    );
                    sessionCount.setParameter("member", m);
                    Long ptSessions = sessionCount.uniqueResult();

                    result.append("📈 ACTIVITY SUMMARY\n");
                    result.append("───────────────────────────────────────────\n");
                    result.append("• Classes Registered: ").append(classesRegistered).append("\n");
                    result.append("• PT Sessions (Total): ").append(ptSessions).append("\n\n");
                }

                resultArea.setText(result.toString());
                resultArea.setCaretPosition(0);

            } catch (Exception ex) {
                resultArea.setText("Error searching: " + ex.getMessage());
            }
        });

        // Allow Enter key to trigger search
        searchField.addActionListener(e -> searchBtn.doClick());

        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    // ==================== HELPER METHODS ====================
    private JButton navButton(String text) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setFont(new Font("Inter", Font.PLAIN, 14));
        btn.setBackground(SIDEBAR_BG);
        btn.setForeground(new Color(255, 255, 255, 180));
        return btn;
    }

    private void highlightNavButton(JButton btn) {
        if (btn != null) {
            btn.setBackground(new Color(240, 248, 255));
            btn.setForeground(SIDEBAR_BG);
            btn.setFont(new Font("Inter", Font.BOLD, 14));
        }
    }

    private void resetNavButton(JButton btn) {
        if (btn != null) {
            btn.setBackground(SIDEBAR_BG);
            btn.setForeground(new Color(255, 255, 255, 180));
            btn.setFont(new Font("Inter", Font.PLAIN, 14));
        }
    }

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

    private JPanel createCard(String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        card.add(titleLabel, BorderLayout.NORTH);

        return card;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Inter", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}
