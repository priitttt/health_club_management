import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import healthClubManagement.gui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
