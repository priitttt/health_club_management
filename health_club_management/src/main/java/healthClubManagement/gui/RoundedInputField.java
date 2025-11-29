package healthClubManagement.gui;

import javax.swing.*;
import java.awt.*;

public class RoundedInputField extends JPanel {

    private final JTextField textField;

    public RoundedInputField(String placeholder, Icon icon, boolean isPassword) {

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // TEXT FIELD
        if (isPassword) {
            textField = new JPasswordField();
        } else {
            textField = new JTextField();
        }

        textField.setBorder(null);
        textField.setOpaque(false);
        textField.setFont(new Font("Inter", Font.PLAIN, 16));
        textField.putClientProperty("JTextField.placeholderText", placeholder);
        textField.setMargin(new Insets(0, 0, 0, 0));

        // ICON LABEL
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));

        // COMPONENTS
        add(iconLabel, BorderLayout.WEST);
        add(textField, BorderLayout.CENTER);

        // PERFECT HEIGHT + WIDTH
        setPreferredSize(new Dimension(380, 55));  // wider + taller
    }

    public String getText() {
        return textField.getText();
    }

    public String getPassword() {
        return new String(((JPasswordField) textField).getPassword());
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // BACKGROUND
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

        // BORDER
        g2.setColor(new Color(220, 220, 220));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
    }
}
