package VueMax;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;

import ControllerMax.ImageController;

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
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        this.controller = controller;
        this.shape = null;

        imageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (shape != null) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    shape.draw(g2d);
                } else if (currentShape != null) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    currentShape.draw(g2d);
                } else if ( imageLabel.getIcon() != null) {
                    super.paintComponent(g);
                    g.drawImage(image, imageLabel.getX(), imageLabel.getY(), null);
                }
            }
        };

        JScrollPane scrollPane = new JScrollPane(imageLabel);
        add(scrollPane, BorderLayout.CENTER);

        JToolBar toolBar = new JToolBar();
        JButton openButton = new JButton("Open");
        JButton saveButton = new JButton("Save");
        JButton paintBucketButton = new JButton("Paint Bucket");
        JButton rotateButtonLeft = new JButton("Rotate left");
        JButton rotatebuttonRight = new JButton("Rotate right");
        JButton rotateCustomButton = new JButton("Rotate Custom");
        JButton flipHorizontalButton = new JButton("Flip Horizontal");
        JButton flipVerticalButton = new JButton("Flip Vertical");
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
        toolBar.add(rotateButtonLeft);
        toolBar.add(rotatebuttonRight);
        toolBar.add(rotateCustomButton);
        toolBar.add(flipHorizontalButton);
        toolBar.add(flipVerticalButton);
        toolBar.add(brightenButtonPlus);
        toolBar.add(new JLabel("Brightness:"));
        toolBar.add(brightenButtonMoin);
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
                controller.applyPaintBucket(10, 10, Color.RED, 50); // Exemple
            }
        });

        rotateButtonLeft.addActionListener(e -> {
            if (controller != null) {
                controller.rotate(true);
            }
        });

        rotatebuttonRight.addActionListener(e -> {
            if (controller != null) {
                controller.rotate(false);
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
