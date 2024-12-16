package ViewLouis.FramePrincipal;

// Importations nécessaires
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.formdev.flatlaf.FlatLightLaf;

// Classe pour la fenêtre principale (JFrame)
public class MainFrame extends JFrame {

    public MainFrame() {
        // Configuration de la fenêtre principale
        setTitle("Application avec FlatLaf");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer la fenêtre

        // Appliquer le look and feel FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Création du panneau principal
        MainPanel mainPanel = new MainPanel();
        setContentPane(mainPanel);

        // Rendre la fenêtre visible
        setVisible(true);
    }

    public static void main(String[] args) {
        // Lancer l'application avec FlatLaf
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}