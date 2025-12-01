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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * MemberDashboard - Main dashboard interface for members
 * 
 * This class provides a comprehensive dashboard for members to:
 * - View and log health metrics (with unit labels)
 * - Set and track fitness goals
 * - Book and view PT sessions (with trainer availability display)
 * - Register for group classes
 * - Manage their profile
 * 
 * @author Health Club Management System
 */
public class MemberDashboard extends JFrame {

    // Current logged-in member instance
    private final Member member;
    
    // UI Components for panel management
    private JPanel contentPanel;          // Main content panel using CardLayout
    private CardLayout cardLayout;         // Layout manager for switching between panels
    private JButton selectedNavButton;     // Currently selected navigation button

    // UI Color Constants
    private static final Color SIDEBAR_BG = new Color(0x0F1C3F);    // Dark blue sidebar background
    private static final Color ACCENT_COLOR = new Color(0xE94560);  // Pink/red accent color
    private static final Color BG_COLOR = new Color(245, 246, 250); // Light gray background

    /**
     * Constructor - Initializes the Member Dashboard
     * 
     * @param member The member object representing the logged-in user
     */
    public MemberDashboard(Member member) {
        this.member = member;

        // Configure main window properties
        setTitle("FitZone Club – Member Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ========== 1. SIDEBAR - Navigation Panel ==========
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

    /**
     * Switches between different dashboard panels
     * 
     * @param panelName The name of the panel to display
     * @param btn The navigation button that was clicked
     */
    private void switchPanel(String panelName, JButton btn) {
        // Show the selected panel
        cardLayout.show(contentPanel, panelName);
        
        // Update navigation button highlighting
        resetNavButton(selectedNavButton);
        highlightNavButton(btn);
        selectedNavButton = btn;

        // Refresh the panel when switching to ensure data is up-to-date
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

    /**
     * Maps panel names to their component indices in the CardLayout
     * 
     * @param name The panel name
     * @return The component index
     */
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
    /**
     * Creates the main dashboard panel showing overview statistics and quick actions
     * 
     * Displays:
     * - Latest health metric with unit
     * - Active fitness goals count
     * - Upcoming PT sessions count
     * - Registered classes count
     * - Today's schedule
     * - Quick action buttons
     * 
     * @return JPanel containing the dashboard overview
     */
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        // Fetch real-time data from database for dashboard statistics
        HealthMetric latestMetric = null;
        List<FitnessGoal> goals = null;
        List<PTSession> upcomingSessions = null;
        List<MemberClass> registeredClasses = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Get the most recent health metric
            Query<HealthMetric> metricQuery = session.createQuery(
                    "FROM HealthMetric WHERE member = :member ORDER BY timestamp DESC",
                    HealthMetric.class
            );
            metricQuery.setParameter("member", member);
            metricQuery.setMaxResults(1);
            latestMetric = metricQuery.uniqueResult();

            // Get all active fitness goals
            Query<FitnessGoal> goalQuery = session.createQuery(
                    "FROM FitnessGoal WHERE member = :member",
                    FitnessGoal.class
            );
            goalQuery.setParameter("member", member);
            goals = goalQuery.getResultList();

            // Get upcoming PT sessions (future sessions only)
            Query<PTSession> sessionQuery = session.createQuery(
                    "FROM PTSession WHERE member = :member AND startTime > :now ORDER BY startTime ASC",
                    PTSession.class
            );
            sessionQuery.setParameter("member", member);
            sessionQuery.setParameter("now", LocalDateTime.now());
            upcomingSessions = sessionQuery.getResultList();

            // Get all registered classes
            Query<MemberClass> classQuery = session.createQuery(
                    "FROM MemberClass WHERE member = :member",
                    MemberClass.class
            );
            classQuery.setParameter("member", member);
            registeredClasses = classQuery.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create statistics cards row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        statsRow.setOpaque(false);

        // Format latest metric with unit for display
        String latestMetricDisplay = "No data";
        if (latestMetric != null) {
            String unit = getUnitForMetricType(latestMetric.getMetricType());
            latestMetricDisplay = latestMetric.getMetricType() + ": " + latestMetric.getValue() + " " + unit;
        }
        
        // Add statistic cards to the row
        statsRow.add(statCard("Latest Metric", latestMetricDisplay,
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
    /**
     * Creates the Health Metrics panel where members can log and view their health data
     * 
     * Features:
     * - Dynamic unit labels that change based on selected metric type
     * - Form to log new health metrics
     * - History table showing all past metrics with units
     * 
     * @return JPanel containing the health metrics interface
     */
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

        // Available metric types for selection
        String[] metricTypes = {"Weight", "Heart Rate", "Blood Pressure", "Body Fat %", "Steps"};
        JComboBox<String> typeCombo = new JComboBox<>(metricTypes);
        typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // Input field for metric value
        JTextField valueField = new JTextField();
        valueField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // Unit label that dynamically changes based on selected metric type
        // Default to "kg" for Weight
        JLabel unitLabel = new JLabel("kg");
        unitLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        unitLabel.setForeground(Color.GRAY);
        unitLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));

        // Panel to hold value field and unit label side by side
        // This creates a nice visual where the unit appears next to the input field
        JPanel valuePanel = new JPanel(new BorderLayout(5, 0));
        valuePanel.setOpaque(false);
        valuePanel.add(valueField, BorderLayout.CENTER);
        valuePanel.add(unitLabel, BorderLayout.EAST);
        valuePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // Update unit label when metric type changes
        // This provides immediate visual feedback about what unit is expected
        typeCombo.addActionListener(e -> {
            String selectedType = (String) typeCombo.getSelectedItem();
            if (selectedType != null) {
                switch (selectedType) {
                    case "Weight":
                        unitLabel.setText("kg");  // Kilograms
                        break;
                    case "Heart Rate":
                        unitLabel.setText("bpm"); // Beats per minute
                        break;
                    case "Blood Pressure":
                        unitLabel.setText("mmHg"); // Millimeters of mercury
                        break;
                    case "Body Fat %":
                        unitLabel.setText("%");   // Percentage
                        break;
                    case "Steps":
                        unitLabel.setText("steps"); // Step count
                        break;
                    default:
                        unitLabel.setText("");
                }
            }
        });

        JButton saveBtn = new JButton("Save Metric");
        saveBtn.setBackground(ACCENT_COLOR);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);

        formCard.add(createFormLabel("Metric Type"));
        formCard.add(typeCombo);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFormLabel("Value"));
        formCard.add(valuePanel);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(saveBtn);

        // Right: History table showing all past health metrics
        JPanel historyCard = createCard("Health History");
        String[] columns = {"Date", "Type", "Value"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        // Load and display health metric history from database
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Query all health metrics for this member, ordered by most recent first
            Query<HealthMetric> query = session.createQuery(
                    "FROM HealthMetric WHERE member = :member ORDER BY timestamp DESC",
                    HealthMetric.class
            );
            query.setParameter("member", member);
            List<HealthMetric> metrics = query.getResultList();

            // Add each metric to the table with appropriate unit label
            for (HealthMetric m : metrics) {
                // Get the appropriate unit for this metric type
                String unit = getUnitForMetricType(m.getMetricType());
                // Format value with unit (e.g., "72 bpm", "80 kg")
                String valueWithUnit = m.getValue() + " " + unit;
                model.addRow(new Object[]{
                        m.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        m.getMetricType(),
                        valueWithUnit
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        historyCard.add(new JScrollPane(table), BorderLayout.CENTER);

        // Save action - handles saving new health metrics
        saveBtn.addActionListener(e -> {
            try {
                // Parse and validate the input value
                int value = Integer.parseInt(valueField.getText().trim());
                String type = (String) typeCombo.getSelectedItem();

                // Save to database using Hibernate
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    session.beginTransaction();

                    // Create new HealthMetric entity
                    HealthMetric metric = new HealthMetric();
                    metric.setMember(member);
                    metric.setMetricType(type);
                    metric.setValue(value);
                    metric.setTimestamp(LocalDateTime.now());

                    // Persist to database
                    session.persist(metric);
                    session.getTransaction().commit();

                    // Show success message and clear form
                    JOptionPane.showMessageDialog(this, "Health metric saved successfully!");
                    valueField.setText("");

                    // Refresh the panel to show the new metric in history
                    switchPanel("Health Metrics", selectedNavButton);
                }
            } catch (NumberFormatException ex) {
                // Handle invalid numeric input
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric value.");
            } catch (Exception ex) {
                // Handle other errors
                JOptionPane.showMessageDialog(this, "Error saving metric: " + ex.getMessage());
            }
        });

        content.add(formCard);
        content.add(historyCard);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    // ==================== FITNESS GOALS PANEL ====================
    /**
     * Creates the Fitness Goals panel where members can set and track their fitness goals
     * 
     * Features:
     * - Set new goals with appropriate unit labels
     * - View existing goals with units
     * - Delete goals
     * 
     * @return JPanel containing the fitness goals interface
     */
    private JPanel createFitnessGoalsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        JLabel title = new JLabel("Fitness Goals");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        // Two-column layout: Form on left, Goals list on right
        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setOpaque(false);

        // Left: Add new goal form
        JPanel formCard = createCard("Set New Goal");
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        // Available goal types
        String[] goalTypes = {"Target Weight", "Body Fat %", "Weekly Workouts", "Daily Steps"};
        JComboBox<String> goalTypeCombo = new JComboBox<>(goalTypes);
        goalTypeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // Input field for target value
        JTextField targetField = new JTextField();
        targetField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // Unit label that dynamically changes based on selected goal type
        // Default to "kg" for Target Weight
        JLabel goalUnitLabel = new JLabel("kg");
        goalUnitLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        goalUnitLabel.setForeground(Color.GRAY);
        goalUnitLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));

        // Panel to hold target field and unit label side by side
        JPanel targetPanel = new JPanel(new BorderLayout(5, 0));
        targetPanel.setOpaque(false);
        targetPanel.add(targetField, BorderLayout.CENTER);
        targetPanel.add(goalUnitLabel, BorderLayout.EAST);
        targetPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // Update unit label when goal type changes
        goalTypeCombo.addActionListener(e -> {
            String selectedType = (String) goalTypeCombo.getSelectedItem();
            if (selectedType != null) {
                switch (selectedType) {
                    case "Target Weight":
                        goalUnitLabel.setText("kg");  // Kilograms
                        break;
                    case "Body Fat %":
                        goalUnitLabel.setText("%");    // Percentage
                        break;
                    case "Weekly Workouts":
                        goalUnitLabel.setText("workouts"); // Number of workouts
                        break;
                    case "Daily Steps":
                        goalUnitLabel.setText("steps"); // Step count
                        break;
                    default:
                        goalUnitLabel.setText("");
                }
            }
        });

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
        formCard.add(targetPanel);
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
                String unit = getUnitForGoalType(g.getGoalType());
                String valueWithUnit = g.getValue() + " " + unit;
                model.addRow(new Object[]{
                        g.getGoalType(),
                        valueWithUnit,
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
    /**
     * Creates the PT Sessions panel where members can:
     * - View trainer availability in real-time
     * - Book new PT sessions
     * - View their booked sessions
     * 
     * Features:
     * - Trainer availability display showing free time slots
     * - Double-click availability to auto-fill booking form
     * - Validation to ensure trainer is available before booking
     * 
     * @return JPanel containing the PT sessions interface
     */
    private JPanel createPTSessionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        JLabel title = new JLabel("Personal Training Sessions");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        // Three-column layout: Booking form | Availability | My Sessions
        JPanel content = new JPanel(new GridLayout(1, 3, 20, 0));
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

        // Middle: Trainer Availability Display
        // This panel shows available time slots for the selected trainer
        JPanel availabilityCard = createCard("Trainer Availability");
        JPanel availabilityContent = new JPanel(new BorderLayout());
        availabilityContent.setOpaque(false);
        availabilityContent.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        // Hint label to guide users
        JLabel availabilityHint = new JLabel("Double-click a time slot to fill the booking form");
        availabilityHint.setFont(new Font("Inter", Font.PLAIN, 11));
        availabilityHint.setForeground(Color.GRAY);
        availabilityHint.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        availabilityContent.add(availabilityHint, BorderLayout.NORTH);
        
        // Table to display available time slots
        String[] availColumns = {"Date", "Time Slot", "Status"};
        DefaultTableModel availModel = new DefaultTableModel(availColumns, 0);
        JTable availabilityTable = new JTable(availModel);
        availabilityTable.setRowHeight(30);
        availabilityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane availabilityScroll = new JScrollPane(availabilityTable);
        availabilityContent.add(availabilityScroll, BorderLayout.CENTER);
        
        // Add content to the card (createCard already has title in NORTH, so add to CENTER)
        availabilityCard.add(availabilityContent, BorderLayout.CENTER);

        /**
         * Method to update availability display
         * Queries the database for available time slots for the selected trainer
         * and filters out slots that are already booked
         */
        Runnable updateAvailability = () -> {
            availModel.setRowCount(0);
            String selectedTrainerDisplay = (String) trainerCombo.getSelectedItem();
            if (selectedTrainerDisplay == null) {
                return;
            }

            Trainer selectedTrainer = trainerMap.get(selectedTrainerDisplay);
            if (selectedTrainer == null) {
                return;
            }

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                // Get available slots for the next 14 days
                // This provides a reasonable window for booking ahead
                LocalDate today = LocalDate.now();
                LocalDate endDate = today.plusDays(14);

                // Query availability table for slots marked as "Available"
                Query<Availability> query = session.createQuery(
                        "FROM Availability a " +
                        "WHERE a.trainer = :trainer " +
                        "AND a.date >= :startDate " +
                        "AND a.date <= :endDate " +
                        "AND a.status = 'Available' " +
                        "ORDER BY a.date ASC, a.startTime ASC",
                        Availability.class
                );
                query.setParameter("trainer", selectedTrainer);
                query.setParameter("startDate", today);
                query.setParameter("endDate", endDate);

                List<Availability> availabilities = query.getResultList();

                // Filter out slots that are already booked
                // Even if a slot is marked "Available", it might be booked by another member
                for (Availability avail : availabilities) {
                    // Convert availability slot to LocalDateTime for comparison
                    LocalDateTime slotStart = avail.getDate().atTime(avail.getStartTime());
                    LocalDateTime slotEnd = avail.getDate().atTime(avail.getEndTime());
                    
                    // Check if any PT sessions overlap with this availability slot
                    Query<PTSession> sessionQuery = session.createQuery(
                            "FROM PTSession s " +
                            "WHERE s.trainer = :trainer " +
                            "AND s.startTime < :slotEnd " +
                            "AND s.endTime > :slotStart",
                            PTSession.class
                    );
                    sessionQuery.setParameter("trainer", selectedTrainer);
                    sessionQuery.setParameter("slotStart", slotStart);
                    sessionQuery.setParameter("slotEnd", slotEnd);

                    List<PTSession> conflictingSessions = sessionQuery.getResultList();

                    // Only show slots that have no conflicts (truly available)
                    if (conflictingSessions.isEmpty()) {
                        // Format time slot for display (e.g., "09:00 - 17:00")
                        String timeSlot = avail.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) +
                                        " - " + avail.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                        // Add to table
                        availModel.addRow(new Object[]{
                                avail.getDate().toString(),
                                timeSlot,
                                "Available"
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // Update availability display when trainer selection changes
        // This ensures the table shows availability for the newly selected trainer
        trainerCombo.addActionListener(e -> updateAvailability.run());

        // Update availability when date field changes
        // This allows filtering by specific date if needed
        dateField.addActionListener(e -> updateAvailability.run());

        // Allow double-clicking on availability table to auto-fill the booking form
        // This improves user experience by reducing manual data entry
        availabilityTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = availabilityTable.getSelectedRow();
                    if (row >= 0) {
                        // Extract date and time slot from selected row
                        String date = (String) availModel.getValueAt(row, 0);
                        String timeSlot = (String) availModel.getValueAt(row, 1);
                        String[] times = timeSlot.split(" - ");
                        if (times.length == 2) {
                            // Auto-fill the booking form fields
                            dateField.setText(date);
                            startTimeField.setText(times[0]);
                            endTimeField.setText(times[1]);
                        }
                    }
                }
            }
        });

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

                // Validate trainer availability before booking
                // This ensures members can only book when trainers are actually available
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    // Step 1: Check if trainer has an availability slot that covers the requested time
                    // The slot must be marked as "Available" and must fully contain the requested time
                    LocalTime requestedStartTime = startTime.toLocalTime();
                    LocalTime requestedEndTime = endTime.toLocalTime();
                    
                    Query<Availability> availabilityQuery = session.createQuery(
                            "FROM Availability a " +
                            "WHERE a.trainer = :trainer " +
                            "AND a.date = :date " +
                            "AND a.startTime <= :requestedStart " +
                            "AND a.endTime >= :requestedEnd " +
                            "AND a.status = 'Available'",
                            Availability.class
                    );
                    availabilityQuery.setParameter("trainer", selectedTrainer);
                    availabilityQuery.setParameter("date", date);
                    availabilityQuery.setParameter("requestedStart", requestedStartTime);
                    availabilityQuery.setParameter("requestedEnd", requestedEndTime);
                    
                    List<Availability> availableSlots = availabilityQuery.getResultList();
                    
                    // If no availability slot found, reject the booking
                    if (availableSlots.isEmpty()) {
                        JOptionPane.showMessageDialog(this, 
                            "Trainer is not available at this time slot. Please check trainer availability.");
                        return;
                    }
                    
                    // Step 2: Check for conflicts with existing PT sessions
                    // Even if availability exists, the slot might already be booked
                    Query<PTSession> conflictQuery = session.createQuery(
                            "FROM PTSession WHERE trainer = :trainer AND startTime < :end AND endTime > :start",
                            PTSession.class
                    );
                    conflictQuery.setParameter("trainer", selectedTrainer);
                    conflictQuery.setParameter("start", startTime);
                    conflictQuery.setParameter("end", endTime);

                    if (!conflictQuery.getResultList().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Trainer already has a session booked at this time!");
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
        content.add(availabilityCard);
        content.add(sessionsCard);
        panel.add(content, BorderLayout.CENTER);

        // Initial availability update
        updateAvailability.run();

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
    /**
     * Creates a styled navigation button for the sidebar
     * 
     * @param text The button text
     * @return Styled JButton
     */
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

    /**
     * Highlights a navigation button to indicate it's selected
     * 
     * @param btn The button to highlight
     */
    private void highlightNavButton(JButton btn) {
        if (btn != null) {
            btn.setBackground(new Color(240, 248, 255));
            btn.setForeground(SIDEBAR_BG);
            btn.setFont(new Font("Inter", Font.BOLD, 14));
        }
    }

    /**
     * Resets a navigation button to its default unselected state
     * 
     * @param btn The button to reset
     */
    private void resetNavButton(JButton btn) {
        if (btn != null) {
            btn.setBackground(SIDEBAR_BG);
            btn.setForeground(new Color(255, 255, 255, 180));
            btn.setFont(new Font("Inter", Font.PLAIN, 14));
        }
    }

    /**
     * Creates a statistic card for the dashboard
     * 
     * @param title The card title (small text at top)
     * @param main The main statistic value (large text)
     * @param subtitle The subtitle/description (small text at bottom)
     * @return JPanel styled as a statistic card
     */
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

    /**
     * Creates a styled card panel with a title
     * 
     * @param title The card title
     * @return JPanel styled as a card with title in NORTH position
     */
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

    /**
     * Creates a styled form label
     * 
     * @param text The label text
     * @return Styled JLabel
     */
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Inter", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /**
     * Styles an action button with accent color
     * 
     * @param btn The button to style
     */
    private void styleActionButton(JButton btn) {
        btn.setBackground(ACCENT_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
    }

    /**
     * Helper method to get the appropriate unit label for a health metric type
     * 
     * @param metricType The type of health metric (e.g., "Weight", "Heart Rate")
     * @return The unit string (e.g., "kg", "bpm", "mmHg")
     */
    private String getUnitForMetricType(String metricType) {
        if (metricType == null) return "";
        return switch (metricType) {
            case "Weight" -> "kg";              // Kilograms
            case "Heart Rate" -> "bpm";         // Beats per minute
            case "Blood Pressure" -> "mmHg";    // Millimeters of mercury
            case "Body Fat %" -> "%";           // Percentage
            case "Steps" -> "steps";            // Step count
            default -> "";
        };
    }

    /**
     * Helper method to get the appropriate unit label for a fitness goal type
     * 
     * @param goalType The type of fitness goal (e.g., "Target Weight", "Daily Steps")
     * @return The unit string (e.g., "kg", "steps", "workouts")
     */
    private String getUnitForGoalType(String goalType) {
        if (goalType == null) return "";
        return switch (goalType) {
            case "Target Weight" -> "kg";       // Kilograms
            case "Body Fat %" -> "%";           // Percentage
            case "Weekly Workouts" -> "workouts"; // Number of workouts
            case "Daily Steps" -> "steps";      // Step count
            default -> "";
        };
    }
}
