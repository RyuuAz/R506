package Vue;

import javax.swing.*;

import Controller.ImageController;
import Model.ImageModel;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

public class ImageView extends JFrame {
    private JLabel imageLabel;
    private ImageController controller;
    private ImageModel model;
    private Color pickedColor;
    private boolean isPickingColor;
    private boolean isPainting;
    private JPanel colorDisplayPanel;

    public ImageView() {
        setTitle("Image Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        imageLabel = new JLabel();
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
                    if (controller != null) 
                    {
                        controller.applyPaintBucket(x, y, pickedColor, 90);
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
                    if (controller != null) 
                    {
                        controller.pickColor(x, y);
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
                String angleStr = JOptionPane.showInputDialog(this, "Enter rotation angle (degrees):", "Rotate Custom", JOptionPane.PLAIN_MESSAGE);
                try {
                    int angle = Integer.parseInt(angleStr);
                    controller.rotateImageByAngle(angle);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input! Please enter a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
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

    }

    private void handleOpenImage(ActionEvent e) {
        if (controller != null) {
            JFileChooser fileChooser = new JFileChooser();
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

    public void setModel(ImageModel model) {
        this.model = model;
    }

    public void updateImage(BufferedImage image) {
        imageLabel.setIcon(new ImageIcon(image));
    }

    public void init() {
        setVisible(true);
    }
}
