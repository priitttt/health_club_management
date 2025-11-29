package healthClubManagement.gui;

import healthClubManagement.db.*;
import healthClubManagement.db.Class;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AdminDashboard extends JFrame {

    private final Admin admin;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton selectedNavButton;

    // Colors
    private static final Color SIDEBAR_BG = new Color(5, 20, 75);
    private static final Color ACCENT_COLOR = new Color(0xE94560);
    private static final Color BG_COLOR = new Color(245, 246, 250);

    public AdminDashboard(Admin admin) {
        this.admin = admin;

        setTitle("FitZone Club – Admin Dashboard");
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
        logo.setForeground(Color.WHITE);
        logo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(10));

        // Navigation buttons
        JButton dashboardBtn = navButton("Dashboard");
        JButton roomBookingBtn = navButton("Room Booking");
        JButton classManagementBtn = navButton("Class Management");
        JButton billingBtn = navButton("Billing & Payment");

        selectedNavButton = dashboardBtn;
        highlightNavButton(dashboardBtn);

        sidebar.add(dashboardBtn);
        sidebar.add(roomBookingBtn);
        sidebar.add(classManagementBtn);
        sidebar.add(billingBtn);

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBackground(Color.WHITE);
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

        // ========== MAIN PANEL ==========
        JPanel main = new JPanel();
        main.setBackground(BG_COLOR);
        main.setLayout(new BorderLayout());
        add(main, BorderLayout.CENTER);

        // --- TOP BAR ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

        JLabel greeting = new JLabel("Welcome, Admin " + admin.getFirstName() + " 👋");
        greeting.setFont(new Font("Inter", Font.BOLD, 22));
        topBar.add(greeting, BorderLayout.WEST);

        JLabel emailLabel = new JLabel(admin.getEmail());
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
        contentPanel.add(createRoomBookingPanel(), "Room Booking");
        contentPanel.add(createClassManagementPanel(), "Class Management");
        contentPanel.add(createBillingPanel(), "Billing & Payment");

        main.add(contentPanel, BorderLayout.CENTER);

        // Wire navigation buttons
        dashboardBtn.addActionListener(e -> switchPanel("Dashboard", dashboardBtn));
        roomBookingBtn.addActionListener(e -> switchPanel("Room Booking", roomBookingBtn));
        classManagementBtn.addActionListener(e -> switchPanel("Class Management", classManagementBtn));
        billingBtn.addActionListener(e -> switchPanel("Billing & Payment", billingBtn));
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
            case "Room Booking" -> contentPanel.add(createRoomBookingPanel(), "Room Booking", 1);
            case "Class Management" -> contentPanel.add(createClassManagementPanel(), "Class Management", 2);
            case "Billing & Payment" -> contentPanel.add(createBillingPanel(), "Billing & Payment", 3);
        }
        cardLayout.show(contentPanel, panelName);
    }

    private int getComponentIndex(String name) {
        return switch (name) {
            case "Dashboard" -> 0;
            case "Room Booking" -> 1;
            case "Class Management" -> 2;
            case "Billing & Payment" -> 3;
            default -> 0;
        };
    }

    // ==================== DASHBOARD PANEL ====================
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        // Fetch stats
        int totalRooms = 0;
        int availableRooms = 0;
        int upcomingClasses = 0;
        int pendingPayments = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Total rooms
            Query<Long> roomQuery = session.createQuery("SELECT COUNT(r) FROM Room r", Long.class);
            totalRooms = roomQuery.uniqueResult().intValue();

            // Available rooms
            Query<Long> availableQuery = session.createQuery(
                    "SELECT COUNT(r) FROM Room r WHERE r.available = true",
                    Long.class
            );
            availableRooms = availableQuery.uniqueResult().intValue();

            // Upcoming classes
            Query<Long> classQuery = session.createQuery(
                    "SELECT COUNT(c) FROM Class c WHERE c.schedule > :now",
                    Long.class
            );
            classQuery.setParameter("now", LocalDateTime.now());
            upcomingClasses = classQuery.uniqueResult().intValue();

            // Pending payments
            Query<Long> pendingQuery = session.createQuery(
                    "SELECT COUNT(b) FROM Billing b WHERE b.paymentStatus = 'Pending'",
                    Long.class
            );
            pendingPayments = pendingQuery.uniqueResult().intValue();

            // Total revenue
            Query<BigDecimal> revenueQuery = session.createQuery(
                    "SELECT COALESCE(SUM(b.amount), 0) FROM Billing b WHERE b.paymentStatus = 'Paid'",
                    BigDecimal.class
            );
            totalRevenue = revenueQuery.uniqueResult();
            if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Stats row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        statsRow.setOpaque(false);

        statsRow.add(statCard("Total Rooms", String.valueOf(totalRooms), availableRooms + " available"));
        statsRow.add(statCard("Upcoming Classes", String.valueOf(upcomingClasses), "Next 7 days"));
        statsRow.add(statCard("Pending Payments", String.valueOf(pendingPayments), "Awaiting confirmation"));
        statsRow.add(statCard("Total Revenue", "$" + totalRevenue.setScale(2), "From paid bills"));

        panel.add(statsRow, BorderLayout.NORTH);

        // Middle content - Quick actions
        JPanel middleRow = new JPanel(new GridLayout(1, 3, 20, 0));
        middleRow.setOpaque(false);
        middleRow.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Room booking card
        JPanel roomCard = createCard("Room Booking");
        roomCard.setLayout(new BoxLayout(roomCard, BoxLayout.Y_AXIS));
        JLabel roomDesc = new JLabel("<html>Manage room availability and<br>assign rooms for sessions</html>");
        roomDesc.setFont(new Font("Inter", Font.PLAIN, 13));
        roomDesc.setForeground(Color.GRAY);
        JButton manageRoomsBtn = new JButton("Manage Rooms");
        styleActionButton(manageRoomsBtn);
        manageRoomsBtn.addActionListener(e -> switchPanel("Room Booking", null));
        roomCard.add(Box.createVerticalStrut(10));
        roomCard.add(roomDesc);
        roomCard.add(Box.createVerticalStrut(15));
        roomCard.add(manageRoomsBtn);

        // Class management card
        JPanel classCard = createCard("Class Management");
        classCard.setLayout(new BoxLayout(classCard, BoxLayout.Y_AXIS));
        JLabel classDesc = new JLabel("<html>Create, update, or cancel<br>group fitness classes</html>");
        classDesc.setFont(new Font("Inter", Font.PLAIN, 13));
        classDesc.setForeground(Color.GRAY);
        JButton manageClassesBtn = new JButton("Manage Classes");
        styleActionButton(manageClassesBtn);
        manageClassesBtn.addActionListener(e -> switchPanel("Class Management", null));
        classCard.add(Box.createVerticalStrut(10));
        classCard.add(classDesc);
        classCard.add(Box.createVerticalStrut(15));
        classCard.add(manageClassesBtn);

        // Billing card
        JPanel billingCard = createCard("Billing & Payments");
        billingCard.setLayout(new BoxLayout(billingCard, BoxLayout.Y_AXIS));
        JLabel billingDesc = new JLabel("<html>Generate bills and record<br>payment transactions</html>");
        billingDesc.setFont(new Font("Inter", Font.PLAIN, 13));
        billingDesc.setForeground(Color.GRAY);
        JButton manageBillingBtn = new JButton("Manage Billing");
        styleActionButton(manageBillingBtn);
        manageBillingBtn.addActionListener(e -> switchPanel("Billing & Payment", null));
        billingCard.add(Box.createVerticalStrut(10));
        billingCard.add(billingDesc);
        billingCard.add(Box.createVerticalStrut(15));
        billingCard.add(manageBillingBtn);

        middleRow.add(roomCard);
        middleRow.add(classCard);
        middleRow.add(billingCard);

        panel.add(middleRow, BorderLayout.CENTER);

        return panel;
    }

    // ==================== ROOM BOOKING PANEL ====================
    private JPanel createRoomBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        JLabel title = new JLabel("Room Booking & Management");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setOpaque(false);

        // Left: Add/Manage room
        JPanel formCard = createCard("Add New Room");
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JTextField roomNameField = new JTextField();
        roomNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JTextField capacityField = new JTextField();
        capacityField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JCheckBox availableCheck = new JCheckBox("Room is available");
        availableCheck.setSelected(true);
        availableCheck.setOpaque(false);

        JButton addRoomBtn = new JButton("Add Room");
        addRoomBtn.setBackground(ACCENT_COLOR);
        addRoomBtn.setForeground(Color.WHITE);
        addRoomBtn.setFocusPainted(false);

        formCard.add(createFormLabel("Room Name"));
        formCard.add(roomNameField);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFormLabel("Capacity"));
        formCard.add(capacityField);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(availableCheck);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(addRoomBtn);

        // Right: Room list
        JPanel roomListCard = createCard("All Rooms");
        String[] columns = {"ID", "Name", "Capacity", "Status", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        java.util.Map<Integer, Room> roomMap = new java.util.HashMap<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Room> rooms = session.createQuery("FROM Room ORDER BY roomId", Room.class).getResultList();
            int row = 0;
            for (Room r : rooms) {
                model.addRow(new Object[]{
                        r.getRoomId(),
                        r.getName(),
                        r.getCapacity(),
                        r.isAvailable() ? "Available" : "Unavailable",
                        "Toggle"
                });
                roomMap.put(row, r);
                row++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable roomTable = new JTable(model);
        roomTable.setRowHeight(30);

        JButton toggleBtn = new JButton("Toggle Selected Room Availability");
        toggleBtn.setBackground(new Color(100, 100, 100));
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setFocusPainted(false);

        toggleBtn.addActionListener(e -> {
            int selectedRow = roomTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a room to toggle.");
                return;
            }

            Room room = roomMap.get(selectedRow);
            if (room == null) return;

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();
                room.setAvailable(!room.isAvailable());
                session.merge(room);
                session.getTransaction().commit();

                JOptionPane.showMessageDialog(this, "Room availability updated!");
                switchPanel("Room Booking", selectedNavButton);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating room: " + ex.getMessage());
            }
        });

        roomListCard.add(new JScrollPane(roomTable), BorderLayout.CENTER);
        roomListCard.add(toggleBtn, BorderLayout.SOUTH);

        addRoomBtn.addActionListener(e -> {
            try {
                String name = roomNameField.getText().trim();
                int capacity = Integer.parseInt(capacityField.getText().trim());
                boolean available = availableCheck.isSelected();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Room name is required.");
                    return;
                }

                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    session.beginTransaction();

                    Room room = new Room();
                    room.setName(name);
                    room.setCapacity(capacity);
                    room.setAvailable(available);

                    session.persist(room);
                    session.getTransaction().commit();

                    JOptionPane.showMessageDialog(this, "Room added successfully!");
                    roomNameField.setText("");
                    capacityField.setText("");

                    switchPanel("Room Booking", selectedNavButton);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid capacity number.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding room: " + ex.getMessage());
            }
        });

        content.add(formCard);
        content.add(roomListCard);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    // ==================== CLASS MANAGEMENT PANEL ====================
    private JPanel createClassManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        JLabel title = new JLabel("Class Management");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setOpaque(false);

        // Left: Create new class
        JPanel formCard = createCard("Create New Class");
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JTextField classNameField = new JTextField();
        classNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

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

        JTextField capacityField = new JTextField();
        capacityField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JTextField dateField = new JTextField("YYYY-MM-DD");
        dateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JTextField timeField = new JTextField("HH:MM (e.g., 10:00)");
        timeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JButton createClassBtn = new JButton("Create Class");
        createClassBtn.setBackground(ACCENT_COLOR);
        createClassBtn.setForeground(Color.WHITE);
        createClassBtn.setFocusPainted(false);

        formCard.add(createFormLabel("Class Name"));
        formCard.add(classNameField);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createFormLabel("Select Trainer"));
        formCard.add(trainerCombo);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createFormLabel("Select Room"));
        formCard.add(roomCombo);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createFormLabel("Capacity"));
        formCard.add(capacityField);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createFormLabel("Date"));
        formCard.add(dateField);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createFormLabel("Time"));
        formCard.add(timeField);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createClassBtn);

        // Right: Class list
        JPanel classListCard = createCard("All Classes");
        String[] columns = {"Class Name", "Trainer", "Room", "Schedule", "Capacity", "Enrolled"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Class> classes = session.createQuery(
                    "FROM Class ORDER BY schedule ASC",
                    Class.class
            ).getResultList();

            for (Class c : classes) {
                Query<Long> countQuery = session.createQuery(
                        "SELECT COUNT(mc) FROM MemberClass mc WHERE mc.gymClass = :c",
                        Long.class
                );
                countQuery.setParameter("c", c);
                Long enrolled = countQuery.uniqueResult();

                model.addRow(new Object[]{
                        c.getName(),
                        c.getTrainer().getFirstName() + " " + c.getTrainer().getLastName(),
                        c.getRoom().getName(),
                        c.getSchedule().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        c.getCapacity(),
                        enrolled
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable classTable = new JTable(model);
        classTable.setRowHeight(30);
        classListCard.add(new JScrollPane(classTable), BorderLayout.CENTER);

        createClassBtn.addActionListener(e -> {
            try {
                String className = classNameField.getText().trim();
                String trainerDisplay = (String) trainerCombo.getSelectedItem();
                String roomDisplay = (String) roomCombo.getSelectedItem();

                if (className.isEmpty() || trainerDisplay == null || roomDisplay == null) {
                    JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                    return;
                }

                Trainer selectedTrainer = trainerMap.get(trainerDisplay);
                Room selectedRoom = roomMap.get(roomDisplay);
                int capacity = Integer.parseInt(capacityField.getText().trim());
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                String[] timeParts = timeField.getText().trim().split(":");
                LocalDateTime schedule = date.atTime(
                        Integer.parseInt(timeParts[0]),
                        Integer.parseInt(timeParts[1])
                );

                // Check for room conflicts
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    Query<Class> conflictQuery = session.createQuery(
                            "FROM Class WHERE room = :room AND schedule = :schedule",
                            Class.class
                    );
                    conflictQuery.setParameter("room", selectedRoom);
                    conflictQuery.setParameter("schedule", schedule);

                    if (!conflictQuery.getResultList().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Room is already booked for this time!");
                        return;
                    }

                    session.beginTransaction();

                    Class newClass = new Class();
                    newClass.setName(className);
                    newClass.setTrainer(selectedTrainer);
                    newClass.setRoom(selectedRoom);
                    newClass.setCapacity(capacity);
                    newClass.setSchedule(schedule);

                    session.persist(newClass);
                    session.getTransaction().commit();

                    JOptionPane.showMessageDialog(this, "Class created successfully!");
                    classNameField.setText("");
                    capacityField.setText("");
                    dateField.setText("YYYY-MM-DD");
                    timeField.setText("HH:MM (e.g., 10:00)");

                    switchPanel("Class Management", selectedNavButton);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for capacity and time.");
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date/time format.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error creating class: " + ex.getMessage());
            }
        });

        content.add(formCard);
        content.add(classListCard);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    // ==================== BILLING & PAYMENT PANEL ====================
    private JPanel createBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        JLabel title = new JLabel("Billing & Payment");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setOpaque(false);

        // Left: Generate new bill
        JPanel formCard = createCard("Generate New Bill");
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        // Member dropdown
        JComboBox<String> memberCombo = new JComboBox<>();
        memberCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        java.util.Map<String, Member> memberMap = new java.util.HashMap<>();

        // Trainer dropdown
        JComboBox<String> trainerCombo = new JComboBox<>();
        trainerCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        java.util.Map<String, Trainer> trainerMap = new java.util.HashMap<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Member> members = session.createQuery("FROM Member", Member.class).getResultList();
            for (Member m : members) {
                String display = m.getFirstName() + " " + m.getLastName() + " (" + m.getEmail() + ")";
                memberCombo.addItem(display);
                memberMap.put(display, m);
            }

            List<Trainer> trainers = session.createQuery("FROM Trainer", Trainer.class).getResultList();
            for (Trainer t : trainers) {
                String display = t.getFirstName() + " " + t.getLastName();
                trainerCombo.addItem(display);
                trainerMap.put(display, t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTextField amountField = new JTextField();
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        String[] statuses = {"Pending", "Paid", "Failed"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        String[] methods = {"Credit Card", "Cash", "Bank Transfer", "Other"};
        JComboBox<String> methodCombo = new JComboBox<>(methods);
        methodCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JButton generateBillBtn = new JButton("Generate Bill");
        generateBillBtn.setBackground(ACCENT_COLOR);
        generateBillBtn.setForeground(Color.WHITE);
        generateBillBtn.setFocusPainted(false);

        formCard.add(createFormLabel("Select Member"));
        formCard.add(memberCombo);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createFormLabel("Select Trainer (for PT bill)"));
        formCard.add(trainerCombo);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createFormLabel("Amount ($)"));
        formCard.add(amountField);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createFormLabel("Payment Status"));
        formCard.add(statusCombo);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createFormLabel("Payment Method"));
        formCard.add(methodCombo);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(generateBillBtn);

        // Right: Bills list with update capability
        JPanel billsCard = createCard("All Bills");
        String[] columns = {"ID", "Member", "Trainer", "Amount", "Date", "Status", "Method"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        java.util.Map<Integer, Billing> billMap = new java.util.HashMap<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Billing> bills = session.createQuery(
                    "FROM Billing ORDER BY paymentDate DESC",
                    Billing.class
            ).getResultList();

            int row = 0;
            for (Billing b : bills) {
                model.addRow(new Object[]{
                        b.getBillId(),
                        b.getMember().getFirstName() + " " + b.getMember().getLastName(),
                        b.getTrainer().getFirstName() + " " + b.getTrainer().getLastName(),
                        "$" + b.getAmount(),
                        b.getPaymentDate().toString(),
                        b.getPaymentStatus(),
                        b.getPaymentMethod()
                });
                billMap.put(row, b);
                row++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable billTable = new JTable(model);
        billTable.setRowHeight(30);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);

        JButton markPaidBtn = new JButton("Mark as Paid");
        markPaidBtn.setBackground(new Color(40, 167, 69));
        markPaidBtn.setForeground(Color.WHITE);
        markPaidBtn.setFocusPainted(false);

        JButton markPendingBtn = new JButton("Mark as Pending");
        markPendingBtn.setBackground(new Color(255, 193, 7));
        markPendingBtn.setForeground(Color.BLACK);
        markPendingBtn.setFocusPainted(false);

        markPaidBtn.addActionListener(e -> updateBillStatus(billTable, billMap, "Paid"));
        markPendingBtn.addActionListener(e -> updateBillStatus(billTable, billMap, "Pending"));

        buttonPanel.add(markPaidBtn);
        buttonPanel.add(markPendingBtn);

        billsCard.add(new JScrollPane(billTable), BorderLayout.CENTER);
        billsCard.add(buttonPanel, BorderLayout.SOUTH);

        generateBillBtn.addActionListener(e -> {
            try {
                String memberDisplay = (String) memberCombo.getSelectedItem();
                String trainerDisplay = (String) trainerCombo.getSelectedItem();

                if (memberDisplay == null || trainerDisplay == null) {
                    JOptionPane.showMessageDialog(this, "Please select member and trainer.");
                    return;
                }

                Member selectedMember = memberMap.get(memberDisplay);
                Trainer selectedTrainer = trainerMap.get(trainerDisplay);
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                String status = (String) statusCombo.getSelectedItem();
                String method = (String) methodCombo.getSelectedItem();

                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    session.beginTransaction();

                    Billing bill = new Billing();
                    bill.setMember(selectedMember);
                    bill.setTrainer(selectedTrainer);
                    bill.setAmount(amount);
                    bill.setPaymentDate(LocalDate.now());
                    bill.setPaymentStatus(status);
                    bill.setPaymentMethod(method);

                    session.persist(bill);
                    session.getTransaction().commit();

                    JOptionPane.showMessageDialog(this, "Bill generated successfully!");
                    amountField.setText("");

                    switchPanel("Billing & Payment", selectedNavButton);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error generating bill: " + ex.getMessage());
            }
        });

        content.add(formCard);
        content.add(billsCard);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private void updateBillStatus(JTable table, java.util.Map<Integer, Billing> billMap, String newStatus) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a bill to update.");
            return;
        }

        Billing bill = billMap.get(selectedRow);
        if (bill == null) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            bill.setPaymentStatus(newStatus);
            session.merge(bill);
            session.getTransaction().commit();

            JOptionPane.showMessageDialog(this, "Bill status updated to: " + newStatus);
            switchPanel("Billing & Payment", selectedNavButton);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating bill: " + ex.getMessage());
        }
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
        btn.setForeground(Color.WHITE);
        return btn;
    }

    private void highlightNavButton(JButton btn) {
        if (btn != null) {
            btn.setBackground(Color.WHITE);
            btn.setForeground(SIDEBAR_BG);
            btn.setFont(new Font("Inter", Font.BOLD, 14));
        }
    }

    private void resetNavButton(JButton btn) {
        if (btn != null) {
            btn.setBackground(SIDEBAR_BG);
            btn.setForeground(Color.WHITE);
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

    private void styleActionButton(JButton btn) {
        btn.setBackground(ACCENT_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
    }
}
