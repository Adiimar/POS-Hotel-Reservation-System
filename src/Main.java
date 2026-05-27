import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // 1. Load rooms, customers, reservations from text files
        
        FileHandler.initialize();

        // 2. Set Nimbus look-and-feel for a modern base appearance
        //    (our custom colors override it per-component)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus isn't available, the system default is fine
        }

        // 3. Launch the Login Screen on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginScreen login = new LoginScreen();
            login.setVisible(true);
        });
    }
}
