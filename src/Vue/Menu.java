package Vue;

import javax.swing.*;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicSliderUI;

import com.formdev.flatlaf.FlatLightLaf; // Import FlatLaf
import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Component;
import java.awt.RenderingHints.Key;
import java.awt.Graphics;

import java.awt.event.*;

import Controller.ImageController;

public class Menu extends JPanel {

    private JPanel colorDisplayPanel;
    private ImageController controller;
    private ImageView view;
    private JSlider toleranceSlider;

    // Constructeur pour initialiser le menu
    public Menu(ImageController controller, ImageView view) {

        this.controller = controller;
        this.view = view;

        // Créer un panneau pour afficher l'image
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Créer la barre de menus avec FlatLaf
        FlatLightLaf.setup(); // Setup FlatLaf Light theme
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
        colorMenu.add(pickColorItem);
        colorMenu.add(paintBucketItem);
        colorMenu.addSeparator();
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

        // Slider de tolérance en pourcentage avec style FlatLaf
        JLabel toleranceLabel = new JLabel("Tolérance:");
        toleranceSlider = new JSlider(0, 100, 20);
        toleranceSlider.setUI(new FlatLafCustomSliderUI(toleranceSlider));
        toleranceSlider.setMajorTickSpacing(20);
        toleranceSlider.setMinorTickSpacing(5);
        toleranceSlider.setPaintTicks(true);
        toleranceSlider.setPaintLabels(true);

        toolBar.add(toleranceLabel);
        toolBar.add(toleranceSlider);

        // Picker la couleur sélectionnée
        colorDisplayPanel = new JPanel();
        colorDisplayPanel.setPreferredSize(new Dimension(30, 30));
        colorDisplayPanel.setBackground(Color.BLACK);
        colorDisplayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        colorDisplayPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            Color newColor = JColorChooser.showDialog(Menu.this, "Choisir une couleur", colorDisplayPanel.getBackground());
            if (newColor != null) {
                colorDisplayPanel.setBackground(newColor);
                view.setColor(newColor);
            }
            }
        });

        toolBar.add(colorDisplayPanel);

        // Ajouter des boutons à la barre d'outils
        JButton copierButton = new JButton("Copier");
        JButton copierSansFButton = new JButton("Copier sans fond");
        JButton couperButton = new JButton("Couper");
        JButton collerButton = new JButton("Coller");

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
                view.updateImage(controller.rotate(view.getImageTemp(), false));
            }
        });

        rotateRightItem.addActionListener(e -> {
            if (controller != null) {
                view.updateImage(controller.rotate(view.getImageTemp(), true));
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
                view.updateImage(controller.flipImage(view.getImageTemp(), true));
            }
        });

        flipVerticalItem.addActionListener(e -> {
            if (controller != null) {
                view.updateImage(controller.flipImage(view.getImageTemp(), false));
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
        KeyStroke drawRectangleKeyStroke = KeyStroke.getKeyStroke("control alt D");
        KeyStroke drawCircleKeyStroke = KeyStroke.getKeyStroke("control alt C");
        KeyStroke copyKeyStroke = KeyStroke.getKeyStroke("control C");
        KeyStroke copyWithoutBackgroundKeyStroke = KeyStroke.getKeyStroke("control shift C");
        KeyStroke pasteKeyStroke = KeyStroke.getKeyStroke("control V");

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
                    view.updateImage(controller.rotate(view.getImageTemp(), false));
                }
            }
        });

        rotateRightItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(rotateRightKeyStroke, "rotateRight");
        rotateRightItem.getActionMap().put("rotateRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    view.updateImage(controller.rotate(view.getImageTemp(), true));
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
                    view.updateImage(controller.flipImage(view.getImageTemp(), true));
                }
            }
        });

        flipVerticalItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(flipVerticalKeyStroke, "flipVertical");
        flipVerticalItem.getActionMap().put("flipVertical", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    view.updateImage(controller.flipImage(view.getImageTemp(), false));
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

        copierButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(copyKeyStroke, "copy");
        copierButton.getActionMap().put("copy", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    view.copyImage();
                }
            }
        });

        copierSansFButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(copyWithoutBackgroundKeyStroke, "copyWithoutBackground");
        copierSansFButton.getActionMap().put("copyWithoutBackground", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    //view.copyImageWithoutBackground();
                }
            }
        });

        collerButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(pasteKeyStroke, "paste");
        collerButton.getActionMap().put("paste", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    controller.pasteImage(view);
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

    // Classe personnalisée pour styliser le JSlider avec FlatLaf
    private static class FlatLafCustomSliderUI extends BasicSliderUI {
        public FlatLafCustomSliderUI(JSlider slider) {
            super(slider);
            slider.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (slider.isEnabled()) {
                        int value = valueForXPosition(e.getX());
                        slider.setValue(value);
                    }
                }
            });
        }

        @Override
        public void paintTrack(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int trackHeight = 8;
            int trackY = trackRect.y + (trackRect.height - trackHeight) / 2;
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(trackRect.x, trackY, trackRect.width, trackHeight);

            int fillWidth = (int) ((slider.getValue() / 100.0) * trackRect.width);
            g2d.setColor(new Color(0, 120, 215)); // Blue color similar to Paint.NET
            g2d.fillRect(trackRect.x, trackY, fillWidth, trackHeight);
        }

        @Override
        public void paintThumb(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int thumbWidth = 16;
            int thumbHeight = 16;
            int thumbX = thumbRect.x + (thumbRect.width - thumbWidth) / 2;
            int thumbY = thumbRect.y + (thumbRect.height - thumbHeight) / 2;

            g2d.setColor(Color.WHITE);
            g2d.fillOval(thumbX, thumbY, thumbWidth, thumbHeight);
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawOval(thumbX, thumbY, thumbWidth, thumbHeight);
        }

        @Override
        public void paintTicks(Graphics g) {
            super.paintTicks(g); // Garder l'affichage des ticks
        }

        @Override
        public void paintLabels(Graphics g) {
            super.paintLabels(g); // Garder l'affichage des labels
        }
    }
}