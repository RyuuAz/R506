package View;

// Importations nécessaires
import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.FlatLightLaf;

// Classe pour la fenêtre principale (JFrame)
public class MainFrame extends JFrame {
    public MainFrame() {
        // Configuration de la fenêtre principale
        setTitle("Éditeur d'Images - Application Moderne");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Barre d'outils en haut
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        toolBar.add(createToolBarButton("Pot de Peinture", "icons/paint_bucket.png"));
        toolBar.add(createToolBarButton("Découper", "icons/cut.png"));
        toolBar.add(createToolBarButton("Rotation", "icons/rotate.png"));
        toolBar.add(createToolBarButton("Retournement", "icons/flip.png"));
        toolBar.add(createToolBarButton("Pipette", "icons/pipette.png"));
        toolBar.add(createToolBarButton("Enlever Fond", "icons/remove_bg.png"));
        toolBar.add(createToolBarButton("Assombrir/Éclairer", "icons/brightness.png"));
        toolBar.add(createToolBarButton("Écrire Texte", "icons/text.png"));

        add(toolBar, BorderLayout.NORTH);

        // Zone de travail principale
        MainPanel mainPanel = new MainPanel();
        add(BorderLayout.CENTER, mainPanel);

        // Barre de statut en bas
        JLabel statusBar = new JLabel("Prêt", JLabel.CENTER);
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        add(statusBar, BorderLayout.SOUTH);
    }

    private JButton createToolBarButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setIcon(new ImageIcon(iconPath)); // Assurez-vous que les icônes existent
        button.setFocusPainted(false);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        return button;
    }

    public static void main(String[] args) {
        // Application du thème FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}