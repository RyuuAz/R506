package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolBar extends JToolBar {
    private JPanel colorDisplayPanel;

    // Constructeur pour initialiser la barre d'outils flottante
    public ToolBar() {
        // Définir la barre d'outils comme flottante
        setFloatable(true);
        setRollover(true); // Pour un effet visuel au survol

        // Ajouter des boutons à la barre d'outils
        JButton paintBucketButton = createToolButton("Seau de peinture", "paintbucket.png");
        JButton pickColorButton = createToolButton("Pipette", "pipette.png");
        JButton selectRectangleButton = createToolButton("Sélection Rectangle", "rectangle.png");
        JButton selectCircleButton = createToolButton("Sélection Cercle", "circle.png");

        // Ajouter des boutons à la barre d'outils
        add(paintBucketButton);
        add(pickColorButton);
        add(selectRectangleButton);
        add(selectCircleButton);

        // Ajouter un séparateur entre les groupes d'outils
        addSeparator();

        // Panneau pour afficher la couleur sélectionnée
        colorDisplayPanel = new JPanel();
        colorDisplayPanel.setPreferredSize(new Dimension(20, 20));
        colorDisplayPanel.setBackground(Color.WHITE); // Couleur initiale (blanc)
        add(colorDisplayPanel);
    }

    // Méthode pour créer un bouton d'outil avec une icône
    private JButton createToolButton(String toolName, String iconPath) {
        ImageIcon icon = new ImageIcon(iconPath); // Assurez-vous que l'icône est dans le bon dossier
        JButton button = new JButton(icon);
        button.setToolTipText(toolName); // Afficher le nom de l'outil au survol
        button.setText(toolName); // Afficher le nom de l'outil sous l'icône
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        return button;
    }

    // Méthode pour obtenir le panneau de couleur (si besoin)
    public JPanel getColorDisplayPanel() {
        return colorDisplayPanel;
    }
}
