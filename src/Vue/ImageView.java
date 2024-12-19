package Vue;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.formdev.flatlaf.FlatLightLaf; // Import FlatLaf
import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

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
    private ImageController controller;
    private ImageModel model;
    private Color pickedColor;
    private boolean isPainting,isRemoving,isDrawingRectangle,isDrawingCircle,isPasting,isPickingColor = false;
    private JPanel colorDisplayPanel;
    private Menu menu;

    private Shape shape;
    private ArrayList<Shape> shapeTextes = new ArrayList<>();

    private Shape currentShape,selectedShape = null; // Forme temporaire en cours de dessin
    private ArrayList<RenderText> renderTexts = new ArrayList<>();
    private Point lastMousePosition;
    private int clickX, clickY;
    private BufferedImage[] textureImage = new BufferedImage[1];

    private int imageWidth, imageHeight, labelWidth, labelHeight;

    private BufferedImage image, imageTemp,imageCopy ,imagePaste,originalImage = null;

    public ImageView(ImageController controller) {

        setTitle("Pix.net");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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

        // Créer une étiquette pour afficher l'image
        imageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imageTemp != null) {
                    // Dessiner l'image temporaire au même emplacement que l'imageLabel
                    g.drawImage(imageTemp, imageLabel.getWidth(), imageLabel.getHeight(), imageTemp.getWidth(),
                            imageTemp.getHeight(), this);
                    imageWidth = imageTemp.getWidth();
                    imageHeight = imageTemp.getHeight();

                    labelWidth = imageLabel.getWidth();
                    labelHeight = imageLabel.getHeight();

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
        ToolPalette toolPalette = new ToolPalette();

        add(toolPalette, BorderLayout.WEST);

        // Event pour le seau de peinture
        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (isPainting) {
                    int x = evt.getX();
                    int y = evt.getY();

                    int imageX = x - (labelWidth - imageWidth) / 2;
                    int imageY = y - (labelHeight - imageHeight) / 2;

                    // Vérifiez si les coordonnées ajustées sont dans les limites de l'image
                    if (imageX >= 0 && imageX < image.getWidth() && imageY >= 0 && imageY < image.getHeight()) {
                        if (controller != null) {
                            // Vérifiez si il y a une forme sélectionnée
                            if (shape != null) {
                                updateImage(controller.applyPaintBucket(getImageTemp(), imageX, imageY, pickedColor,
                                        menu.getSliderValue(), shape, imageLabel));
                            } else {
                                updateImage(controller.applyPaintBucket(getImageTemp(), imageX, imageY, pickedColor,
                                        menu.getSliderValue(), null, imageLabel));
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
        if (this.image == null) {
			this.image = image;
			this.originalImage = deepCopy(image);
			
		} 
        this.imageTemp = deepCopy(image);
        imageLabel.setIcon(new ImageIcon(imageTemp));

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
						Point topLeft = controller.convertToImageCoordinates(selectedShape.getX(), selectedShape.getY());

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
						Point newTopLeft = controller.convertToImageCoordinates(newX, newY);

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
            BufferedImage copiedImage;
    
            if (shape.isRectangle()) {
                copiedImage = originalImage.getSubimage(x1, y1, width, height);
            } else {
                BufferedImage subImage = originalImage.getSubimage(x1, y1, width, height);
                copiedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = copiedImage.createGraphics();
    
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setClip(new Ellipse2D.Float(0, 0, width, height));
                g2d.drawImage(subImage, 0, 0, null);
                g2d.dispose();
            }
    
            // Dessiner le texte
            Graphics2D g2d = copiedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
            for (Shape shapeTexte : shapeTextes) {
                if (shape.contains(shapeTexte.getX(), shapeTexte.getY())) {
                    RenderText renderText = shapeTexte.getRenderText();
                    if (renderText != null) {
                        System.out.println("Dessin du texte : " + renderText.getText());
                        renderText.draw(g2d, shapeTexte.getX() - shape.getX(), shapeTexte.getY() - shape.getY());
                    } else {
                        System.out.println("Aucun texte à dessiner pour cette forme.");
                    }
                }
            }
    
            g2d.dispose();
            this.imageCopy = copiedImage;
            this.controller.copyImage(imageCopy, shape);
            this.shape = null;
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

    public void pickColor(int x, int y) {
        if (image == null || x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) {
            return;
        }
        int rgb = image.getRGB(x, y);
        this.displayPickedColor(new Color(rgb));
    }

    public void setColor(Color color) {
        this.pickedColor = color;
    }
}
