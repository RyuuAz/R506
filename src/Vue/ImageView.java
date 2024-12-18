package Vue;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.formdev.flatlaf.FlatLightLaf; // Import FlatLaf
import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import Controller.ImageController;
import Model.ImageModel;

public class ImageView extends JFrame {

    private JLabel imageLabel;
    private JLabel affichJLabel;
    private ImageController controller;
    private ImageModel model;
    private Color pickedColor;
    private boolean isPickingColor;
    private boolean isPainting;
    private JPanel colorDisplayPanel;

    private Shape shape;
    private Shape currentShape = null; // Forme temporaire en cours de dessin
    private Shape selectedShape = null;
    private Point lastMousePosition;
    private boolean isDrawingRectangle = false;
    private boolean isDrawingCircle = false;
    private int clickX, clickY;

    private BufferedImage image;

    public ImageView(ImageController controller) {
        setTitle("Pix.net");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        this.controller = controller;

        // Initialisation de FlatLaf (par défaut Light)
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        imageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image != null) {
                    int x = (getWidth() - image.getWidth()) / 2;
                    int y = (getHeight() - image.getHeight()) / 2;
                    g.drawImage(image, x, y, this);
                }
                if (shape != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    shape.draw(g2d);
                } else if (currentShape != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    currentShape.draw(g2d);
                }
            }
        };

        this.shape = null;

        // Créer un panneau pour afficher l'image
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(imageLabel);
        add(scrollPane, BorderLayout.CENTER);

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
        setJMenuBar(menuBar);

        // Créer la barre d'outils (JToolBar)
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false); // Désactive la possibilité de faire flotter la barre d'outils

        //Slider de tolerance
        JSlider toleranceSlider = new JSlider(1, 255, 50);
        toleranceSlider.setMajorTickSpacing(50);
        toleranceSlider.setMinorTickSpacing(10);
        toleranceSlider.setPaintTicks(true);
        toleranceSlider.setPaintLabels(true);
        toolBar.add(toleranceSlider);

        // Ajouter des boutons à la barre d'outils
        JButton copierButton = new JButton("Copier");
        JButton couperButton = new JButton("Couper");
        JButton collerButton = new JButton("Coller");
        toolBar.add(copierButton);
        toolBar.add(couperButton);
        toolBar.add(collerButton);

        // Ajouter la barre de menus et la barre d'outils au topPanel
        topPanel.add(menuBar);
        topPanel.add(toolBar);

        // Ajouter le topPanel à la fenêtre
        add(topPanel, BorderLayout.NORTH);

        // Crée la palette de d'outils
        ToolBar toolbar = new ToolBar();

        add(toolbar, BorderLayout.WEST);

        openItem.addActionListener(this::handleOpenImage);
        saveItem.addActionListener(this::handleSaveImage);

        paintBucketItem.addActionListener(e -> {
            this.togglePaintBucket(e);
        });

        // Event pour le seau de peinture
        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (isPainting) {
                    int x = evt.getX();
                    int y = evt.getY();

                    // Ajustement des coordonnées
                    int imageX = x - (imageLabel.getWidth() - image.getWidth()) / 2;
                    int imageY = y - (imageLabel.getHeight() - image.getHeight()) / 2;

                    // Vérifiez si les coordonnées ajustées sont dans les limites de l'image
                    if (imageX >= 0 && imageX < image.getWidth() && imageY >= 0 && imageY < image.getHeight()) {
                        if (controller != null) {
                            controller.applyPaintBucket(imageX, imageY, pickedColor, toleranceSlider.getValue(), ImageView.this.shape);
                        }
                    }

                    isPainting = false;
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        // ActionListener pour la pipette
        pickColorItem.addActionListener(e -> {
            this.togglePickColor(e);
        });

        // Event pour la pipette
        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (isPickingColor) {
                    int x = evt.getX();
                    int y = evt.getY();

                    // Ajustement des coordonnées
                    int imageX = x - (imageLabel.getWidth() - image.getWidth()) / 2;
                    int imageY = y - (imageLabel.getHeight() - image.getHeight()) / 2;

                    // Vérifiez si les coordonnées ajustées sont dans les limites de l'image
                    if (imageX >= 0 && imageX < image.getWidth() && imageY >= 0 && imageY < image.getHeight()) {
                        if (controller != null) {
                            controller.pickColor(imageX, imageY);
                        }
                    }

                    isPickingColor = false;
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        rotateLeftItem.addActionListener(e -> {
            if (controller != null) {
                controller.rotate(false);
            }
        });

        rotateRightItem.addActionListener(e -> {
            if (controller != null) {
                controller.rotate(true);
            }
        });

        rotateCustomItem.addActionListener(e -> {
            if (controller != null) {
                String angleStr = JOptionPane.showInputDialog(ImageView.this, "Enter rotation angle (degrees):",
                        "Rotate Custom",
                        JOptionPane.PLAIN_MESSAGE);
                try {
                    int angle = Integer.parseInt(angleStr);
                    controller.rotateImageByAngle(angle);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ImageView.this, "Invalid input! Please enter a numeric value.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        flipHorizontalItem.addActionListener(e -> {
            if (controller != null) {
                controller.flipImage(true);
            }
        });

        flipVerticalItem.addActionListener(e -> {
            if (controller != null) {
                controller.flipImage(false);
            }
        });

        brightenPlusItem.addActionListener(e -> {
            if (controller != null) {
                controller.adjustBrightness(10);
            }
        });

        brightenMinusItem.addActionListener(e -> {
            if (controller != null) {
                controller.adjustBrightness(-10);
            }
        });

        contrastPlusItem.addActionListener(e -> {
            if (controller != null) {
                controller.adjustContrast(10);
            }
        });

        contrastMinusItem.addActionListener(e -> {
            if (controller != null) {
                controller.adjustContrast(-10);
            }
        });

        drawRectangleItem.addActionListener(e -> {
            if (controller != null) {
                toggleIsDrawingRectangle();
            }
        });

        drawCircleItem.addActionListener(e -> {
            if (controller != null) {
                toggleIsDrawingCircle();
            }
        });

        addMouseListeners();

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
                ImageView.this.togglePaintBucket(e);
            }
        });

        pickColorItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(pickColorKeyStroke, "pickColor");
        pickColorItem.getActionMap().put("pickColor", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageView.this.togglePickColor(e);
            }
        });

        rotateLeftItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(rotateLeftKeyStroke, "rotateLeft");
        rotateLeftItem.getActionMap().put("rotateLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    controller.rotate(false);
                }
            }
        });

        rotateRightItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(rotateRightKeyStroke, "rotateRight");
        rotateRightItem.getActionMap().put("rotateRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    controller.rotate(true);
                }
            }
        });

        rotateCustomItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(rotateCustomKeyStroke, "rotateCustom");
        rotateCustomItem.getActionMap().put("rotateCustom", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    String angleStr = JOptionPane.showInputDialog(ImageView.this, "Enter rotation angle (degrees):",
                            "Rotate Custom",
                            JOptionPane.PLAIN_MESSAGE);
                    try {
                        int angle = Integer.parseInt(angleStr);
                        controller.rotateImageByAngle(angle);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(ImageView.this, "Invalid input! Please enter a numeric value.",
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
                    controller.flipImage(true);
                }
            }
        });

        flipVerticalItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(flipVerticalKeyStroke, "flipVertical");
        flipVerticalItem.getActionMap().put("flipVertical", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    controller.flipImage(false);
                }
            }
        });

        brightenPlusItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(brightenPlusKeyStroke, "brightenPlus");
        brightenPlusItem.getActionMap().put("brightenPlus", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    controller.adjustBrightness(10);
                }
            }
        });

        brightenMinusItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(brightenMoinKeyStroke, "brightenMoin");
        brightenMinusItem.getActionMap().put("brightenMoin", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    controller.adjustBrightness(-10);
                }
            }
        });

        contrastPlusItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(darkenPlusKeyStroke, "darkenPlus");
        contrastPlusItem.getActionMap().put("darkenPlus", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    controller.adjustContrast(10);
                }
            }
        });

        contrastMinusItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(darkenMoinKeyStroke, "darkenMoin");
        contrastMinusItem.getActionMap().put("darkenMoin", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    controller.adjustContrast(-10);
                }
            }
        });

        drawRectangleItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(drawRectangleKeyStroke, "drawRectangle");
        drawRectangleItem.getActionMap().put("drawRectangle", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    toggleIsDrawingRectangle();
                }
            }
        });

        drawCircleItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(drawCircleKeyStroke, "drawCircle");
        drawCircleItem.getActionMap().put("drawCircle", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    toggleIsDrawingCircle();
                }
            }
        });

        this.setVisible(true);

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

    private void togglePickColor(ActionEvent e) {
        if (controller != null) {
            if (isPainting || isPickingColor) {
                setCursor(Cursor.getDefaultCursor());
                isPainting = false;
                isPickingColor = false;
            } else {
                Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
                setCursor(cursor);
                isPickingColor = true;
            }
        }
    }

    private void togglePaintBucket(ActionEvent e) {
        if (controller != null) {
            if (isPainting || isPickingColor) {
                setCursor(Cursor.getDefaultCursor());
                isPainting = false;
                isPickingColor = false;
            } else {
                Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
                setCursor(cursor);
                isPainting = true;
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
                controller.saveImage(fileChooser.getSelectedFile());
            }
        }
    }

    public void displayPickedColor(Color color) {
        if (color != null) {
            pickedColor = color;
            colorDisplayPanel.setBackground(pickedColor);
        }
    }

    public void setController(ImageController controller) {
        this.controller = controller;
    }

    public JLabel getImageLabel() {
        return imageLabel;
    }

    public void addShape(Shape shape) {
        this.shape = shape;
    }

    public boolean getIsDrawingRectangle() {
        System.out.println("isDrawingRectangle: " + isDrawingRectangle);
        return this.isDrawingRectangle;
    }

    public void toggleIsDrawingRectangle() {
        this.isDrawingRectangle = !this.isDrawingRectangle;
    }

    public boolean getIsDrawingCircle() {
        System.out.println("isDrawingCircle: " + isDrawingCircle);
        return this.isDrawingCircle;

    }

    public void toggleIsDrawingCircle() {
        this.isDrawingCircle = !this.isDrawingCircle;

    }

    public void updateImage(BufferedImage image) {
        this.image = image;
        imageLabel.setIcon(new ImageIcon(image));
        imageLabel.repaint();
    }

    public void init() {
        setVisible(true);
    }

    private void addMouseListeners() {
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                if (shape != null && shape.contains(e.getX(), e.getY()) && !isDrawingRectangle && !isDrawingCircle) {
                    selectedShape = shape;
                    lastMousePosition = e.getPoint();
                } else if (isDrawingRectangle || isDrawingCircle) {
                    clickX = e.getX();
                    clickY = e.getY();
                    selectedShape = null;
                    lastMousePosition = null;
                }

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentShape != null) {
                    shape = currentShape; // Ajoute la forme temporaire à la liste des formes
                    currentShape = null; // Réinitialise la forme temporaire

                }
                selectedShape = null;
                isDrawingCircle = false;
                isDrawingRectangle = false;
            }
        });

        imageLabel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedShape != null && lastMousePosition != null) {
                    int deltaX = e.getX() - lastMousePosition.x;
                    int deltaY = e.getY() - lastMousePosition.y;
                    selectedShape.moveTo(selectedShape.getX() + deltaX, selectedShape.getY() + deltaY);
                    lastMousePosition = e.getPoint();
                    imageLabel.repaint();
                } else if ((isDrawingCircle || isDrawingRectangle)) {
                    // Met à jour la forme temporaire
                    if (currentShape != null) {
                        int startX = currentShape.getX();
                        int startY = currentShape.getY();
                        int endX = e.getX();
                        int endY = e.getY();

                        if (e.getX() < clickX) {
                            startX = e.getX();
                            endX = clickX;
                        } else {
                            endX = e.getX();
                            startX = clickX;
                        }

                        if (e.getY() < clickY) {
                            startY = e.getY();
                            endY = clickY;
                        } else {
                            endY = e.getY();
                            startY = clickY;
                        }

                        int width = Math.abs(endX - startX);
                        int height = Math.abs(endY - startY);

                        if (!currentShape.isRectangle()) {
                            int diameter = Math.max(width, height);
                            width = diameter;
                            height = diameter;
                        }

                        currentShape.setX(startX);
                        currentShape.setY(startY);
                        currentShape.resize(width, height);
                    } else {
                        currentShape = new Shape(e.getX(), e.getY(), 0, 0, isDrawingRectangle, Color.RED); // Initialise
                        shape = null; // une forme
                    }
                    imageLabel.repaint();
                }
            }
        });
    }
}
