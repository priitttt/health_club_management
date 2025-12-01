package healthClubManagement.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import healthClubManagement.db.Member;
import healthClubManagement.db.Trainer;
import healthClubManagement.db.Admin;
import healthClubManagement.db.HibernateUtil;
import org.hibernate.Session;

import com.toedter.calendar.JDateChooser;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

/**
 * RegisterFrame - Registration interface for new users
 * 
 * This class provides a registration form for:
 * - Members
 * - Trainers (with speciality selection)
 * - Admins
 * 
 * Features:
 * - Role-based registration
 * - Form validation
 * - Database persistence using Hibernate
 * 
 * @author Health Club Management System
 */
public class RegisterFrame extends JFrame {

    // Currently selected user role (default: Member)
    private String selectedRole = "Member";

    // Speciality components for Trainer registration
    private JPanel specialityGroup;
    private JComboBox<String> specialityCombo;

    /**
     * Helper method to create a form group with label and field
     * Creates a vertically stacked layout with label on top and field below
     * 
     * @param label The label text
     * @param field The input component
     * @return JPanel containing the label and field
     */
    private JPanel singleFieldGroup(String label, JComponent field) {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Inter", Font.BOLD, 14));
        lbl.setForeground(new Color(40, 40, 40));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        group.add(lbl);
        group.add(Box.createVerticalStrut(5));
        group.add(field);

        return group;
    }

    /**
     * Constructor - Initializes the registration frame
     * Sets up the UI with left branding panel and right registration form
     */
    public RegisterFrame() {
        // Configure main window properties
        setTitle("FitZone Club – Create Account");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 850);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        /* ---------- LEFT GRADIENT PANEL - Branding Section ---------- */
        JPanel leftPanel = new GradientPanel();
        leftPanel.setPreferredSize(new Dimension(520, 700));
        leftPanel.setLayout(new GridBagLayout());

        JPanel branding = new JPanel();
        branding.setOpaque(false);
        branding.setLayout(new BoxLayout(branding, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("🏋️");
        logo.setFont(new Font("Inter", Font.BOLD, 70));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("FitZone Club");
        title.setFont(new Font("Inter", Font.BOLD, 38));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Your Journey to Fitness Starts Here");
        subtitle.setFont(new Font("Inter", Font.PLAIN, 16));
        subtitle.setForeground(new Color(255, 255, 255, 180));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        branding.add(logo);
        branding.add(Box.createVerticalStrut(20));
        branding.add(title);
        branding.add(Box.createVerticalStrut(5));
        branding.add(subtitle);

        leftPanel.add(branding);
        add(leftPanel, BorderLayout.WEST);

        /* ---------- RIGHT MAIN PANEL ---------- */
        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(new Color(245, 245, 245));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));
        card.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        JLabel heading = new JLabel("Create Account");
        heading.setFont(new Font("Inter", Font.BOLD, 24));
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel desc = new JLabel("Join our fitness community today");
        desc.setFont(new Font("Inter", Font.PLAIN, 14));
        desc.setForeground(Color.GRAY);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(heading);
        card.add(desc);
        card.add(Box.createVerticalStrut(25));

        /* ---------- ROLE SELECTOR - Member/Trainer/Admin ---------- */
        JPanel rolePanel = new JPanel(new GridLayout(1, 3, 12, 0));
        rolePanel.setOpaque(false);
        rolePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create role selection buttons
        JButton member = createRoleButton("Member");
        JButton trainer = createRoleButton("Trainer");
        JButton admin = createRoleButton("Admin");

        // Set Member as default selected role
        highlightRole(member);

        rolePanel.add(member);
        rolePanel.add(trainer);
        rolePanel.add(admin);

        card.add(rolePanel);
        card.add(Box.createVerticalStrut(20));

        /* ---------- FIRST / LAST NAME ---------- */
        RoundedInputField first = new RoundedInputField("First Name", null, false);
        RoundedInputField last = new RoundedInputField("Last Name", null, false);

        JPanel rowName = new JPanel(new GridLayout(1, 2, 15, 0));
        rowName.setOpaque(false);
        rowName.add(singleFieldGroup("First Name", first));
        rowName.add(singleFieldGroup("Last Name", last));
        rowName.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(rowName);
        card.add(Box.createVerticalStrut(20));

        /* ---------- EMAIL ---------- */
        RoundedInputField email = new RoundedInputField(
                "john@example.com",
                new ImageIcon("src/main/resources/icons/email.png"),
                false
        );
        card.add(formGroup("Email Address", email));
        card.add(Box.createVerticalStrut(20));

        /* ---------- PHONE ---------- */
        RoundedInputField phone = new RoundedInputField(
                "(555) 123-4567",
                new ImageIcon("src/main/resources/icons/phone.png"),
                false
        );
        card.add(formGroup("Phone Number", phone));
        card.add(Box.createVerticalStrut(20));

