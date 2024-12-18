package Vue;

import javax.swing.*;

import javax.swing.filechooser.FileNameExtensionFilter;
import com.formdev.flatlaf.FlatLightLaf; // Import FlatLaf
import com.formdev.flatlaf.FlatDarkLaf;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import java.awt.event.*;

import Controller.ImageController;



public class Menu extends JPanel {

    private JPanel colorDisplayPanel;
    private ImageController controller;
	private ImageView view;
	private JSlider toleranceSlider;

	// Constructeur pour initialiser le menu
	public Menu (ImageController controller, ImageView view) {

		this.controller = controller;
		this.view = view;
		
		// Créer un panneau pour afficher l'image
		this.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Créer la barre de menus
        JMenuBar menuBar = new JMenuBar();

        // Menu Fichier
        JMenu fileMenu = new JMenu("Fichier");
        JMenuItem openItem = new JMenuItem("Ouvrir");
        JMenuItem saveItem = new JMenuItem("Sauvegarder");
        fileMenu.add(openItem);
        fileMenu.add(saveItem);

        // Menu Édition
        JMenu editMenu = new JMenu("Édition");

        // Sous-menu Transformation
        JMenu transformMenu = new JMenu("Transformation");
        JMenuItem rotateLeftItem = new JMenuItem("Rotation gauche");
        JMenuItem rotateRightItem = new JMenuItem("Rotation droite");
        JMenuItem rotateCustomItem = new JMenuItem("Rotation personnalisée");
        JMenuItem flipHorizontalItem = new JMenuItem("Retourner horizontalement");
        JMenuItem flipVerticalItem = new JMenuItem("Retourner verticalement");
        transformMenu.add(rotateLeftItem);
        transformMenu.add(rotateRightItem);
        transformMenu.add(rotateCustomItem);
        transformMenu.addSeparator();
        transformMenu.add(flipHorizontalItem);
        transformMenu.add(flipVerticalItem);

        // Sous-menu Couleur
        JMenu colorMenu = new JMenu("Couleur");
        JMenuItem pickColorItem = new JMenuItem("Pipette de couleur");
        JMenuItem paintBucketItem = new JMenuItem("Seau de peinture");
        colorDisplayPanel = new JPanel();
        colorDisplayPanel.setPreferredSize(new Dimension(20, 20));
        colorDisplayPanel.setBackground(Color.WHITE); // Couleur initiale (blanc)
        JMenuItem colorDisplayItem = new JMenuItem("Couleur sélectionnée");
        colorDisplayItem.setEnabled(false); // Non interactif
        colorMenu.add(pickColorItem);
        colorMenu.add(paintBucketItem);
        colorMenu.addSeparator();
        colorMenu.add(colorDisplayItem);
        colorMenu.add(new JSeparator());

        // Sous-menu Luminosité/Contraste
        JMenu brightnessMenu = new JMenu("Luminosité / Contraste");
        JMenuItem brightenPlusItem = new JMenuItem("Luminosité +");
        JMenuItem brightenMinusItem = new JMenuItem("Luminosité -");
        JMenuItem contrastPlusItem = new JMenuItem("Contraste +");
        JMenuItem contrastMinusItem = new JMenuItem("Contraste -");
        brightnessMenu.add(brightenPlusItem);
        brightnessMenu.add(brightenMinusItem);
        brightnessMenu.addSeparator();
        brightnessMenu.add(contrastPlusItem);
        brightnessMenu.add(contrastMinusItem);

        // Sous-menu Dessin
        JMenu drawMenu = new JMenu("Dessin");
        JMenuItem drawRectangleItem = new JMenuItem("Rectangle");
        JMenuItem drawCircleItem = new JMenuItem("Cercle");
        drawMenu.add(drawRectangleItem);
        drawMenu.add(drawCircleItem);

        // Ajouter les sous-menus au menu Édition
        editMenu.add(transformMenu);
        editMenu.add(colorMenu);
        editMenu.addSeparator();
        editMenu.add(brightnessMenu);
        editMenu.add(drawMenu);

        // Ajouter les menus à la barre de menus
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        // Ajouter la barre de menus à la fenêtre
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.setJMenuBar(menuBar);
        }

