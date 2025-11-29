package healthClubManagement.gui;

import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(
                0, 0, new Color(26, 26, 46),
                getWidth(), getHeight(), new Color(15, 52, 96)
        );
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}