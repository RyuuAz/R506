package Vue;

import java.awt.*;
import javax.swing.*;

public class ToolPalette extends JToolBar {
    // Constructeur pour initialiser la barre d'outils flottante
    public ToolPalette() {
        // Appliquer les propriétés de la barre d'outils
        setFloatable(true);
        setRollover(true);

        // Utiliser un GridLayout pour organiser les boutons en deux colonnes
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // Espacement de 10 pixels pour plus d'air

        // Ajouter des boutons à la barre d'outils
        JButton paintBucketButton = createToolButton("Seau de peinture", "../img/ToolPalette/paint-bucket.svg");
        JButton pickColorButton = createToolButton("Pipette", "../img/ToolPalette/eyedropper.svg");
        JButton selectRectangleButton = createToolButton("Sélection Rectangle", "../img/ToolPalette/rectangle.svg");
        JButton selectCircleButton = createToolButton("Sélection Cercle", "../img/ToolPalette/circle.svg");

        // Ajouter des boutons au panneau de boutons
        buttonPanel.add(paintBucketButton);
        buttonPanel.add(pickColorButton);
        buttonPanel.add(selectRectangleButton);
        buttonPanel.add(selectCircleButton);

        // Ajouter le panneau de boutons à la barre d'outils
        add(buttonPanel);

        // Ajouter un séparateur visuel entre les groupes d'outils
        addSeparator();
    }

    // Méthode pour créer un bouton d'outil avec une icône
    private JButton createToolButton(String toolName, String iconPath) {
        // Charger l'icône (à adapter si les ressources ne sont pas chargées correctement)
        ImageIcon icon = new ImageIcon(iconPath); 
        JButton button = new JButton(icon);
        button.setToolTipText(toolName);
        button.setText(toolName);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);

        // Style FlatLaf : ajuster la taille et le padding
        button.setFocusPainted(false); // Désactiver l'effet de focus par défaut
        button.setMargin(new Insets(5, 5, 5, 5)); // Espacement interne
        button.setPreferredSize(new Dimension(90, 70)); // Taille uniforme des boutons
        return button;
    }
}