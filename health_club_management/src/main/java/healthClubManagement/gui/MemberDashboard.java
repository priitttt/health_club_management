package healthClubManagement.gui;

import healthClubManagement.db.*;
import healthClubManagement.db.Class;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MemberDashboard extends JFrame {

    private final Member member;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton selectedNavButton;

    // Colors
    private static final Color SIDEBAR_BG = new Color(0x0F1C3F);
    private static final Color ACCENT_COLOR = new Color(0xE94560);
    private static final Color BG_COLOR = new Color(245, 246, 250);

    public MemberDashboard(Member member) {
        this.member = member;

        setTitle("FitZone Club – Member Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ========== 1. SIDEBAR ==========
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
        JButton healthMetricsBtn = navButton("Health Metrics");
        JButton fitnessGoalsBtn = navButton("Fitness Goals");
        JButton ptSessionsBtn = navButton("PT Sessions");
        JButton classesBtn = navButton("Classes");
        JButton profileBtn = navButton("My Profile");

        // Set Dashboard as default selected
        selectedNavButton = dashboardBtn;
        highlightNavButton(dashboardBtn);

        sidebar.add(dashboardBtn);
        sidebar.add(healthMetricsBtn);
        sidebar.add(fitnessGoalsBtn);
        sidebar.add(ptSessionsBtn);
        sidebar.add(classesBtn);
        sidebar.add(profileBtn);

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

        // ========== 2. MAIN PANEL WITH CARDLAYOUT ==========
        JPanel main = new JPanel();
        main.setBackground(BG_COLOR);
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

        // --- CARD LAYOUT FOR CONTENT ---
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));

        // Add all panels
        contentPanel.add(createDashboardPanel(), "Dashboard");
        contentPanel.add(createHealthMetricsPanel(), "Health Metrics");
        contentPanel.add(createFitnessGoalsPanel(), "Fitness Goals");
        contentPanel.add(createPTSessionsPanel(), "PT Sessions");
        contentPanel.add(createClassesPanel(), "Classes");
        contentPanel.add(createProfilePanel(), "My Profile");

        main.add(contentPanel, BorderLayout.CENTER);

        // Wire navigation buttons
        dashboardBtn.addActionListener(e -> switchPanel("Dashboard", dashboardBtn));
        healthMetricsBtn.addActionListener(e -> switchPanel("Health Metrics", healthMetricsBtn));
        fitnessGoalsBtn.addActionListener(e -> switchPanel("Fitness Goals", fitnessGoalsBtn));
        ptSessionsBtn.addActionListener(e -> switchPanel("PT Sessions", ptSessionsBtn));
        classesBtn.addActionListener(e -> switchPanel("Classes", classesBtn));
        profileBtn.addActionListener(e -> switchPanel("My Profile", profileBtn));
    }

    private void switchPanel(String panelName, JButton btn) {
        cardLayout.show(contentPanel, panelName);
        resetNavButton(selectedNavButton);
        highlightNavButton(btn);
        selectedNavButton = btn;

        // Refresh the panel when switching
        contentPanel.remove(contentPanel.getComponent(getComponentIndex(panelName)));
        switch (panelName) {
            case "Dashboard" -> contentPanel.add(createDashboardPanel(), "Dashboard", 0);
            case "Health Metrics" -> contentPanel.add(createHealthMetricsPanel(), "Health Metrics", 1);
            case "Fitness Goals" -> contentPanel.add(createFitnessGoalsPanel(), "Fitness Goals", 2);
            case "PT Sessions" -> contentPanel.add(createPTSessionsPanel(), "PT Sessions", 3);
            case "Classes" -> contentPanel.add(createClassesPanel(), "Classes", 4);
            case "My Profile" -> contentPanel.add(createProfilePanel(), "My Profile", 5);
        }
        cardLayout.show(contentPanel, panelName);
    }

    private int getComponentIndex(String name) {
        return switch (name) {
            case "Dashboard" -> 0;
            case "Health Metrics" -> 1;
            case "Fitness Goals" -> 2;
            case "PT Sessions" -> 3;
            case "Classes" -> 4;
            case "My Profile" -> 5;
            default -> 0;
        };
    }

    // ==================== DASHBOARD PANEL ====================
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        // Fetch real data
        HealthMetric latestMetric = null;
        List<FitnessGoal> goals = null;
        List<PTSession> upcomingSessions = null;
        List<MemberClass> registeredClasses = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Latest health metric
            Query<HealthMetric> metricQuery = session.createQuery(
                    "FROM HealthMetric WHERE member = :member ORDER BY timestamp DESC",
                    HealthMetric.class
            );
            metricQuery.setParameter("member", member);
            metricQuery.setMaxResults(1);
            latestMetric = metricQuery.uniqueResult();

            // Fitness goals
            Query<FitnessGoal> goalQuery = session.createQuery(
                    "FROM FitnessGoal WHERE member = :member",
                    FitnessGoal.class
            );
            goalQuery.setParameter("member", member);
            goals = goalQuery.getResultList();

            // Upcoming PT sessions
            Query<PTSession> sessionQuery = session.createQuery(
                    "FROM PTSession WHERE member = :member AND startTime > :now ORDER BY startTime ASC",
                    PTSession.class
            );
            sessionQuery.setParameter("member", member);
            sessionQuery.setParameter("now", LocalDateTime.now());
            upcomingSessions = sessionQuery.getResultList();

            // Registered classes
            Query<MemberClass> classQuery = session.createQuery(
                    "FROM MemberClass WHERE member = :member",
                    MemberClass.class
            );
            classQuery.setParameter("member", member);
            registeredClasses = classQuery.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Stats row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        statsRow.setOpaque(false);

        statsRow.add(statCard("Latest Metric", latestMetric != null ? latestMetric.getMetricType() + ": " + latestMetric.getValue() : "No data",
                latestMetric != null ? latestMetric.getTimestamp().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : ""));
        statsRow.add(statCard("Active Goals", goals != null ? String.valueOf(goals.size()) : "0", "Track your progress"));
        statsRow.add(statCard("Upcoming Sessions", upcomingSessions != null ? String.valueOf(upcomingSessions.size()) : "0", "PT sessions booked"));
        statsRow.add(statCard("Classes Registered", registeredClasses != null ? String.valueOf(registeredClasses.size()) : "0", "Group fitness classes"));

        panel.add(statsRow, BorderLayout.NORTH);

        // Middle content
        JPanel middleRow = new JPanel(new BorderLayout(20, 0));
        middleRow.setOpaque(false);
        middleRow.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Left: Upcoming schedule
        JPanel scheduleCard = createCard("Today's Schedule");
        JTextArea scheduleText = new JTextArea();
        scheduleText.setEditable(false);
        scheduleText.setFont(new Font("Inter", Font.PLAIN, 13));
        scheduleText.setBackground(Color.WHITE);

        StringBuilder scheduleBuilder = new StringBuilder();
        if (upcomingSessions != null && !upcomingSessions.isEmpty()) {
            for (PTSession s : upcomingSessions) {
                if (s.getStartTime().toLocalDate().equals(LocalDate.now())) {
                    scheduleBuilder.append("• ").append(s.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                            .append(" - PT Session with ").append(s.getTrainer().getFirstName()).append("\n");
                }
            }
        }
        if (scheduleBuilder.length() == 0) {
            scheduleBuilder.append("No sessions scheduled for today");
        }
        scheduleText.setText(scheduleBuilder.toString());
        scheduleCard.add(new JScrollPane(scheduleText), BorderLayout.CENTER);
        middleRow.add(scheduleCard, BorderLayout.CENTER);

        // Right: Quick actions
        JPanel rightColumn = new JPanel();
        rightColumn.setOpaque(false);
        rightColumn.setPreferredSize(new Dimension(300, 0));
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));

        JPanel quickActions = createCard("Quick Actions");
        quickActions.setLayout(new BoxLayout(quickActions, BoxLayout.Y_AXIS));

        JButton logMetricBtn = new JButton("Log Health Metric");
        styleActionButton(logMetricBtn);
        logMetricBtn.addActionListener(e -> switchPanel("Health Metrics", null));

        JButton bookPTBtn = new JButton("Book PT Session");
        styleActionButton(bookPTBtn);
        bookPTBtn.addActionListener(e -> switchPanel("PT Sessions", null));

        JButton registerClassBtn = new JButton("Register for Class");
        styleActionButton(registerClassBtn);
        registerClassBtn.addActionListener(e -> switchPanel("Classes", null));

        quickActions.add(Box.createVerticalStrut(10));
        quickActions.add(logMetricBtn);
        quickActions.add(Box.createVerticalStrut(10));
        quickActions.add(bookPTBtn);
        quickActions.add(Box.createVerticalStrut(10));
        quickActions.add(registerClassBtn);

        rightColumn.add(quickActions);
        middleRow.add(rightColumn, BorderLayout.EAST);

        panel.add(middleRow, BorderLayout.CENTER);

        return panel;
    }

    // ==================== HEALTH METRICS PANEL ====================
    private JPanel createHealthMetricsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        JLabel title = new JLabel("Health History");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        // Split panel: form on left, history on right
        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setOpaque(false);

        // Left: Add new metric form
        JPanel formCard = createCard("Log New Metric");
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        String[] metricTypes = {"Weight", "Heart Rate", "Blood Pressure", "Body Fat %", "Steps"};
        JComboBox<String> typeCombo = new JComboBox<>(metricTypes);
        typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JTextField valueField = new JTextField();
        valueField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JButton saveBtn = new JButton("Save Metric");
        saveBtn.setBackground(ACCENT_COLOR);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);

        formCard.add(createFormLabel("Metric Type"));
        formCard.add(typeCombo);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFormLabel("Value"));
        formCard.add(valueField);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(saveBtn);

        // Right: History table
        JPanel historyCard = createCard("Health History");
        String[] columns = {"Date", "Type", "Value"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<HealthMetric> query = session.createQuery(
                    "FROM HealthMetric WHERE member = :member ORDER BY timestamp DESC",
                    HealthMetric.class
            );
            query.setParameter("member", member);
            List<HealthMetric> metrics = query.getResultList();

            for (HealthMetric m : metrics) {
                model.addRow(new Object[]{
                        m.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        m.getMetricType(),
                        m.getValue()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        historyCard.add(new JScrollPane(table), BorderLayout.CENTER);

        // Save action
        saveBtn.addActionListener(e -> {
            try {
                int value = Integer.parseInt(valueField.getText().trim());
                String type = (String) typeCombo.getSelectedItem();

                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    session.beginTransaction();

                    HealthMetric metric = new HealthMetric();
                    metric.setMember(member);
                    metric.setMetricType(type);
                    metric.setValue(value);
                    metric.setTimestamp(LocalDateTime.now());

                    session.persist(metric);
                    session.getTransaction().commit();

                    JOptionPane.showMessageDialog(this, "Health metric saved successfully!");
                    valueField.setText("");

                    // Refresh the panel
                    switchPanel("Health Metrics", selectedNavButton);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric value.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving metric: " + ex.getMessage());
            }
        });

        content.add(formCard);
        content.add(historyCard);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    // ==================== FITNESS GOALS PANEL ====================
    private JPanel createFitnessGoalsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        JLabel title = new JLabel("Fitness Goals");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setOpaque(false);

        // Left: Add new goal
        JPanel formCard = createCard("Set New Goal");
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        String[] goalTypes = {"Target Weight", "Body Fat %", "Weekly Workouts", "Daily Steps"};
        JComboBox<String> goalTypeCombo = new JComboBox<>(goalTypes);
        goalTypeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JTextField targetField = new JTextField();
        targetField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JTextField deadlineField = new JTextField("YYYY-MM-DD");
        deadlineField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JButton saveGoalBtn = new JButton("Save Goal");
        saveGoalBtn.setBackground(ACCENT_COLOR);
        saveGoalBtn.setForeground(Color.WHITE);
        saveGoalBtn.setFocusPainted(false);

        formCard.add(createFormLabel("Goal Type"));
        formCard.add(goalTypeCombo);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFormLabel("Target Value"));
        formCard.add(targetField);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFormLabel("Deadline"));
        formCard.add(deadlineField);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(saveGoalBtn);

        // Right: Current goals
        JPanel goalsCard = createCard("Your Goals");
        String[] columns = {"Goal Type", "Target", "Deadline", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<FitnessGoal> query = session.createQuery(
                    "FROM FitnessGoal WHERE member = :member ORDER BY deadline ASC",
                    FitnessGoal.class
            );
            query.setParameter("member", member);
            List<FitnessGoal> goals = query.getResultList();

            for (FitnessGoal g : goals) {
                model.addRow(new Object[]{
                        g.getGoalType(),
                        g.getValue(),
                        g.getDeadline().toString(),
                        "Delete"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable goalsTable = new JTable(model);
        goalsTable.setRowHeight(30);
        goalsCard.add(new JScrollPane(goalsTable), BorderLayout.CENTER);

        saveGoalBtn.addActionListener(e -> {
            try {
                int target = Integer.parseInt(targetField.getText().trim());
                LocalDate deadline = LocalDate.parse(deadlineField.getText().trim());
                String goalType = (String) goalTypeCombo.getSelectedItem();

                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    session.beginTransaction();

                    FitnessGoal goal = new FitnessGoal();
                    goal.setMember(member);
                    goal.setGoalType(goalType);
                    goal.setValue(target);
                    goal.setDeadline(deadline);

                    session.persist(goal);
                    session.getTransaction().commit();

                    JOptionPane.showMessageDialog(this, "Fitness goal saved successfully!");
                    targetField.setText("");
                    deadlineField.setText("YYYY-MM-DD");

                    switchPanel("Fitness Goals", selectedNavButton);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid target value.");
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Please enter date in YYYY-MM-DD format.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving goal: " + ex.getMessage());
            }
        });

        content.add(formCard);
        content.add(goalsCard);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    // ==================== PT SESSIONS PANEL ====================
    private JPanel createPTSessionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        JLabel title = new JLabel("Personal Training Sessions");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setOpaque(false);

        // Left: Book new session
        JPanel formCard = createCard("Book PT Session");
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        // Trainer dropdown
        JComboBox<String> trainerCombo = new JComboBox<>();
        trainerCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        java.util.Map<String, Trainer> trainerMap = new java.util.HashMap<>();

        // Room dropdown
        JComboBox<String> roomCombo = new JComboBox<>();
        roomCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        java.util.Map<String, Room> roomMap = new java.util.HashMap<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Trainer> trainers = session.createQuery("FROM Trainer", Trainer.class).getResultList();
            for (Trainer t : trainers) {
                String display = t.getFirstName() + " " + t.getLastName() + " (" + t.getSpecialization() + ")";
                trainerCombo.addItem(display);
                trainerMap.put(display, t);
            }

            List<Room> rooms = session.createQuery("FROM Room WHERE available = true", Room.class).getResultList();
            for (Room r : rooms) {
                String display = r.getName() + " (Cap: " + r.getCapacity() + ")";
                roomCombo.addItem(display);
                roomMap.put(display, r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTextField dateField = new JTextField("YYYY-MM-DD");
        dateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JTextField startTimeField = new JTextField("HH:MM (e.g., 14:00)");
        startTimeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JTextField endTimeField = new JTextField("HH:MM (e.g., 15:00)");
        endTimeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JButton bookBtn = new JButton("Book Session");
        bookBtn.setBackground(ACCENT_COLOR);
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFocusPainted(false);

        formCard.add(createFormLabel("Select Trainer"));
        formCard.add(trainerCombo);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createFormLabel("Select Room"));
        formCard.add(roomCombo);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createFormLabel("Date"));
        formCard.add(dateField);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createFormLabel("Start Time"));
        formCard.add(startTimeField);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createFormLabel("End Time"));
        formCard.add(endTimeField);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(bookBtn);

        // Right: My sessions
        JPanel sessionsCard = createCard("My PT Sessions");
        String[] columns = {"Date", "Time", "Trainer", "Room", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PTSession> query = session.createQuery(
                    "FROM PTSession WHERE member = :member ORDER BY startTime DESC",
                    PTSession.class
            );
            query.setParameter("member", member);
            List<PTSession> sessions = query.getResultList();

            for (PTSession s : sessions) {
                model.addRow(new Object[]{
                        s.getStartTime().toLocalDate().toString(),
                        s.getStartTime().toLocalTime() + " - " + s.getEndTime().toLocalTime(),
                        s.getTrainer().getFirstName() + " " + s.getTrainer().getLastName(),
                        s.getRoom().getName(),
                        s.getStatus()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable sessionsTable = new JTable(model);
        sessionsTable.setRowHeight(30);
        sessionsCard.add(new JScrollPane(sessionsTable), BorderLayout.CENTER);

        bookBtn.addActionListener(e -> {
            try {
                String trainerDisplay = (String) trainerCombo.getSelectedItem();
                String roomDisplay = (String) roomCombo.getSelectedItem();

                if (trainerDisplay == null || roomDisplay == null) {
                    JOptionPane.showMessageDialog(this, "Please select a trainer and room.");
                    return;
                }

                Trainer selectedTrainer = trainerMap.get(trainerDisplay);
                Room selectedRoom = roomMap.get(roomDisplay);

                LocalDate date = LocalDate.parse(dateField.getText().trim());
                String[] startParts = startTimeField.getText().trim().split(":");
                String[] endParts = endTimeField.getText().trim().split(":");

                LocalDateTime startTime = date.atTime(Integer.parseInt(startParts[0]), Integer.parseInt(startParts[1]));
                LocalDateTime endTime = date.atTime(Integer.parseInt(endParts[0]), Integer.parseInt(endParts[1]));

                // Validate trainer availability
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    // Check for trainer conflicts
                    Query<PTSession> conflictQuery = session.createQuery(
                            "FROM PTSession WHERE trainer = :trainer AND startTime < :end AND endTime > :start",
                            PTSession.class
                    );
                    conflictQuery.setParameter("trainer", selectedTrainer);
                    conflictQuery.setParameter("start", startTime);
                    conflictQuery.setParameter("end", endTime);

                    if (!conflictQuery.getResultList().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Trainer is not available at this time!");
                        return;
                    }

                    // Check room conflicts
                    Query<PTSession> roomConflict = session.createQuery(
                            "FROM PTSession WHERE room = :room AND startTime < :end AND endTime > :start",
                            PTSession.class
                    );
                    roomConflict.setParameter("room", selectedRoom);
                    roomConflict.setParameter("start", startTime);
                    roomConflict.setParameter("end", endTime);

                    if (!roomConflict.getResultList().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Room is already booked at this time!");
                        return;
                    }

                    session.beginTransaction();

                    PTSession ptSession = new PTSession();
                    ptSession.setMember(member);
                    ptSession.setTrainer(selectedTrainer);
                    ptSession.setRoom(selectedRoom);
                    ptSession.setStartTime(startTime);
                    ptSession.setEndTime(endTime);
                    ptSession.setStatus("Scheduled");

                    session.persist(ptSession);
                    session.getTransaction().commit();

                    JOptionPane.showMessageDialog(this, "PT Session booked successfully!");
                    switchPanel("PT Sessions", selectedNavButton);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error booking session: " + ex.getMessage());
            }
        });

        content.add(formCard);
        content.add(sessionsCard);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    // ==================== CLASSES PANEL ====================
    private JPanel createClassesPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        JLabel title = new JLabel("Group Classes");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setOpaque(false);

        // Left: Available classes
        JPanel availableCard = createCard("Available Classes");
        String[] columns = {"Class Name", "Trainer", "Schedule", "Capacity", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        java.util.Map<Integer, Class> classMap = new java.util.HashMap<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Class> query = session.createQuery(
                    "FROM Class WHERE schedule > :now ORDER BY schedule ASC",
                    Class.class
            );
            query.setParameter("now", LocalDateTime.now());
            List<Class> classes = query.getResultList();

            int row = 0;
            for (Class c : classes) {
                // Check current enrollment
                Query<Long> countQuery = session.createQuery(
                        "SELECT COUNT(mc) FROM MemberClass mc WHERE mc.gymClass = :c",
                        Long.class
                );
                countQuery.setParameter("c", c);
                Long enrolled = countQuery.uniqueResult();

                String capacityStr = enrolled + "/" + c.getCapacity();

                model.addRow(new Object[]{
                        c.getName(),
                        c.getTrainer().getFirstName() + " " + c.getTrainer().getLastName(),
                        c.getSchedule().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")),
                        capacityStr,
                        enrolled < c.getCapacity() ? "Register" : "Full"
                });
                classMap.put(row, c);
                row++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable classTable = new JTable(model);
        classTable.setRowHeight(30);

        JButton registerBtn = new JButton("Register for Selected Class");
        registerBtn.setBackground(ACCENT_COLOR);
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);

        registerBtn.addActionListener(e -> {
            int selectedRow = classTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a class to register.");
                return;
            }

            Class selectedClass = classMap.get(selectedRow);
            if (selectedClass == null) return;

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                // Check if already registered
                Query<MemberClass> checkQuery = session.createQuery(
                        "FROM MemberClass WHERE member = :member AND gymClass = :gymClass",
                        MemberClass.class
                );
                checkQuery.setParameter("member", member);
                checkQuery.setParameter("gymClass", selectedClass);

                if (checkQuery.uniqueResult() != null) {
                    JOptionPane.showMessageDialog(this, "You are already registered for this class!");
                    return;
                }

                // Check capacity
                Query<Long> countQuery = session.createQuery(
                        "SELECT COUNT(mc) FROM MemberClass mc WHERE mc.gymClass = :c",
                        Long.class
                );
                countQuery.setParameter("c", selectedClass);
                Long enrolled = countQuery.uniqueResult();

                if (enrolled >= selectedClass.getCapacity()) {
                    JOptionPane.showMessageDialog(this, "This class is full!");
                    return;
                }

                session.beginTransaction();

                MemberClass mc = new MemberClass();
                mc.setMember(member);
                mc.setGymClass(selectedClass);

                session.persist(mc);
                session.getTransaction().commit();

                JOptionPane.showMessageDialog(this, "Successfully registered for " + selectedClass.getName() + "!");
                switchPanel("Classes", selectedNavButton);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error registering: " + ex.getMessage());
            }
        });

        availableCard.add(new JScrollPane(classTable), BorderLayout.CENTER);
        availableCard.add(registerBtn, BorderLayout.SOUTH);

        // Right: My registered classes
        JPanel myClassesCard = createCard("My Registered Classes");
        String[] myColumns = {"Class Name", "Trainer", "Schedule", "Room"};
        DefaultTableModel myModel = new DefaultTableModel(myColumns, 0);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<MemberClass> query = session.createQuery(
                    "FROM MemberClass WHERE member = :member",
                    MemberClass.class
            );
            query.setParameter("member", member);
            List<MemberClass> myClasses = query.getResultList();

            for (MemberClass mc : myClasses) {
                Class c = mc.getGymClass();
                myModel.addRow(new Object[]{
                        c.getName(),
                        c.getTrainer().getFirstName() + " " + c.getTrainer().getLastName(),
                        c.getSchedule().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")),
                        c.getRoom().getName()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable myClassesTable = new JTable(myModel);
        myClassesTable.setRowHeight(30);
        myClassesCard.add(new JScrollPane(myClassesTable), BorderLayout.CENTER);

        content.add(availableCard);
        content.add(myClassesCard);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    // ==================== PROFILE PANEL ====================
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        JLabel title = new JLabel("Profile Management");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel formCard = createCard("Edit Your Profile");
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JTextField firstNameField = new JTextField(member.getFirstName());
        firstNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JTextField lastNameField = new JTextField(member.getLastName());
        lastNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JTextField emailField = new JTextField(member.getEmail());
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        emailField.setEditable(false);
        emailField.setBackground(new Color(240, 240, 240));

        JTextField phoneField = new JTextField(member.getPhoneNumber());
        phoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        String[] genders = {"Male", "Female", "Other"};
        JComboBox<String> genderCombo = new JComboBox<>(genders);
        genderCombo.setSelectedItem(member.getGender());
        genderCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setBackground(ACCENT_COLOR);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);

        formCard.add(createFormLabel("First Name"));
        formCard.add(firstNameField);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFormLabel("Last Name"));
        formCard.add(lastNameField);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFormLabel("Email (cannot be changed)"));
        formCard.add(emailField);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFormLabel("Phone Number"));
        formCard.add(phoneField);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFormLabel("Gender"));
        formCard.add(genderCombo);
        formCard.add(Box.createVerticalStrut(25));
        formCard.add(saveBtn);

        saveBtn.addActionListener(e -> {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();

                member.setFirstName(firstNameField.getText().trim());
                member.setLastName(lastNameField.getText().trim());
                member.setPhoneNumber(phoneField.getText().trim());
                member.setGender((String) genderCombo.getSelectedItem());

                session.merge(member);
                session.getTransaction().commit();

                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating profile: " + ex.getMessage());
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(formCard, BorderLayout.NORTH);
        panel.add(wrapper, BorderLayout.CENTER);

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
        m.setFont(new Font("Inter", Font.BOLD, 18));

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
        card.add(titleLabel, BorderLayout.NORTH);

        return card;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Inter", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleActionButton(JButton btn) {
        btn.setBackground(ACCENT_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
    }
}