        // Créer la barre d'outils (JToolBar)
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false); // Désactive la possibilité de faire flotter la barre d'outils

        //Slider de tolerance
        toleranceSlider = new JSlider(1, 255, 50);
        toleranceSlider.setMajorTickSpacing(50);
        toleranceSlider.setMinorTickSpacing(10);
        toleranceSlider.setPaintTicks(true);
        toleranceSlider.setPaintLabels(true);
        toolBar.add(toleranceSlider);

		//Ajout d'une case couelur pour afficher la couleur selectionnée
		colorDisplayPanel = new JPanel();
		colorDisplayPanel.setPreferredSize(new Dimension(20, 20));
		colorDisplayPanel.setBackground(Color.WHITE);
		toolBar.add(colorDisplayPanel);

        // Ajouter des boutons à la barre d'outils
        JButton copierButton = new JButton("Copier");
        JButton couperButton = new JButton("Couper");
        JButton collerButton = new JButton("Coller");

		//Ajout d'un btn dark mode
		JButton darkModeButton = new JButton("Dark Mode");
		darkModeButton.addActionListener(e -> {
			try {
				UIManager.setLookAndFeel(new FlatDarkLaf());
			} catch (UnsupportedLookAndFeelException ex) {
				ex.printStackTrace();
			}
		});

        toolBar.add(copierButton);
        toolBar.add(couperButton);
        toolBar.add(collerButton);

        // Ajouter la barre de menus et la barre d'outils au topPanel
        this.add(menuBar);
        this.add(toolBar);

		openItem.addActionListener(this::handleOpenImage);
        saveItem.addActionListener(this::handleSaveImage);

        paintBucketItem.addActionListener(e -> {
            view.togglePaintBucket(e);
        });

		 // ActionListener pour la pipette
		 pickColorItem.addActionListener(e -> {
            view.togglePickColor(e);
        });

		rotateLeftItem.addActionListener(e -> {
            if (controller != null) {
                view.updateImage(controller.rotate(view.getImageTemp(),false));
            }
        });

        rotateRightItem.addActionListener(e -> {
            if (controller != null) {
                view.updateImage(controller.rotate(view.getImageTemp(),true));
            }
        });

        rotateCustomItem.addActionListener(e -> {
            if (controller != null) {
                String angleStr = JOptionPane.showInputDialog(Menu.this, "Enter rotation angle (degrees):",
                        "Rotate Custom",
                        JOptionPane.PLAIN_MESSAGE);
                try {
                    int angle = Integer.parseInt(angleStr);
					view.updateImage(controller.rotateImageByAngle(view.getImageTemp(), angle));
				} catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(Menu.this, "Invalid input! Please enter a numeric value.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        flipHorizontalItem.addActionListener(e -> {
            if (controller != null) {
                view.updateImage(controller.flipImage(view.getImageTemp(),true));
            }
        });

        flipVerticalItem.addActionListener(e -> {
            if (controller != null) {
              view.updateImage(controller.flipImage(view.getImageTemp(),false));
            }
        });

        brightenPlusItem.addActionListener(e -> {
            if (controller != null) {
                view.updateImage(controller.adjustBrightness(view.getImageTemp(), 10));
            }
        });

        brightenMinusItem.addActionListener(e -> {
            if (controller != null) {
               view.updateImage(controller.adjustBrightness(view.getImageTemp(), -10));
            }
        });

        contrastPlusItem.addActionListener(e -> {
            if (controller != null) {
               view.updateImage(controller.adjustContrast(view.getImageTemp(), 10));
            }
        });

        contrastMinusItem.addActionListener(e -> {
            if (controller != null) {
                view.updateImage(controller.adjustContrast(view.getImageTemp(), -10));
            }
        });

        copierButton.addActionListener(e -> {
            if (controller != null) {
                view.copyImage();
            }
        });

        collerButton.addActionListener(e -> {
            if (controller != null) {
                controller.pasteImage(view);
            }
        });

        drawRectangleItem.addActionListener(e -> {
            if (controller != null) {
                view.toggleIsDrawingRectangle();
            }
        });

        drawCircleItem.addActionListener(e -> {
            if (controller != null) {
                view.toggleIsDrawingCircle();
            }
        });

		 // Raccourcis clavier
        KeyStroke openKeyStroke = KeyStroke.getKeyStroke("control O");
        KeyStroke saveKeyStroke = KeyStroke.getKeyStroke("control S");
        KeyStroke paintBucketKeyStroke = KeyStroke.getKeyStroke("control P");
        KeyStroke pickColorKeyStroke = KeyStroke.getKeyStroke("control I");
        KeyStroke rotateLeftKeyStroke = KeyStroke.getKeyStroke("control LEFT");
        KeyStroke rotateRightKeyStroke = KeyStroke.getKeyStroke("control RIGHT");
        KeyStroke rotateCustomKeyStroke = KeyStroke.getKeyStroke("control R");
        KeyStroke flipHorizontalKeyStroke = KeyStroke.getKeyStroke("control UP");
        KeyStroke flipVerticalKeyStroke = KeyStroke.getKeyStroke("control DOWN");
        KeyStroke brightenPlusKeyStroke = KeyStroke.getKeyStroke("control ADD");
        KeyStroke brightenMoinKeyStroke = KeyStroke.getKeyStroke("control SUBTRACT");
        KeyStroke darkenPlusKeyStroke = KeyStroke.getKeyStroke("control shift ADD");
        KeyStroke darkenMoinKeyStroke = KeyStroke.getKeyStroke("control shift SUBTRACT");
        KeyStroke drawRectangleKeyStroke = KeyStroke.getKeyStroke("control D");
        KeyStroke drawCircleKeyStroke = KeyStroke.getKeyStroke("control C");

        openItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(openKeyStroke, "open");
        openItem.getActionMap().put("open", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleOpenImage(e);
            }
        });

        saveItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(saveKeyStroke, "save");
        saveItem.getActionMap().put("save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSaveImage(e);
            }
        });

        paintBucketItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(paintBucketKeyStroke, "paintBucket");
        paintBucketItem.getActionMap().put("paintBucket", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.togglePaintBucket(e);
            }
        });

        pickColorItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(pickColorKeyStroke, "pickColor");
        pickColorItem.getActionMap().put("pickColor", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.togglePickColor(e);
            }
        });

        rotateLeftItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(rotateLeftKeyStroke, "rotateLeft");
        rotateLeftItem.getActionMap().put("rotateLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
					view.updateImage(controller.rotate(view.getImageTemp(),false));
                }
            }
        });

        rotateRightItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(rotateRightKeyStroke, "rotateRight");
        rotateRightItem.getActionMap().put("rotateRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    view.updateImage(controller.rotate(view.getImageTemp(),true));
                }
            }
        });

        rotateCustomItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(rotateCustomKeyStroke, "rotateCustom");
        rotateCustomItem.getActionMap().put("rotateCustom", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    String angleStr = JOptionPane.showInputDialog(Menu.this, "Enter rotation angle (degrees):",
                            "Rotate Custom",
                            JOptionPane.PLAIN_MESSAGE);
                    try {
                        int angle = Integer.parseInt(angleStr);
						view.updateImage(controller.rotateImageByAngle(view.getImageTemp(), angle));
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(Menu.this, "Invalid input! Please enter a numeric value.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        flipHorizontalItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(flipHorizontalKeyStroke,
                "flipHorizontal");
        flipHorizontalItem.getActionMap().put("flipHorizontal", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    view.updateImage(controller.flipImage(view.getImageTemp() ,true));
                }
            }
        });

        flipVerticalItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(flipVerticalKeyStroke, "flipVertical");
        flipVerticalItem.getActionMap().put("flipVertical", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    view.updateImage(controller.flipImage(view.getImageTemp(),false));
                }
            }
        });

        brightenPlusItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(brightenPlusKeyStroke, "brightenPlus");
        brightenPlusItem.getActionMap().put("brightenPlus", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    view.updateImage(controller.adjustBrightness(view.getImageTemp(), 10));
                }
            }
        });

        brightenMinusItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(brightenMoinKeyStroke, "brightenMoin");
        brightenMinusItem.getActionMap().put("brightenMoin", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    view.updateImage(controller.adjustBrightness(view.getImageTemp(), -10));
                }
            }
        });

        contrastPlusItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(darkenPlusKeyStroke, "darkenPlus");
        contrastPlusItem.getActionMap().put("darkenPlus", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    view.updateImage(controller.adjustContrast(view.getImageTemp(), 10));
                }
            }
        });

        contrastMinusItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(darkenMoinKeyStroke, "darkenMoin");
        contrastMinusItem.getActionMap().put("darkenMoin", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    view.updateImage(controller.adjustContrast(view.getImageTemp(), -10));
                }
            }
        });

        drawRectangleItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(drawRectangleKeyStroke, "drawRectangle");
        drawRectangleItem.getActionMap().put("drawRectangle", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    view.toggleIsDrawingRectangle();
                }
            }
        });

        drawCircleItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(drawCircleKeyStroke, "drawCircle");
        drawCircleItem.getActionMap().put("drawCircle", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    view.toggleIsDrawingCircle();
                }
            }
        });
	}

	private void handleOpenImage(ActionEvent e) {
		if (controller != null) {
			JFileChooser fileChooser = new JFileChooser();
			FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
					"Images", "jpg", "jpeg", "png", "gif", "bmp", "tiff", "webp");
			fileChooser.setFileFilter(imageFilter);
			int result = fileChooser.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				controller.openImage(fileChooser.getSelectedFile());
				int imageWidth = view.getImageTemp().getWidth();
        		int imageHeight = view.getImageTemp().getHeight();
				view.setImageTaille(imageWidth, imageHeight);
			}
		}
	}

    private void handleSaveImage(ActionEvent e) {
        if (controller != null) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
                    "Images", "png", "jpg", "jpeg", "gif", "bmp", "tiff", "webp");
            fileChooser.setFileFilter(imageFilter);
            fileChooser.setSelectedFile(new java.io.File("image.png")); // Default file name with .png extension
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                controller.saveImage(view.getImageTemp(), fileChooser.getSelectedFile());
            }
        }
    }

	public int getSliderValue() {
		return toleranceSlider.getValue();
	}

	public JPanel getColorDisplayPanel() {
		return colorDisplayPanel;
	}

	public void setColorDisplayPanelColor(Color color) {
		colorDisplayPanel.setBackground(color);
	}
}
