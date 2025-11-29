package healthClubManagement.gui;

import healthClubManagement.db.Member;
import healthClubManagement.db.Trainer;
import healthClubManagement.db.Admin;
import healthClubManagement.db.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

    private String selectedRole = "Member";   // default role

    public LoginFrame() {
        setTitle("FitZone Club – Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        /* ---------- LEFT GRADIENT PANEL ---------- */
        JPanel leftPanel = new GradientPanel();
        leftPanel.setPreferredSize(new Dimension(520, 700));
        leftPanel.setLayout(new GridBagLayout());

        JPanel brandingBox = new JPanel();
        brandingBox.setOpaque(false);
        brandingBox.setLayout(new BoxLayout(brandingBox, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("🏋️");
        logo.setFont(new Font("Inter", Font.BOLD, 70));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brandName = new JLabel("FitZone Club");
        brandName.setFont(new Font("Inter", Font.BOLD, 38));
        brandName.setForeground(Color.WHITE);
        brandName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Your Journey to Fitness Starts Here");
        tagline.setFont(new Font("Inter", Font.PLAIN, 16));
        tagline.setForeground(new Color(255, 255, 255, 180));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        brandingBox.add(logo);
        brandingBox.add(Box.createVerticalStrut(20));
        brandingBox.add(brandName);
        brandingBox.add(Box.createVerticalStrut(5));
        brandingBox.add(tagline);

        leftPanel.add(brandingBox);
        add(leftPanel, BorderLayout.WEST);

        /* ---------- RIGHT LOGIN CARD ---------- */
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(245, 245, 245));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(500, 580));
        card.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        JLabel title = new JLabel("Welcome Back!");
        title.setFont(new Font("Inter", Font.BOLD, 26));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);

        JLabel subtitle = new JLabel("Please sign in to your account");
        subtitle.setFont(new Font("Inter", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(subtitle);

        card.add(Box.createVerticalStrut(20));

        /* ---------- ROLE SELECT ---------- */
        JPanel rolePanel = new JPanel(new GridLayout(1, 3, 12, 0));
        rolePanel.setOpaque(false);

        JButton memberBtn = createRoleButton("Member");
        JButton trainerBtn = createRoleButton("Trainer");
        JButton adminBtn = createRoleButton("Admin");

        highlightRoleButton(memberBtn);

        rolePanel.add(memberBtn);
        rolePanel.add(trainerBtn);
        rolePanel.add(adminBtn);

        rolePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(rolePanel);
        card.add(Box.createVerticalStrut(20));

        /* ---------- EMAIL FIELD ---------- */
        RoundedInputField emailField = new RoundedInputField(
                "Enter your email",
                new ImageIcon("src/main/resources/icons/email.png"),
                false
        );
        card.add(formGroup("Email Address", emailField));
        card.add(Box.createVerticalStrut(15));

        /* ---------- PASSWORD FIELD ---------- */
        RoundedInputField passField = new RoundedInputField(
                "Enter your password",
                new ImageIcon("src/main/resources/icons/lock.png"),
                true
        );
        card.add(formGroup("Password", passField));
        card.add(Box.createVerticalStrut(10));

        /* ---------- REMEMBER ---------- */
        JPanel options = new JPanel(new BorderLayout());
        options.setOpaque(false);

        JCheckBox remember = new JCheckBox("Remember me");
        remember.setOpaque(false);

        JLabel forgot = new JLabel("Forgot Password?");
        forgot.setForeground(new Color(233, 69, 96));
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        options.add(remember, BorderLayout.WEST);
        options.add(forgot, BorderLayout.EAST);

        card.add(options);
        card.add(Box.createVerticalStrut(25));

        /* ---------- LOGIN BUTTON ---------- */
        JButton loginBtn = new JButton("Sign In");
        loginBtn.setBackground(new Color(233, 69, 96));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Inter", Font.BOLD, 16));
        loginBtn.setFocusPainted(false);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setPreferredSize(new Dimension(300, 50));

        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = passField.getPassword().trim();

            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email is required.");
                return;
            }

            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password is required.");
                return;
            }

            loginUser(selectedRole, email, password);
        });

        card.add(loginBtn);
        card.add(Box.createVerticalStrut(20));

        /* ---------- SWITCH TO REGISTER ---------- */
        JLabel switchReg = new JLabel("Don't have an account? Create Account");
        switchReg.setFont(new Font("Inter", Font.PLAIN, 14));
        switchReg.setForeground(new Color(233, 69, 96));
        switchReg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        switchReg.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new RegisterFrame().setVisible(true);
            }
        });

        card.add(switchReg);

        rightPanel.add(card);
        add(rightPanel, BorderLayout.CENTER);
    }

    /* ===========================================================
                 🔥 LOGIN + DASHBOARD OPENING LOGIC
       =========================================================== */
    private void loginUser(String role, String email, String password) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            if (role.equals("Member")) {
                Query<Member> query = session.createQuery(
                        "FROM Member WHERE email = :email AND password = :password",
                        Member.class
                );
                query.setParameter("email", email);
                query.setParameter("password", password);

                Member member = query.uniqueResult();

                if (member != null) {
                    dispose();
                    new MemberDashboard(member).setVisible(true);
                    return;
                }

                JOptionPane.showMessageDialog(this, "Invalid Member login.");
                return;
            }

            if (role.equals("Trainer")) {
                Query<Trainer> query = session.createQuery(
                        "FROM Trainer WHERE email = :email AND password = :password",
                        Trainer.class
                );
                query.setParameter("email", email);
                query.setParameter("password", password);

                Trainer trainer = query.uniqueResult();

                if (trainer != null) {
                    dispose();
                    new TrainerDashboard(trainer).setVisible(true);
                    return;
                }

                JOptionPane.showMessageDialog(this, "Invalid Trainer login.");
                return;
            }

            if (role.equals("Admin")) {
                Query<Admin> query = session.createQuery(
                        "FROM Admin WHERE email = :email AND password = :password",
                        Admin.class
                );
                query.setParameter("email", email);
                query.setParameter("password", password);

                Admin admin = query.uniqueResult();

                if (admin != null) {
                    dispose();
                    new AdminDashboard(admin).setVisible(true); // placeholder
                    return;
                }

                JOptionPane.showMessageDialog(this, "Invalid Admin login.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Login failed: " + e.getMessage());
        }
    }

    /* ---------- ROLE SELECT BUTTONS ---------- */
    private JButton createRoleButton(String role) {
        JButton btn = new JButton(role);
        btn.setFont(new Font("Inter", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(245, 245, 245));
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        btn.addActionListener(e -> {
            selectedRole = role;
            JPanel parent = (JPanel) btn.getParent();
            for (Component c : parent.getComponents()) resetRoleButton((JButton) c);
            highlightRoleButton(btn);
        });

        return btn;
    }

    private void highlightRoleButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(new Color(233, 69, 96)));
    }

    private void resetRoleButton(JButton btn) {
        btn.setBackground(new Color(245, 245, 245));
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
    }

    /* ---------- FORM GROUP ---------- */
    private JPanel formGroup(String label, JComponent input) {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Inter", Font.BOLD, 14));
        lbl.setForeground(Color.BLACK);

        group.add(lbl);
        group.add(Box.createVerticalStrut(5));
        group.add(input);

        return group;
    }
}
