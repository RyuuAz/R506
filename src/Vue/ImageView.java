package Vue;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;

import Controller.ImageController;
import Model.ImageModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;

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
        setTitle("Image Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLayout(new BorderLayout());

        this.controller = controller;
        this.shape = null;

        this.affichJLabel = new JLabel("Hello World");

        if (this.image != null) {
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

        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(imageLabel);
        add(scrollPane, BorderLayout.CENTER);

        JToolBar toolBar = new JToolBar();
        JButton openButton = new JButton("Ouvrir");
        JButton saveButton = new JButton("Sauvegarder");
        JButton paintBucketButton = new JButton("Seau de peinture");

        // Création du panneau pour afficher la couleur sélectionnée
        colorDisplayPanel = new JPanel();
        colorDisplayPanel.setPreferredSize(new Dimension(30, 30)); // Taille du carré de couleur
        colorDisplayPanel.setBackground(Color.WHITE); // Couleur initiale (blanc)

        JButton pickColorButton = new JButton("Pipette de couleur");
        JButton rotateButtonLeft = new JButton("Rotation gauche");
        JButton rotatebuttonRight = new JButton("Rotation droite");
        JButton rotateCustomButton = new JButton("Rotation personnalisée");
        JButton flipHorizontalButton = new JButton("Retourner horizontalement");
        JButton flipVerticalButton = new JButton("Retourner verticalement");
        JButton brightenButtonPlus = new JButton("ec +");
        JButton brightenButtonMoin = new JButton("ec -");
        JButton darkenButtonPlus = new JButton("as +");
        JButton darkenButtonMoin = new JButton("as -");
        JButton drawRectangleButton = new JButton("Rectangle");
        JButton drawCircleButton = new JButton("Circle");

        toolBar.add(openButton);
        toolBar.add(saveButton);

        toolBar.addSeparator();

        toolBar.add(paintBucketButton);
        toolBar.add(pickColorButton);
        toolBar.add(colorDisplayPanel);

        toolBar.add(rotateButtonLeft);

        toolBar.addSeparator();

        toolBar.add(rotatebuttonRight);
        toolBar.add(rotateCustomButton);
        toolBar.add(flipHorizontalButton);
        toolBar.add(flipVerticalButton);

        toolBar.addSeparator();

        toolBar.add(brightenButtonPlus);
        toolBar.add(new JLabel("Brightness:"));
        toolBar.add(brightenButtonMoin);

        toolBar.addSeparator();

        toolBar.add(darkenButtonPlus);
        toolBar.add(new JLabel("Contrast:"));
        toolBar.add(darkenButtonMoin);
        toolBar.add(drawRectangleButton);
        toolBar.add(drawCircleButton);

        add(toolBar, BorderLayout.NORTH);

        openButton.addActionListener(this::handleOpenImage);
        saveButton.addActionListener(this::handleSaveImage);

        paintBucketButton.addActionListener(e -> {
            if (controller != null) {
                //Change le curseur pour le seau de peinture
                Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
                setCursor(cursor);
                isPainting = true;
            }
        });

        // Event pour le seau de peinture
        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() 
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) 
            {
                if (isPainting) 
                {
                    int x = evt.getX();
                    int y = evt.getY();

                    // Ajustement des coordonnées
                    int imageX = x - (imageLabel.getWidth() - image.getWidth()) / 2;
                    int imageY = y - (imageLabel.getHeight() - image.getHeight()) / 2;

                    // Vérifiez si les coordonnées ajustées sont dans les limites de l'image
                    if (imageX >= 0 && imageX < image.getWidth() && imageY >= 0 && imageY < image.getHeight()) {
                        if (controller != null) {
                            controller.applyPaintBucket(imageX, imageY, pickedColor, 90);
                        }
                    }

                    isPainting = false;
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        // ActionListener pour la pipette
        pickColorButton.addActionListener(e -> {
            if (controller != null) {
                //Changer le curseur pour la pipette
                Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
                setCursor(cursor);
                isPickingColor = true;
            }
        });

        // Event pour la pipette
        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() 
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) 
            {
                if (isPickingColor) 
                {
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


        rotateButtonLeft.addActionListener(e -> {
            if (controller != null) {
                controller.rotate(false);
            }
        });

        rotatebuttonRight.addActionListener(e -> {
            if (controller != null) {
                controller.rotate(true);
            }
        });

        rotateCustomButton.addActionListener(e -> {
            if (controller != null) {
                String angleStr = JOptionPane.showInputDialog(this, "Enter rotation angle (degrees):", "Rotate Custom",
                        JOptionPane.PLAIN_MESSAGE);
                try {
                    int angle = Integer.parseInt(angleStr);
                    controller.rotateImageByAngle(angle);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input! Please enter a numeric value.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        flipHorizontalButton.addActionListener(e -> {
            if (controller != null) {
                controller.flipImage(true);
            }
        });

        flipVerticalButton.addActionListener(e -> {
            if (controller != null) {
                controller.flipImage(false);
            }
        });

        brightenButtonPlus.addActionListener(e -> {
            if (controller != null) {
                controller.adjustBrightness(10);
            }
        });

        brightenButtonMoin.addActionListener(e -> {
            if (controller != null) {
                controller.adjustBrightness(-10);
            }
        });

        darkenButtonPlus.addActionListener(e -> {
            if (controller != null) {
                controller.adjustContrast(10);
            }
        });

        darkenButtonMoin.addActionListener(e -> {
            if (controller != null) {
                controller.adjustContrast(-10);
            }
        });

        drawRectangleButton.addActionListener(e -> {
            if (controller != null) {
                toggleIsDrawingRectangle();
            }
        });

        drawCircleButton.addActionListener(e -> {
            if (controller != null) {
                toggleIsDrawingCircle();
            }
        });

        addMouseListeners();

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

    private void handleSaveImage(ActionEvent e) {
        if (controller != null) {
            JFileChooser fileChooser = new JFileChooser();
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
                } else  if (isDrawingRectangle || isDrawingCircle) {
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

                        if(e.getX() < clickX) {
                            startX = e.getX();
                            endX = clickX;
                        } else {
                            endX = e.getX();
                            startX = clickX;
                        }

                        if(e.getY() < clickY) {
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
