package healthClubManagement.gui;

import javax.swing.*;
import java.awt.*;

public class BigCalendarIcon implements Icon {

    private int size;

    public BigCalendarIcon(int size) {
        this.size = size;
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Outer square
        g2.setColor(new Color(230, 230, 230));
        g2.fillRoundRect(x, y, size, size, 6, 6);

        g2.setColor(new Color(180, 180, 180));
        g2.drawRoundRect(x, y, size, size, 6, 6);

        // Calendar rings
        g2.fillRect(x + size/4 - 3, y + 4, 6, 8);
        g2.fillRect(x + (3*size)/4 - 3, y + 4, 6, 8);

        // Inside date area
        g2.drawRect(x + size/4, y + size/2, size/2, size/3);
    }
}