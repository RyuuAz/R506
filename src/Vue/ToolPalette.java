package Vue;

import java.awt.*;
import javax.swing.*;

public class ToolPalette extends JToolBar {
	// Constructeur pour initialiser la barre d'outils flottante
	private ImageView view;

	public ToolPalette(ImageView view) {
		// Appliquer les propriétés de la barre d'outils
		super(JToolBar.VERTICAL);
		this.view = view;
		setFloatable(true); // Activer la flottabilité
		setRollover(true);

		// Ajouter des boutons à la barre d'outils
		JButton paintBucketButton = createToolButton("Seau de peinture", "img/ToolPalette/paint-bucket.png");
		JButton pickColorButton = createToolButton("Pipette", "img/ToolPalette/eyedrop.png");
		JButton selectRectangleButton = createToolButton("Sélection Rectangle", "img/ToolPalette/select_rectangle.png");
		JButton selectCircleButton = createToolButton("Sélection Cercle", "img/ToolPalette/select_circle.png");

		// Ajouter des boutons directement à la barre d'outils
		add(paintBucketButton);
		add(pickColorButton);
		add(selectRectangleButton);
		add(selectCircleButton);

		// Ajouter un séparateur visuel entre les groupes d'outils
		addSeparator();

		paintBucketButton.addActionListener(e -> {
			view.togglePaintBucket(e);
		});
	
		// ActionListener pour la pipette
		pickColorButton.addActionListener(e -> {
			view.togglePickColor(e);
		});
	
		selectRectangleButton.addActionListener(e -> {
			view.toggleIsDrawingRectangle();
		});
	
		selectCircleButton.addActionListener(e -> {
			view.toggleIsDrawingCircle();
		});
	}

	// Méthode pour créer un bouton d'outil avec une icône redimensionnée
	private JButton createToolButton(String toolName, String iconPath) {
		// Charger l'icône et la redimensionner
		ImageIcon icon = new ImageIcon(iconPath);
		Image image = icon.getImage();
		Image newimg = image.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH); // Redimensionner l'image
		icon = new ImageIcon(newimg);

		JButton button = new JButton(icon);
		button.setToolTipText(toolName);
		button.setText(toolName);
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setVerticalTextPosition(SwingConstants.BOTTOM);

		// Style FlatLaf : ajuster la taille et le padding
		button.setFocusPainted(false); // Désactiver l'effet de focus par défaut
		button.setMargin(new Insets(10, 10, 10, 10)); // Espacement interne
		button.setPreferredSize(new Dimension(100, 80)); // Taille uniforme des boutons
		return button;
	}
}