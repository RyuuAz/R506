package Vue;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.formdev.flatlaf.FlatLightLaf; // Import FlatLaf
import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.*;

import Controller.ImageController;
import Model.ImageModel;

import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;

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
    private ArrayList<Shape> shapeTextes = new ArrayList<>();

    private Shape currentShape = null; // Forme temporaire en cours de dessin
    private Shape selectedShape = null;
    private ArrayList<RenderText> renderTexts = new ArrayList<>();
    private Point lastMousePosition;
    private boolean isDrawingRectangle = false;
    private boolean isDrawingCircle = false;
    private boolean isPasting = false;
    private int clickX, clickY;

    private BufferedImage image, imageTemp, imagePaste,originalImage = null;

    public ImageView(ImageController controller) {
        setTitle("Pix.net");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.controller = controller;
        this.shape = null;

        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                controller.setActiveView(ImageView.this); // Définit cette fenêtre comme active
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                controller.removeView(ImageView.this); // Retire la vue de la liste des vues
            }

        });

        imageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imageTemp != null) {
                    int x = (getWidth() - imageTemp.getWidth()) / 2;
                    int y = (getHeight() - imageTemp.getHeight()) / 2;
                    g.drawImage(imageTemp, x, y, this);
                }
                if (shape != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    shape.draw(g2d);
                }  if (currentShape != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    currentShape.draw(g2d);
                }  if (shapeTextes.size() > 0) {
                    Graphics2D g2d = (Graphics2D) g;
                    for (Shape shapeTexte : shapeTextes) {
                        if (shapeTexte.isOver()) {
                            shapeTexte.draw(g2d);
                        }
                    }
                } 
                if (renderTexts.size() > 0) {
                    for (RenderText renderText : renderTexts) {
                        renderText.draw((Graphics2D) g);
                    }
                }
            }
        };

        this.shape = null;

        // Créer un panneau pour afficher l'image
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        imageLabel.setHorizontalAlignment(JLabel.LEFT);
        imageLabel.setVerticalAlignment(JLabel.TOP);

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

        // Sous-menu Texte
        JMenu textMenu = new JMenu("Texte");
        JMenuItem addTextItem = new JMenuItem("Ajouter du texte");
        textMenu.add(addTextItem);

        // Ajouter les sous-menus au menu Édition
        editMenu.add(transformMenu);
        editMenu.add(colorMenu);
        editMenu.addSeparator();
        editMenu.add(brightnessMenu);
        editMenu.add(drawMenu);
        editMenu.addSeparator();
        editMenu.add(textMenu);

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
                            //controller.applyPaintBucket(imageX, imageY, pickedColor, toleranceSlider.getValue(), ImageView.this.shape);
                        }
                    }

                    isPainting = false;
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });

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

        copierButton.addActionListener(e -> {
            if (controller != null) {
                copyImage();
            }
        });

        collerButton.addActionListener(e -> {
            if (controller != null) {
                controller.pasteImage(this);
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

        addTextItem.addActionListener(e -> {
            if (controller != null) {
                // Créer un JPanel personnalisé avec plusieurs composants
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
                // Champ de texte pour saisir le texte à ajouter
                JTextField textField = new JTextField(20);
                panel.add(new JLabel("Entrez le texte de votre choix :"));
                panel.add(textField);
        
                // Sélecteur de police (JComboBox)
                String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
                JComboBox<String> fontComboBox = new JComboBox<>(fonts);
                panel.add(new JLabel("Choisissez la police :"));
                panel.add(fontComboBox);
        
                // Sélecteur de taille de police (JSpinner)
                SpinnerModel sizeModel = new SpinnerNumberModel(20, 1, 100, 1); // Min 1, Max 100, Incr 1
                JSpinner fontSizeSpinner = new JSpinner(sizeModel);
                panel.add(new JLabel("Choisissez la taille de la police :"));
                panel.add(fontSizeSpinner);
        
                // Sélecteur de couleur (JColorChooser)
                JButton colorButton = new JButton("Choisir la couleur");
                panel.add(new JLabel("Choisissez la couleur :"));
                panel.add(colorButton);
        
                // Ouvrir le JColorChooser quand on clique sur le bouton
                Color[] selectedColor = new Color[1];
                colorButton.addActionListener(ae -> {
                    selectedColor[0] = JColorChooser.showDialog(ImageView.this, "Sélectionner une couleur", Color.BLACK);
                });
        
                // Bouton pour importer une texture
                JButton textureButton = new JButton("Importer une texture");
                textureButton.setEnabled(false); // Désactivé par défaut
                panel.add(new JLabel("Ou choisissez une texture :"));
                panel.add(textureButton);
        
                BufferedImage[] textureImage = new BufferedImage[1];
                textureButton.addActionListener(ae -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
                    int result = fileChooser.showOpenDialog(ImageView.this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        try {
                            textureImage[0] = ImageIO.read(fileChooser.getSelectedFile());
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(ImageView.this, 
                                    "Erreur lors du chargement de la texture.",
                                    "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
        
                // Checkbox pour activer ou désactiver la texture
                JCheckBox useTextureCheckBox = new JCheckBox("Activer la texture");
                panel.add(useTextureCheckBox);
        
                // Activer/Désactiver le bouton texture et couleur en fonction de la checkbox
                useTextureCheckBox.addItemListener(ae -> {
                    boolean textureEnabled = useTextureCheckBox.isSelected();
                    textureButton.setEnabled(textureEnabled);  // Activer/Désactiver le bouton texture
                    colorButton.setEnabled(!textureEnabled);  // Désactiver le bouton couleur si texture activée
                });
        
                // Afficher la boîte de dialogue avec les champs
                int option = JOptionPane.showConfirmDialog(ImageView.this, panel, "Ajouter du texte à l'image",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        // Vérifier si le champ texte est rempli
                        String text = textField.getText().trim();
                        if (text.isEmpty()) {
                            JOptionPane.showMessageDialog(ImageView.this, 
                                    "Veuillez entrer un texte avant de valider.", 
                                    "Champ vide", JOptionPane.WARNING_MESSAGE);
                            return; // Réafficher la boîte de dialogue
                        }
        
                        // Récupérer les valeurs des champs
                        String selectedFont = (String) fontComboBox.getSelectedItem();
                        int fontSize = (Integer) fontSizeSpinner.getValue();
                        Font font = new Font(selectedFont, Font.PLAIN, fontSize);
        
                        // Activer ou désactiver la texture
                        TexturePaint texturePaint = null;
                        if (useTextureCheckBox.isSelected() && textureImage[0] != null) {
                            // Créer un TexturePaint pour appliquer la texture
                            Rectangle2D rect = new Rectangle2D.Double(0, 0, textureImage[0].getWidth(), textureImage[0].getHeight());
                            texturePaint = new TexturePaint(textureImage[0], rect);
                        }
                        Color color = selectedColor[0] != null ? selectedColor[0] : Color.BLACK;
        
                        // Calcul des coordonnées
                        int x = ((imageLabel.getWidth() - imageTemp.getWidth()) / 2) + imageTemp.getWidth() / 2;
                        int y = ((imageLabel.getHeight() - imageTemp.getHeight()) / 2) + imageTemp.getHeight() / 2;
                        int width = getTextWidth(text, font);
        
                        // Créer le RenderText
                        RenderText renderText = new RenderText(x, y, text, font, color, width);
                        if (texturePaint != null) {
                            renderText.setTexture(texturePaint); // Appliquer la texture
                        }
        
                        // Ajouter le texte et rafraîchir l'image
                        shapeTextes.add(new Shape(x - 20, y - fontSize, width + 20, fontSize + 20, true, color, renderText));
                        renderTexts.add(renderText);
                        imageLabel.repaint();
        
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ImageView.this, "Erreur d'entrée ! Veuillez entrer des valeurs valides.",
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
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

    private int getTextWidth(String text, Font font) {
        if (text == null || font == null) {
            return 0; // Éviter les NullPointerException
        }
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImage.createGraphics();
        FontMetrics metrics = g2d.getFontMetrics(font);
        g2d.dispose();
        return metrics.stringWidth(text);
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
        if (this.image == null) {
			this.image = image;
			this.originalImage = deepCopy(image);
			this.imageTemp = deepCopy(image);
		} else {
			this.imageTemp = image;
		}
		imageLabel.repaint();
    }

    public BufferedImage getImage() {
        return image;
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
                else if (shapeTextes.size() > 0  && !isDrawingRectangle && !isDrawingCircle) {

                    for (Shape shapeTexte : shapeTextes) {
                        if (shapeTexte.contains(e.getX(), e.getY())) {
                            selectedShape = shapeTexte;
                            lastMousePosition = e.getPoint();
                            break;
                        }
                    }
                    
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
                if (selectedShape != null && lastMousePosition != null && !isPasting) {
                    int deltaX = e.getX() - lastMousePosition.x;
                    int deltaY = e.getY() - lastMousePosition.y;
                    selectedShape.moveTo(selectedShape.getX() + deltaX, selectedShape.getY() + deltaY);
                    if (selectedShape.getRenderText() != null) {
                        selectedShape.getRenderText().moveTo(selectedShape.getRenderText().getX() + deltaX, selectedShape.getRenderText().getY() + deltaY);
                    }

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
                        currentShape = new Shape(e.getX(), e.getY(), 0, 0, isDrawingRectangle, Color.RED,null); // Initialise
                        shape = null; // une forme
                    }
                    imageLabel.repaint();
                } else if (selectedShape != null && lastMousePosition != null && isPasting) {
					int deltaX = e.getX() - lastMousePosition.x;
					int deltaY = e.getY() - lastMousePosition.y;

					// Calculer la nouvelle position proposée
					int newX = selectedShape.getX() + deltaX;
					int newY = selectedShape.getY() + deltaY;

					// Contraindre les nouvelles coordonnées pour rester dans les limites de l'image
					// principale
					int maxWidth = imageTemp.getWidth() + (imageLabel.getWidth() - imageTemp.getWidth()) / 2;
					int maxHeight = imageTemp.getHeight() + (imageLabel.getHeight() - imageTemp.getHeight()) / 2;

					if (imagePaste != null) {
						int pasteWidth = imagePaste.getWidth();
						int pasteHeight = imagePaste.getHeight();

						// Contraindre X
						if (newX < (imageLabel.getWidth() - imageTemp.getWidth()) / 2)
							newX = (imageLabel.getWidth() - imageTemp.getWidth()) / 2;
						if (newX + pasteWidth > maxWidth)
							newX = maxWidth - pasteWidth;

						// Contraindre Y
						if (newY < (imageLabel.getHeight() - imageTemp.getHeight()) / 2)
							newY = (imageLabel.getHeight() - imageTemp.getHeight()) / 2;
						if (newY + pasteHeight > maxHeight)
							newY = maxHeight - pasteHeight;
					}

					// Restaurer les pixels de l'image originale avant le déplacement
					if (shape != null) {
						Point topLeft = convertToImageCoordinates(selectedShape.getX(), selectedShape.getY());

						int x1 = Math.max(0, topLeft.x);
						int y1 = Math.max(0, topLeft.y);
						int width = Math.min(imagePaste.getWidth(), shape.getWidth());
						int height = Math.min(imagePaste.getHeight(), shape.getHeight());

						BufferedImage originalSubImage = originalImage.getSubimage(x1, y1, width, height);

						Graphics2D g2dRestore = imageTemp.createGraphics();
						g2dRestore.drawImage(originalSubImage, x1, y1, null);
						g2dRestore.dispose();
					}

					// Mettre à jour la position de la forme avec les coordonnées contraintes
					selectedShape.moveTo(newX, newY);
					lastMousePosition = e.getPoint();

					// Dessiner l'image collée à la nouvelle position
					if (imagePaste != null) {
						Point newTopLeft = convertToImageCoordinates(newX, newY);

						Graphics2D g2dDraw = imageTemp.createGraphics();
						g2dDraw.drawImage(imagePaste, newTopLeft.x, newTopLeft.y, null);
						g2dDraw.dispose();
					}

					// Mise à jour de l'affichage
					updateImage(imageTemp);
					imageLabel.repaint();
				}

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                for (Shape shapeTexte : shapeTextes) {
                    if (shapeTexte.contains(x, y)) {
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                        shapeTexte.setOver(true);
                    } else {
                        shapeTexte.setOver(false);
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                }
                imageLabel.repaint();
            }
        });

    }

    private Point convertToImageCoordinates(int x, int y) {
        int offsetX = (imageLabel.getWidth() - imageTemp.getWidth()) / 2;
        int offsetY = (imageLabel.getHeight() - imageTemp.getHeight()) / 2;
        int imageX = x - offsetX;
        int imageY = y - offsetY;
        return new Point(imageX, imageY);
    }

    public void copyImage() {
		if (shape == null) {
			JOptionPane.showMessageDialog(this, "Aucune zone sélectionnée.", "Erreur", JOptionPane.ERROR_MESSAGE);
			return;
		}
	
		Point topLeft = convertToImageCoordinates(shape.getX(), shape.getY());
		int x1 = Math.max(0, topLeft.x);
		int y1 = Math.max(0, topLeft.y);
		int x2 = Math.min(image.getWidth(), x1 + shape.getWidth());
		int y2 = Math.min(image.getHeight(), y1 + shape.getHeight());
	
		int width = x2 - x1;
		int height = y2 - y1;
	
		if (width > 0 && height > 0) {
			if (shape.isRectangle()) { 
				// Cas où la forme est rectangulaire : copie classique
				this.imagePaste = this.image.getSubimage(x1, y1, width, height);
			} else { 
				// Cas où la forme n'est pas rectangulaire : appliquer un masque circulaire
				BufferedImage subImage = image.getSubimage(x1, y1, width, height);
	
				// Créer une nouvelle image avec transparence
				BufferedImage circularImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = circularImage.createGraphics();
	
				// Activer l'anticrénelage pour des bords plus doux
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	
				// Appliquer un masque circulaire (Ellipse)
				g2d.setClip(new Ellipse2D.Float(0, 0, width, height));
				g2d.drawImage(subImage, 0, 0, null);
	
				// Libérer les ressources
				g2d.dispose();
	
				this.imagePaste = circularImage; 
			}
	
			this.controller.copyImage(imagePaste, shape);
			JOptionPane.showMessageDialog(this, "Zone copiée.", "Information", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Zone invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}

    public void pasteImage(BufferedImage image) {
		this.isPasting = true;
		this.imagePaste = image;
		if (imagePaste == null) {
			JOptionPane.showMessageDialog(this, "Aucune image à coller.", "Erreur", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (shape != null) {

			//centrer l'image collée
			int x = (imageLabel.getWidth() - imagePaste.getWidth()) / 2;
			int y = (imageLabel.getHeight() - imagePaste.getHeight()) / 2;

			this.shape.moveTo(x, y);

			Point topLeft = convertToImageCoordinates(shape.getX(), shape.getY());
			Graphics2D g2d = imageTemp.createGraphics();
			g2d.drawImage(imagePaste, topLeft.x, topLeft.y, null);
			g2d.dispose();
			updateImage(imageTemp);
		} else {
			JOptionPane.showMessageDialog(this, "Aucune zone de collage sélectionnée.", "Erreur",
					JOptionPane.ERROR_MESSAGE);
		}
	}
    public static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

    private void adjustShapeToResize() {
        if (shape != null) {
            double scaleX = (double) imageTemp.getWidth() / this.getWidth();
            double scaleY = (double) imageTemp.getHeight() / this.getHeight();

            shape.moveTo((int) (shape.getX() * scaleX), (int) (shape.getY() * scaleY));
        }
    }

}
