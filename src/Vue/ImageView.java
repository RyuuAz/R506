package Vue;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.formdev.flatlaf.FlatLightLaf; // Import FlatLaf
import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.*;
import java.awt.event.*;
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
    private Menu menu;

    private Shape shape;
    private Shape currentShape = null; // Forme temporaire en cours de dessin
    private Shape selectedShape = null;
    private Point lastMousePosition;
    private boolean isDrawingRectangle = false;
    private boolean isDrawingCircle = false;
    private boolean isPasting = false;
    private int clickX, clickY;

    private int imageWidth, imageHeight, labelWidth, labelHeight;

    private BufferedImage image, imageTemp, imagePaste = null;

    public ImageView(ImageController controller) {

        setTitle("Pix.net");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.controller = controller;

        // Initialisation de FlatLaf (par défaut Light)
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

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

        // Créer une étiquette pour afficher l'image
        imageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imageTemp != null) {
                    // Dessiner l'image temporaire au même emplacement que l'imageLabel
                    g.drawImage(imageTemp, imageLabel.getWidth(), imageLabel.getHeight(), imageTemp.getWidth(), imageTemp.getHeight(), this);
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

        // Initialiser les variables
        this.shape = null;

        // Centrer l'image dans le JLabel
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);

        labelWidth = imageLabel.getWidth();
        labelHeight = imageLabel.getHeight();


        JScrollPane scrollPane = new JScrollPane(imageLabel);
        add(scrollPane, BorderLayout.CENTER);

        
        // Ajouter le topPanel à la fenêtre
        menu = new Menu(controller, this);
        add(menu, BorderLayout.NORTH);
        

        // Crée la palette de d'outils
        ToolBar toolbar = new ToolBar();

        add(toolbar, BorderLayout.WEST);

        // Event pour le seau de peinture
        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (isPainting) {
                    int x = evt.getX();
                    int y = evt.getY();

                    System.out.println("x: " + x + " y: " + y);
                  
                    int imageX = x - (labelWidth - imageWidth) / 2;
                    int imageY = y - (labelHeight - imageHeight) / 2;

                    System.out.println("imageX: " + imageX + " imageY: " + imageY);

                    // Vérifiez si les coordonnées ajustées sont dans les limites de l'image
                    if (imageX >= 0 && imageX < image.getWidth() && imageY >= 0 && imageY < image.getHeight()) {
                        if (controller != null) {
                            //Vérifiez si il y a une forme sélectionnée
                            if (shape != null) {
                                updateImage(controller.applyPaintBucket(getImageTemp(),imageX, imageY, pickedColor, menu.getSliderValue(), shape, imageLabel));
                            } else {
                                updateImage(controller.applyPaintBucket(getImageTemp(),imageX, imageY, pickedColor, menu.getSliderValue(), null, imageLabel));
                            }
                        }
                    }

                    isPainting = false;
                    setCursor(Cursor.getDefaultCursor());
                }
            }
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
                            pickColor(imageX, imageY);
                        }
                    }

                    isPickingColor = false;
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        addMouseListeners();

        this.setVisible(true);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustShapeToResize();
                imageLabel.repaint();
            }
        });
    }

    public void togglePickColor(ActionEvent e) {
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

    public void togglePaintBucket(ActionEvent e) {
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

	public void toggleIsDrawingRectangle() {
        this.isDrawingRectangle = !this.isDrawingRectangle;
    }

	public void toggleIsDrawingCircle() {
        this.isDrawingCircle = !this.isDrawingCircle;

    }

    public void displayPickedColor(Color color) {
        this.pickedColor = color;
        menu.setColorDisplayPanelColor(color);
    }

    public JLabel getImageLabel() {
        return imageLabel;
    }

    public BufferedImage getImageTemp() {
        return imageTemp; 
    }

    public void addShape(Shape shape) {
        this.shape = shape;
    }

    public boolean getIsDrawingRectangle() {
        System.out.println("isDrawingRectangle: " + isDrawingRectangle);
        return this.isDrawingRectangle;
    }

    

    public boolean getIsDrawingCircle() {
        System.out.println("isDrawingCircle: " + isDrawingCircle);
        return this.isDrawingCircle;

    }

    public void updateImage(BufferedImage image) {
        this.image = image;
        imageLabel.setIcon(new ImageIcon(image));
        if (this.image == null) {
            this.image = image;
        }
        this.imageTemp = image;
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
                if (selectedShape != null && lastMousePosition != null && !isPasting) {
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
                } else if (selectedShape != null && lastMousePosition != null && isPasting) {
                    int deltaX = e.getX() - lastMousePosition.x;
                    int deltaY = e.getY() - lastMousePosition.y;

                    // Restaurer la zone de l'image précédente
                    if (imagePaste != null && shape != null) {
                        Point topLeft = controller.convertToImageCoordinates(shape.getX(), shape.getY());
                        Graphics2D g2dTemp = imageTemp.createGraphics();

                        int x1 = Math.max(0, topLeft.x);
                        int y1 = Math.max(0, topLeft.y);
                        int x2 = Math.min(imagePaste.getWidth() + x1, x1 + shape.getWidth());
                        int y2 = Math.min(imagePaste.getHeight()+ y1, y1 + shape.getHeight());
                
                        int width = x2 - x1;
                        int height = y2 - y1;
                        BufferedImage imagesub =null;
                        if (width > 0 && height > 0) {
                            imagesub = image.getSubimage(x1, y1, width, height);
                        }

                        // Restaurer les pixels originaux
                        g2dTemp.drawImage(imagesub,
                        topLeft.x, 
                        topLeft.y,
                        topLeft.x + imagesub.getWidth(),
                        topLeft.y + imagesub.getHeight(), 
                        null);

                        g2dTemp.dispose();
                    }
                    updateImage(imageTemp);
                    imageLabel.repaint();

                    // Mettre à jour la position de la forme
                    selectedShape.moveTo(selectedShape.getX() + deltaX, selectedShape.getY() + deltaY);
                    lastMousePosition = e.getPoint();

                    // Dessiner la nouvelle position de l'image
                    if (imagePaste != null && shape != null) {
                        Point newTopLeft = controller.convertToImageCoordinates(selectedShape.getX(), selectedShape.getY());
                        Graphics2D g2d = imageTemp.createGraphics();
                        g2d.drawImage(imagePaste, newTopLeft.x, newTopLeft.y, null);
                        g2d.dispose();
                    }

                    // Mise à jour et réaffichage
                    updateImage(imageTemp);
                    imageLabel.repaint();
                }

            }
        });

    }

    public void copyImage() {
        if (shape == null) {
            JOptionPane.showMessageDialog(this, "Aucune zone sélectionnée.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Point topLeft = this.controller.convertToImageCoordinates(shape.getX(), shape.getY());
        int x1 = Math.max(0, topLeft.x);
        int y1 = Math.max(0, topLeft.y);
        int x2 = Math.min(image.getWidth(), x1 + shape.getWidth());
        int y2 = Math.min(image.getHeight(), y1 + shape.getHeight());

        int width = x2 - x1;
        int height = y2 - y1;

        if (width > 0 && height > 0) {
            this.imagePaste = this.image.getSubimage(x1, y1, width, height);
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
            Point topLeft = this.controller.convertToImageCoordinates(shape.getX(), shape.getY());
            Graphics2D g2d = imageTemp.createGraphics();
            g2d.drawImage(imagePaste, topLeft.x, topLeft.y, null);
            g2d.dispose();
            updateImage(imageTemp);
        } else {
            JOptionPane.showMessageDialog(this, "Aucune zone de collage sélectionnée.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adjustShapeToResize() {
        if (shape != null) {
            double scaleX = (double) imageTemp.getWidth() / this.getWidth();
            double scaleY = (double) imageTemp.getHeight() / this.getHeight();

            shape.moveTo((int) (shape.getX() * scaleX), (int) (shape.getY() * scaleY));
        }
    }

    public void pickColor(int x, int y) {
        if (image == null || x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) {
            return;
        }
        int rgb = image.getRGB(x, y);
        this.displayPickedColor(new Color(rgb));
    }

    public void setImageTaille (int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
    }

}