        /* ---------- DOB + GENDER ---------- */
        JDateChooser dobChooser = new JDateChooser();
        dobChooser.setDateFormatString("yyyy-MM-dd");
        dobChooser.setPreferredSize(new Dimension(250, 45));
        dobChooser.setFont(new Font("Inter", Font.PLAIN, 14));
        dobChooser.getCalendarButton().setIcon(new BigCalendarIcon(28));

        String[] genders = {"Select", "Male", "Female", "Other"};
        JComboBox<String> genderCombo = new JComboBox<>(genders);
        genderCombo.setPreferredSize(new Dimension(140, 45));
        genderCombo.setFont(new Font("Inter", Font.PLAIN, 14));

        JPanel rowDobGender = new JPanel(new GridLayout(1, 2, 15, 0));
        rowDobGender.setOpaque(false);
        rowDobGender.add(singleFieldGroup("Date of Birth", dobChooser));
        rowDobGender.add(singleFieldGroup("Gender", genderCombo));
        rowDobGender.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(rowDobGender);
        card.add(Box.createVerticalStrut(20));

        /* ---------- SPECIALITY FIELD (ONLY FOR TRAINERS) ---------- */
        String[] specialities = {
                "Select Speciality",
                "Strength Training",
                "Yoga",
                "Cardio",
                "Weight Loss",
                "CrossFit",
                "Zumba",
                "Pilates",
                "Personal Training"
        };

        specialityCombo = new JComboBox<>(specialities);
        specialityCombo.setPreferredSize(new Dimension(250, 45));
        specialityCombo.setFont(new Font("Inter", Font.PLAIN, 14));

        specialityGroup = formGroup("Trainer Speciality", specialityCombo);
        specialityGroup.setVisible(false); // hidden by default

        card.add(specialityGroup);
        card.add(Box.createVerticalStrut(20));

        /* ---------- PASSWORD ---------- */
        RoundedInputField pass = new RoundedInputField(
                "Create a password",
                new ImageIcon("src/main/resources/icons/lock.png"),
                true
        );
        card.add(formGroup("Password", pass));
        card.add(Box.createVerticalStrut(10));

        JLabel strength = new JLabel("Password strength");
        strength.setFont(new Font("Inter", Font.PLAIN, 12));
        strength.setForeground(Color.GRAY);
        strength.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(strength);

        card.add(Box.createVerticalStrut(20));

        /* ---------- CONFIRM PASSWORD ---------- */
        RoundedInputField confirm = new RoundedInputField(
                "Confirm your password",
                new ImageIcon("src/main/resources/icons/lock.png"),
                true
        );
        card.add(formGroup("Confirm Password", confirm));
        card.add(Box.createVerticalStrut(15));

        /* ---------- TERMS ---------- */
        JCheckBox terms = new JCheckBox("I agree to the Terms of Service and Privacy Policy");
        terms.setFont(new Font("Inter", Font.PLAIN, 13));
        terms.setOpaque(false);
        terms.setForeground(new Color(60, 60, 60));
        terms.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(terms);

        card.add(Box.createVerticalStrut(25));

