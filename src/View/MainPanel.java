package View;

import javax.swing.*;
import java.awt.*;

// Classe pour la zone de travail (JPanel)
class MainPanel extends JPanel {
    public MainPanel() {
        // Configuration du panneau principal
        setBackground(Color.LIGHT_GRAY);
        setLayout(new BorderLayout());

        // Exemple d'image avec barre de défilement
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setIcon(new ImageIcon("chemin/vers/votre/image.jpg")); // Remplacez par un vrai chemin

        JScrollPane scrollPane = new JScrollPane(imageLabel);
        add(scrollPane, BorderLayout.CENTER);

        // Zone clicable pour test (peut être améliorée selon les besoins)
        JButton clickZone = new JButton("Zone Cliquez");
        clickZone.setPreferredSize(new Dimension(200, 50));
        clickZone.setBackground(new Color(0, 120, 215));
        clickZone.setForeground(Color.WHITE);
        clickZone.setFocusPainted(false);
        add(clickZone, BorderLayout.SOUTH);
    }
}
