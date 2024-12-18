package Vue;

import java.awt.*;
import javax.swing.*;

public class ToolPalette extends JToolBar {
    // Constructeur pour initialiser la barre d'outils flottante
    public ToolPalette() {
        // Définir la barre d'outils comme flottante
        setFloatable(true);
        setRollover(true); // Pour un effet visuel au survol

        // Utiliser un GridLayout pour organiser les boutons en deux colonnes
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 5, 5)); // 0 lignes signifie un nombre de lignes dynamique,
                                                                     // 2 colonnes, 5 pixels d'espace entre les
                                                                     // composants

        // Ajouter des boutons à la barre d'outils
        JButton paintBucketButton = createToolButton("Seau de peinture", "paintbucket.png");
        JButton pickColorButton = createToolButton("Pipette", "pipette.png");
        JButton selectRectangleButton = createToolButton("Sélection Rectangle", "rectangle.png");
        JButton selectCircleButton = createToolButton("Sélection Cercle", "circle.png");

        // Ajouter des boutons au panneau de boutons
        buttonPanel.add(paintBucketButton);
        buttonPanel.add(pickColorButton);
        buttonPanel.add(selectRectangleButton);
        buttonPanel.add(selectCircleButton);

        // Ajouter le panneau de boutons à la barre d'outils
        add(buttonPanel);

        // Ajouter un séparateur entre les groupes d'outils
        addSeparator();

        // Définir la taille préférée de la barre d'outils
        setPreferredSize(new Dimension(200, 100)); // Ajustez les dimensions selon vos besoins
    }

    // Méthode pour créer un bouton d'outil avec une icône
    private JButton createToolButton(String toolName, String iconPath) {
        ImageIcon icon = new ImageIcon(iconPath); // Assurez-vous que l'icône est dans le bon dossier
        JButton button = new JButton(icon);
        button.setToolTipText(toolName); // Afficher le nom de l'outil au survol
        button.setText(toolName); // Afficher le nom de l'outil sous l'icône
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setPreferredSize(null); // Laisser le bouton prendre la taille nécessaire
        return button;
    }
}