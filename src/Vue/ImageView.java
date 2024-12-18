package Vue;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;

import Controller.ImageController;
import Model.ImageModel;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

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
	private boolean isPasting = false;
	private int clickX, clickY;

	private BufferedImage image, imageTemp, imagePaste, originalImage = null;

	public ImageView(ImageController controller) {
		setTitle("Image Editor");

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
				} else if (currentShape != null) {
					Graphics2D g2d = (Graphics2D) g;
					currentShape.draw(g2d);
				}
			}
		};

		imageLabel.setHorizontalAlignment(JLabel.LEFT);
		imageLabel.setVerticalAlignment(JLabel.TOP);

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
		JButton copierButton = new JButton("Copier");
		JButton collerButton = new JButton("Coller");
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
		toolBar.add(copierButton);
		toolBar.add(collerButton);
		toolBar.add(drawRectangleButton);
		toolBar.add(drawCircleButton);

		add(toolBar, BorderLayout.NORTH);

		openButton.addActionListener(this::handleOpenImage);
		saveButton.addActionListener(this::handleSaveImage);

		paintBucketButton.addActionListener(e -> {
				Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
				setCursor(cursor);
				isPainting = true;
			
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
				// Changer le curseur pour la pipette
				Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
				setCursor(cursor);
				isPickingColor = true;
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
							controller.pickColor(imageX, imageY);
						}
					}

					isPickingColor = false;
					setCursor(Cursor.getDefaultCursor());
				}
			}
		});

		rotateButtonLeft.addActionListener(e -> {controller.rotate(false);});

		rotatebuttonRight.addActionListener(e -> {controller.rotate(true);
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

		flipHorizontalButton.addActionListener(e -> {controller.flipImage(true);});

		flipVerticalButton.addActionListener(e -> {controller.flipImage(false);});

		brightenButtonPlus.addActionListener(e -> {controller.adjustBrightness(10);});

		brightenButtonMoin.addActionListener(e -> {controller.adjustBrightness(-10);});

		darkenButtonPlus.addActionListener(e ->{controller.adjustContrast(10);});

		darkenButtonMoin.addActionListener(e -> {
				controller.adjustContrast(-10);});

		copierButton.addActionListener(e -> {copyImage();});

		collerButton.addActionListener(e -> {controller.pasteImage(this);});

		drawRectangleButton.addActionListener(e -> {toggleIsDrawingRectangle();});

		drawCircleButton.addActionListener(e -> {toggleIsDrawingCircle();});

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

	public BufferedImage getImageTemp() {
		return imageTemp;
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

	private void adjustShapeToResize() {
		if (shape != null) {
			double scaleX = (double) imageTemp.getWidth() / this.getWidth();
			double scaleY = (double) imageTemp.getHeight() / this.getHeight();

			shape.moveTo((int) (shape.getX() * scaleX), (int) (shape.getY() * scaleY));
		}
	}

	public static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

}