        /* ---------- CREATE ACCOUNT BUTTON ---------- */
        JButton createBtn = new JButton("Create Account");
        createBtn.setBackground(new Color(233, 69, 96));
        createBtn.setForeground(Color.WHITE);
        createBtn.setFont(new Font("Inter", Font.BOLD, 16));
        createBtn.setPreferredSize(new Dimension(300, 50));
        createBtn.setFocusPainted(false);
        createBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create Account button action handler
        createBtn.addActionListener(e -> {

            System.out.println("Button clicked!");

            // ========== FORM VALIDATION (ALL FIELDS COMPULSORY) ==========
            // Validate all required fields before proceeding with registration
            if (first.getText().trim().isEmpty()) {
                error("First name is required.");
                return;
            }

            if (last.getText().trim().isEmpty()) {
                error("Last name is required.");
                return;
            }

            String emailVal = email.getText().trim();
            if (emailVal.isEmpty()) {
                error("Email is required.");
                return;
            }
            if (!emailVal.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                error("Invalid email format.");
                return;
            }

            String phoneVal = phone.getText().replaceAll("\\D", "");
            if (phoneVal.isEmpty()) {
                error("Phone number is required.");
                return;
            }
            if (!phoneVal.matches("^\\d{10,15}$")) {
                error("Phone must be 10–15 digits.");
                return;
            }

            Date dobDate = dobChooser.getDate();
            if (dobDate == null) {
                error("Please select your date of birth.");
                return;
            }

            LocalDate birth = dobDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int age = Period.between(birth, LocalDate.now()).getYears();
            if (age < 16) {
                error("You must be at least 16 years old to register.");
                return;
            }

            if (genderCombo.getSelectedIndex() == 0) {
                error("Please select your gender.");
                return;
            }

            String p1 = pass.getText().trim();
            String p2 = confirm.getText().trim();

            if (p1.isEmpty()) {
                error("Password is required.");
                return;
            }

            if (p2.isEmpty()) {
                error("Please confirm your password.");
                return;
            }

            if (!p1.equals(p2)) {
                error("Passwords do not match.");
                return;
            }

            if (!terms.isSelected()) {
                error("You must accept the Terms of Service and Privacy Policy.");
                return;
            }

            // SPECIALITY CHECK (Trainer only)
            // Trainers must select a speciality
            if (selectedRole.equals("Trainer")) {
                if (specialityCombo.getSelectedIndex() == 0) {
                    error("Please select trainer speciality.");
                    return;
                }
            }

            // ========== SAVE TO DATABASE (HIBERNATE) ==========
            // Create and persist the appropriate entity based on selected role
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();

                // Create Member entity
                if (selectedRole.equals("Member")) {
                    Member m = new Member();
                    m.setFirstName(first.getText().trim());
                    m.setLastName(last.getText().trim());
                    m.setEmail(emailVal);
                    m.setDateOfBirth(birth); // LocalDate
                    m.setGender(genderCombo.getSelectedItem().toString());
                    m.setPhoneNumber(phoneVal);
                    m.setPassword(p1);
                    session.persist(m);
                }

                // Create Trainer entity
                if (selectedRole.equals("Trainer")) {
                    Trainer t = new Trainer();
                    t.setFirstName(first.getText().trim());
                    t.setLastName(last.getText().trim());
                    t.setEmail(emailVal);
                    t.setSpecialization(specialityCombo.getSelectedItem().toString());
                    t.setPassword(p1);
                    session.persist(t);
                }

                // Create Admin entity
                if (selectedRole.equals("Admin")) {
                    Admin a = new Admin();
                    a.setFirstName(first.getText().trim());
                    a.setLastName(last.getText().trim());
                    a.setEmail(emailVal);
                    a.setRole("Admin");
                    a.setPassword(p1);
                    session.persist(a);
                }

                // Commit transaction to save to database
                session.getTransaction().commit();
            } catch (Exception ex) {
                ex.printStackTrace();
                error("Error while creating account: " + ex.getMessage());
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Account Created Successfully!\nPlease login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();
            new LoginFrame().setVisible(true);
        });

        card.add(createBtn);
        card.add(Box.createVerticalStrut(15));

        /* ---------- BACK TO LOGIN ---------- */
        JLabel login = new JLabel("Already have an account? Sign In");
        login.setFont(new Font("Inter", Font.PLAIN, 14));
        login.setForeground(new Color(233, 69, 96));
        login.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        login.setAlignmentX(Component.LEFT_ALIGNMENT);

        login.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        card.add(login);

        /* ---------- SCROLL WRAPPER ---------- */
        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        right.add(scroll, BorderLayout.CENTER);
        add(right, BorderLayout.CENTER);
    }

    /* ---------- FORM GROUP HELPER ---------- */
    /**
     * Creates a form group with label and field
     * 
     * @param label The label text
     * @param field The input component
     * @return JPanel containing the form group
     */
    private JPanel formGroup(String label, JComponent field) {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Inter", Font.BOLD, 14));
        lbl.setForeground(new Color(40, 40, 40));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        group.add(lbl);
        group.add(Box.createVerticalStrut(5));
        group.add(field);
        return group;
    }

    /* ---------- ROLE BUTTON HELPERS ---------- */
    /**
     * Creates a role selection button
     * 
     * @param role The role name (Member, Trainer, or Admin)
     * @return Styled JButton for role selection
     */
    private JButton createRoleButton(String role) {
        JButton btn = new JButton(role);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Inter", Font.PLAIN, 14));
        btn.setBackground(new Color(245, 245, 245));
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        btn.addActionListener(e -> {
            selectedRole = role;

            JPanel parent = (JPanel) btn.getParent();
            for (Component c : parent.getComponents()) resetRole((JButton) c);
            highlightRole(btn);

            // Show/hide speciality field based on selected role
            // Speciality field is only visible for Trainers
            if (role.equals("Trainer")) {
                specialityGroup.setVisible(true);
            } else {
                specialityGroup.setVisible(false);
                specialityCombo.setSelectedIndex(0); // Reset selection
            }
        });

        return btn;
    }

    /**
     * Highlights a role button to indicate it's selected
     * 
     * @param btn The button to highlight
     */
    private void highlightRole(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(new Color(233, 69, 96)));
    }

    /**
     * Resets a role button to its default unselected state
     * 
     * @param btn The button to reset
     */
    private void resetRole(JButton btn) {
        btn.setBackground(new Color(245, 245, 245));
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
    }

    /* ---------- ERROR DIALOG ---------- */
    /**
     * Displays an error message dialog
     * 
     * @param msg The error message to display
     */
    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}
